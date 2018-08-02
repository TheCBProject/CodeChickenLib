package codechicken.lib.render;

import codechicken.lib.internal.CCLLog;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import java.util.function.BooleanSupplier;

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
    public static boolean openGL43;
    public static boolean openGL44;
    public static boolean openGL45;

    public static void loadCaps() {
        ContextCapabilities caps = GLContext.getCapabilities();
        openGL20 = caps.OpenGL20;
        openGL32 = caps.OpenGL32;
        openGL40 = tryGet(() -> caps.OpenGL40, "LWJGL Outdated, OpenGL 4.0 is not supported.");
        openGL43 = tryGet(() -> caps.OpenGL43, "LWJGL Outdated, OpenGL 4.3 is not supported.");
        openGL44 = tryGet(() -> caps.OpenGL44, "LWJGL Outdated, OpenGL 4.4 is not supported.");
        openGL45 = tryGet(() -> caps.OpenGL45, "LWJGL Outdated, OpenGL 4.5 is not supported.");
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

    private static boolean tryGet(BooleanSupplier sup, String log) {
        try {
            return sup.getAsBoolean();
        } catch (Throwable ignored) {
            CCLLog.log(Level.INFO, log);
            return false;
        }
    }
}
