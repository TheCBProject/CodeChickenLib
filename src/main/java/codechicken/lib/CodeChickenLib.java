package codechicken.lib;

import codechicken.lib.config.ConfigCategory;
import codechicken.lib.config.ConfigFile;
import codechicken.lib.datagen.ConditionalIngredient;
import codechicken.lib.internal.ClientInit;
import codechicken.lib.internal.command.CCLCommands;
import codechicken.lib.internal.network.CCLNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Paths;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod (CodeChickenLib.MOD_ID)
public class CodeChickenLib {

    public static final String MOD_ID = "codechickenlib";

    public static ConfigCategory config;

    public CodeChickenLib() {
        config = new ConfigFile(MOD_ID)
                .path(Paths.get("config/ccl.cfg"))
                .load();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientInit::init);

        CCLCommands.init();
        CCLNetwork.init();
        ConditionalIngredient.Serializer.init();
    }
}
