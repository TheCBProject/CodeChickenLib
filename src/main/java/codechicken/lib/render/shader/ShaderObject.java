package codechicken.lib.render.shader;

import com.google.common.collect.ImmutableList;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

/**
 * Created by covers1624 on 24/5/20.
 */
public interface ShaderObject {

    /**
     * A simple identifier for this {@link ShaderObject},
     * used for logging.
     *
     * @return The name.
     */
    String getName();

    /**
     * Gets the {@link ShaderType} for this shader.
     *
     * @return The {@link ShaderType}.
     */
    ShaderType getShaderType();

    /**
     * Gets all {@link UniformPair}s this shader exposes.
     * It is expected that a {@link ShaderObject} will not
     * dynamically change the uniforms it exposes over its
     * lifetime. Doing so is not enforced, but will break
     * the Uniform pipeline.
     *
     * @return The {@link UniformPair}s.
     */
    ImmutableList<UniformPair> getUniforms();

    /**
     * Checks if this shader is dirty and requires re-compiling.
     *
     * @return If this shader is dirty.
     */
    boolean isDirty();

    /**
     * Allocates and compiles this {@link ShaderObject}.
     * Does nothing if the shader is already compiled.
     */
    void alloc();

    /**
     * Gets the GL id for this {@link ShaderObject}.
     *
     * @return The id, -1 of not allocated.
     */
    int getShaderID();

    /**
     * Called when this {@link ShaderObject} is linked to a {@link ShaderProgram}.
     *
     * @param programId The GL id of the program.
     */
    void onLink(int programId);

    /**
     * Represents a Shader Type.
     */
    interface ShaderType {

        /**
         * The GL code used in {@link GL20#glCreateShader}.
         *
         * @return The code.
         */
        int getGLCode();
    }

    /**
     * Standard ShaderTypes.
     */
    enum StandardShaderType implements ShaderType {
        //@formatter:off
        VERTEX      (GL20.GL_VERTEX_SHADER         ),
        FRAGMENT    (GL20.GL_FRAGMENT_SHADER       ),
        GEOMETRY    (GL32.GL_GEOMETRY_SHADER       ),
        TESS_CONTROL(GL40.GL_TESS_CONTROL_SHADER   ),
        TESS_EVAL   (GL40.GL_TESS_EVALUATION_SHADER),
        COMPUTE     (GL43.GL_COMPUTE_SHADER        );
        //@formatter:on

        private final int glCode;

        StandardShaderType(int glCode) {
            this.glCode = glCode;
        }

        @Override
        public int getGLCode() {
            return glCode;
        }
    }
}
