package codechicken.lib.texture;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;
import java.util.function.Consumer;

/**
 * Utilities for persistent TextureAtlasSprite registration.
 * <p>
 * Created by covers1624 on 27/10/19.
 */
public class SpriteRegistryHelper {

    public static final ResourceLocation TEXTURES = PlayerContainer.BLOCK_ATLAS;
    //    public static final ResourceLocation PARTICLE_TEXTURES = "textures/particle";
    //    public static final ResourceLocation MOB_EFFECT_TEXTURES = "textures/mob_effect";
    //    public static final ResourceLocation PAINTING_TEXTURES = "textures/painting";

    private final Multimap<ResourceLocation, IIconRegister> iconRegisters = HashMultimap.create();
    private final Map<ResourceLocation, AtlasRegistrarImpl> atlasRegistrars = new HashMap<>();

    public SpriteRegistryHelper() {
        this(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public SpriteRegistryHelper(IEventBus eventBus) {
        eventBus.register(this);
    }

    /**
     * Adds an IIconRegister for the given basePath.
     * The base path should be that returned by {@link AtlasTexture#location()}
     * Some known vanilla defaults are provided above.
     *
     * @param basePath     The base path for the Atlas.
     * @param iconRegister The IIconRegister.
     */
    public void addIIconRegister(ResourceLocation basePath, IIconRegister iconRegister) {
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
        AtlasRegistrarImpl registrar = atlasRegistrars.get(atlas.location());
        if (registrar == null) {
            registrar = new AtlasRegistrarImpl();
            atlasRegistrars.put(atlas.location(), registrar);
        }
        return registrar;
    }

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        AtlasTexture atlas = event.getMap();
        AtlasRegistrarImpl registrar = getRegistrar(atlas);
        iconRegisters.get(atlas.location()).forEach(e -> e.registerIcons(registrar));
        registrar.processPre(event::addSprite);
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public void onTextureStitchPostFirst(TextureStitchEvent.Post event) {
        AtlasTexture atlas = event.getMap();
        AtlasRegistrarImpl registrar = getRegistrar(atlas);
        registrar.processPost(atlas);
    }

    @SubscribeEvent
    public void onTextureStitchPost(TextureStitchEvent.Post event) {
        AtlasTexture atlas = event.getMap();
        AtlasRegistrarImpl registrar = getRegistrar(atlas);
        registrar.processPostFirst(atlas);
    }

    private static final class AtlasRegistrarImpl implements AtlasRegistrar {

        private final Multimap<ResourceLocation, Consumer<TextureAtlasSprite>> sprites = HashMultimap.create();
        private final List<Consumer<AtlasTexture>> postCallbacks = new ArrayList<>();
        private final Map<ResourceLocation, Consumer<ProceduralTexture>> proceduralTextures = new HashMap<>();

        @Override
        public void registerSprite(ResourceLocation loc, Consumer<TextureAtlasSprite> onReady) {
            sprites.put(loc, onReady);
        }

        @Override
        public void registerProceduralSprite(ResourceLocation loc, Consumer<ProceduralTexture> cycleFunc, Consumer<TextureAtlasSprite> onReady) {
            registerSprite(loc, onReady);
            proceduralTextures.put(loc, cycleFunc);
        }

        @Override
        public void postRegister(Consumer<AtlasTexture> func) {
            postCallbacks.add(func);
        }

        private void processPre(Consumer<ResourceLocation> register) {
            sprites.keySet().forEach(register);
        }

        private void processPostFirst(AtlasTexture atlas) {
            for (Map.Entry<ResourceLocation, Consumer<ProceduralTexture>> entry : proceduralTextures.entrySet()) {
                ResourceLocation name = entry.getKey();
                ProceduralTexture texture = new ProceduralTexture(atlas, atlas.getSprite(name), entry.getValue());
                atlas.animatedTextures.add(texture);
                atlas.texturesByName.put(name, texture);
            }
        }

        private void processPost(AtlasTexture atlas) {
            for (Map.Entry<ResourceLocation, Collection<Consumer<TextureAtlasSprite>>> entry : sprites.asMap().entrySet()) {
                TextureAtlasSprite sprite = atlas.getSprite(entry.getKey());
                entry.getValue().forEach(e -> e.accept(sprite));
            }
            for (Consumer<AtlasTexture> callback : postCallbacks) {
                callback.accept(atlas);
            }
        }
    }
}
