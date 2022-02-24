package codechicken.lib.util;

import net.minecraft.world.level.Level;

public class CommonUtils {

    public static boolean isClientWorld(Level world) {
        return world.isClientSide;
    }

    public static boolean isServerWorld(Level world) {
        return !world.isClientSide;
    }
}
