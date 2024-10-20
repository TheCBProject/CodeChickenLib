package codechicken.lib.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.file.Path;

/**
 * Created by covers1624 on 22/10/2016.
 */
public class ServerUtils {

    public static Path getSaveDirectory() {
        return getSaveDirectory(Level.OVERWORLD);
    }

    public static Path getSaveDirectory(ResourceKey<Level> dimension) {
        return ServerLifecycleHooks.getCurrentServer().storageSource.getDimensionPath(dimension);
    }
}
