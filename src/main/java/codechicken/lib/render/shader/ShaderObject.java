package codechicken.lib.render.shader;

import codechicken.lib.internal.CCLLog;
import codechicken.lib.render.OpenGLUtils;
import codechicken.lib.render.shader.ShaderProgram.UniformCache;
import org.apache.logging.log4j.Level;
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
    private String shaderSource;
    int shaderID;
    private boolean isLocked;
    private IntConsumer onLink;
    private Consumer<UniformCache> useCallback;

    /**
     * A ShaderObject!
     * Shader's have a type and a source. Simple.
     * Dynamically created shader's are possible.
     *
     * @param shaderType   The type of shader we are creating.
     * @param shaderSource The Source for this shader.
     */
    public ShaderObject(ShaderType shaderType, String shaderSource) {
        this(shaderType, shaderSource, false);
    }

    /**
     * A ShaderObject!
     * Shader's have a type and a source. Simple.
     * Dynamically created shader's are possible.
     *
     * @param shaderType   The type of shader we are creating.
     * @param shaderSource The source for this shader.
     * @param lockShader   If the shader is unable to be deleted.
     */
    public ShaderObject(ShaderType shaderType, String shaderSource, boolean lockShader) {
        this(shaderType, lockShader);
        this.shaderSource = shaderSource;
    }

    /**
     * Used for dynamic shader's!
     * Override {@link #getShaderSource}.
     *
     * @param shaderType The type of shader we are creating.
     */
    protected ShaderObject(ShaderType shaderType) {
        this(shaderType, false);
    }

    /**
     * Used for dynamic shader's!
     * Override {@link #getShaderSource}.
     *
     * @param shaderType The type of shader we are creating.
     * @param lockShader If the shader is unable to be deleted.
     */
    protected ShaderObject(ShaderType shaderType, boolean lockShader) {
        this.shaderType = shaderType;
        if (!shaderType.isSupported()) {
            throw new RuntimeException(String.format("Unable to create ShaderObject with type %s, Type not supported in current OpenGL context!", shaderType));
        }
        this.isLocked = lockShader;
        shaderID = GL20.glCreateShader(shaderType.glCode);
        if (shaderID == 0) {
            throw new RuntimeException("Unable to create new ShaderObject! GL Allocation has failed.");
        }
    }

    /**
     * Compiles the ShaderObject.
     */
    public ShaderObject compileShader() {
        GL20.glShaderSource(shaderID, getShaderSource());
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new IllegalStateException(String.format("Unable to compile %s shader object:\n%s", shaderType.name(), OpenGLUtils.glGetShaderInfoLog(shaderID)));
        }
        return this;
    }

    /**
     * Called when the Shader Object is linked to a ShaderProgram.
     *
     * @param program The shader program we are linked to.
     */
    public void onShaderLink(int program) {
        if (onLink != null) {
            onLink.accept(program);
        }
    }

    public void onShaderUse(UniformCache cache) {
        if (useCallback != null) {
            useCallback.accept(cache);
        }
    }

    /**
     * Used to set the callback for when this Specific ShaderObject is linked to a ShaderProgram.
     *
     * @param onLink The callback.
     */
    public ShaderObject setLinkCallback(IntConsumer onLink) {
        if (this.onLink == null) {
            this.onLink = onLink;
        } else {
            throw new RuntimeException("Link callback already set.");
        }
        return this;
    }

    /**
     * Used to set the callback for when this Specific ShaderObject in a ShaderProgram is bound for rendering.
     *
     * @param onUse The callback.
     */
    public ShaderObject setUseCallback(Consumer<UniformCache> onUse) {
        if (this.useCallback == null) {
            this.useCallback = onUse;
        } else {
            throw new RuntimeException("Use callback already set.");
        }
        return this;
    }

    /**
     * Marks the shader for deletion.
     * Any ShaderPrograms this Object is linked to will be invalidated also.
     * Make sure you remove the object from the shader if you intend on keeping the Program.
     */
    public void disposeObject() {
        if (!isLocked) {
            GL20.glDeleteShader(shaderID);
        } else {
            CCLLog.big(Level.WARN, "Deletion of locked ShaderObject was attempted.");
        }
    }

    /**
     * Checks if the shader object is locked and cannot be deleted.
     *
     * @return If deletion is not allowed.
     */
    public boolean isLocked() {
        return isLocked;
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
