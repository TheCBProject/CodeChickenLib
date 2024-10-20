package codechicken.lib.datagen;

import codechicken.lib.gui.modular.lib.DynamicTextures;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This provider can be used to create a data generator that can generate various textures programmatically.
 * The primary function is to convert dynamically resized textures into new fixed size textures.
 * <p>
 * Created by brandon3055 on 07/09/2023
 */
public class DynamicTextureProvider implements DataProvider {

    private final DataGenerator gen;
    private final ExistingFileHelper fileHelper;
    private final String modid;
    private final List<GeneratorResult> results = new ArrayList<>();

    public DynamicTextureProvider(DataGenerator gen, ExistingFileHelper fileHelper, String modid) {
        this.gen = gen;
        this.fileHelper = fileHelper;
        this.modid = modid;
    }

    /**
     * If you are extending this provider, then override this method to add your textures.
     */
    public void addTextures() {
    }

    public void addDynamicTextures(DynamicTextures dynamicTextures) {
        dynamicTextures.makeTextures(t -> {
            addDynamicTexture(t.dynamicInput(), t.outputLocation(), t.width(), t.height(), t.topBorder(), t.leftBorder(), t.bottomBorder(), t.rightBorder());
            return t.guiTexturePath();
        });
    }

    public void addDynamicTexture(String dynamicInput, String outputTexture, int width, int height, int border) {
        addDynamicTexture(dynamicInput, outputTexture, width, height, border, border, border, border);
    }

    public void addDynamicTexture(String dynamicInput, String outputTexture, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        addDynamicTexture(ResourceLocation.fromNamespaceAndPath(modid, dynamicInput), ResourceLocation.fromNamespaceAndPath(modid, outputTexture), width, height, topBorder, leftBorder, bottomBorder, rightBorder);
    }

    public void addDynamicTexture(ResourceLocation dynamicInput, ResourceLocation outputTexture, int width, int height, int border) {
        addDynamicTexture(dynamicInput, outputTexture, width, height, border, border, border, border);
    }

    public void addDynamicTexture(ResourceLocation dynamicInput, ResourceLocation outputTexture, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        try {
            if (!dynamicInput.getPath().endsWith(".png")) {
                dynamicInput = ResourceLocation.fromNamespaceAndPath(dynamicInput.getNamespace(), dynamicInput.getPath() + ".png");
            }
            if (!outputTexture.getPath().endsWith(".png")) {
                outputTexture = ResourceLocation.fromNamespaceAndPath(outputTexture.getNamespace(), outputTexture.getPath() + ".png");
            }

            Resource inputResource = fileHelper.getResource(dynamicInput, PackType.CLIENT_RESOURCES);
            PackOutput packOutput = gen.getPackOutput("assets/" + outputTexture.getNamespace());
            Path outputFile = packOutput.getOutputFolder().resolve(outputTexture.getPath());

            BufferedImage input = ImageIO.read(inputResource.open());
            BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = output.createGraphics();

            int texWidth = input.getWidth();
            int texHeight = input.getHeight();
            int trimWidth = texWidth - leftBorder - rightBorder;
            int trimHeight = texHeight - topBorder - bottomBorder;
            if (width <= texWidth) trimWidth = Math.min(trimWidth, width - rightBorder);
            if (width <= 0 || height <= 0 || trimWidth <= 0 || trimHeight <= 0) throw new IllegalArgumentException("Invalid size parameters");

            for (int x = 0; x < width; ) {
                int rWidth = Math.min(width - x, trimWidth);
                int trimU = 0;
                if (x != 0) {
                    if (x + leftBorder + trimWidth <= width) {
                        trimU = leftBorder;
                    } else {
                        trimU = (texWidth - (width - x));
                    }
                }

                //Top & Bottom trim
                bufferDynamic(graphics, input, x, 0, trimU, 0, rWidth, topBorder);
                bufferDynamic(graphics, input, x, height - bottomBorder, trimU, texHeight - bottomBorder, rWidth, bottomBorder);

                rWidth = Math.min(width - x - leftBorder - rightBorder, trimWidth);
                for (int y = 0; y < height; ) {
                    int rHeight = Math.min(height - y - topBorder - bottomBorder, trimHeight);
                    int trimV;
                    if (y + (texHeight - topBorder - bottomBorder) <= height) {
                        trimV = topBorder;
                    } else {
                        trimV = texHeight - (height - y);
                    }

                    //Left & Right trim
                    if (x == 0 && y + topBorder < height - bottomBorder) {
                        bufferDynamic(graphics, input, 0, y + topBorder, 0, trimV, leftBorder, rHeight);
                        bufferDynamic(graphics, input, width - rightBorder, y + topBorder, trimU + texWidth - rightBorder, trimV, rightBorder, rHeight);
                    }

                    //Core
                    if (y + topBorder < height - bottomBorder && x + leftBorder < width - rightBorder) {
                        bufferDynamic(graphics, input, x + leftBorder, y + topBorder, leftBorder, topBorder, rWidth, rHeight);
                    }
                    y += trimHeight;
                }
                x += trimWidth;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(output, "png", bos);
            results.add(new GeneratorResult(bos.toByteArray(), outputFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void bufferDynamic(Graphics2D graphics, BufferedImage image, int x, int y, int textureX, int textureY, int width, int height) {
        graphics.drawImage(image, x, y, x + width, y + height, textureX, textureY, textureX + width, textureY + height, null);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        addTextures();
        CompletableFuture<?>[] futures = new CompletableFuture[results.size()];
        for (int i = 0; i < results.size(); i++) {
            futures[i] = saveResult(writer, results.get(i));
        }
        return CompletableFuture.allOf(futures);
    }

    @SuppressWarnings ("UnstableApiUsage")
    static CompletableFuture<?> saveResult(CachedOutput arg, GeneratorResult result) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                HashingOutputStream hos = new HashingOutputStream(Hashing.sha1(), bos);
                hos.write(result.fileBytes);
                arg.writeIfNeeded(result.path, bos.toByteArray(), hos.hash());
                LOGGER.info("Saved file to {}", result.path);
            } catch (IOException ex) {
                LOGGER.error("Failed to save file to {}", result.path, ex);
            }

        }, Util.backgroundExecutor());
    }

    @Override
    public String getName() {
        return "dynamic-textures:" + modid;
    }

    private static class GeneratorResult {

        private final byte[] fileBytes;
        private final Path path;

        private GeneratorResult(byte[] fileBytes, Path filePath) {
            this.fileBytes = fileBytes;
            this.path = filePath;
        }
    }
}
