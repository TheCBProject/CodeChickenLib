package codechicken.lib.vec.uv;

import codechicken.lib.vec.IrreversibleTransformationException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ScaledIconTransformation extends IconTransformation {

    public double su = 0.0F;
    public double sv = 0.0F;

    public ScaledIconTransformation(TextureAtlasSprite icon) {
        super(icon);
    }

    public ScaledIconTransformation(TextureAtlasSprite icon, double su, double sv) {
        super(icon);

        this.su = su;
        this.sv = sv;
    }

    public ScaledIconTransformation(ScaledIconTransformation other) {
        this(other.icon, other.su, other.sv);
    }

    @Override
    public void apply(UV uv) {
        uv.u = icon.getU((float) (uv.u % 2)) + su * (icon.getU1() - icon.getU0());
        uv.v = icon.getV((float) (uv.v % 2)) + sv * (icon.getV1() - icon.getV0());
    }

    @Override
    public UVTransformation inverse() {
        throw new IrreversibleTransformationException(this);
    }

    @Override
    public ScaledIconTransformation copy() {
        return new ScaledIconTransformation(this);
    }
}
