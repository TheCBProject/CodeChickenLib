package codechicken.lib.gui.modular.sprite;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static codechicken.lib.CodeChickenLib.MOD_ID;
import static java.util.Objects.requireNonNull;

/**
 * Gui texture handler implementation.
 * This sets up a custom atlas that will be populated with all textures in "modid:textures/gui/"
 * <p>
 * Created by brandon3055 on 21/10/2023
 */
public class GuiTextures {

    public static final GuiTextures CCL = new GuiTextures(MOD_ID);

    private final String modId;
    private final @Nullable ModAtlasHolder atlasHolder;
    private final Map<String, Material> materialCache = new HashMap<>();

    public GuiTextures(String modId) {
        this.modId = modId;
        if (DatagenModLoader.isRunningDataGen()) {
            atlasHolder = null;
        } else {
            atlasHolder = new ModAtlasHolder(modId, "textures/atlas/gui.png", "gui");
        }
    }

    /**
     * Called to initialize the {@link GuiTextures} helper
     *
     * @param modBus Your mod event bus.
     */
    public void init(IEventBus modBus) {
        modBus.addListener(this::onRegisterReloadListeners);
    }

    private void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        getAtlasHolder().init();
        event.registerReloadListener(getAtlasHolder());
    }

    /**
     * @return The underlying {@link ModAtlasHolder} instance.
     */
    public ModAtlasHolder getAtlasHolder() {
        return requireNonNull(atlasHolder, "AtlasHolder not available when datagen is running.");
    }

    /**
     * Returns a cached Material for the specified gui texture.
     * Warning: Do not use this if you intend to use the material with multiple render types.
     * The material will cache the first render type it is used with.
     * Instead use {@link #getUncached(String)}
     *
     * @param texture The texture path relative to "modid:gui/"
     */
    public Material get(String texture) {
        return materialCache.computeIfAbsent(modId + ":" + texture, e -> getUncached(texture));
    }

    public Material get(Supplier<String> texture) {
        return get(texture.get());
    }

    public Supplier<Material> getter(Supplier<String> texture) {
        return () -> get(texture.get());
    }

    /**
     * Use this to retrieve a new uncached material for the specified gui texture.
     * Feel free to hold onto the returned material.
     * Storing it somewhere is more efficient than recreating it every render frame.
     *
     * @param texture The texture path relative to "modid:gui/"
     * @return A new Material for the specified gui texture.
     */
    public Material getUncached(String texture) {
        return new Material(getAtlasHolder().atlasLocation(), ResourceLocation.fromNamespaceAndPath(modId, "gui/" + texture), getAtlasHolder()::getSprite);
    }
}
