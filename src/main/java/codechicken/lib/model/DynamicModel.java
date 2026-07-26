package codechicken.lib.model;

import net.covers1624.quack.util.CrashLock;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

/**
 * Created by covers1624 on 7/26/26.
 */
public abstract class DynamicModel {

    private final CrashLock LOCK = new CrashLock("Already Initialized.");

    protected Identifier identifier;

    public DynamicModel(Identifier identifier) {
        this.identifier = identifier;
    }

    public void register(IEventBus modBus) {
        LOCK.lock();

        modBus.addListener(AddClientReloadListenersEvent.class, event -> event.addListener(identifier, new ReloadListener()));
    }

    /**
     * Called to reload your model.
     *
     * @param resourceManager The resource manager.
     */
    protected abstract void reload(ResourceManager resourceManager);

    protected void clear() {
    }

    private class ReloadListener implements ResourceManagerReloadListener {

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            clear();
            DynamicModel.this.reload(resourceManager);
        }
    }
}
