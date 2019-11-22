package codechicken.lib.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 27/10/19.
 */
public interface AtlasRegistrar {

    /**
     * Called to register a sprite and a callback for when the TextureAtlasSprite has been loaded.
     *
     * @param loc     The sprite's ResourceLocation.
     * @param onReady The callback for the sprite being ready.
     */
    void registerSprite(ResourceLocation loc, Consumer<TextureAtlasSprite> onReady);

    /**
     * Same as above, just takes a String for the ResourceLocation instead.
     *
     * @param loc     The sprite's ResourceLocation.
     * @param onReady The callback for the sprite being ready.
     */
    default void registerSprite(String loc, Consumer<TextureAtlasSprite> onReady) {
        registerSprite(new ResourceLocation(loc), onReady);
    }

    /**
     * Same as above, ignores callback.
     * May be useful in _some_ cases.
     *
     * @param loc The sprite's ResourceLocation.
     */
    default void registerSprite(ResourceLocation loc) {
        registerSprite(loc, e -> {
        });
    }
}
