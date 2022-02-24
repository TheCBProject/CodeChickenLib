package codechicken.lib.util;

import codechicken.lib.render.CCRenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.world.level.Level;

public class ClientUtils extends CommonUtils {

    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }

    public static boolean inWorld() {
        return Minecraft.getInstance().getConnection() != null;
    }

    public static float getRenderFrame() {
        return CCRenderEventHandler.renderFrame;
    }

    public static double getRenderTime() {
        return CCRenderEventHandler.renderTime + getRenderFrame();
    }

    public static String getServerIP() {
        try {
            Connection networkManager = Minecraft.getInstance().getConnection().getConnection();
            String s = networkManager.getRemoteAddress().toString();
            s = s.substring(s.indexOf("/") + 1);
            return s;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
