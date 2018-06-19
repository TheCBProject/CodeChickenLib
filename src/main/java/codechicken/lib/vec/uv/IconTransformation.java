package codechicken.lib.vec.uv;

import codechicken.lib.render.CCRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconTransformation extends UVTransformation {

    public TextureAtlasSprite icon;

    public IconTransformation(TextureAtlasSprite icon) {
        this.icon = icon;
    }

    @Override
    public void operate(CCRenderState state) {
        super.operate(state);
        state.sprite = icon;
    }

    @Override
    public void apply(UV uv) {
        uv.u = icon.getInterpolatedU(uv.u * 16);
        uv.v = icon.getInterpolatedV(uv.v * 16);
    }

    @Override
    public UVTransformation inverse() {
        return new UVTransformation() {
            @Override
            public void apply(UV uv) {
                uv.u = icon.getUnInterpolatedU((float) uv.u) / 16;
                uv.v = icon.getUnInterpolatedV((float) uv.v) / 16;
            }

            @Override
            public UVTransformation inverse() {
                return new IconTransformation(icon);
            }
        };
    }
}
