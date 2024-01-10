package codechicken.lib.gui.modular.sprite;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Custom sprite resource loader that allows filtering of resources based on mod id.
 * <p>
 * Created by brandon3055 on 21/08/2023
 */
public class ModSpriteResourceLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter ATLAS_INFO_CONVERTER = new FileToIdConverter("atlases", ".json");
    private final List<SpriteSource> sources;
    private final String modid;

    private ModSpriteResourceLoader(List<SpriteSource> list, String modid) {
        this.sources = list;
        this.modid = modid;
    }

    public List<Supplier<SpriteContents>> list(ResourceManager arg) {
        final Map<ResourceLocation, SpriteSource.SpriteSupplier> map = new HashMap();
        SpriteSource.Output output = new SpriteSource.Output() {
            public void add(ResourceLocation location, SpriteSource.SpriteSupplier arg2) {
                if (location.getNamespace().equals(modid)) {
                    SpriteSource.SpriteSupplier spriteSupplier = map.put(location, arg2);
                    if (spriteSupplier != null) {
                        spriteSupplier.discard();
                    }
                }
            }

            public void removeAll(Predicate<ResourceLocation> predicate) {
                Iterator<Map.Entry<ResourceLocation, SpriteSource.SpriteSupplier>> iterator = map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<ResourceLocation, SpriteSource.SpriteSupplier> entry = iterator.next();
                    if (predicate.test(entry.getKey())) {
                        entry.getValue().discard();
                        iterator.remove();
                    }
                }

            }
        };
        this.sources.forEach((arg3) -> arg3.run(arg, output));

        ImmutableList.Builder<Supplier<SpriteContents>> builder = ImmutableList.builder();
        builder.add(MissingTextureAtlasSprite::create);
        builder.addAll(map.values());
        return builder.build();
    }

    public static ModSpriteResourceLoader load(ResourceManager arg, ResourceLocation arg2, String modid) {
        ResourceLocation resourceLocation = ATLAS_INFO_CONVERTER.idToFile(arg2);
        List<SpriteSource> list = new ArrayList<>();

        for (Resource resource : arg.getResourceStack(resourceLocation)) {
            try {
                BufferedReader bufferedReader = resource.openAsReader();

                try {
                    Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseReader(bufferedReader));
                    DataResult<List<SpriteSource>> var10001 = SpriteSources.FILE_CODEC.parse(dynamic);
                    Logger var10003 = LOGGER;
                    Objects.requireNonNull(var10003);
                    list.addAll(var10001.getOrThrow(false, var10003::error));
                } catch (Throwable var10) {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable var9) {
                            var10.addSuppressed(var9);
                        }
                    }

                    throw var10;
                }

                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception var11) {
                LOGGER.warn("Failed to parse atlas definition {} in pack {}", resourceLocation, resource.sourcePackId(), var11);
            }
        }

        return new ModSpriteResourceLoader(list, modid);
    }
}
