package codechicken.lib.texture;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utilities for persistent TextureAtlasSprite registration.
 *
 * Created by covers1624 on 27/10/19.
 */
public class SpriteRegistryHelper {

    public static final String TEXTURES = "textures";
    public static final String PARTICLE_TEXTURES = "textures/particle";
    public static final String MOB_EFFECT_TEXTURES = "textures/mob_effect";
    public static final String PAINTING_TEXTURES = "textures/painting";

    private final Multimap<String, IIconRegister> iconRegisters = HashMultimap.create();
    private final Map<String, AtlasRegistrarImpl> atlasRegistrars = new HashMap<>();

    public SpriteRegistryHelper() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    /**
     * Adds an IIconRegister for the given basePath.
     * The base path should be that returned by {@link AtlasTexture#getBasePath()}
     * Some known vanilla defaults are provided above.
     *
     * @param basePath     The base path for the Atlas.
     * @param iconRegister The IIconRegister.
     */
    public void addIIconRegister(String basePath, IIconRegister iconRegister) {
        iconRegisters.put(basePath, iconRegister);
    }

    /**
     * Adds an IIconRegister for the {@link #TEXTURES} Atlas.
     *
     * @param iconRegister The IIconRegister.
     */
    public void addIIconRegister(IIconRegister iconRegister) {
        addIIconRegister(TEXTURES, iconRegister);
    }

    //######### INTERNAL
    private AtlasRegistrarImpl getRegistrar(AtlasTexture atlas) {
        AtlasRegistrarImpl registrar = atlasRegistrars.get(atlas.getBasePath());
        if (registrar == null) {
            registrar = new AtlasRegistrarImpl();
            atlasRegistrars.put(atlas.getBasePath(), registrar);
        }
        return registrar;
    }

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        AtlasTexture atlas = event.getMap();
        AtlasRegistrarImpl registrar = getRegistrar(atlas);
        iconRegisters.get(atlas.getBasePath()).forEach(e -> e.registerIcons(registrar));
        registrar.processPre(event::addSprite);
    }

    @SubscribeEvent
    public void onTextureStitchPost(TextureStitchEvent.Post event) {
        AtlasTexture atlas = event.getMap();
        AtlasRegistrarImpl registrar = getRegistrar(atlas);
        registrar.processPost(atlas);
    }

    private static final class AtlasRegistrarImpl implements AtlasRegistrar {

        private Multimap<ResourceLocation, Consumer<TextureAtlasSprite>> sprites = HashMultimap.create();

        @Override
        public void registerSprite(ResourceLocation loc, Consumer<TextureAtlasSprite> onReady) {
            sprites.put(loc, onReady);
        }

        private void processPre(Consumer<ResourceLocation> register) {
            sprites.keySet().forEach(register);
        }

        private void processPost(AtlasTexture atlas) {
            for (Map.Entry<ResourceLocation, Collection<Consumer<TextureAtlasSprite>>> entry : sprites.asMap().entrySet()) {
                TextureAtlasSprite sprite = atlas.getSprite(entry.getKey());
                entry.getValue().forEach(e -> e.accept(sprite));
            }
        }
    }
}
