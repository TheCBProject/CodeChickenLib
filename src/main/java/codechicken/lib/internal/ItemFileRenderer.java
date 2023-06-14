package codechicken.lib.internal;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.covers1624.quack.image.AnimatedGifEncoder;
import net.covers1624.quack.io.IOUtils;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
    private static final List<GifRenderTask> gifTasks = new LinkedList<>();

    public static void addRenderTask(ItemStack stack, Path file, int resolution) {
        tasks.add(new RenderTask(stack, file, resolution));
    }

    public static void addGifRenderTask(ItemStack stack, Path file, int resolution, int fps, int duration) {
        gifTasks.add(new GifRenderTask(stack, file, resolution, fps, duration));
    }

    public static void tick() {
        renderStackToFile();
        renderGifs();
    }

    private static void renderStackToFile() {
        if (tasks.isEmpty()) return;

        RenderTask task = tasks.pop();
        takeItemScreenshot(task.stack, task.resolution, image -> {
            try {
                image.writeToFile(IOUtils.makeParents(task.file));
            } catch (IOException ex) {
                LOGGER.error("Failed to write image to file.", ex);
            }
        });
    }

    private static void renderGifs() {
        gifTasks.removeIf(GifRenderTask::render);
    }

    private static void takeItemScreenshot(ItemStack stack, int res, Consumer<NativeImage> cons) {
        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();
        PoseStack pStack = RenderSystem.getModelViewStack();
        if (mainTarget.width < res || mainTarget.height < res) {
            // TODO explode instead?
            LOGGER.warn("Window is not at least 512x512 make it bigger! Your image is probably cropped a bit.");
        }

        Matrix4f ortho = Matrix4f.orthographic(0, mainTarget.width * 16F / res, 0, mainTarget.height * 16F / res, -3000, 3000);
        RenderSystem.setProjectionMatrix(ortho);

        pStack.pushPose();
        pStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        mainTarget.bindWrite(true);
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glClearDepth(1.0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Lighting.setupFor3DItems();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();

        mc.getItemRenderer().renderGuiItem(stack, 0, 0);

        try (NativeImage fullScreenshot = new NativeImage(mainTarget.width, mainTarget.height, false);
             NativeImage subImage = new NativeImage(res, res, false)) {
            RenderSystem.bindTexture(mainTarget.getColorTextureId());
            fullScreenshot.downloadTexture(0, false);
            fullScreenshot.flipY();
            fullScreenshot.resizeSubRectTo(0, 0, res, res, subImage);
            cons.accept(subImage);
        }

        pStack.popPose();
        RenderSystem.applyModelViewMatrix();
        mainTarget.unbindWrite();
    }

    private record RenderTask(ItemStack stack, Path file, int resolution) {
    }

    private static final class GifRenderTask {

        public final ItemStack stack;
        public final Path file;
        private final int resolution;
        public final int fps;
        public final long targetDuration;

        public final List<byte[]> frames;
        public final long frameDelay;

        public long startTime = -1;
        public long lastFrame;

        private GifRenderTask(ItemStack stack, Path file, int resolution, int fps, int targetDuration) {
            this.stack = stack;
            this.file = file;
            this.resolution = resolution;
            this.fps = fps;
            this.targetDuration = TimeUnit.SECONDS.toMillis(targetDuration);

            frames = new ArrayList<>(targetDuration * fps);
            frameDelay = (long) ((1F / fps) * 1000F);
        }

        public boolean render() {
            long currTime = System.currentTimeMillis();
            if (startTime == -1) {
                startTime = currTime;
            }
            if (startTime + targetDuration <= currTime) {
                CompletableFuture.runAsync(this::finishGif);
                return true;
            }

            // Wait more for next frame.
            if (lastFrame + frameDelay > currTime) return false;
            lastFrame = currTime;
            takeItemScreenshot(stack, resolution, SneakyUtils.<NativeImage>sneak(e -> frames.add(e.asByteArray())));
            LOGGER.info("Captured gif frame {} / {}", frames.size(), ((targetDuration / 1000) * fps));
            return false;
        }

        private void finishGif() {
            LOGGER.info("Writing gif..");
            try (OutputStream os = Files.newOutputStream(file)) {
                AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                encoder.start(os);
                encoder.setDelay((int) frameDelay);
                encoder.setRepeat(0);  // Always repeat.
                encoder.setQuality(1); // Best quality possible.
                for (int i = 0; i < frames.size(); i++) {
                    byte[] frame = frames.get(i);
                    LOGGER.info("Encoding Frame {} / {}", i + 1, frames.size());
                    encoder.addFrame(ImageIO.read(new ByteArrayInputStream(frame)));
                }
                encoder.finish();
                LOGGER.info("Finished writing gif.");
            } catch (IOException ex) {
                LOGGER.error("Failed to write gif.", ex);
            }
        }
    }
}
