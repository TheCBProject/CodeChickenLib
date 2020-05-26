package codechicken.lib.render.shader;

import codechicken.lib.render.OpenGLUtils;

import java.util.function.BooleanSupplier;

/**
 * Created by covers1624 on 24/5/20.
 */
public enum UniformType {
    //@formatter:off
    INT     (Carrier.INT,      () -> OpenGLUtils.openGL20, 1),
    U_INT   (Carrier.U_INT,    () -> OpenGLUtils.openGL20, 1),
    FLOAT   (Carrier.FLOAT,    () -> OpenGLUtils.openGL20, 1),

    VEC2    (Carrier.FLOAT,    () -> OpenGLUtils.openGL20, 2),
    I_VEC2  (Carrier.INT,      () -> OpenGLUtils.openGL20, 2),
    U_VEC2  (Carrier.U_INT,    () -> OpenGLUtils.openGL20, 2),
    B_VEC2  (Carrier.INT,      () -> OpenGLUtils.openGL20, 2),

    VEC3    (Carrier.FLOAT,    () -> OpenGLUtils.openGL20, 3),
    I_VEC3  (Carrier.INT,      () -> OpenGLUtils.openGL20, 3),
    U_VEC3  (Carrier.U_INT,    () -> OpenGLUtils.openGL20, 3),
    B_VEC3  (Carrier.INT,      () -> OpenGLUtils.openGL20, 3),

    VEC4    (Carrier.FLOAT,    () -> OpenGLUtils.openGL20, 4),
    I_VEC4  (Carrier.INT,      () -> OpenGLUtils.openGL20, 4),
    U_VEC4  (Carrier.U_INT,    () -> OpenGLUtils.openGL20, 4),
    B_VEC4  (Carrier.INT,      () -> OpenGLUtils.openGL20, 4),

    MAT2    (Carrier.MATRIX,   () -> OpenGLUtils.openGL20, 2 * 2),
    MAT2x3  (Carrier.MATRIX,   () -> OpenGLUtils.openGL21, 2 * 3),
    MAT2x4  (Carrier.MATRIX,   () -> OpenGLUtils.openGL21, 2 * 4),

    MAT3    (Carrier.MATRIX,   () -> OpenGLUtils.openGL20, 3 * 3),
    MAT3x2  (Carrier.MATRIX,   () -> OpenGLUtils.openGL21, 3 * 2),
    MAT3x4  (Carrier.MATRIX,   () -> OpenGLUtils.openGL21, 3 * 4),

    MAT4    (Carrier.MATRIX,   () -> OpenGLUtils.openGL20, 4 * 4),
    MAT4x2  (Carrier.MATRIX,   () -> OpenGLUtils.openGL21, 4 * 2),
    MAT4x3  (Carrier.MATRIX,   () -> OpenGLUtils.openGL21, 4 * 3),

    DOUBLE  (Carrier.DOUBLE,   () -> OpenGLUtils.openGL40, 1),
    D_VEC2  (Carrier.DOUBLE,   () -> OpenGLUtils.openGL40, 2),
    D_VEC3  (Carrier.DOUBLE,   () -> OpenGLUtils.openGL40, 3),
    D_VEC4  (Carrier.DOUBLE,   () -> OpenGLUtils.openGL40, 4),

    D_MAT2  (Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 2 * 2),
    D_MAT2x3(Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 2 * 3),
    D_MAT2x4(Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 2 * 4),

    D_MAT3  (Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 3 * 3),
    D_MAT3x2(Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 3 * 2),
    D_MAT3x4(Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 3 * 4),

    D_MAT4  (Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 4 * 4),
    D_MAT4x2(Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 4 * 2),
    D_MAT4x3(Carrier.D_MATRIX, () -> OpenGLUtils.openGL40, 4 * 3);
    //@formatter:on

    private final Carrier carrier;
    private BooleanSupplier func;
    private final int size;

    private boolean isSupported;

    UniformType(Carrier carrier, BooleanSupplier func, int size) {
        this.carrier = carrier;
        this.func = func;
        this.size = size;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public int getSize() {
        return size;
    }

    public boolean isSupported() {
        if (func != null) {
            isSupported = func.getAsBoolean();
            func = null;
        }
        return isSupported;
    }

    public enum Carrier {
        INT,
        U_INT,
        FLOAT,
        DOUBLE,
        MATRIX,
        D_MATRIX;
    }
}
