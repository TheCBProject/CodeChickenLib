package codechicken.lib.render;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;

/**
 * Most of this is available in later LWJGL versions..
 * But.. Mojang..
 * Also contains some utilities.
 *
 * Created by covers1624 on 20/06/2017.
 */
public class OpenGLUtils {

    public static boolean openGL20;
    public static boolean openGL32;
    public static boolean openGL40;

    public static void loadCaps() {
        ContextCapabilities caps = GLContext.getCapabilities();
        openGL20 = caps.OpenGL20;
        openGL32 = caps.OpenGL32;
        openGL40 = caps.OpenGL40;
    }

    /**
     * <p><a href="http://www.opengl.org/sdk/docs/man/html/glGetProgramInfoLog.xhtml">OpenGL SDK Reference</a></p>
     *
     * Returns the information log for a program object.
     *
     * @param program the program object whose information log is to be queried
     */
    public static String glGetProgramInfoLog(int program) {
        int maxLength = GL20.glGetProgrami(program, GL_INFO_LOG_LENGTH);
        return GL20.glGetProgramInfoLog(program, maxLength);
    }

    /**
     * <p><a href="http://www.opengl.org/sdk/docs/man/html/glGetShaderInfoLog.xhtml">OpenGL SDK Reference</a></p>
     *
     * Returns the information log for a shader object.
     *
     * @param shader the shader object whose information log is to be queried
     */
    public static String glGetShaderInfoLog(int shader) {
        int maxLength = GL20.glGetShaderi(shader, GL_INFO_LOG_LENGTH);
        return GL20.glGetShaderInfoLog(shader, maxLength);
    }
}
