package codechicken.lib.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Extend this if you want to implement {@link IProceduralTextureCallback}.
 * Facilitates easy access to the raw texture data for procedural generation.
 *
 * @author KitsuneAlex
 * @since 12/04/2021
 */
public abstract class AbstractProceduralTexture implements IIconRegister, IProceduralTextureCallback {

    protected final ResourceLocation location;
    protected TextureAtlasSprite texture;
    protected int width;
    protected int height;

    /**
     * Creates a new procedural texture using the given placeholder location and {@link SpriteRegistryHelper}.
     *
     * @param location The location of the placeholder texture.
     * @param registryHelper The {@link SpriteRegistryHelper} instance to register with.
     */
    public AbstractProceduralTexture(@Nonnull ResourceLocation location, @Nonnull SpriteRegistryHelper registryHelper) {
        this.location = location;
        registryHelper.addIIconRegister(this);
    }

    /**
     * Called right after the delegate texture of this procedural
     * texture has been registered and retrieved.<br>
     * Used for setting up internal data of the procedural texture.
     */
    protected abstract void init();

    @Override
    public void registerIcons(AtlasRegistrar registrar) {
        registrar.registerSprite(location, texture -> {
            this.texture = texture;
            width = texture.getWidth();
            height = texture.getHeight();
            init();
        });
    }

    @Nonnull
    public ResourceLocation getLocation() {
        return location;
    }

    @Nullable
    public TextureAtlasSprite getTexture() {
        return texture;
    }

}
