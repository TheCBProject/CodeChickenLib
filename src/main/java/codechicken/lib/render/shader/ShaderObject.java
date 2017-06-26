package codechicken.lib.render.shader;

import codechicken.lib.render.OpenGLUtils;
import codechicken.lib.render.shader.ShaderProgram.IUniformCallback;
import codechicken.lib.render.shader.ShaderProgram.UniformCache;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Created by covers1624 on 15/06/2017.
 */
public class ShaderObject {

    private ShaderType shaderType;
    protected String shaderSource;
    int shaderID;

    //@formatter:off
    private IntConsumer onLink = i -> {};
    private IUniformCallback useCallback = cache ->{};
    //@formatter:on

    /**
     * Set to true only IF a successful compilation has occurred.
     */
    private boolean isCompiled;
    /**
     * If the shader is marked for recompilation.
     * This is handled by ShaderProgram.
     */
    private boolean recompileRequested;

    /**
     * A ShaderObject!
     * Shader's have a type and a source. Simple.
     * Dynamically created shader's are possible.
     *
     * @param shaderType   The type of shader we are creating.
     * @param shaderSource The source for this shader.
     */
    public ShaderObject(ShaderType shaderType, String shaderSource) {
        this(shaderType);
        this.shaderSource = shaderSource;
    }

    /**
     * Used for dynamic shader's!
     * Override {@link #getShaderSource}.
     *
     * @param shaderType The type of shader we are creating.
     */
    protected ShaderObject(ShaderType shaderType) {
        this.shaderType = shaderType;
        if (!shaderType.isSupported()) {
            throw new RuntimeException(String.format("Unable to create ShaderObject with type %s, Type not supported in current OpenGL context!", shaderType));
        }
        shaderID = GL20.glCreateShader(shaderType.glCode);
        if (shaderID == 0) {
            throw new RuntimeException("Unable to create new ShaderObject! GL Allocation has failed.");
        }
    }

    /**
     * Compiles the ShaderObject.
     */
    public ShaderObject compileShader() {
        if (!isCompiled || recompileRequested) {
            isCompiled = false;
            GL20.glShaderSource(shaderID, getShaderSource());
            GL20.glCompileShader(shaderID);
            if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new IllegalStateException(String.format("Unable to compile %s shader object:\n%s", shaderType.name(), OpenGLUtils.glGetShaderInfoLog(shaderID)));
            }
            isCompiled = true;
        }
        return this;
    }

    /**
     * Used to set the callback for when this Specific ShaderObject is linked to a ShaderProgram.
     * Subsequent calls to this will append Callbacks together.
     *
     * @param callback The callback.
     */
    public ShaderObject addLinkCallback(IntConsumer callback) {
        onLink = onLink.andThen(callback);
        return this;
    }

    /**
     * Used to set the callback for when this Specific ShaderObject in a ShaderProgram is bound for rendering.
     * Subsequent calls to this will append Callbacks together.
     *
     * @param callback The callback.
     */
    public ShaderObject addUseCallback(IUniformCallback callback) {
        useCallback = useCallback.with(callback);
        return this;
    }

    /**
     * Marks the shader for deletion.
     * Any ShaderPrograms this Object is linked to will be invalidated also.
     * Make sure you remove the object from the shader if you intend on keeping the Program.
     */
    public ShaderObject disposeObject() {
        GL20.glDeleteShader(shaderID);
        return this;
    }

    /**
     * Sets the shader to be recompiled next time the shader is used.
     */
    public ShaderObject requestRecompile() {
        recompileRequested = true;
        return this;
    }

    /**
     * If the shader is marked for recompilation.
     *
     * @return If the shader is marked for recompilation.
     */
    public boolean isRecompileRequested() {
        return recompileRequested;
    }

    /**
     * Used to retrieve the ShaderObject's Source when being built.
     * Useful for a shader that is dynamically created.
     *
     * @return The shader's source.
     */
    protected String getShaderSource() {
        return shaderSource;
    }

    //region Internal

    /**
     * Called when the Shader Object is linked to a ShaderProgram.
     *
     * @param program The shader program we are linked to.
     */
    void onShaderLink(int program) {
        if (onLink != null) {
            onLink.accept(program);
        }
    }

    /**
     * Called from ShaderProgram when the ShaderObject is used.
     *
     * @param cache
     */
    void onShaderUse(UniformCache cache) {
        if (useCallback != null) {
            useCallback.apply(cache);
        }
    }
    //endregion

    /**
     * Specifies the type of ShaderObject something is.
     */
    public enum ShaderType {

        //@formatter:off
		VERTEX(GL20.GL_VERTEX_SHADER,               OpenGLUtils.openGL20),
		FRAGMENT(GL20.GL_FRAGMENT_SHADER,           OpenGLUtils.openGL20),
		GEOMETRY(GL32.GL_GEOMETRY_SHADER,           OpenGLUtils.openGL32),
		TESS_CONTROL(GL40.GL_TESS_CONTROL_SHADER,   OpenGLUtils.openGL40),
		TESS_EVAL(GL40.GL_TESS_EVALUATION_SHADER,   OpenGLUtils.openGL40);
		//@formatter:on

        private int glCode;
        private boolean isSupported;

        ShaderType(int glCode, boolean isSupported) {

            this.glCode = glCode;
            this.isSupported = isSupported;
        }

        /**
         * Used to determine if this specific ShaderType is supported by the current OpenGL Context.
         *
         * @return If the operation is supported.
         */
        public boolean isSupported() {
            return isSupported;
        }

    }
}
