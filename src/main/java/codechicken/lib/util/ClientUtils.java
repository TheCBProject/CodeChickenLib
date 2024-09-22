package codechicken.lib.util;

import codechicken.lib.render.CCRenderEventHandler;
import net.minecraft.client.Minecraft;

public class ClientUtils {

    public static boolean inWorld() {
        return Minecraft.getInstance().getConnection() != null;
    }

    public static float getRenderFrame() {
        return CCRenderEventHandler.renderFrame;
    }

    public static double getRenderTime() {
        return CCRenderEventHandler.renderTime + getRenderFrame();
    }
}
