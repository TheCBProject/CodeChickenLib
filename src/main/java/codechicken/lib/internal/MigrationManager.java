package codechicken.lib.internal;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 2/03/2017.
 */
public class MigrationManager {

    private static final Map<String, String> migrationHandlers = new HashMap<>();

    public static void registerMigrationHandler(String oldModID, String newModID) {
        migrationHandlers.put(oldModID, newModID);
    }

    public static void handleMissingMappings(FMLMissingMappingsEvent event) {
        for (MissingMapping mapping : event.getAll()) {
            ResourceLocation location = new ResourceLocation(mapping.name);
            if (migrationHandlers.containsKey(location.getResourceDomain())) {
                switch (mapping.type) {

                    case BLOCK:
                        mapping.remap(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(migrationHandlers.get(location.getResourceDomain()), location.getResourcePath())));
                        break;
                    case ITEM:
                        mapping.remap(ForgeRegistries.ITEMS.getValue(new ResourceLocation(migrationHandlers.get(location.getResourceDomain()), location.getResourcePath())));
                        break;
                }
            }
        }
    }

}
