package codechicken.lib.util;

import net.minecraft.world.World;

public class CommonUtils {

    public static boolean isClientWorld(World world) {
        return world.isRemote;
    }

    public static boolean isServerWorld(World world) {
        return !world.isRemote;
    }

    //    public static boolean isClient() {
    //        return FMLCommonHandler.instance().getSide().isClient();
    //    }

    //    public static File getSaveLocation(World world) {
    //        File base = DimensionManager.getCurrentSaveRootDirectory();
    //        return world.provider.getDimension() == 0 ? base : new File(base, world.provider.getSaveFolder());
    //    }
    //
    //    public static File getSaveLocation(int dim) {
    //        return getSaveLocation(DimensionManager.getWorld(dim));
    //    }

    //    public static String getWorldName(World world) {
    //        return world.getWorldInfo().getWorldName();
    //    }
    //
    //    public static int getDimension(World world) {
    //        return world.provider.getDimension();
    //    }
    //
    //    public static File getMinecraftDir() {
    //        return (File) FMLInjectionData.data()[6];
    //    }
}
