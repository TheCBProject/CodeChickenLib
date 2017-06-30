package codechicken.lib;

import codechicken.lib.asm.proxy.ProxyInjector;
import codechicken.lib.fingerprint.FingerprintChecker;
import codechicken.lib.internal.proxy.Proxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.File;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod (modid = CodeChickenLib.MOD_ID, name = CodeChickenLib.MOD_NAME, acceptedMinecraftVersions = CodeChickenLib.MC_VERSION_DEP, certificateFingerprint = "f1850c39b2516232a2108a7bd84d1cb5df93b261", updateJSON = CodeChickenLib.UPDATE_URL)
public class CodeChickenLib {

    public static final String MOD_ID = "codechickenlib";
    public static final String MOD_NAME = "CodeChicken Lib";
    public static final String MOD_VERSION = "${mod_version}";
    public static final String MOD_VERSION_DEP = "required-after:codechickenlib@[" + MOD_VERSION + ",);";
    public static final String MC_VERSION = "1.12";
    public static final String MC_VERSION_DEP = "[" + MC_VERSION + "]";
    static final String UPDATE_URL = "http://chickenbones.net/Files/notification/version.php?query=forge&version=" + MC_VERSION + "&file=CodeChickenLib";

    public static final File MINECRAFT_DIR = (File) FMLInjectionData.data()[6];

    @SidedProxy (clientSide = "codechicken.lib.internal.proxy.ProxyClient", serverSide = "codechicken.lib.internal.proxy.Proxy")
    public static Proxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FingerprintChecker.runFingerprintChecks();
        ProxyInjector.runInjector(event.getAsmData());
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public void onServerStartingEvent(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
