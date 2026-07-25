package codechicken.lib.internal;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.config.ConfigCategory;
import codechicken.lib.gui.modular.lib.CursorHelper;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.render.CCRenderPipelines;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 8/9/23.
 */
public class ClientInit {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static void init(IEventBus modBus) {
        LOCK.lock();

        loadClientConfig();
        CCRenderEventHandler.init();
        HighlightHandler.init();

        modBus.addListener(ClientInit::onResourceReload);
        modBus.addListener(ClientInit::onRegisterPipelines);
    }

    private static void loadClientConfig() {
        ConfigCategory clientTag = CodeChickenLib.config.getCategory("client");
        clientTag.delete("block_renderer_dispatcher_misc");
        clientTag.delete("catchItemRenderExceptions");
        clientTag.delete("attemptRecoveryOnItemRenderException");
        clientTag.delete("catchBlockRenderExceptions");
        clientTag.delete("messagePlayerOnRenderExceptionCaught");

        clientTag.save();
    }

    private static void onResourceReload(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "cursor_helper"), (ResourceManagerReloadListener) e -> CursorHelper.onResourceReload());
    }

    private static void onRegisterPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(CCRenderPipelines.SOLID_TRIANGLES);
        event.registerPipeline(CCRenderPipelines.POSITION_COLOR);
        event.registerPipeline(CCRenderPipelines.POSITION_COLOR_NO_DEPTH);
    }
}
