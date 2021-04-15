package codechicken.lib.texture;

import codechicken.lib.colour.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.function.Consumer;

/**
 * Created by KitsuneAlex on 14/4/21.
 */
public class ProceduralTexture extends TextureAtlasSprite {

    private final Consumer<ProceduralTexture> cycleFunc;

    public ProceduralTexture(AtlasTexture atlas, TextureAtlasSprite other, Consumer<ProceduralTexture> cycleFunc) {
        this(atlas, other.info, other.mainImage.length - 1, 1, 1, other.x, other.y, other.mainImage[0], cycleFunc);
        this.u0 = other.u0;
        this.u1 = other.u1;
        this.v0 = other.v0;
        this.v1 = other.v1;
    }

    private ProceduralTexture(AtlasTexture atlas, Info info, int mipmapLevels, int storageX, int storageY, int x, int y, NativeImage image, Consumer<ProceduralTexture> cycleFunc) {
        super(atlas, info, mipmapLevels, storageX, storageY, x, y, image);
        this.cycleFunc = cycleFunc;
    }

    @Override
    public void cycleFrames() {
        cycleFunc.accept(this);

        int levels = mainImage.length - 1;

        if (levels > 0) {
            NativeImage[] mipped = MipmapGenerator.generateMipLevels(mainImage[0], levels);
            System.arraycopy(mipped, 0, mainImage, 0, mipped.length);
        }

        uploadFirstFrame();
    }

    /**
     * Gets the RGBA value of a given pixel.
     *
     * @param x The X coord.
     * @param y The Y coord.
     * @return The RGBA pixel value.
     */
    public int getPixel(int x, int y) {
        return Colour.flipABGR(mainImage[0].getPixelRGBA(x, y));// Flip to RGBA..
    }

    /**
     * Sets the RGBA value of a given pixel.
     *
     * @param x The X coord.
     * @param y The Y coord.
     * @param rgba The RGBA pixel value.
     */
    public void setPixel(int x, int y, int rgba) {
        mainImage[0].setPixelRGBA(x, y, Colour.flipABGR(rgba));// Flip to ABGR..
    }

}
