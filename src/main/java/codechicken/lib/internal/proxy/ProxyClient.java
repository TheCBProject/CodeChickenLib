package codechicken.lib.internal.proxy;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.configuration.ConfigTag;
import codechicken.lib.configuration.ConfigTagImpl;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.render.block.BlockRenderingRegistry;
import codechicken.lib.render.item.map.MapRenderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Created by covers1624 on 30/10/19.
 */
public class ProxyClient extends Proxy {

    public static boolean catchBlockRenderExceptions;
    public static boolean messagePlayerOnRenderExceptionCaught;

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        loadClientConfig();
        //OpenGLUtils.loadCaps();
        //        CustomParticleHandler.init();
        BlockRenderingRegistry.init();
        ModelBakery.init();
        CCRenderEventHandler.init();

        MinecraftForge.EVENT_BUS.register(new MapRenderRegistry());
        //        ClientCommandHandler.instance.registerCommand(new CCLClientCommand());
    }

    private void loadClientConfig() {
        ConfigTag tag;
        ConfigTag clientTag = CodeChickenLib.config.getTag("client");
        clientTag.deleteTag("block_renderer_dispatcher_misc");

        tag = clientTag.getTag("catchBlockRenderExceptions")//
                .setComment(//
                        "With this enabled, CCL will catch all exceptions thrown whilst rendering blocks.",//
                        "If an exception is caught, the block will not be rendered."//
                );
        catchBlockRenderExceptions = tag.setDefaultBoolean(true).getBoolean();
        tag = clientTag.getTag("catchItemRenderExceptions")//
                .setComment(//
                        "With this enabled, CCL will catch all exceptions thrown whilst rendering items.",//
                        "By default CCL will only enhance the crash report, but with 'attemptRecoveryOnItemRenderException' enabled",//
                        " CCL will attempt to recover after the exception."//
                );
        messagePlayerOnRenderExceptionCaught = tag.setDefaultBoolean(true).getBoolean();

        clientTag.save();
    }
}
