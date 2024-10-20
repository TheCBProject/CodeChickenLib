package codechicken.lib.internal;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.config.ConfigCategory;
import codechicken.lib.config.ConfigSyncManager;
import codechicken.lib.gui.modular.lib.CursorHelper;
import codechicken.lib.gui.modular.sprite.GuiTextures;
import codechicken.lib.model.ClassModelLoader;
import codechicken.lib.model.CompositeItemModel;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.render.block.BlockRenderingRegistry;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 8/9/23.
 */
public class ClientInit {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static boolean catchBlockRenderExceptions;
    public static boolean messagePlayerOnRenderExceptionCaught;

    public static void init(IEventBus modBus) {
        LOCK.lock();

        loadClientConfig();
        CCRenderEventHandler.init();
        HighlightHandler.init();
        ExceptionMessageEventHandler.init();

        GuiTextures.CCL.init(modBus);

        modBus.addListener(ClientInit::onClientSetup);
        modBus.addListener(ClientInit::onRegisterGeometryLoaders);
        modBus.addListener(ClientInit::onResourceReload);
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

    private static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_composite"), new CompositeItemModel());
        event.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "class"), new ClassModelLoader());
    }

    private static void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) e -> CursorHelper.onResourceReload());
    }
}
