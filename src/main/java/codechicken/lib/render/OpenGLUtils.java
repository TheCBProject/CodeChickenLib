package codechicken.lib.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by covers1624 on 20/06/2017.
 */
public class OpenGLUtils {
    private static final int[][] VERSIONS = {{4, 6}, {4, 5}, {4, 4}, {4, 3}, {4, 0}, {3, 2}, {2, 1}, {2, 0}};
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicBoolean IS_INITIALIZED = new AtomicBoolean(false);

    public static boolean openGL20;
    public static boolean openGL21;
    public static boolean openGL32;
    public static boolean openGL40;
    public static boolean openGL43;
    public static boolean openGL44;
    public static boolean openGL45;
    public static boolean openGL46;

    /**
     * Detect the GPUs maximum supported GL capabilities by
     * repeatedly trying to create an invisible fake-window.
     * This function may be called from any thread and as many
     * times as wanted, since after initialization we just return.
     *
     * @author KitsuneAlex
     * @since 16/07/2022
     */
    public static void init() {
        if (IS_INITIALIZED.get()) {
            return;
        }

        LOGGER.info("Checking GPU capabilities..");

        try {
            final Thread testThread = new Thread(() -> {
                GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

                for(final int[] version : VERSIONS) {
                    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, version[0]);
                    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, version[1]);

                    final long window = GLFW.glfwCreateWindow(100, 100, "CCL OpenGL Checker", 0L, 0L);

                    if (window == 0L) {
                        try (final MemoryStack stack = MemoryStack.stackPush()) {
                            final PointerBuffer errorBuffer = stack.callocPointer(1);

                            if(GLFW.glfwGetError(errorBuffer) == GLFW.GLFW_VERSION_UNAVAILABLE) {
                                continue; // Skip and try the next version
                            }

                            if (errorBuffer.get() == 0L) {
                                throw new IllegalStateException("Could not retrieve GLFW error");
                            }

                            throw new IllegalStateException(errorBuffer.getStringUTF8());
                        }
                    }

                    GLFW.glfwMakeContextCurrent(window);
                    final GLCapabilities caps = GL.createCapabilities();

                    synchronized (OpenGLUtils.class) {
                        openGL20 = caps.OpenGL20;
                        openGL21 = caps.OpenGL21;
                        openGL32 = caps.OpenGL32;
                        openGL40 = caps.OpenGL40;
                        openGL43 = caps.OpenGL43;
                        openGL44 = caps.OpenGL44;
                        openGL45 = caps.OpenGL45;
                        openGL46 = caps.OpenGL46;
                    }

                    GLFW.glfwDestroyWindow(window);
                    LOGGER.info("Detected OpenGL {}.{} support", version[0], version[1]);
                    break;
                }
            });

            testThread.start();
            testThread.join(); // Wait for test thread to finish before we continue
        }
        catch (Throwable t) {
            LOGGER.error(t);
        }

        IS_INITIALIZED.set(true);
    }
}
