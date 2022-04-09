package codechicken.lib.vec.uv;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class UVScale extends UVTransformation {

    double su;
    double sv;

    public UVScale(double scaleu, double scalev) {
        su = scaleu;
        sv = scalev;
    }

    public UVScale(double d) {
        this(d, d);
    }

    public UVScale(UVScale other) {
        this(other.su, other.sv);
    }

    @Override
    public void apply(UV uv) {
        uv.u *= su;
        uv.v *= sv;
    }

    @Override
    public UVTransformation inverse() {
        return new UVScale(1 / su, 1 / sv);
    }

    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVScale(" + new BigDecimal(su, cont) + ", " + new BigDecimal(sv, cont) + ")";
    }

    @Override
    public UVScale copy() {
        return new UVScale(this);
    }
}
