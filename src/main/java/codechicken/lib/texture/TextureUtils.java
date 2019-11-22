package codechicken.lib.texture;

import codechicken.lib.util.ResourceUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class TextureUtils {

    //    /**
    //     * @return an array of ARGB pixel data
    //     */
    //    public static int[] loadTextureData(ResourceLocation resource) {
    //        return loadTexture(resource).data;
    //    }

    //    public static Colour[] loadTextureColours(ResourceLocation resource) {
    //        int[] idata = loadTextureData(resource);
    //        Colour[] data = new Colour[idata.length];
    //        for (int i = 0; i < data.length; i++) {
    //            data[i] = new ColourARGB(idata[i]);
    //        }
    //        return data;
    //    }

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
    //
    //    public static void copySubImg(int[] fromTex, int fromWidth, int fromX, int fromY, int width, int height, int[] toTex, int toWidth, int toX, int toY) {
    //        for (int y = 0; y < height; y++) {
    //            for (int x = 0; x < width; x++) {
    //                int fp = (y + fromY) * fromWidth + x + fromX;
    //                int tp = (y + toX) * toWidth + x + toX;
    //
    //                toTex[tp] = fromTex[fp];
    //            }
    //        }
    //    }

    //    public static TextureAtlasSprite getBlankIcon(int size, AtlasTexture textureMap) {
    //        String s = "blank_" + size;
    //        TextureAtlasSprite icon = textureMap.getSprite(s);
    //        if (icon == null) {
    //            icon = new TextureSpecial(s).blank(size);
    //            setTextureEntry(textureMap, icon);
    //        }
    //
    //        return icon;
    //    }

    //    public static TextureSpecial getTextureSpecial(AtlasTexture textureMap, String name) {
    //        if (textureMap.getTextureExtry(name) != null) {
    //            throw new IllegalStateException("Texture: " + name + " is already registered");
    //        }
    //
    //        TextureSpecial icon = new TextureSpecial(name);
    //        setTextureEntry(textureMap, icon);
    //        return icon;
    //    }

    public static void prepareTexture(int target, int texture, int min_mag_filter, int wrap) {
        GlStateManager.texParameter(target, GL11.GL_TEXTURE_MIN_FILTER, min_mag_filter);
        GlStateManager.texParameter(target, GL11.GL_TEXTURE_MAG_FILTER, min_mag_filter);
        if (target == GL11.GL_TEXTURE_2D) {
            GlStateManager.bindTexture(target);
        } else {
            GL11.glBindTexture(target, texture);
        }

        switch (target) {
            case GL12.GL_TEXTURE_3D:
                GlStateManager.texParameter(target, GL12.GL_TEXTURE_WRAP_R, wrap);
            case GL11.GL_TEXTURE_2D:
                GlStateManager.texParameter(target, GL11.GL_TEXTURE_WRAP_T, wrap);
            case GL11.GL_TEXTURE_1D:
                GlStateManager.texParameter(target, GL11.GL_TEXTURE_WRAP_S, wrap);
        }
    }

    //    public static TextureDataHolder loadTexture(ResourceLocation resource) {
    //        BufferedImage img = loadBufferedImage(resource);
    //        if (img == null) {
    //            throw new RuntimeException("Texture not found: " + resource);
    //        }
    //        return new TextureDataHolder(img);
    //    }

    //    /**
    //     * Uses an empty placeholder texture to tell if the map has been reloaded since the last call to refresh texture and the texture with name needs to be reacquired to be valid
    //     */
    //    public static boolean refreshTexture(AtlasTexture atlas, String name) {
    //        if (atlas.getAtlasSprite(name) == null) {
    //            setTextureEntry(atlas, new PlaceholderTexture(name));
    //            return true;
    //        }
    //        return false;
    //    }
    //
    //    public static boolean setTextureEntry(AtlasTexture atlas, TextureAtlasSprite sprite) {
    //        ResourceLocation name = sprite.getName();
    //        if (!atlas.mapUploadedSprites.containsKey(name)) {
    //            atlas.mapUploadedSprites.put(name, sprite);
    //            return true;
    //        }
    //        return false;
    //    }

    public static TextureManager getTextureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    public static AtlasTexture getTextureMap() {
        return Minecraft.getInstance().getTextureMap();
    }

    public static TextureAtlasSprite getMissingSprite() {
        return getTextureMap().missingImage;
    }

    public static TextureAtlasSprite getTexture(String location) {
        return getTextureMap().getAtlasSprite(location);
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {
        return getTexture(location.toString());
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

    public static void changeTexture(String texture) {
        changeTexture(new ResourceLocation(texture));
    }

    public static void changeTexture(ResourceLocation texture) {
        getTextureManager().bindTexture(texture);
    }

    public static void disableMipmap(String texture) {
        disableMipmap(new ResourceLocation(texture));
    }

    public static void disableMipmap(ResourceLocation texture) {
        getTextureManager().getTexture(texture).setBlurMipmap(false, false);
    }

    public static void restoreLastMipmap(String texture) {
        restoreLastMipmap(new ResourceLocation(texture));
    }

    public static void restoreLastMipmap(ResourceLocation location) {
        getTextureManager().getTexture(location).restoreLastBlurMipmap();
    }

    public static void bindBlockTexture() {
        changeTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }

    public static void dissableBlockMipmap() {
        disableMipmap(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }

    public static void restoreBlockMipmap() {
        restoreLastMipmap(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
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
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
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
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        if (model != null) {
            return model.getParticleTexture();
        }
        return null;
    }

}
