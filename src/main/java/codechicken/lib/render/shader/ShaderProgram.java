package codechicken.lib.render.shader;

import codechicken.lib.render.OpenGLUtils;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.BooleanUniformEntry;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.FloatUniformEntry;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.IntUniformEntry;
import codechicken.lib.render.shader.ShaderProgram.UniformEntry.Matrix4UniformEntry;
import codechicken.lib.vec.Matrix4;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.lwjgl.opengl.GL20;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.GL_FALSE;

//TODO Better error throwing, Use MC's CrashReportCategory.
public class ShaderProgram {

	private Set<ShaderObject> shaderObjects = new LinkedHashSet<>();
	private int programID;

	private UniformCache uniformCache = new UniformCache();
	private boolean isInvalid;

	private IntConsumer onLink;

	public ShaderProgram() {
		this((program) -> {
		});
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

			onLink.accept(programID);
			shaderObjects.forEach(shaderObject -> shaderObject.onShaderLink(programID));

			GL20.glLinkProgram(programID);

			if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL_FALSE) {
				throw new RuntimeException(String.format("ShaderProgram validation has failed!\n%s", OpenGLUtils.glGetProgramInfoLog(programID)));
			}
			isInvalid = false;
		}
	}

	/**
	 * Called to "use" or bind the shader.
	 */
	public void useShader() {
		useShader(uniformCache1 -> {
		});
	}

	/**
	 * Called to "use" or bind the shader.
	 * You are provided a UniformCache to upload Uniforms to the GPU.
	 * Uniforms are cached and will only be uploaded if their state changes, or the program is invalidated.
	 *
	 * @param uniformApplier The callback to apply Uniforms.
	 */
	public void useShader(Consumer<UniformCache> uniformApplier) {
		checkValidation();
		GL20.glUseProgram(programID);
		shaderObjects.forEach(shaderObject -> shaderObject.onShaderUse(uniformCache));
		uniformApplier.accept(uniformCache);
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
		public int getUniformLocation(String name) {
			int uniformLocation;
			if (uniformLocationCache.containsKey(name)) {
				uniformLocation = uniformLocationCache.get(name);
			} else {
				uniformLocation = GL20.glGetUniformLocation(programID, name);
				uniformLocationCache.put(name, uniformLocation);
			}
			return uniformLocation;
		}

		public void glUniform1F(int location, float v0) {
			glUniformF(location, () -> GL20.glUniform1f(location, v0), v0);
		}

		public void glUniform2F(int location, float v0, float v1) {
			glUniformF(location, () -> GL20.glUniform2f(location, v0, v1), v0, v1);
		}

		public void glUniform3F(int location, float v0, float v1, float v2) {
			glUniformF(location, () -> GL20.glUniform3f(location, v0, v1, v2), v0, v1, v2);
		}

		public void glUniform4F(int location, float v0, float v1, float v2, float v3) {
			glUniformF(location, () -> GL20.glUniform4f(location, v0, v1, v2, v3), v0, v1, v2, v3);
		}

		private void glUniformF(int location, IUniformCallback callback, float... values) {
			glUniform(location, UniformEntry.IS_FLOAT, FloatUniformEntry::new, callback, values);
		}

		public void glUniform1I(int location, int v0) {
			glUniformI(location, () -> GL20.glUniform1i(location, v0), v0);
		}

		public void glUniform2I(int location, int v0, int v1) {
			glUniformI(location, () -> GL20.glUniform2i(location, v0, v1), v0, v1);
		}

		public void glUniform3I(int location, int v0, int v1, int v2) {
			glUniformI(location, () -> GL20.glUniform3i(location, v0, v1, v2), v0, v1, v2);
		}

		public void glUniform4I(int location, int v0, int v1, int v2, int v3) {
			glUniformI(location, () -> GL20.glUniform4i(location, v0, v1, v2, v3), v0, v1, v2, v3);
		}

		private void glUniformI(int location, IUniformCallback callback, int... values) {
			glUniform(location, UniformEntry.IS_INT, IntUniformEntry::new, callback, values);
		}

		public void glUniformMatrix4(int location, boolean transpose, Matrix4 matrix) {
			glUniform(location, UniformEntry.IS_MATRIX, Matrix4UniformEntry::new, () -> GL20.glUniformMatrix4(location, transpose, matrix.toFloatBuffer()), matrix);
		}

		public void glUniformBoolean(int location, boolean value) {
			glUniform(location, UniformEntry.IS_BOOLEAN, BooleanUniformEntry::new, () -> GL20.glUniform1i(location, value ? 1 : 0), value);
		}

		private <T> void glUniform(int location, Predicate<UniformEntry> isType, Function<T, UniformEntry<T>> createUniform, IUniformCallback applyCallback, T value) {
			boolean update = true;
			if (uniformObjectCache.containsKey(location)) {
				UniformEntry uniformEntry = uniformObjectCache.get(location);
				if (isType.test(uniformEntry)) {
					update = !uniformEntry.check(value);
				}
			}

			if (update) {
				UniformEntry<T> entry = createUniform.apply(value);
				applyCallback.apply();
				uniformObjectCache.put(location, entry);
			}
		}

	}

	public static abstract class UniformEntry<T> {

		public static Predicate<UniformEntry> IS_INT = uniformEntry -> uniformEntry instanceof IntUniformEntry;
		public static Predicate<UniformEntry> IS_FLOAT = uniformEntry -> uniformEntry instanceof FloatUniformEntry;
		public static Predicate<UniformEntry> IS_MATRIX = uniformEntry -> uniformEntry instanceof Matrix4UniformEntry;
		public static Predicate<UniformEntry> IS_BOOLEAN = uniformEntry -> uniformEntry instanceof BooleanUniformEntry;

		public abstract boolean check(T other);

		public abstract boolean isType(Object object);

		public static class IntUniformEntry extends UniformEntry<int[]> {

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

			@Override
			public boolean isType(Object object) {
				return object instanceof IntUniformEntry;
			}
		}

		public static class FloatUniformEntry extends UniformEntry<float[]> {

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

			@Override
			public boolean isType(Object object) {
				return object instanceof FloatUniformEntry;
			}
		}

		public static class Matrix4UniformEntry extends UniformEntry<Matrix4> {

			private Matrix4 matrix;

			public Matrix4UniformEntry(Matrix4 matrix) {
				this.matrix = matrix;
			}

			@Override
			public boolean check(Matrix4 other) {
				return this.matrix.equals(other);
			}

			@Override
			public boolean isType(Object object) {
				return object instanceof Matrix4UniformEntry;
			}
		}

		public static class BooleanUniformEntry extends UniformEntry<Boolean> {

			private boolean bool;

			public BooleanUniformEntry(boolean bool) {
				this.bool = bool;
			}

			@Override
			public boolean check(Boolean other) {
				return bool == other;
			}

			@Override
			public boolean isType(Object object) {
				return false;
			}
		}
	}


	private interface IUniformCallback {
		void apply();
	}
}
