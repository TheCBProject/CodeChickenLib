package codechicken.lib.vec;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedundantTransformation extends Transformation {

    @Override
    public void apply(Vector3 vec) {
    }

    @Override
    public void apply(Matrix4 mat) {
    }

    @Override
    public void applyN(Vector3 normal) {
    }

    @Override
    public Transformation at(Vector3 point) {
        return this;
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void glApply() {
    }

    @Override
    public Transformation inverse() {
        return this;
    }

    @Override
    public Transformation merge(Transformation next) {
        return next;
    }

    @Override
    public boolean isRedundant() {
        return true;
    }

    @Override
    public String toString() {
        return "Nothing()";
    }
}
