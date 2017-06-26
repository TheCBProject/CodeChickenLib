package codechicken.lib.render.shader;

import codechicken.lib.internal.CCLLog;
import codechicken.lib.render.OpenGLUtils;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.BooleanUniformEntry;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.FloatUniformEntry;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.IntUniformEntry;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.MatrixUniformEntry;
import codechicken.lib.vec.Matrix4;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.GL_FALSE;

//TODO Better error throwing, Use MC's CrashReportCategory.
public class ShaderProgram {

    public static final IUniformCallback NULL_UNIFORM_CONSUMER = cache -> {
    };
    public static final IntConsumer NULL_INT_CONSUMER = i -> {
    };

    private Set<ShaderObject> shaderObjects = new LinkedHashSet<>();
    private int programID;

    private UniformCache uniformCache = new UniformCache();
    private boolean isInvalid;

    private IUniformCallback globalUniformCallback = NULL_UNIFORM_CONSUMER;

    private IntConsumer onLink;

    public ShaderProgram() {
        this(NULL_INT_CONSUMER);
    }

    /**
     * The definition of a ShaderProgram object.
     *
     * @param onLink Called on validation before the ShaderProgram Links.
     */
    public ShaderProgram(IntConsumer onLink) {
        this.onLink = onLink;
        programID = GL20.glCreateProgram();
        if (programID == 0) {
            throw new RuntimeException("Unable to create new ShaderProgram! GL Allocation has failed.");
        }
    }

    /**
     * Attaches a ShaderObject to the program.
     * Multiple ShaderTypes are permissible.
     * The ShaderProgram is marked for validation and will be validated next bind.
     *
     * @param shaderObject The ShaderObject to attach.
     */
    public void attachShader(ShaderObject shaderObject) {
        if (shaderObjects.contains(shaderObject)) {
            throw new IllegalStateException("Unable to attach ShaderObject. Object is already attached!");
        }
        shaderObjects.add(shaderObject);
        GL20.glAttachShader(programID, shaderObject.shaderID);
        isInvalid = true;
    }

    /**
     * If the shader has been marked as invalid, this will call for the shader to be validated.
     */
    public void checkValidation() {
        if (isInvalid) {
            uniformCache.invalidateCache();

            GL20.glLinkProgram(programID);

            onLink.accept(programID);
            shaderObjects.forEach(shaderObject -> shaderObject.onShaderLink(programID));

            if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL_FALSE) {
                throw new RuntimeException(String.format("ShaderProgram validation has failed!\n%s", OpenGLUtils.glGetProgramInfoLog(programID)));
            }
            isInvalid = false;
        }
    }

    /**
     * Attaches a Global uniform callback.
     * Subsequent calls to this will append Callbacks together.
     * These are fired before any Use specific callbacks.
     *
     * @param callback The callback.
     */
    public void addGlobalUniformCallback(IUniformCallback callback) {
        globalUniformCallback = globalUniformCallback.with(callback);
    }

    /**
     * Called to "use" or bind the shader.
     */
    public void useShader() {
        useShader(NULL_UNIFORM_CONSUMER);
    }

    /**
     * Called to "use" or bind the shader.
     * You are provided a UniformCache to upload Uniforms to the GPU.
     * Uniforms are cached and will only be uploaded if their state changes, or the program is invalidated.
     *
     * @param callback The callback to apply Uniforms.
     */
    public void useShader(IUniformCallback callback) {
        shaderObjects.forEach(ShaderObject::compileShader);
        checkValidation();
        GL20.glUseProgram(programID);
        shaderObjects.forEach(shaderObject -> shaderObject.onShaderUse(uniformCache));
        globalUniformCallback.apply(uniformCache);
        callback.apply(uniformCache);
    }

    /**
     * Called to release the shader.
     */
    public void releaseShader() {
        GL20.glUseProgram(0);
    }

    /**
     * An object that stores the currently uploaded Uniforms for this specific ShaderProgram.
     */
    public class UniformCache {

        private TIntObjectHashMap<UniformEntry> uniformObjectCache = new TIntObjectHashMap<>();
        private TObjectIntHashMap<String> uniformLocationCache = new TObjectIntHashMap<>();

        private void invalidateCache() {
            uniformLocationCache.clear();
            uniformObjectCache.clear();
        }

        /**
         * A cached call to get the location of a Uniform.
         *
         * @param name The name requested.
         * @return The location.
         */
        private int getUniformLocation(String name) {
            int uniformLocation;
            if (uniformLocationCache.containsKey(name)) {
                uniformLocation = uniformLocationCache.get(name);
            } else {
                uniformLocation = GL20.glGetUniformLocation(programID, name);
                uniformLocationCache.put(name, uniformLocation);
            }
            return uniformLocation;
        }

        public void glUniform1F(String location, float v0) {
            glUniformF(location, (loc) -> GL20.glUniform1f(loc, v0), v0);
        }

        public void glUniform2F(String location, float v0, float v1) {
            glUniformF(location, (loc) -> GL20.glUniform2f(loc, v0, v1), v0, v1);
        }

        public void glUniform3F(String location, float v0, float v1, float v2) {
            glUniformF(location, (loc) -> GL20.glUniform3f(loc, v0, v1, v2), v0, v1, v2);
        }

        public void glUniform4F(String location, float v0, float v1, float v2, float v3) {
            glUniformF(location, (loc) -> GL20.glUniform4f(loc, v0, v1, v2, v3), v0, v1, v2, v3);
        }

        private void glUniformF(String location, IGLUniformCallback callback, float... values) {
            glUniform(location, UniformEntry.IS_FLOAT, FloatUniformEntry.NEW, callback, values);
        }

        public void glUniform1I(String location, int v0) {
            glUniformI(location, (loc) -> GL20.glUniform1i(loc, v0), v0);
        }

        public void glUniform2I(String location, int v0, int v1) {
            glUniformI(location, (loc) -> GL20.glUniform2i(loc, v0, v1), v0, v1);
        }

        public void glUniform3I(String location, int v0, int v1, int v2) {
            glUniformI(location, (loc) -> GL20.glUniform3i(loc, v0, v1, v2), v0, v1, v2);
        }

        public void glUniform4I(String location, int v0, int v1, int v2, int v3) {
            glUniformI(location, (loc) -> GL20.glUniform4i(loc, v0, v1, v2, v3), v0, v1, v2, v3);
        }

        private void glUniformI(String location, IGLUniformCallback callback, int... values) {
            glUniform(location, UniformEntry.IS_INT, IntUniformEntry.NEW, callback, values);
        }

        public void glUniformMatrix2(String location, boolean transpose, FloatBuffer matrix) {
            glUniformMatrix(location, (loc) -> GL20.glUniformMatrix2(loc, transpose, matrix), transpose, matrix);
        }

        public void glUniformMatrix4(String location, boolean transpose, Matrix4 matrix) {
            glUniformMatrix(location, (loc) -> GL20.glUniformMatrix4(loc, transpose, matrix.toFloatBuffer()), transpose, matrix.toFloatBuffer());
        }

        public void glUniformMatrix4(String location, boolean transpose, FloatBuffer matrix) {
            glUniformMatrix(location, (loc) -> GL20.glUniformMatrix4(loc, transpose, matrix), transpose, matrix);
        }

        public void glUniformMatrix(String location, IGLUniformCallback callback, boolean transpose, FloatBuffer matrix) {
            glUniform(location, UniformEntry.IS_MATRIX, MatrixUniformEntry.NEW, callback, ImmutablePair.of(matrix, transpose));
        }

        public void glUniformBoolean(String location, boolean value) {
            glUniform(location, UniformEntry.IS_BOOLEAN, BooleanUniformEntry.NEW, (loc) -> GL20.glUniform1i(loc, value ? 1 : 0), value);
        }

        private <T> void glUniform(String location, Predicate<UniformEntry> isType, Function<T, UniformEntry<T>> createUniform, IGLUniformCallback applyCallback, T value) {
            int loc = getUniformLocation(location);
            boolean update = true;
            if (uniformObjectCache.containsKey(loc)) {
                UniformEntry uniformEntry = uniformObjectCache.get(loc);
                if (isType.test(uniformEntry)) {
                    update = !uniformEntry.check(value);
                }
            }

            if (update) {
                UniformEntry<T> entry = createUniform.apply(value);
                applyCallback.apply(loc);
                uniformObjectCache.put(loc, entry);
            }
        }

    }

    public static abstract class UniformEntry<T> {

        public static Predicate<UniformEntry> IS_INT = uniformEntry -> uniformEntry instanceof IntUniformEntry;
        public static Predicate<UniformEntry> IS_FLOAT = uniformEntry -> uniformEntry instanceof FloatUniformEntry;
        public static Predicate<UniformEntry> IS_MATRIX = uniformEntry -> uniformEntry instanceof MatrixUniformEntry;
        public static Predicate<UniformEntry> IS_BOOLEAN = uniformEntry -> uniformEntry instanceof BooleanUniformEntry;

        public abstract boolean check(T other);

        public static class IntUniformEntry extends UniformEntry<int[]> {

            public static Function<int[], UniformEntry<int[]>> NEW = IntUniformEntry::new;

            private int[] cache;

            public IntUniformEntry(int... cache) {
                this.cache = cache;
            }

            @Override
            public boolean check(int... other) {
                if (cache.length != other.length) {
                    return false;
                }
                for (int i = 0; i < cache.length; i++) {
                    if (cache[i] != other[i]) {
                        return false;
                    }
                }
                return true;
            }
        }

        public static class FloatUniformEntry extends UniformEntry<float[]> {

            public static Function<float[], UniformEntry<float[]>> NEW = FloatUniformEntry::new;

            private float[] cache;

            public FloatUniformEntry(float... cache) {
                this.cache = cache;
            }

            @Override
            public boolean check(float... other) {
                if (cache.length != other.length) {
                    return false;
                }
                for (int i = 0; i < cache.length; i++) {
                    if (cache[i] != other[i]) {
                        return false;
                    }
                }
                return true;
            }
        }

        public static class MatrixUniformEntry extends UniformEntry<Pair<FloatBuffer, Boolean>> {

            public static Function<Pair<FloatBuffer, Boolean>, UniformEntry<Pair<FloatBuffer, Boolean>>> NEW = MatrixUniformEntry::new;

            FloatBuffer matrix;
            boolean transpose;

            public MatrixUniformEntry(Pair<FloatBuffer, Boolean> other) {
                this.matrix = other.getKey();
                this.transpose = other.getValue();
            }

            @Override
            public boolean check(Pair<FloatBuffer, Boolean> other) {
                return matrix.equals(other.getKey()) && transpose == other.getValue();
            }
        }

        public static class BooleanUniformEntry extends UniformEntry<Boolean> {

            public static Function<Boolean, UniformEntry<Boolean>> NEW = BooleanUniformEntry::new;

            private boolean bool;

            public BooleanUniformEntry(boolean bool) {
                this.bool = bool;
            }

            @Override
            public boolean check(Boolean other) {
                return bool == other;
            }
        }
    }

    private interface IGLUniformCallback {

        void apply(int loc);
    }

    @FunctionalInterface
    //Because i want cache to be the default name for the lambda field.
    public interface IUniformCallback {

        void apply(UniformCache cache);

        default IUniformCallback with(IUniformCallback callback) {
            return cache -> {
                apply(cache);
                callback.apply(cache);
            };
        }
    }

}
