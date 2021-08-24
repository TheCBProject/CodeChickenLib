package codechicken.lib;

import codechicken.lib.annotation.ProxyInjector;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.configuration.ConfigFile;
import codechicken.lib.internal.proxy.Proxy;
import codechicken.lib.reflect.ObfMapping;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod (modid = CodeChickenLib.MOD_ID, name = CodeChickenLib.MOD_NAME, dependencies = "required-after:forge@[14.23.4.2854,)", acceptedMinecraftVersions = CodeChickenLib.MC_VERSION_DEP, certificateFingerprint = "f1850c39b2516232a2108a7bd84d1cb5df93b261", updateJSON = CodeChickenLib.UPDATE_URL)
public class CodeChickenLib {

    public static final String MOD_ID = "codechickenlib";
    public static final String MOD_NAME = "CodeChicken Lib";
    public static final String MOD_VERSION = "${mod_version}";
    public static final String MOD_VERSION_DEP = "required-after:codechickenlib@[" + MOD_VERSION + ",);";
    public static final String MC_VERSION = "1.12";
    public static final String MC_VERSION_DEP = "[" + MC_VERSION + "]";
    static final String UPDATE_URL = "http://chickenbones.net/Files/notification/version.php?query=forge&version=" + MC_VERSION + "&file=CodeChickenLib";

    public static final File MINECRAFT_DIR = (File) FMLInjectionData.data()[6];

    public static ConfigFile config;

    @SidedProxy (clientSide = "codechicken.lib.internal.proxy.ProxyClient", serverSide = "codechicken.lib.internal.proxy.Proxy")
    public static Proxy proxy;

    public CodeChickenLib() {
        ObfMapping.init();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ProxyInjector.runInjector(event.getAsmData());
        config = new ConfigFile(event.getSuggestedConfigurationFile());
        proxy.loadConfig();
        proxy.preInit();
        initOreDict();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @EventHandler
    public void onServerStartingEvent(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    private static void initOreDict() {
        for (EnumColour c : EnumColour.values()) {
            OreDictionary.registerOre(c.getWoolOreName(), new ItemStack(Blocks.WOOL, 1, c.getWoolMeta()));
        }
    }
}
