package codechicken.lib.texture;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;
import codechicken.lib.util.ResourceUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class TextureUtils {

    /**
     * @return an array of ARGB pixel data
     */
    public static int[] loadTextureData(ResourceLocation resource) {
        BufferedImage img = loadBufferedImage(resource);
        if (img == null) {
            return new int[0];
        }
        int w = img.getWidth();
        int h = img.getHeight();
        int[] data = new int[w * h];
        img.getRGB(0, 0, w, h, data, 0, w);
        return data;
    }

    public static Colour[] loadTextureColours(ResourceLocation resource) {
        int[] idata = loadTextureData(resource);
        Colour[] data = new Colour[idata.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new ColourARGB(idata[i]);
        }
        return data;
    }

    public static BufferedImage loadBufferedImage(ResourceLocation textureFile) {
        try {
            return loadBufferedImage(ResourceUtils.getResourceAsStream(textureFile));
        } catch (Exception e) {
            System.err.println("Failed to load texture file: " + textureFile);
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage loadBufferedImage(InputStream in) throws IOException {
        BufferedImage img = ImageIO.read(in);
        in.close();
        return img;
    }

    public static void copySubImg(int[] fromTex, int fromWidth, int fromX, int fromY, int width, int height, int[] toTex, int toWidth, int toX, int toY) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int fp = (y + fromY) * fromWidth + x + fromX;
                int tp = (y + toX) * toWidth + x + toX;

                toTex[tp] = fromTex[fp];
            }
        }
    }

    public static void prepareTexture(int target, int texture, int min_mag_filter, int wrap) {
        GlStateManager._texParameter(target, GL11.GL_TEXTURE_MIN_FILTER, min_mag_filter);
        GlStateManager._texParameter(target, GL11.GL_TEXTURE_MAG_FILTER, min_mag_filter);
        if (target == GL11.GL_TEXTURE_2D) {
            GlStateManager._bindTexture(target);
        } else {
            GL11.glBindTexture(target, texture);
        }

        switch (target) {
            case GL12.GL_TEXTURE_3D:
                GlStateManager._texParameter(target, GL12.GL_TEXTURE_WRAP_R, wrap);
            case GL11.GL_TEXTURE_2D:
                GlStateManager._texParameter(target, GL11.GL_TEXTURE_WRAP_T, wrap);
            case GL11.GL_TEXTURE_1D:
                GlStateManager._texParameter(target, GL11.GL_TEXTURE_WRAP_S, wrap);
        }
    }

    public static TextureManager getTextureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    public static TextureAtlas getTextureMap() {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
    }

    public static TextureAtlasSprite getMissingSprite() {
        return getTextureMap().getSprite(MissingTextureAtlasSprite.getLocation());
    }

    public static TextureAtlasSprite getTexture(String location) {
        return getTextureMap().getSprite(new ResourceLocation(location));
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {
        return getTextureMap().getSprite(location);
    }

    public static TextureAtlasSprite getBlockTexture(String string) {
        return getBlockTexture(new ResourceLocation(string));
    }

    public static TextureAtlasSprite getBlockTexture(ResourceLocation location) {
        return getTexture(new ResourceLocation(location.getNamespace(), "block/" + location.getPath()));
    }

    public static TextureAtlasSprite getItemTexture(String string) {
        return getItemTexture(new ResourceLocation(string));
    }

    public static TextureAtlasSprite getItemTexture(ResourceLocation location) {
        return getTexture(new ResourceLocation(location.getNamespace(), "items/" + location.getPath()));
    }

    @Deprecated
    public static TextureAtlasSprite[] getSideIconsForBlock(BlockState state) {
        TextureAtlasSprite[] sideSprites = new TextureAtlasSprite[6];
        TextureAtlasSprite missingSprite = getMissingSprite();
        for (int i = 0; i < 6; i++) {
            TextureAtlasSprite[] sprites = getIconsForBlock(state, i);
            TextureAtlasSprite sideSprite = missingSprite;
            if (sprites.length != 0) {
                sideSprite = sprites[0];
            }
            sideSprites[i] = sideSprite;
        }
        return sideSprites;
    }

    @Deprecated
    public static TextureAtlasSprite[] getIconsForBlock(BlockState state, int side) {
        return getIconsForBlock(state, Direction.values()[side]);
    }

    @Deprecated
    public static TextureAtlasSprite[] getIconsForBlock(BlockState state, Direction side) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (model != null) {
            List<BakedQuad> quads = model.getQuads(state, side, new Random(0));
            if (quads != null && quads.size() > 0) {
                TextureAtlasSprite[] sprites = new TextureAtlasSprite[quads.size()];
                for (int i = 0; i < quads.size(); i++) {
                    sprites[i] = quads.get(i).getSprite();
                }
                return sprites;
            }
        }
        return new TextureAtlasSprite[0];
    }

    @Deprecated
    public static TextureAtlasSprite getParticleIconForBlock(BlockState state) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (model != null) {
            return model.getParticleIcon();
        }
        return null;
    }

}
