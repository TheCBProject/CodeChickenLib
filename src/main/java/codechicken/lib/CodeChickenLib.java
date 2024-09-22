package codechicken.lib;

import codechicken.lib.config.ConfigCategory;
import codechicken.lib.config.ConfigFile;
import codechicken.lib.config.ConfigSyncManager;
import codechicken.lib.internal.ClientInit;
import codechicken.lib.internal.DataGenerators;
import codechicken.lib.internal.TileChunkLoadHook;
import codechicken.lib.internal.command.CCLCommands;
import codechicken.lib.internal.network.CCLNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLEnvironment;

import java.nio.file.Paths;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod (CodeChickenLib.MOD_ID)
public class CodeChickenLib {

    static {
        if (Boolean.getBoolean("ccl.noModUpdateChecking")) {
            FMLConfig.updateConfig(FMLConfig.ConfigValue.VERSION_CHECK, false);
        }
    }

    public static final String MOD_ID = "codechickenlib";

    public static ConfigCategory config;

    public CodeChickenLib(IEventBus modBus) {
        config = new ConfigFile(MOD_ID)
                .path(Paths.get("config/ccl.cfg"))
                .load();
        if (FMLEnvironment.dist.isClient()) {
            ClientInit.init(modBus);
        }

        ConfigSyncManager.init(modBus);

        CCLCommands.init();
        CCLNetwork.init(modBus);
        TileChunkLoadHook.init();
//        ConditionalIngredient.Serializer.init();
        DataGenerators.init(modBus);
    }
}
