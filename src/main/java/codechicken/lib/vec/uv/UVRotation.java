package codechicken.lib.vec.uv;

import codechicken.lib.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class UVRotation extends UVTransformation {

    public double angle;

    /**
     * @param angle The angle to rotate counterclockwise in radians
     */
    public UVRotation(double angle) {
        this.angle = angle;
    }

    public UVRotation(UVRotation other) {
        this(other.angle);
    }

    @Override
    public void apply(UV uv) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double u2 = c * uv.u + s * uv.v;
        uv.v = -s * uv.u + c * uv.v;
        uv.u = u2;
    }

    @Override
    public UVTransformation inverse() {
        return new UVRotation(-angle);
    }

    @Nullable
    @Override
    public UVTransformation merge(UVTransformation next) {
        if (next instanceof UVRotation) {
            return new UVRotation(angle + ((UVRotation) next).angle);
        }

        return null;
    }

    @Override
    public boolean isRedundant() {
        return MathHelper.between(-1E-5, angle, 1E-5);
    }

    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVRotation(" + new BigDecimal(angle, cont) + ")";
    }

    @Override
    public UVRotation copy() {
        return new UVRotation(this);
    }
}
