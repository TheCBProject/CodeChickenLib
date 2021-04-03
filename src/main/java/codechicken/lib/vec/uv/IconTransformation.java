package codechicken.lib.vec.uv;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.IrreversibleTransformationException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconTransformation extends UVTransformation {

    public TextureAtlasSprite icon;

    public IconTransformation(TextureAtlasSprite icon) {
        this.icon = icon;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        super.operate(ccrs);
        ccrs.sprite = icon;
    }

    @Override
    public void apply(UV uv) {
        uv.u = icon.getU(uv.u * 16);
        uv.v = icon.getV(uv.v * 16);
    }

    @Override
    public UVTransformation inverse() {
        throw new IrreversibleTransformationException(this);
//        return new UVTransformation() {
//            @Override
//            public void apply(UV uv) {
//                uv.u = icon.getUnInterpolatedU((float) uv.u) / 16;
//                uv.v = icon.getUnInterpolatedV((float) uv.v) / 16;
//            }
//
//            @Override
//            public UVTransformation inverse() {
//                return new IconTransformation(icon);
//            }
//        };
    }
}
