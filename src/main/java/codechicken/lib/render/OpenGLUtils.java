package codechicken.lib.render;

import codechicken.lib.internal.CCLLog;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.BooleanSupplier;

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
    public static boolean openGL46;

    public static void loadCaps() {
        GLCapabilities caps = GL.getCapabilities();
        openGL20 = caps.OpenGL20;
        openGL32 = caps.OpenGL32;
        openGL40 = tryGet(() -> caps.OpenGL40, "LWJGL Outdated, OpenGL 4.0 is not supported.");
        openGL43 = tryGet(() -> caps.OpenGL43, "LWJGL Outdated, OpenGL 4.3 is not supported.");
        openGL44 = tryGet(() -> caps.OpenGL44, "LWJGL Outdated, OpenGL 4.4 is not supported.");
        openGL45 = tryGet(() -> caps.OpenGL45, "LWJGL Outdated, OpenGL 4.5 is not supported.");
        openGL46 = tryGet(() -> caps.OpenGL46, "LWJGL Outdated, OpenGL 4.6 is not supported.");
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
