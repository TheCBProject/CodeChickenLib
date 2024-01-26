package codechicken.lib.gui.modular.sprite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 20/08/2023
 */
public class ModAtlasHolder implements PreparableReloadListener, AutoCloseable {
    private final TextureAtlas textureAtlas;
    private final ResourceLocation atlasLocation;
    private final ResourceLocation atlasInfoLocation;
    private final String modid;

    /**
     * Defines a mod texture atlas.
     * Must be registered as a resource reload listener via RegisterClientReloadListenersEvent
     * This is all that is needed to create a custom texture atlas.
     *
     * @param modid             The mod id of the mod registering this atlas.
     * @param atlasLocation     The texture atlas location. e.g. "textures/atlas/gui.png" (Will have the modid: prefix added automatically)
     * @param atlasInfoLocation The path to the atlas json file relative to modid:atlases/
     *                          e.g. "gui" will point to modid:atlases/gui.json
     */
    public ModAtlasHolder(String modid, String atlasLocation, String atlasInfoLocation) {
        this.atlasInfoLocation = new ResourceLocation(modid, atlasInfoLocation);
        this.atlasLocation = new ResourceLocation(modid, atlasLocation);
        this.textureAtlas = new TextureAtlas(this.atlasLocation);
        this.modid = modid;
        Minecraft.getInstance().getTextureManager().register(this.textureAtlas.location(), this.textureAtlas);
    }

    public ResourceLocation atlasLocation() {
        return atlasLocation;
    }

    public TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        return this.textureAtlas.getSprite(resourceLocation);
    }

    @Override
    public final @NotNull CompletableFuture<Void> reload(PreparationBarrier prepBarrier, ResourceManager resourceManager, ProfilerFiller profiler, ProfilerFiller profiler2, Executor executor, Executor executor2) {
        Objects.requireNonNull(prepBarrier);
        SpriteLoader spriteLoader = SpriteLoader.create(this.textureAtlas);
        return spriteLoader.loadAndStitch(new ModResourceManager(resourceManager, modid), this.atlasInfoLocation, 0, executor)
                .thenCompose(SpriteLoader.Preparations::waitForUpload)
                .thenCompose(prepBarrier::wait)
                .thenAcceptAsync((preparations) -> this.apply(preparations, profiler2), executor2);
    }

    private void apply(SpriteLoader.Preparations preparations, ProfilerFiller profilerFiller) {
        profilerFiller.startTick();
        profilerFiller.push("upload");
        this.textureAtlas.upload(preparations);
        profilerFiller.pop();
        profilerFiller.endTick();
    }

    @Override
    public void close() {
        this.textureAtlas.clearTextureData();
    }

    public static class ModResourceManager implements ResourceManager {
        private final ResourceManager wrapped;
        private final String modid;

        public ModResourceManager(ResourceManager wrapped, String modid) {
            this.wrapped = wrapped;
            this.modid = modid;
        }

        @Override
        public Map<ResourceLocation, Resource> listResources(String pPath, Predicate<ResourceLocation> pFilter) {
            return wrapped.listResources(pPath, pFilter.and(e -> e.getNamespace().equals(modid)));
        }

        //@formatter:off
        @Override public Set<String> getNamespaces() { return wrapped.getNamespaces(); }
        @Override public List<Resource> getResourceStack(ResourceLocation pLocation) { return wrapped.getResourceStack(pLocation); }
        @Override public Map<ResourceLocation, List<Resource>> listResourceStacks(String pPath, Predicate<ResourceLocation> pFilter) { return wrapped.listResourceStacks(pPath, pFilter); }
        @Override public Stream<PackResources> listPacks() { return wrapped.listPacks(); }
        @Override public Optional<Resource> getResource(ResourceLocation pLocation) { return wrapped.getResource(pLocation); }
        //@formatter:on
    }
}
