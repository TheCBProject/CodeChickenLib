package codechicken.lib.internal;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.config.ConfigCategory;
import codechicken.lib.config.ConfigSyncManager;
import codechicken.lib.gui.modular.lib.CursorHelper;
import codechicken.lib.gui.modular.sprite.CCGuiTextures;
import codechicken.lib.model.CompositeItemModel;
import codechicken.lib.model.ClassModelLoader;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.render.block.BlockRenderingRegistry;
import net.covers1624.quack.util.CrashLock;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created by covers1624 on 8/9/23.
 */
public class ClientInit {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static boolean catchBlockRenderExceptions;
    public static boolean messagePlayerOnRenderExceptionCaught;

    public static void init() {
        LOCK.lock();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        loadClientConfig();
        CCRenderEventHandler.init();

        MinecraftForge.EVENT_BUS.addListener(ClientInit::onClientDisconnected);

        bus.addListener(ClientInit::onClientSetup);
        bus.addListener(ClientInit::onRegisterGeometryLoaders);
        bus.addListener(ClientInit::onResourceReload);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        BlockRenderingRegistry.init();
    }

    private static void loadClientConfig() {
        ConfigCategory clientTag = CodeChickenLib.config.getCategory("client");
        clientTag.delete("block_renderer_dispatcher_misc");
        clientTag.delete("catchItemRenderExceptions");
        clientTag.delete("attemptRecoveryOnItemRenderException");

        catchBlockRenderExceptions = clientTag.getValue("catchBlockRenderExceptions")
                .setComment(
                        "With this enabled, CCL will catch all exceptions thrown whilst rendering blocks.",
                        "If an exception is caught, the block will not be rendered."
                )
                .setDefaultBoolean(true)
                .getBoolean();
        messagePlayerOnRenderExceptionCaught = clientTag.getValue("messagePlayerOnRenderExceptionCaught")
                .setComment(
                        "With this enabled, CCL will message the player upon an exception from rendering blocks or items.",
                        "Messages are Rate-Limited to one per 5 seconds in the event that the exception continues."
                )
                .setDefaultBoolean(true)
                .getBoolean();

        clientTag.save();
    }

    private static void onClientDisconnected(ClientPlayerNetworkEvent.LoggingOut event) {
        ConfigSyncManager.onClientDisconnected();
    }

    private static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("item_composite", new CompositeItemModel());
        event.register("class", new ClassModelLoader());
    }

    public static void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(CCGuiTextures.getAtlasHolder());
        CursorHelper.onResourceReload();
    }
}
