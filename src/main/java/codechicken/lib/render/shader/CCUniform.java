package codechicken.lib.render.shader;

import codechicken.lib.render.shader.UniformType.Carrier;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Created by covers1624 on 8/4/22.
 */
public abstract class CCUniform extends Uniform implements ICCUniform {

    protected final UniformType type;

    @SuppressWarnings ("ConstantConditions")
    protected CCUniform(String name, UniformType type, int count, @Nullable Shader parent) {
        super(name, type.getVanillaType(), count, parent);
        this.type = type;
        if (intValues != null) {
            MemoryUtil.memFree(intValues);
            intValues = null;
        }
        if (floatValues != null) {
            MemoryUtil.memFree(floatValues);
            floatValues = null;
        }
    }

    static CCUniform makeUniform(String name, UniformType type, int count, @Nullable Shader parent) {
        if (count % type.getSize() != 0) throw new IllegalArgumentException("Expected count to be a multiple of the uniform type size: " + type.getSize());
        return switch (type.getCarrier()) {
            case INT, U_INT -> new IntUniform(name, type, count, parent);
            case FLOAT, MATRIX -> new FloatUniform(name, type, count, parent);
            case DOUBLE, D_MATRIX -> new DoubleUniform(name, type, count, parent);
        };
    }

    @Override
    public IntBuffer getIntBuffer() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public FloatBuffer getFloatBuffer() {
        throw new NotImplementedException("TODO");
    }

    private static abstract class UniformEntry<T> extends CCUniform {

        @Nullable
        protected T cache;
        protected boolean transpose;

        public UniformEntry(String name, UniformType type, int count, @Nullable Shader parent) {
            super(name, type, count, parent);
        }

        // @formatter:off
        @Override public void set(float f0) { glUniformF(false, f0); }
        @Override public void set(float f0, float f1) { glUniformF(false, f0, f1); }
        @Override public void set(int i, float f) { throw new UnsupportedOperationException("Unable to set specific index."); }
        @Override public void set(float f0, float f1, float f2) { glUniformF(false, f0, f1, f2); }
        @Override public void set(Vector3f vec) { glUniformF(false, vec.x(), vec.y(), vec.z()); }
        @Override public void set(float f0, float f1, float f2, float f3) { glUniformF(false, f0, f1, f2, f3); }
        @Override public void set(Vector4f vec) { glUniformF(false, vec.x(), vec.y(), vec.z(), vec.w()); }
        @Override public void set(int i0) { glUniformI(i0); }
        @Override public void set(int i0, int i1) { glUniformI(i0, i1); }
        @Override public void set(int i0, int i1, int i2) { glUniformI(i0, i1, i2); }
        @Override public void set(int i0, int i1, int i2, int i3) { glUniformI(i0, i1, i2, i3); }
        @Override public void set(float[] p_85632_) { glUniformF(false, p_85632_); }
        @Override public void setMat2x2(float m00, float m01, float m10, float m11) { glUniformF(true, m00, m01, m10, m11); }
        @Override public void setMat2x3(float m00, float m01, float m02, float m10, float m11, float m12) { glUniformF(true, m00, m01, m02, m10, m11, m12); }
        @Override public void setMat2x4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13) { glUniformF(true, m00, m01, m02, m03, m10, m11, m12, m13); }
        @Override public void setMat3x2(float m00, float m01, float m10, float m11, float m20, float m21) { glUniformF(true, m00, m01, m10, m11, m20, m21); }
        @Override public void setMat3x3(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) { glUniformF(true, m00, m01, m02, m10, m11, m12, m20, m21, m22); }
        @Override public void setMat3x4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23) { glUniformF(true, m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23); }
        @Override public void setMat4x2(float m00, float m01, float m10, float m11, float m20, float m21, float m30, float m31) { glUniformF(true, m00, m01, m10, m11, m20, m21, m30, m31); }
        @Override public void setMat4x3(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m23, float m30, float m31, float m32) { glUniformF(true, m00, m01, m02, m10, m11, m12, m20, m21, m23, m30, m31, m32); }
        @Override public void setMat4x4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) { glUniformF(true, m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33); }
        @Override public void set(Matrix4f mat) { glUniformMatrix4f(mat); }
        @Override public void set(Matrix3f mat) { glUniformMatrix3f(mat); }
        // @formatter:on

        @Override
        public void setSafe(float f0, float f1, float f2, float f3) {
            assert type.getCarrier() == Carrier.FLOAT;
            switch (type.getSize()) {
                case 1 -> glUniform1f(f0);
                case 2 -> glUniform2f(f0, f1);
                case 3 -> glUniform3f(f0, f1, f2);
                case 4 -> glUniform4f(f0, f1, f2, f3);
            }
            throw new IllegalStateException("Unexpected type size: " + type);
        }

        @Override
        public void setSafe(int i0, int i1, int i2, int i3) {
            assert type.getCarrier() == Carrier.INT || type.getCarrier() == Carrier.U_INT;
            switch (type.getSize()) {
                case 1 -> glUniform1i(i0);
                case 2 -> glUniform2i(i0, i1);
                case 3 -> glUniform3i(i0, i1, i2);
                case 4 -> glUniform4i(i0, i1, i2, i3);
            }
            throw new IllegalStateException("Unexpected type size: " + type);
        }

        @Override
        public void glUniformI(int... values) {
            if (type.getCarrier() != Carrier.INT && type.getCarrier() != Carrier.U_INT) {
                throw new IllegalArgumentException("Uniform '%s' isn't registered with the carrier of INT or U_INT, Got type '%s' with carrier '%s'.".formatted(getName(), type, type.getCarrier()));
            }
            set((T) values, false);
        }

        @Override
        public void glUniformF(boolean transpose, float... values) {
            if (type.getCarrier() != Carrier.FLOAT && type.getCarrier() != Carrier.MATRIX) {
                throw new IllegalArgumentException("Uniform '%s' isn't registered with the carrier of FLOAT or MATRIX, Got type '%s' with carrier '%s'.".formatted(getName(), type, type.getCarrier()));
            }
            set((T) values, transpose);
        }

        @Override
        public void glUniformD(boolean transpose, double... values) {
            if (type.getCarrier() != Carrier.DOUBLE && type.getCarrier() != Carrier.D_MATRIX) {
                throw new IllegalArgumentException("Uniform '%s' isn't registered with the carrier of DOUBLE or D_MATRIX, Got type '%s' with carrier '%s'.".formatted(getName(), type, type.getCarrier()));
            }
            set((T) values, transpose);
        }

        public void set(T values, boolean transpose) {
            assert !transpose || type.getCarrier() == Carrier.MATRIX || type.getCarrier() == Carrier.D_MATRIX;

            if (len(values) != getCount()) {
                throw new IllegalArgumentException("Invalid size for uniform '%s', Expected: '%s', Got: '%s'.".formatted(getName(), getCount(), len(values)));
            }

            if (!equals(cache, values) || this.transpose != transpose) {
                cache = values;
                this.transpose = transpose;
                dirty = true;
            }
        }

        @Override
        public void upload() {
            if (!dirty) return;

            flush();
            dirty = false;
        }

        public abstract void flush();

        public abstract T make(int len);

        public abstract int len(T arr);

        public abstract boolean equals(@Nullable T a, T b);
    }

    private static class IntUniform extends UniformEntry<int[]> {

        public IntUniform(String name, UniformType type, int count, @Nullable Shader parent) {
            super(name, type, count, parent);
            assert type.getCarrier() == Carrier.INT || type.getCarrier() == Carrier.U_INT;
        }

        @Override
        public void flush() {
            assert cache != null;

            switch (type) {
                case INT -> GL20.glUniform1iv(getLocation(), cache);
                case U_INT -> GL30.glUniform1uiv(getLocation(), cache);
                case I_VEC2, B_VEC2 -> GL20.glUniform2iv(getLocation(), cache);
                case U_VEC2 -> GL30.glUniform2uiv(getLocation(), cache);
                case I_VEC3, B_VEC3 -> GL20.glUniform3iv(getLocation(), cache);
                case U_VEC3 -> GL30.glUniform3uiv(getLocation(), cache);
                case I_VEC4, B_VEC4 -> GL20.glUniform4iv(getLocation(), cache);
                case U_VEC4 -> GL30.glUniform4uiv(getLocation(), cache);
                default -> throw new IllegalStateException("Unhandled uniform type for IntUniform: " + type);
            }
        }

        //@formatter:off
        @Override public int[] make(int len) { return new int[len]; }
        @Override public int len(int[] cache) { return cache.length; }
        @Override public boolean equals(int @Nullable [] a, int[] b) { return Arrays.equals(a, b); }
        //@formatter:on
    }

    private static class FloatUniform extends UniformEntry<float[]> {

        public FloatUniform(String name, UniformType type, int count, @Nullable Shader parent) {
            super(name, type, count, parent);
            assert type.getCarrier() == Carrier.FLOAT || type.getCarrier() == Carrier.MATRIX;
        }

        @Override
        public void flush() {
            assert cache != null;

            switch (type) {
                case FLOAT -> GL20.glUniform1fv(getLocation(), cache);
                case VEC2 -> GL20.glUniform2fv(getLocation(), cache);
                case VEC3 -> GL20.glUniform3fv(getLocation(), cache);
                case VEC4 -> GL20.glUniform4fv(getLocation(), cache);

                case MAT2 -> GL20.glUniformMatrix2fv  (getLocation(), transpose, cache);
                case MAT2x3 -> GL21.glUniformMatrix2x3fv(getLocation(), transpose, cache);
                case MAT2x4 -> GL21.glUniformMatrix2x4fv(getLocation(), transpose, cache);

                case MAT3 -> GL20.glUniformMatrix3fv  (getLocation(), transpose, cache);
                case MAT3x2 -> GL21.glUniformMatrix3x2fv(getLocation(), transpose, cache);
                case MAT3x4 -> GL21.glUniformMatrix3x4fv(getLocation(), transpose, cache);

                case MAT4 -> GL20.glUniformMatrix4fv  (getLocation(), transpose, cache);
                case MAT4x2 -> GL21.glUniformMatrix4x2fv(getLocation(), transpose, cache);
                case MAT4x3 -> GL21.glUniformMatrix4x3fv(getLocation(), transpose, cache);

                default -> throw new IllegalStateException("Unhandled uniform type for FloatUniform: " + type);
            }
        }

        //@formatter:off
        @Override public float[] make(int len) { return new float[len]; }
        @Override public int len(float[] cache) { return cache.length; }
        @Override public boolean equals(float @Nullable [] a, float[] b) { return Arrays.equals(a, b); }
        //@formatter:on
    }

    private static class DoubleUniform extends UniformEntry<double[]> {

        public DoubleUniform(String name, UniformType type, int count, @Nullable Shader parent) {
            super(name, type, count, parent);
            assert type.getCarrier() == Carrier.DOUBLE || type.getCarrier() == Carrier.D_MATRIX;
        }

        @Override
        public void flush() {
            assert cache != null;

            switch (type) {
                case DOUBLE -> GL40.glUniform1dv(getLocation(), cache);
                case D_VEC2 -> GL40.glUniform2dv(getLocation(), cache);
                case D_VEC3 -> GL40.glUniform3dv(getLocation(), cache);
                case D_VEC4 -> GL40.glUniform4dv(getLocation(), cache);

                case D_MAT2 -> GL40.glUniformMatrix2dv  (getLocation(), transpose, cache);
                case D_MAT2x3 -> GL40.glUniformMatrix2x3dv(getLocation(), transpose, cache);
                case D_MAT2x4 -> GL40.glUniformMatrix2x4dv(getLocation(), transpose, cache);

                case D_MAT3 -> GL40.glUniformMatrix3dv  (getLocation(), transpose, cache);
                case D_MAT3x2 -> GL40.glUniformMatrix3x2dv(getLocation(), transpose, cache);
                case D_MAT3x4 -> GL40.glUniformMatrix3x4dv(getLocation(), transpose, cache);

                case D_MAT4 -> GL40.glUniformMatrix4dv  (getLocation(), transpose, cache);
                case D_MAT4x2 -> GL40.glUniformMatrix4x2dv(getLocation(), transpose, cache);
                case D_MAT4x3 -> GL40.glUniformMatrix4x3dv(getLocation(), transpose, cache);

                default -> throw new IllegalStateException("Unhandled uniform type for DoubleUniform: " + type);
            }
        }

        //@formatter:off
        @Override public double[] make(int len) { return new double[len]; }
        @Override public int len(double[] cache) { return cache.length; }
        @Override public boolean equals(double @Nullable [] a, double[] b) { return Arrays.equals(a, b); }
        //@formatter:on
    }
}
