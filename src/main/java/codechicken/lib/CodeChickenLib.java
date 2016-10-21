package codechicken.lib;

import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.util.FuelUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod(modid = CodeChickenLib.MOD_ID, name = CodeChickenLib.MOD_NAME, acceptedMinecraftVersions = CodeChickenLib.mcVersion)
public class CodeChickenLib {

    public static final String MOD_ID = "CodeChickenLib";
    public static final String MOD_NAME = "CodeChicken Lib";
    public static final String version = "${mod_version}";
    public static final String mcVersion = "[1.10.2]";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerFuelHandler(new FuelUtils());
        if (event.getSide().equals(Side.CLIENT)){
            CCRenderEventHandler.init();
        }
    }

}
