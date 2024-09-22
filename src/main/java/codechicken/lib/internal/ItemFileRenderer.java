package codechicken.lib.internal;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.covers1624.quack.image.AnimatedGifEncoder;
import net.covers1624.quack.io.IOUtils;
import net.covers1624.quack.platform.OperatingSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Mostly internal, intended for developer use.
 * <p>
 * May change at any time.
 * <p>
 * Created by covers1624 on 27/2/23.
 */
public class ItemFileRenderer {

    public static final int DEFAULT_RES = 512;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final LinkedList<RenderTask> tasks = new LinkedList<>();
    private static final Path OUTPUT = Paths.get("exports");
    private static @Nullable RenderTarget target;

    public static void renderStatic(ItemStack stack, String file, int resolution) {
        tasks.add(new StaticRenderTask(stack, resolution, OUTPUT.resolve(file)));
    }

    public static void addGifRenderTask(ItemStack stack, String file, int resolution, int fps, int duration) {
        tasks.add(new GifRenderTask(stack, resolution, OUTPUT.resolve(file), fps, duration));
    }

    public static boolean addWebpRenderTask(ItemStack stack, String file, int resolution, int fps, int duration) {
        if (!WebpRenderTask.isFfmpegAvailable()) return false;

        tasks.add(new WebpRenderTask(stack, resolution, OUTPUT.resolve(file), fps, duration));
        return true;
    }

    public static void tick() {
        RenderTask task = tasks.peek();
        if (task != null && guardRender(task)) {
            tasks.pop();
        }
    }

    private static boolean guardRender(RenderTask task) {
        try {
            return task.render();
        } catch (Throwable ex) {
            LOGGER.error("Failed to render.", ex);
            return true;
        }
    }

    private static abstract class RenderTask {

        private static final boolean DEBUG = Boolean.getBoolean("ccl.ItemFileRenderer.debug");

        protected final ItemStack stack;

        protected final int resolution;
        protected final Path path;

        private RenderTask(ItemStack stack, int resolution, Path path) {
            this.stack = stack;
            this.resolution = resolution;
            this.path = path;
        }

        /**
         * Perform the render operation.
         *
         * @return If the render operation is complete.
         */
        protected abstract boolean render() throws IOException;

        protected NativeImage takeItemScreenshot() {
            long start = System.nanoTime();
            Minecraft mc = Minecraft.getInstance();
            PoseStack pStack = RenderSystem.getModelViewStack();
            GuiGraphics guiGraphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());

            if (target == null || target.width < resolution || target.height < resolution) {
                if (target == null) {
                    target = new RenderTarget(true) { };
                }
                target.resize(resolution, resolution, Minecraft.ON_OSX);
            }

            Matrix4f ortho = new Matrix4f().setOrtho(0, resolution * 16F / resolution, resolution * 16F / resolution, 0, -3000, 3000);
            RenderSystem.setProjectionMatrix(ortho, VertexSorting.ORTHOGRAPHIC_Z);

            pStack.pushPose();
            pStack.setIdentity();
            RenderSystem.applyModelViewMatrix();
            target.bindWrite(true);

            GL11.glClearColor(0, 0, 0, 0);
            GL11.glClearDepth(1.0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            Lighting.setupFor3DItems();
            RenderSystem.enableCull();
            long preRender = System.nanoTime();
            if (DEBUG) LOGGER.info("Setup: {}ns", preRender - start);

            guiGraphics.renderItem(stack, 0, 0);

            long postRender = System.nanoTime();
            if (DEBUG) LOGGER.info("Render: {}ns", postRender - preRender);

            NativeImage image = new NativeImage(resolution, resolution, false);
            target.bindRead();
            image.downloadTexture(0, false);
            image.flipY();
            if (DEBUG) LOGGER.info("Screenshot: {}ns", System.nanoTime() - postRender);

            pStack.popPose();
            RenderSystem.applyModelViewMatrix();

            target.unbindWrite();
            return image;
        }
    }

    private static class StaticRenderTask extends RenderTask {

        private StaticRenderTask(ItemStack stack, int resolution, Path path) {
            super(stack, resolution, path);
        }

        @Override
        protected boolean render() throws IOException {
            try (NativeImage image = takeItemScreenshot()) {
                image.flipY();
                image.writeToFile(IOUtils.makeParents(path));
            }
            return true;
        }
    }

    private static abstract class AnimatedRenderTask extends RenderTask {

        protected final int fps;
        protected final long targetDuration;

        protected final List<NativeImage> frames;
        protected final long frameDelay;

        protected long startTime = -1;
        protected long lastFrame;

        private @Nullable CompletableFuture<Void> finalizeTask;

        private AnimatedRenderTask(ItemStack stack, int resolution, Path path, int fps, int duration) {
            super(stack, resolution, path);
            this.fps = fps;
            this.targetDuration = TimeUnit.SECONDS.toMillis(duration);

            frames = new ArrayList<>(duration * fps);
            frameDelay = (long) ((1F / fps) * 1000F);
        }

        @Override
        protected boolean render() {
            long currTime = System.currentTimeMillis();
            if (startTime == -1) {
                startTime = currTime;
            }
            if (startTime + targetDuration <= currTime) {
                if (finalizeTask == null) {
                    finalizeTask = CompletableFuture.runAsync(() -> {
                        try {
                            serialize();
                        } catch (Throwable ex) {
                            LOGGER.error("Failed to serialize item render task.", ex);
                        }
                    });
                }
                return finalizeTask.isDone();
            }

            // Wait more for next frame.
            if (lastFrame + frameDelay > currTime) return false;
            lastFrame = currTime;
            frames.add(takeItemScreenshot());
            LOGGER.info("Captured frame {} / {}", frames.size(), ((targetDuration / 1000) * fps));
            return false;
        }

        protected abstract void serialize() throws IOException;
    }

    private static class GifRenderTask extends AnimatedRenderTask {

        private GifRenderTask(ItemStack stack, int resolution, Path path, int fps, int duration) {
            super(stack, resolution, path, fps, duration);
        }

        @Override
        protected void serialize() throws IOException {
            try (OutputStream os = Files.newOutputStream(IOUtils.makeParents(path))) {
                AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                encoder.start(os);
                encoder.setDelay((int) frameDelay);
                encoder.setRepeat(0);  // Always repeat.
                encoder.setQuality(1); // Best quality possible.
                for (int i = 0; i < frames.size(); i++) {
                    LOGGER.info("Encoding Frame {} / {}", i + 1, frames.size());
                    NativeImage frame = frames.get(i);
                    try (frame) {
                        encoder.addFrame(ImageIO.read(new ByteArrayInputStream(frame.asByteArray())));
                    }
                }
                encoder.finish();
                LOGGER.info("Finished writing gif.");
            }
        }
    }

    private static class WebpRenderTask extends AnimatedRenderTask {

        public static final Supplier<@Nullable String> FFMPEG_BINARY = Suppliers.memoize(() -> {
            String ffmpeg = System.getProperty("ccl.ffmpeg", "ffmpeg" + (OperatingSystem.current().isWindows() ? ".exe" : ""));
            LOGGER.info("Probing for ffmpeg with {}", ffmpeg);
            try {
                int ret = runFfmpeg(List.of(ffmpeg, "-version"), LOGGER::info);
                if (ret == 0) {
                    LOGGER.info("ffmpeg available via {}", ffmpeg);
                    return ffmpeg;
                }
                LOGGER.error("Failed to find working ffmpeg. Got exit code: {}", ret);
            } catch (IOException ex) {
                LOGGER.error("Failed to find ffmpeg. Exception whilst running command.", ex);
            }
            return null;
        });

        public static boolean isFfmpegAvailable() {
            return FFMPEG_BINARY.get() != null;
        }

        private WebpRenderTask(ItemStack stack, int resolution, Path path, int fps, int duration) {
            super(stack, resolution, path, fps, duration);
        }

        @Override
        protected void serialize() throws IOException {
            Path tempDir = path.getParent().resolve(path.getFileName() + ".tmp");
            Files.createDirectory(tempDir);
            LOGGER.info("Dumping frames to temp directory..");
            int i = 0;
            List<Path> tempFiles = new ArrayList<>(frames.size() + 1);
            for (NativeImage frame : frames) {
                try (frame) {
                    Path file = tempDir.resolve(i++ + ".png");
                    tempFiles.add(file);
                    frame.writeToFile(file);
                }
            }
            tempFiles.add(tempDir);

            int ret = runFfmpeg(
                    List.of(
                            requireNonNull(FFMPEG_BINARY.get()),
                            "-y",
                            "-framerate", String.valueOf(fps),
                            "-i", tempDir.toAbsolutePath() + "/%d.png",
                            "-loop", "0",
                            "-lossless", "1",
                            "-compression_level", "0",
                            path.toAbsolutePath().toString()
                    ),
                    LOGGER::info
            );
            if (ret != 0) {
                LOGGER.error("ffmpeg exited with exit code {}", ret);
            } else {
                LOGGER.info("ffmpeg success!");
            }

            for (Path tempFile : tempFiles) {
                Files.deleteIfExists(tempFile);
            }
        }

        private static int runFfmpeg(List<String> args, Consumer<String> out) throws IOException {
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().forEach(out);
            }
            proc.onExit().join();
            return proc.exitValue();
        }
    }
}
