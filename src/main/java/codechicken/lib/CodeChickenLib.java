package codechicken.lib;

import codechicken.lib.config.ConfigCategory;
import codechicken.lib.config.ConfigFile;
import codechicken.lib.internal.command.CCLCommands;
import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.internal.proxy.Proxy;
import codechicken.lib.internal.proxy.ProxyClient;
import codechicken.lib.datagen.ConditionalIngredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.file.Paths;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod (CodeChickenLib.MOD_ID)
public class CodeChickenLib {

    public static final String MOD_ID = "codechickenlib";

    public static ConfigCategory config;

    public static Proxy proxy;

    public CodeChickenLib() {
        config = new ConfigFile(MOD_ID)
                .path(Paths.get("config/ccl.cfg"))
                .load();
        proxy = DistExecutor.safeRunForDist(() -> ProxyClient::new, () -> Proxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        CCLCommands.init();
        ConditionalIngredient.Serializer.init();
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);
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
}
