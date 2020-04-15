package codechicken.lib;

import codechicken.lib.configuration.ConfigFile;
import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.internal.proxy.Proxy;
import codechicken.lib.internal.proxy.ProxyClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.File;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod (CodeChickenLib.MOD_ID)
public class CodeChickenLib {

    public static final String MOD_ID = "codechickenlib";

    public static ConfigFile config;

    public static Proxy proxy;

    public CodeChickenLib() {
        proxy = DistExecutor.runForDist(() -> ProxyClient::new, () -> Proxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);
        config = new ConfigFile(new File("config/ccl.cfg"));//TODO, Investigate forge config.
        CCLNetwork.init();
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        proxy.clientSetup(event);
    }

    @SubscribeEvent
    public void onServerSetup(FMLDedicatedServerSetupEvent event) {
        proxy.serverSetup(event);

    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }

}
