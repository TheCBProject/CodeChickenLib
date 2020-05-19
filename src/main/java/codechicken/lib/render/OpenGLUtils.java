package codechicken.lib.render;

import net.minecraftforge.client.event.ModelRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.BooleanSupplier;

/**
 * Created by covers1624 on 20/06/2017.
 */
public class OpenGLUtils {

    private static final Logger logger = LogManager.getLogger();

    public static boolean openGL20;
    public static boolean openGL32;
    public static boolean openGL40;
    public static boolean openGL43;
    public static boolean openGL44;
    public static boolean openGL45;
    public static boolean openGL46;

    public static void onModelRegistryEvent(ModelRegistryEvent event) {
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
            logger.info(log);
            return false;
        }
    }
}
