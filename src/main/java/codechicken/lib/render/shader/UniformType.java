package codechicken.lib.render.shader;

import com.mojang.blaze3d.shaders.Uniform;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Created by covers1624 on 24/5/20.
 */
public enum UniformType {
    //@formatter:off
    INT     (Carrier.INT,      1),
    U_INT   (Carrier.U_INT,    1),
    FLOAT   (Carrier.FLOAT,    1),

    VEC2    (Carrier.FLOAT,    2),
    I_VEC2  (Carrier.INT,      2),
    U_VEC2  (Carrier.U_INT,    2),
    B_VEC2  (Carrier.INT,      2),

    VEC3    (Carrier.FLOAT,    3),
    I_VEC3  (Carrier.INT,      3),
    U_VEC3  (Carrier.U_INT,    3),
    B_VEC3  (Carrier.INT,      3),

    VEC4    (Carrier.FLOAT,    4),
    I_VEC4  (Carrier.INT,      4),
    U_VEC4  (Carrier.U_INT,    4),
    B_VEC4  (Carrier.INT,      4),

    MAT2    (Carrier.MATRIX,   2 * 2),
    MAT2x3  (Carrier.MATRIX,   2 * 3),
    MAT2x4  (Carrier.MATRIX,   2 * 4),

    MAT3    (Carrier.MATRIX,   3 * 3),
    MAT3x2  (Carrier.MATRIX,   3 * 2),
    MAT3x4  (Carrier.MATRIX,   3 * 4),

    MAT4    (Carrier.MATRIX,   4 * 4),
    MAT4x2  (Carrier.MATRIX,   4 * 2),
    MAT4x3  (Carrier.MATRIX,   4 * 3),

    DOUBLE  (Carrier.DOUBLE,   1),
    D_VEC2  (Carrier.DOUBLE,   2),
    D_VEC3  (Carrier.DOUBLE,   3),
    D_VEC4  (Carrier.DOUBLE,   4),

    D_MAT2  (Carrier.D_MATRIX, 2 * 2),
    D_MAT2x3(Carrier.D_MATRIX, 2 * 3),
    D_MAT2x4(Carrier.D_MATRIX, 2 * 4),

    D_MAT3  (Carrier.D_MATRIX, 3 * 3),
    D_MAT3x2(Carrier.D_MATRIX, 3 * 2),
    D_MAT3x4(Carrier.D_MATRIX, 3 * 4),

    D_MAT4  (Carrier.D_MATRIX, 4 * 4),
    D_MAT4x2(Carrier.D_MATRIX, 4 * 2),
    D_MAT4x3(Carrier.D_MATRIX, 4 * 3);
    //@formatter:on

    public static final UniformType[] VALUES = values();

    private final Carrier carrier;
    private final int size;

    UniformType(Carrier carrier, int size) {
        this.carrier = carrier;
        this.size = size;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public int getSize() {
        return size;
    }

    public int getVanillaType() {
        return switch (this) {
            case INT, U_INT -> Uniform.UT_INT1;
            case FLOAT -> Uniform.UT_FLOAT1;
            case VEC2 -> Uniform.UT_FLOAT2;
            case I_VEC2, U_VEC2, B_VEC2 -> Uniform.UT_INT2;
            case VEC3 -> Uniform.UT_FLOAT3;
            case I_VEC3, U_VEC3, B_VEC3 -> Uniform.UT_INT3;
            case VEC4 -> Uniform.UT_FLOAT4;
            case I_VEC4, U_VEC4, B_VEC4 -> Uniform.UT_INT4;
            case MAT2 -> Uniform.UT_MAT2;
            case MAT3 -> Uniform.UT_MAT3;
            case MAT4 -> Uniform.UT_MAT4;
            default -> -1;
        };
    }

    @Nullable
    public static UniformType parse(String s) {
        s = s.toLowerCase(Locale.ROOT);

        // Handle vanilla Matrix names
        switch (s) {
            case "matrix2x2" -> { return MAT2; }
            case "matrix3x3" -> { return MAT3; }
            case "matrix4x4" -> { return MAT4; }
        }
        for (UniformType value : VALUES) {
            String n = value.name().toLowerCase(Locale.ROOT);
            if (n.equals(s)) {
                return value;
            }
        }
        return null;
    }

    public enum Carrier {
        INT,
        U_INT,
        FLOAT,
        DOUBLE,
        MATRIX,
        D_MATRIX
    }
}
