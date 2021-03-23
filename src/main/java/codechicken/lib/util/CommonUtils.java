package codechicken.lib.util;

import net.minecraft.world.World;

public class CommonUtils {

    public static boolean isClientWorld(World world) {
        return world.isClientSide;
    }

    public static boolean isServerWorld(World world) {
        return !world.isClientSide;
    }
}
