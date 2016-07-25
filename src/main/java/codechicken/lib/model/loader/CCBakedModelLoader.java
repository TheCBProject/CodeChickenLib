package codechicken.lib.model.loader;

import codechicken.lib.model.loader.IBakedModelLoader.IModKeyProvider;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.render.TextureUtils.IIconRegister;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 7/25/2016.
 * This is a flexible IBakedModel loader, It runs off IBakedModelLoaders and IModKeyProviders.
 * When getModel(ItemStack) is called it will first try and obtain a IModKeyProvider, If no KeyProvider is found then it returns a null model.
 * It then calls IModKeyProvider.createKey(ItemStack) if this returns null a null model is returned.
 * It will then check the cache for a key -> IBakedModel reference, if the cache contains a model it is returned otherwise it bakes a model using IBakedModelLoader.
 * <p/>
 * To use this call registerLoader(IBakedModelLoader) from PRE INIT!
 */
public class CCBakedModelLoader implements IIconRegister, IResourceManagerReloadListener {

    public static final CCBakedModelLoader INSTANCE = new CCBakedModelLoader();
    private static final Map<String, IBakedModel> modelCache = new HashMap<String, IBakedModel>();
    private static final Map<String, IModKeyProvider> modKeyProviders = new HashMap<String, IModKeyProvider>();
    private static final Map<IModKeyProvider, IBakedModelLoader> loaders = new HashMap<IModKeyProvider, IBakedModelLoader>();

    static {
        TextureUtils.addIconRegister(INSTANCE);
        TextureUtils.registerReloadListener(INSTANCE);
    }

    /**
     * Registers a IModKeyProvider and IBakedModelLoader.
     *
     * @param loader Registers a loader.
     */
    public static void registerLoader(IBakedModelLoader loader) {
        if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
            throw new RuntimeException("Unable to register IBakedModelLoader after Pre Initialization! Please register as the first thing you do in Pre Init!");
        }
        if (loaders.containsValue(loader)) {
            throw new RuntimeException("Unable to register IBakedModelLoader as it has already been registered!");
        }

        IModKeyProvider provider = loader.createKeyProvider();
        loaders.put(provider, loader);
        modKeyProviders.put(provider.getMod(), provider);
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        for (ResourceLocation location : getTextures()) {
            textureMap.registerSprite(location);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        modelCache.clear();
    }

    private static Collection<ResourceLocation> getTextures() {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for (IBakedModelLoader loader : loaders.values()) {
            loader.addTextures(builder);
        }
        return builder.build();
    }

    /**
     * Call this from ItemOverrideList.handleItemState()
     *
     * @param stack The ItemStack to attempt to obtain a model for.
     * @return The baked model if it can be found/generated, else null.
     */
    public static IBakedModel getModel(ItemStack stack) {
        if (stack.getItem() == null || stack.getItem().getRegistryName() == null) {
            return null;
        }
        String resourceDomain = stack.getItem().getRegistryName().getResourceDomain();
        IModKeyProvider provider = modKeyProviders.get(resourceDomain);
        if (provider == null) {
            FMLLog.bigWarning("Unable to find IModKeyProvider for domain %s!", resourceDomain);
            return null;
        }
        String key = provider.createKey(stack);
        if (key == null) {
            return null;
        }
        if (!modelCache.containsKey(resourceDomain + ":" + key)) {
            IBakedModel model = generateModel(provider, stack);
            if (model == null) {
                return null;
            }
            modelCache.put(resourceDomain + ":" + key, model);
        }
        return modelCache.get(resourceDomain + ":" + key);
    }

    public static IBakedModel generateModel(IModKeyProvider provider, ItemStack stack) {
        String key = provider.createKey(stack);
        IBakedModelLoader loader = loaders.get(provider);
        return loader.bakeModel(key);
    }

}
