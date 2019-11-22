package codechicken.lib.texture;

/**
 * Used for easy persistent TextureAtlasSprite registration.
 *
 * See {@link SpriteRegistryHelper}
 * Created by covers1624 on 27/10/19.
 */
public interface IIconRegister {

    /**
     * Called to register your sprites to the AtlasRegistrar.
     * Can be called any number of times at runtime.
     *
     * @param registrar
     */
    void registerIcons(AtlasRegistrar registrar);
}
