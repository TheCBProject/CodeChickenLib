package codechicken.lib.gui.modular.sprite;

import codechicken.lib.CodeChickenLib;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Gui texture handler implementation.
 * This sets up a custom atlas that will be populated with all textures in "modid:textures/gui/"
 * To use your own textures you can just create a carbon copy of this class with that uses your own modid.
 * <p>
 * Created by brandon3055 on 21/10/2023
 */
// TODO 1.20.4, use GuiTextures instance.
public class CCGuiTextures {
    private static final ModAtlasHolder ATLAS_HOLDER = new ModAtlasHolder(CodeChickenLib.MOD_ID, "textures/atlas/gui.png", "gui");
    private static final Map<String, Material> MATERIAL_CACHE = new HashMap<>();

    /**
     * The returned AtlasLoader needs to be registered as a resource reload listener using the appropriate NeoForge / Fabric event.
     */
    public static ModAtlasHolder getAtlasHolder() {
        return ATLAS_HOLDER;
    }

    /**
     * Returns a cached Material for the specified gui texture.
     * Warning: Do not use this if you intend to use the material with multiple render types.
     * The material will cache the first render type it is used with.
     * Instead use {@link #getUncached(String)}
     *
     * @param texture The texture path relative to "modid:gui/"
     */
    public static Material get(String texture) {
        return MATERIAL_CACHE.computeIfAbsent(CodeChickenLib.MOD_ID + ":" + texture, e -> getUncached(texture));
    }

    public static Material get(Supplier<String> texture) {
        return get(texture.get());
    }

    public static Supplier<Material> getter(Supplier<String> texture) {
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
    public static Material getUncached(String texture) {
        return new Material(ATLAS_HOLDER.atlasLocation(), new ResourceLocation(CodeChickenLib.MOD_ID, "gui/" + texture), ATLAS_HOLDER::getSprite);
    }
}
