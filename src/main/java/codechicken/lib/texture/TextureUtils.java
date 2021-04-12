package codechicken.lib.texture;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.internal.hook.ICCAtlasSprite;
import codechicken.lib.texture.IProceduralTextureCallback.ITextureAccessor;
import codechicken.lib.util.ResourceUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class TextureUtils {

    /**
     * Creates a texture accessor for the given texture. Result should be cached.
     *
     * @param texture The texture to access.
     * @return A new {@link ITextureAccessor} instance.
     */
    @Nonnull
    public static ITextureAccessor createAccessor(@Nonnull TextureAtlasSprite texture) {
        return new ITextureAccessor() {
            @Override
            public void setPixel(int x, int y, @Nonnull Colour colour) {
                final int abgr = (colour.a << 24) | (colour.b << 16) | (colour.g << 8) | colour.r; // Someone dun fucked up..
                texture.mainImage[0].setPixelRGBA(x, y, abgr);
            }

            @Nonnull
            @Override
            public Colour getPixel(int x, int y) {
                final int abgr = texture.mainImage[0].getPixelRGBA(x, y); // Seriously..
                final byte r = (byte)(abgr & 0xFF);
                final byte g = (byte)((abgr >> 8) & 0xFF);
                final byte b = (byte)((abgr >> 16) & 0xFF);
                final byte a = (byte)((abgr >> 24) & 0xFF);
                return new ColourRGBA(r, g, b, a);
            }
        };
    }

    /**
     * Sets the procedural callback of the given texture.
     * If either parameter is null, this function just returns.
     *
     * @param texture The texture to set the callback for.
     * @param callback The callback.
     */
    public static void setProceduralCallback(@Nullable TextureAtlasSprite texture, @Nullable IProceduralTextureCallback callback) {
        if(texture == null || callback == null) {
            return;
        }

        ((ICCAtlasSprite)texture).setProceduralCallback(callback);
    }

    /**
     * Retrieves the procedural callback of the given texture.
     * If the texture is null, the resulting callback is also null.
     *
     * @param texture The texture to get the callback from.
     * @return The callback of the given texture or null.
     */
    @Nullable
    public static IProceduralTextureCallback getProceduralCallback(@Nullable TextureAtlasSprite texture) {
        if(texture == null) {
            return null;
        }

        return ((ICCAtlasSprite)texture).getProceduralCallback();
    }

    /**
     * @return an array of ARGB pixel data
     */
    public static int[] loadTextureData(ResourceLocation resource) {
        BufferedImage img = loadBufferedImage(resource);
        if(img == null) {
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
        for(int i = 0; i < data.length; i++) {
            data[i] = new ColourARGB(idata[i]);
        }
        return data;
    }

    public static BufferedImage loadBufferedImage(ResourceLocation textureFile) {
        try {
            return loadBufferedImage(ResourceUtils.getResourceAsStream(textureFile));
        }
        catch(Exception e) {
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
        GlStateManager._texParameter(target, GL11.GL_TEXTURE_MIN_FILTER, min_mag_filter);
        GlStateManager._texParameter(target, GL11.GL_TEXTURE_MAG_FILTER, min_mag_filter);
        if(target == GL11.GL_TEXTURE_2D) {
            GlStateManager._bindTexture(target);
        }
        else {
            GL11.glBindTexture(target, texture);
        }

        switch(target) {
            case GL12.GL_TEXTURE_3D:
                GlStateManager._texParameter(target, GL12.GL_TEXTURE_WRAP_R, wrap);
            case GL11.GL_TEXTURE_2D:
                GlStateManager._texParameter(target, GL11.GL_TEXTURE_WRAP_T, wrap);
            case GL11.GL_TEXTURE_1D:
                GlStateManager._texParameter(target, GL11.GL_TEXTURE_WRAP_S, wrap);
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
        return Minecraft.getInstance().getModelManager().getAtlas(PlayerContainer.BLOCK_ATLAS);
    }

    public static TextureAtlasSprite getMissingSprite() {
        return getTextureMap().getSprite(MissingTextureSprite.getLocation());
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

    public static void changeTexture(String texture) {
        changeTexture(new ResourceLocation(texture));
    }

    public static void changeTexture(ResourceLocation texture) {
        getTextureManager().bind(texture);
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
        changeTexture(AtlasTexture.LOCATION_BLOCKS);
    }

    public static void dissableBlockMipmap() {
        disableMipmap(AtlasTexture.LOCATION_BLOCKS);
    }

    public static void restoreBlockMipmap() {
        restoreLastMipmap(AtlasTexture.LOCATION_BLOCKS);
    }

    @Deprecated
    public static TextureAtlasSprite[] getSideIconsForBlock(BlockState state) {
        TextureAtlasSprite[] sideSprites = new TextureAtlasSprite[6];
        TextureAtlasSprite missingSprite = getMissingSprite();
        for(int i = 0; i < 6; i++) {
            TextureAtlasSprite[] sprites = getIconsForBlock(state, i);
            TextureAtlasSprite sideSprite = missingSprite;
            if(sprites.length != 0) {
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
        IBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if(model != null) {
            List<BakedQuad> quads = model.getQuads(state, side, new Random(0));
            if(quads != null && quads.size() > 0) {
                TextureAtlasSprite[] sprites = new TextureAtlasSprite[quads.size()];
                for(int i = 0; i < quads.size(); i++) {
                    sprites[i] = quads.get(i).getSprite();
                }
                return sprites;
            }
        }
        return new TextureAtlasSprite[0];
    }

    @Deprecated
    public static TextureAtlasSprite getParticleIconForBlock(BlockState state) {
        IBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if(model != null) {
            return model.getParticleIcon();
        }
        return null;
    }

}
