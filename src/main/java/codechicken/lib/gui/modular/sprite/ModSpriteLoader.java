package codechicken.lib.gui.modular.sprite;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Custom sprite loader that allows filtering of resources based on mod id.
 * <p>
 * Created by brandon3055 on 21/08/2023
 */
public class ModSpriteLoader extends SpriteLoader {
    private final String modid;

    public ModSpriteLoader(ResourceLocation resourceLocation, int i, int j, int k, String modid) {
        super(resourceLocation, i, j, k);
        this.modid = modid;
    }

    public static SpriteLoader create(TextureAtlas textureAtlas, String modid) {
        return new ModSpriteLoader(textureAtlas.location(), textureAtlas.maxSupportedTextureSize(), textureAtlas.getWidth(), textureAtlas.getHeight(), modid);
    }

    @Override
    public CompletableFuture<Preparations> loadAndStitch(ResourceManager resourceManager, ResourceLocation resourceLocation, int i, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            return ModSpriteResourceLoader.load(resourceManager, resourceLocation, modid).list(resourceManager);
        }, executor).thenCompose((list) -> {
            return runSpriteSuppliers(list, executor);
        }).thenApply((list) -> {
            return this.stitch(list, i, executor);
        });
    }
}
