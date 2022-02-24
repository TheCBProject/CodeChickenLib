package codechicken.lib.render.shader;

import codechicken.lib.util.Copyable;
import codechicken.lib.vec.Matrix4;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import java.util.*;

import static codechicken.lib.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 24/5/20.
 */
public class ShaderUniformCache implements UniformCache {

    private static final Logger logger = LogManager.getLogger();

    private final ShaderUniformCache parent;
    private final ShaderProgram program;

    private final ArrayDeque<ShaderUniformCache> pool = new ArrayDeque<>();
    private final Set<ShaderUniformCache> allocated = new HashSet<>();

    private final Map<String, UniformEntry<?>> uniformEntries = new HashMap<>();
    private final Object2IntMap<String> locationCache = new Object2IntOpenHashMap<>();

    public ShaderUniformCache(ShaderProgram program) {
        this.parent = null;
        this.program = program;
        Map<String, String> uniformToShader = new HashMap<>();
        for (ShaderObject shader : program.getShaders()) {
            for (Uniform uniform : shader.getUniforms()) {
                String existingOwner = uniformToShader.get(uniform.getName());
                if (existingOwner != null) {
                    throw new IllegalArgumentException(String.format("ShaderObject '%s' tried to add a uniform with name '%s', already owned by ShaderObject '%s'", shader.getName(), uniform.getName(), existingOwner));
                }
                uniformToShader.put(uniform.getName(), shader.getName());
                uniformEntries.put(uniform.getName(), makeEntry(uniform));
            }
        }
    }

    public ShaderUniformCache(ShaderUniformCache parent, ShaderProgram program) {
        this.parent = parent;
        this.program = program;
        for (Map.Entry<String, UniformEntry<?>> entry : parent.uniformEntries.entrySet()) {
            uniformEntries.put(entry.getKey(), entry.getValue().copy());
        }
    }

    public void onLink() {
        locationCache.clear();
        for (UniformEntry<?> entry : uniformEntries.values()) {
            entry.reset();
            Uniform uniform = entry.uniform;
            if (!uniform.getType().isSupported()) {
                throw new IllegalStateException(String.format("Uniform '%s' is not supported in this environment.", uniform.getName()));
            }
        }
    }

    public ShaderUniformCache pushCache() {
        if (parent != null) {
            throw new UnsupportedOperationException("Nested caches isn't possible.");
        }
        ShaderUniformCache child = pool.poll();
        if (child == null) {
            child = new ShaderUniformCache(this, program);
            allocated.add(child);
            if ((allocated.size() % 40) == 0) {
                logger.warn("Potential runaway UniformCache pushes. {} caches allocated.", allocated.size());
            }
        }
        for (Map.Entry<String, UniformEntry<?>> entry : uniformEntries.entrySet()) {
            child.uniformEntries.get(entry.getKey()).setupFrom(unsafeCast(entry.getValue()));
        }
        return child;
    }

    public void popApply(ShaderUniformCache other) {
        if (parent != null) {
            throw new UnsupportedOperationException("Nested caches aren't possible.");
        }
        if (other.parent != this) {
            throw new IllegalArgumentException("The provided UniformCache is not owned by this UniformCache.");
        }
        for (UniformEntry<?> uniformEntry : other.uniformEntries.values()) {
            uniformEntry.push();
            uniformEntry.apply();
        }
        pool.push(other);
    }

    public void apply() {
        uniformEntries.values().forEach(UniformEntry::apply);
    }

    private int getLocation(String name) {
        if (parent != null) {
            return parent.getLocation(name);
        }
        return locationCache.computeIntIfAbsent(name, e -> GL20.glGetUniformLocation(program.getProgramId(), name));
    }

    private UniformEntry<?> makeEntry(Uniform uniform) {
        switch (uniform.getType().getCarrier()) {
            case INT:
            case U_INT:
                return new IntUniformEntry(uniform);
            case FLOAT:
            case MATRIX:
                return new FloatUniformEntry(uniform);
            case DOUBLE:
            case D_MATRIX:
                return new DoubleUniformEntry(uniform);
            default:
                throw new IllegalArgumentException("Unknown uniform carrier type.");
        }
    }

    //@formatter:off
    @Override public void glUniform1i(String name, int i0) { glUniformI(name, i0); }
    @Override public void glUniform2i(String name, int i0, int i1) { glUniformI(name, i0, i1); }
    @Override public void glUniform3i(String name, int i0, int i1, int i2) { glUniformI(name, i0, i1, i2); }
    @Override public void glUniform4i(String name, int i0, int i1, int i2, int i3) { glUniformI(name, i0, i1, i2, i3); }

    @Override public void glUniform1ui(String name, int i0) { glUniformI(name, i0); }
    @Override public void glUniform2ui(String name, int i0, int i1) { glUniformI(name, i0, i1); }
    @Override public void glUniform3ui(String name, int i0, int i1, int i2) { glUniformI(name, i0, i1, i2); }
    @Override public void glUniform4ui(String name, int i0, int i1, int i2, int i3) { glUniformI(name, i0, i1, i2, i3); }

    @Override public void glUniform1f(String name, float f0) { glUniformF(name, false, f0); }
    @Override public void glUniform2f(String name, float f0, float f1) { glUniformF(name, false, f0, f1); }
    @Override public void glUniform3f(String name, float f0, float f1, float f2) { glUniformF(name, false, f0, f1, f2); }
    @Override public void glUniform4f(String name, float f0, float f1, float f2, float f3) { glUniformF(name, false, f0, f1, f2, f3); }

    @Override public void glUniform1d(String name, float d0) { glUniformD(name, false, d0); }
    @Override public void glUniform2d(String name, float d0, float d1) { glUniformD(name, false, d0, d1); }
    @Override public void glUniform3d(String name, float d0, float d1, float d2) { glUniformD(name, false, d0, d1, d2); }
    @Override public void glUniform4d(String name, float d0, float d1, float d2, float d3) { glUniformD(name, false, d0, d1, d2, d3); }

    @Override public void glUniform1b(String name, boolean b0) { glUniform1i(name, b0 ? 1 : 0); }
    @Override public void glUniform2b(String name, boolean b0, boolean b1) { glUniform2i(name, b0 ? 1 : 0, b1 ? 1 : 0); }
    @Override public void glUniform3b(String name, boolean b0, boolean b1, boolean b2) { glUniform3i(name, b0 ? 1 : 0, b1 ? 1 : 0, b2 ? 1 : 0); }
    @Override public void glUniform4b(String name, boolean b0, boolean b1, boolean b2, boolean b3) { glUniform4i(name, b0 ? 1 : 0, b1 ? 1 : 0, b2 ? 1 : 0, b3 ? 1 : 0); }

    @Override public void glUniformMatrix2f(String name, float[] matrix) { glUniformMatrix2f(name, false, matrix); }
    @Override public void glUniformMatrix2f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix2x3f(String name, float[] matrix) { glUniformMatrix2x3f(name, false, matrix); }
    @Override public void glUniformMatrix2x3f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix2x4f(String name, float[] matrix) { glUniformMatrix2x4f(name, false, matrix); }
    @Override public void glUniformMatrix2x4f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix3f(String name, float[] matrix) { glUniformMatrix3f(name, false, matrix); }
    @Override public void glUniformMatrix3f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix3f(String name, Matrix3f matrix) { glUniformMatrix3f(name, false, matrix); }
    @Override public void glUniformMatrix3f(String name, boolean transpose, Matrix3f matrix) { glUniformF(name, transpose, toArrayF(matrix)); }
    @Override public void glUniformMatrix3x2f(String name, float[] matrix) { glUniformMatrix3x2f(name, false, matrix); }
    @Override public void glUniformMatrix3x2f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix3x4f(String name, float[] matrix) { glUniformMatrix3x4f(name, false, matrix); }
    @Override public void glUniformMatrix3x4f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix4f(String name, float[] matrix) { glUniformMatrix4f(name, false, matrix); }
    @Override public void glUniformMatrix4f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix4f(String name, Matrix4 matrix) { glUniformMatrix4f(name, false, matrix); }
    @Override public void glUniformMatrix4f(String name, boolean transpose, Matrix4 matrix) { glUniformMatrix4f(name, transpose, matrix.toArrayF()); }
    @Override public void glUniformMatrix4f(String name, Matrix4f matrix) { glUniformMatrix4f(name, false, matrix); }
    @Override public void glUniformMatrix4f(String name, boolean transpose, Matrix4f matrix) { glUniformMatrix4f(name, transpose, new Matrix4(matrix)); }
    @Override public void glUniformMatrix4x2f(String name, float[] matrix) { glUniformMatrix4x2f(name, false, matrix); }
    @Override public void glUniformMatrix4x2f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }
    @Override public void glUniformMatrix4x3f(String name, float[] matrix) { glUniformMatrix4x3f(name, false, matrix); }
    @Override public void glUniformMatrix4x3f(String name, boolean transpose, float[] matrix) { glUniformF(name, transpose, matrix); }

    @Override public void glUniformMatrix2d(String name, double[] matrix) { glUniformMatrix2d(name, false, matrix); }
    @Override public void glUniformMatrix2d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix2x3d(String name, double[] matrix) { glUniformMatrix2x3d(name, false, matrix); }
    @Override public void glUniformMatrix2x3d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix2x4d(String name, double[] matrix) { glUniformMatrix2x4d(name, false, matrix); }
    @Override public void glUniformMatrix2x4d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix3d(String name, double[] matrix) { glUniformMatrix3d(name, false, matrix); }
    @Override public void glUniformMatrix3d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix3d(String name, Matrix3f matrix) { glUniformMatrix3d(name, false, matrix); }
    @Override public void glUniformMatrix3d(String name, boolean transpose, Matrix3f matrix) { glUniformD(name, transpose, toArrayD(matrix)); }
    @Override public void glUniformMatrix3x2d(String name, double[] matrix) { glUniformMatrix3x2d(name, false, matrix); }
    @Override public void glUniformMatrix3x2d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix3x4d(String name, double[] matrix) { glUniformMatrix3x4d(name, false, matrix); }
    @Override public void glUniformMatrix3x4d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix4d(String name, double[] matrix) { glUniformMatrix4d(name, false, matrix); }
    @Override public void glUniformMatrix4d(String name, Matrix4 matrix) { glUniformMatrix4d(name, false, matrix); }
    @Override public void glUniformMatrix4d(String name, boolean transpose, Matrix4 matrix) { glUniformMatrix4d(name, transpose, matrix.toArrayD()); }
    @Override public void glUniformMatrix4d(String name, Matrix4f matrix) { glUniformMatrix4d(name, false, matrix); }
    @Override public void glUniformMatrix4d(String name, boolean transpose, Matrix4f matrix) { glUniformMatrix4d(name, transpose, new Matrix4(matrix)); }
    @Override public void glUniformMatrix4d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix4x2d(String name, double[] matrix) { glUniformMatrix4x2d(name, false, matrix); }
    @Override public void glUniformMatrix4x2d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    @Override public void glUniformMatrix4x3d(String name, double[] matrix) { glUniformMatrix4x3d(name, false, matrix); }
    @Override public void glUniformMatrix4x3d(String name, boolean transpose, double[] matrix) { glUniformD(name, transpose, matrix); }
    //@formatter:on

    private void glUniformI(String name, int... values) {
        UniformEntry<?> entry = uniformEntries.get(name);
        if (entry == null) {
            throw new IllegalArgumentException(String.format("Uniform with name '%s' does not exist.", name));
        }
        UniformType type = entry.uniform.getType();
        if (!(entry instanceof IntUniformEntry)) {
            throw new IllegalArgumentException(String.format("Uniform with name '%s' isn't registered with the carrier of INT, Got type '%s' with carrier '%s'.", name, type, type.getCarrier()));
        }
        if (type.getSize() != values.length) {
            throw new IllegalArgumentException(String.format("Invalid uniform length, Expected: '%s', Got: '%s'.", type.getSize(), values.length));
        }

        ((IntUniformEntry) entry).set(values, false);
    }

    private void glUniformF(String name, boolean transpose, float... values) {
        UniformEntry<?> entry = uniformEntries.get(name);
        if (entry == null) {
            throw new IllegalArgumentException(String.format("Uniform with name '%s' does not exist.", name));
        }
        UniformType type = entry.uniform.getType();
        if (!(entry instanceof FloatUniformEntry)) {
            throw new IllegalArgumentException(String.format("Uniform with name '%s' isn't registered with the carrier of FLOAT or MATRIX, Got type '%s' with carrier '%s'.", name, type, type.getCarrier()));
        }
        if (type.getSize() != values.length) {
            throw new IllegalArgumentException(String.format("Invalid uniform length, Expected: '%s', Got: '%s'.", type.getSize(), values.length));
        }

        ((FloatUniformEntry) entry).set(values, transpose);
    }

    private void glUniformD(String name, boolean transpose, double... values) {
        UniformEntry<?> entry = uniformEntries.get(name);
        if (entry == null) {
            throw new IllegalArgumentException(String.format("Uniform with name '%s' does not exist.", name));
        }
        UniformType type = entry.uniform.getType();
        if (!(entry instanceof DoubleUniformEntry)) {
            throw new IllegalArgumentException(String.format("Uniform with name '%s' isn't registered with the carrier of DOUBLE or D_MATRIX, Got type '%s' with carrier '%s'.", name, type, type.getCarrier()));
        }
        if (type.getSize() != values.length) {
            throw new IllegalArgumentException(String.format("Invalid uniform length, Expected: '%s', Got: '%s'.", type.getSize(), values.length));
        }

        ((DoubleUniformEntry) entry).set(values, transpose);
    }

    private static double[] toArrayD(Matrix3f matrix) {
        return new double[] {
                matrix.m00,
                matrix.m01,
                matrix.m02,
                matrix.m10,
                matrix.m11,
                matrix.m12,
                matrix.m20,
                matrix.m21,
                matrix.m22
        };
    }

    private static float[] toArrayF(Matrix3f matrix) {
        return new float[] {
                matrix.m00,
                matrix.m01,
                matrix.m02,
                matrix.m10,
                matrix.m11,
                matrix.m12,
                matrix.m20,
                matrix.m21,
                matrix.m22
        };
    }

    public abstract class UniformEntry<T> implements Copyable<UniformEntry<T>> {

        protected final UniformEntry<T> parent;
        protected final Uniform uniform;
        protected final UniformType type;
        protected T cache;
        protected boolean transpose;
        protected boolean dirty;
        private int location = -1;

        protected UniformEntry(Uniform uniform) {
            parent = null;
            this.uniform = uniform;
            type = uniform.getType();
            reset();
        }

        protected UniformEntry(UniformEntry<T> other) {
            if (other.parent != null) {
                throw new IllegalArgumentException("Cannot make clone of a clone.");
            }
            parent = other;
            uniform = other.uniform;
            type = other.type;
            dirty = other.dirty;
            location = other.location;
        }

        public void setupFrom(UniformEntry<T> other) {
            cache = clone(other.cache);
            transpose = false;
            dirty = false;
        }

        public void push() {
            parent.set(this);
        }

        public void set(UniformEntry<T> other) {
            set(other.cache, other.transpose);
        }

        public void set(T values, boolean transpose) {
            if (transpose && (type.getCarrier() != UniformType.Carrier.MATRIX || type.getCarrier() != UniformType.Carrier.D_MATRIX)) {
                throw new IllegalArgumentException("Transpose only supported for MATRIX and D_MATRIX carrier types.");
            }
            if (type.getCarrier() == UniformType.Carrier.INT && transpose) {
                throw new IllegalArgumentException("Transpose not supported for all Integer uniform types.");
            }
            if (len(values) != type.getSize()) {
                throw new IllegalArgumentException(String.format("Invalid size for uniform '%s', Expected: '%s', Got: '%s'.", uniform.getName(), type.getSize(), len(values)));
            }
            if (!equals(cache, values) || this.transpose != transpose) {
                cache = values;
                this.transpose = transpose;
                dirty = true;
            }
        }

        public int getLocation() {
            if (location == -1) {
                if (!uniform.getType().isSupported()) {
                    throw new IllegalStateException("Unsupported Uniform type.");
                }
                location = ShaderUniformCache.this.getLocation(uniform.getName());
            }
            return location;
        }

        public void reset() {
            cache = make(type.getSize());
            location = -1;
        }

        public abstract void apply();

        //I hate java primitive arrays in generics..
        public abstract T make(int len);

        public abstract int len(T cache);

        public abstract T clone(T other);

        public abstract boolean equals(T a, T b);
    }

    public class IntUniformEntry extends UniformEntry<int[]> {

        public IntUniformEntry(Uniform uniform) {
            super(uniform);
        }

        private IntUniformEntry(IntUniformEntry other) {
            super(other);
        }

        @Override
        public void apply() {
            if (dirty) {
                switch (type.getCarrier()) {
                    //@formatter:off
                    case INT:
                        switch (type.getSize()) {
                            case 1: GL20.glUniform1i(getLocation(), cache[0]); break;
                            case 2: GL20.glUniform2i(getLocation(), cache[0], cache[1]); break;
                            case 3: GL20.glUniform3i(getLocation(), cache[0], cache[1], cache[2]); break;
                            case 4: GL20.glUniform4i(getLocation(), cache[0], cache[1], cache[2], cache[3]); break;
                            default: throw new IllegalStateException("Invalid size for Int type." + type.getSize());
                        }
                        break;
                    case U_INT:
                        switch (type.getSize()) {
                            case 1: GL30.glUniform1ui(getLocation(), cache[0]); break;
                            case 2: GL30.glUniform2ui(getLocation(), cache[0], cache[1]); break;
                            case 3: GL30.glUniform3ui(getLocation(), cache[0], cache[1], cache[2]); break;
                            case 4: GL30.glUniform4ui(getLocation(), cache[0], cache[1], cache[2], cache[3]); break;
                            default: throw new IllegalStateException("Invalid size for Int type." + type.getSize());
                        }
                        break;
                    default: throw new IllegalStateException("Invalid type for IntUniformEntry: " + type.getCarrier());
                    //@formatter:on
                }
                dirty = false;
            }
        }

        //@formatter:off
        @Override public int[] make(int len) { return new int[len]; }
        @Override public int len(int[] cache) { return cache.length; }
        @Override public int[] clone(int[] other) { return other.clone(); }
        @Override public boolean equals(int[] a, int[] b) { return Arrays.equals(a, b); }
        @Override public UniformEntry<int[]> copy() { return new IntUniformEntry(this); }
        //@formatter:on
    }

    private class FloatUniformEntry extends UniformEntry<float[]> {

        public FloatUniformEntry(Uniform uniform) {
            super(uniform);
        }

        private FloatUniformEntry(FloatUniformEntry other) {
            super(other);
        }

        @Override
        public void apply() {
            if (dirty) {
                switch (type.getCarrier()) {
                    //@formatter:off
                    case FLOAT:
                        switch (type.getSize()) {
                            case 1: GL20.glUniform1f(getLocation(), cache[0]); break;
                            case 2: GL20.glUniform2f(getLocation(), cache[0], cache[1]); break;
                            case 3: GL20.glUniform3f(getLocation(), cache[0], cache[1], cache[2]); break;
                            case 4: GL20.glUniform4f(getLocation(), cache[0], cache[1], cache[2], cache[3]); break;
                            default: throw new IllegalStateException("Invalid size for Float type." + type.getSize());
                        }
                        break;
                    case MATRIX:
                        switch (type) {
                            case MAT2: GL20.glUniformMatrix2fv  (getLocation(), transpose, cache); break;
                            case MAT3: GL20.glUniformMatrix3fv  (getLocation(), transpose, cache); break;
                            case MAT4: GL20.glUniformMatrix4fv  (getLocation(), transpose, cache); break;
                            case MAT2x3: GL21.glUniformMatrix2x3fv(getLocation(), transpose, cache); break;
                            case MAT2x4: GL21.glUniformMatrix2x4fv(getLocation(), transpose, cache); break;
                            case MAT3x2: GL21.glUniformMatrix3x2fv(getLocation(), transpose, cache); break;
                            case MAT3x4: GL21.glUniformMatrix3x4fv(getLocation(), transpose, cache); break;
                            case MAT4x2: GL21.glUniformMatrix4x2fv(getLocation(), transpose, cache); break;
                            case MAT4x3: GL21.glUniformMatrix4x3fv(getLocation(), transpose, cache); break;
                            default: throw new IllegalStateException("Invalid Matrix type: " + type);
                        }
                        break;
                    default: throw new IllegalStateException("Invalid type for FloatUniformEntry: " + type.getCarrier());
                    //@formatter:on
                }
                dirty = false;
            }
        }

        //@formatter:off
        @Override public float[] make(int len) { return new float[len]; }
        @Override public int len(float[] cache) { return cache.length; }
        @Override public float[] clone(float[] other) { return other.clone(); }
        @Override public boolean equals(float[] a, float[] b) { return Arrays.equals(a, b); }
        @Override public UniformEntry<float[]> copy() { return new FloatUniformEntry(this); }
        //@formatter:on
    }

    private class DoubleUniformEntry extends UniformEntry<double[]> {

        public DoubleUniformEntry(Uniform uniform) {
            super(uniform);
        }

        private DoubleUniformEntry(DoubleUniformEntry other) {
            super(other);
        }

        @Override
        public void apply() {
            if (dirty) {
                switch (type.getCarrier()) {
                    //@formatter:off
                    case DOUBLE:
                        switch (type.getSize()) {
                            case 1: GL40.glUniform1d(getLocation(), cache[0]); break;
                            case 2: GL40.glUniform2d(getLocation(), cache[0], cache[1]); break;
                            case 3: GL40.glUniform3d(getLocation(), cache[0], cache[1], cache[2]); break;
                            case 4: GL40.glUniform4d(getLocation(), cache[0], cache[1], cache[2], cache[3]); break;
                            default: throw new IllegalStateException("Invalid size for Double type." + type.getSize());
                        }
                        break;
                    case D_MATRIX:
                        switch (type) {
                            case D_MAT2:   GL40.glUniformMatrix2dv  (getLocation(), transpose, cache); break;
                            case D_MAT3:   GL40.glUniformMatrix3dv  (getLocation(), transpose, cache); break;
                            case D_MAT4:   GL40.glUniformMatrix4dv  (getLocation(), transpose, cache); break;
                            case D_MAT2x3: GL40.glUniformMatrix2x3dv(getLocation(), transpose, cache); break;
                            case D_MAT2x4: GL40.glUniformMatrix2x4dv(getLocation(), transpose, cache); break;
                            case D_MAT3x2: GL40.glUniformMatrix3x2dv(getLocation(), transpose, cache); break;
                            case D_MAT3x4: GL40.glUniformMatrix3x4dv(getLocation(), transpose, cache); break;
                            case D_MAT4x2: GL40.glUniformMatrix4x2dv(getLocation(), transpose, cache); break;
                            case D_MAT4x3: GL40.glUniformMatrix4x3dv(getLocation(), transpose, cache); break;
                            default: throw new IllegalStateException("Invalid Matrix type: " + type);
                        }
                        break;
                    default: throw new IllegalStateException("Invalid type for DoubleUniformEntry: " + type.getCarrier());
                    //@formatter:on
                }
                dirty = false;
            }
        }

        //@formatter:off
        @Override public double[] make(int len) { return new double[len]; }
        @Override public int len(double[] cache) { return cache.length; }
        @Override public double[] clone(double[] other) { return other.clone(); }
        @Override public boolean equals(double[] a, double[] b) { return Arrays.equals(a, b); }
        @Override public UniformEntry<double[]> copy() { return new DoubleUniformEntry(this); }
        //@formatter:on
    }
}
