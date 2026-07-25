package codechicken.lib.vec.uv;

import codechicken.lib.render.CCRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconTransformation extends UVTransformation {

    public TextureAtlasSprite icon;

    public IconTransformation(TextureAtlasSprite icon) {
        this.icon = icon;
    }

    public IconTransformation(IconTransformation other) {
        this(other.icon);
    }

    @Override
    public void operate(CCRenderState ccrs) {
        super.operate(ccrs);
        ccrs.sprite = icon;
    }

    @Override
    public void apply(UV uv) {
        uv.u = icon.getU((float) uv.u);
        uv.v = icon.getV((float) uv.v);
    }

    @Override
    public UVTransformation inverse() {
        return new Inverse(icon);
    }

    @Override
    public IconTransformation copy() {
        return new IconTransformation(this);
    }

    private static class Inverse extends IconTransformation {

        public Inverse(TextureAtlasSprite icon) {
            super(icon);
        }

        public Inverse(Inverse other) {
            super(other);
        }

        @Override
        public void apply(UV uv) {
            uv.u = getUOffset(icon, (float) uv.u);
            uv.v = getVOffset(icon, (float) uv.v);
        }

        @Override
        public UVTransformation inverse() {
            return new IconTransformation(icon);
        }

        @Override
        public IconTransformation copy() {
            return new Inverse(this);
        }

        public static float getUOffset(TextureAtlasSprite sprite, float u) {
            return (u - sprite.getU0()) / (sprite.getU1() - sprite.getU0());
        }

        public static float getVOffset(TextureAtlasSprite sprite, float v) {
            return (v - sprite.getV0()) / (sprite.getV1() - sprite.getV0());
        }
    }
}
