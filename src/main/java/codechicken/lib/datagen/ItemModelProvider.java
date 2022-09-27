package codechicken.lib.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 15/10/20.
 */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected static final ModelFile.UncheckedModelFile GENERATED = new ModelFile.UncheckedModelFile("item/generated");
    protected static final ModelFile.UncheckedModelFile HANDHELD = new ModelFile.UncheckedModelFile("item/handheld");

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, WrappedItemModelBuilder::new, existingFileHelper);
    }

    //region Location helpers
    protected String name(ItemLike item) {
        return item.asItem().getRegistryName().getPath();
    }

    protected ResourceLocation itemTexture(ItemLike item) {
        return itemTexture(item, "");
    }

    protected ResourceLocation itemTexture(Supplier<? extends Item> item) {
        return itemTexture(item, "");
    }

    protected ResourceLocation itemTexture(ItemLike item, String folder) {
        String f = "";
        if (!StringUtils.isEmpty(folder)) {
            f = StringUtils.appendIfMissing(folder, "/");
        }
        return modLoc("item/" + f + name(item));
    }

    protected ResourceLocation itemTexture(Supplier<? extends Item> item, String folder) {
        return itemTexture(item.get(), folder);
    }

    protected ResourceLocation blockTexture(Block block) {
        return itemTexture(block, "");
    }

    protected ResourceLocation blockTexture(Supplier<? extends Block> block) {
        return blockTexture(block, "");
    }

    protected ResourceLocation blockTexture(Block block, String folder) {
        String f = "";
        if (!StringUtils.isEmpty(folder)) {
            f = StringUtils.appendIfMissing(folder, "/");
        }
        return modLoc("block/" + f + name(block));
    }

    protected ResourceLocation blockTexture(Supplier<? extends Block> block, String folder) {
        return blockTexture(block.get(), folder);
    }
    //endregion

    //region Builder helpers.
    protected ItemModelBuilder getBuilder(ItemLike item) {
        return getBuilder(name(item));
    }

    protected ItemModelBuilder getBuilder(Supplier<? extends Item> item) {
        return getBuilder(name(item.get()));
    }
    //endregion

    //region Simple builder
    protected SimpleItemModelBuilder getSimple(ItemLike item) {
        WrappedItemModelBuilder builder = (WrappedItemModelBuilder) getBuilder(item);
        if (builder.simpleBuilder == null) {
            builder.simpleBuilder = new SimpleItemModelBuilder(this, builder, item.asItem());
        }
        return builder.simpleBuilder;
    }

    protected SimpleItemModelBuilder getSimple(ItemLike item, String name) {
        WrappedItemModelBuilder builder = (WrappedItemModelBuilder) getBuilder(name);
        if (builder.simpleBuilder == null) {
            builder.simpleBuilder = new SimpleItemModelBuilder(this, builder, item.asItem());
        }
        return builder.simpleBuilder;
    }

    protected SimpleItemModelBuilder getSimple(Supplier<? extends Item> item) {
        return getSimple(item.get());
    }

    protected SimpleItemModelBuilder generated(ItemLike item) {
        return getSimple(item)
                .parent(GENERATED);
    }

    protected SimpleItemModelBuilder generated(Supplier<? extends Item> item) {
        return generated(item.get());
    }

    protected SimpleItemModelBuilder handheld(ItemLike item) {
        return getSimple(item)
                .parent(HANDHELD);
    }

    protected SimpleItemModelBuilder handheld(Supplier<? extends Item> item) {
        return handheld(item.get());
    }

    protected void simpleItemBlock(Block block) {
        getSimple(block)
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + name(block))))
                .noTexture();
    }
    //endregion

    private ExistingFileHelper getExistingFileHelper() {
        return existingFileHelper;
    }

    public static class SimpleItemModelBuilder {

        private final ItemModelProvider provider;
        private final ItemModelBuilder builder;
        private final Item item;
        private final Map<String, ResourceLocation> layers = new HashMap<>();
        private ModelFile parent;
        private boolean noTexture = false;
        private String folder = "";

        @Nullable
        private CustomLoaderBuilder loader;

        private boolean built = false;

        private SimpleItemModelBuilder(ItemModelProvider provider, ItemModelBuilder builder, Item item) {
            this.provider = provider;
            this.builder = builder;
            this.item = item;
        }

        public SimpleItemModelBuilder parent(ModelFile parent) {
            this.parent = Objects.requireNonNull(parent);
            return this;
        }

        public SimpleItemModelBuilder texture(ResourceLocation texture) {
            if (texture == null) throw new IllegalStateException("Use '.noTexture()' instead of '.texture(null)'");
            return texture("layer0", texture);
        }

        public SimpleItemModelBuilder texture(String layer, ResourceLocation texture) {
            if (noTexture) throw new IllegalStateException("Unable to set texture. NoTexture set.");
            if (!StringUtils.isEmpty(folder)) throw new IllegalArgumentException("Adding texture would ignore existing folder.");

            ResourceLocation existing = layers.put(layer, Objects.requireNonNull(texture));
            if (existing != null) {
                LOGGER.warn("Overwriting layer '{}' texture '{}' with '{}'", layer, existing, texture);
            }
            return this;
        }

        public SimpleItemModelBuilder folder(String folder) {
            if (!layers.isEmpty()) throw new IllegalStateException("Textures set, folder would be ignored.");
            if (noTexture) throw new IllegalStateException("No Texture set, folder would be ignored.");

            this.folder = Objects.requireNonNull(folder);
            return this;
        }

        public SimpleItemModelBuilder noTexture() {
            if (!layers.isEmpty()) throw new IllegalStateException("Setting No Texture would ignore textures.");
            if (!StringUtils.isEmpty(folder)) throw new IllegalArgumentException("Setting No Texture would ignore existing folder.");
            this.noTexture = true;
            return this;
        }

        public SimpleItemModelBuilder override(Consumer<OverrideBuilder> cons) {
            ItemModelBuilder.OverrideBuilder forgeBuilder = builder.override();
            OverrideBuilder builder = new OverrideBuilder(this, forgeBuilder);
            cons.accept(builder);
            return this;
        }

        public <L extends CustomLoaderBuilder> L customLoader(Function<SimpleItemModelBuilder, L> factory) {
            if (loader != null) throw new IllegalStateException("Loader already set!");

            L loader = factory.apply(this);
            this.loader = loader;
            return loader;
        }

        public ModelFile getModel() {
            return builder;
        }

        private void build() {
            if (!built) {
                builder.parent(parent);
                if (!noTexture) {
                    if (layers.isEmpty()) {
                        builder.texture("layer0", provider.itemTexture(item, folder));
                    } else {
                        layers.forEach(builder::texture);
                    }
                }
                if (loader != null) {
                    loader.build(builder);
                }
                built = true;
            }
        }
    }

    private static class WrappedItemModelBuilder extends ItemModelBuilder {

        private SimpleItemModelBuilder simpleBuilder;

        public WrappedItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
            super(outputLocation, existingFileHelper);
        }

        @Override
        public JsonObject toJson() {
            if (simpleBuilder != null) {
                simpleBuilder.build();
                if (simpleBuilder.loader != null) {
                    return simpleBuilder.loader.toJson(super.toJson());
                }
            }
            return super.toJson();
        }
    }

    public static class OverrideBuilder {

        private final SimpleItemModelBuilder parent;
        private final ItemModelBuilder.OverrideBuilder forgeBuilder;

        public OverrideBuilder(SimpleItemModelBuilder parent, ItemModelBuilder.OverrideBuilder forgeBuilder) {
            this.parent = parent;
            this.forgeBuilder = forgeBuilder;
        }

        public OverrideBuilder model(String name, Consumer<SimpleItemModelBuilder> cons) {
            SimpleItemModelBuilder model = parent.provider.getSimple(parent.item, name);
            forgeBuilder.model(model.getModel());
            cons.accept(model);
            return this;
        }

        public OverrideBuilder predicate(ResourceLocation key, float value) {
            forgeBuilder.predicate(key, value);
            return this;
        }
    }

    public static class CustomLoaderBuilder {

        protected final ResourceLocation loaderId;
        protected final SimpleItemModelBuilder parent;
        protected final ExistingFileHelper existingFileHelper;
        protected final Map<String, Boolean> visibility = new LinkedHashMap<>();

        protected CustomLoaderBuilder(ResourceLocation loaderId, SimpleItemModelBuilder parent) {
            this.loaderId = loaderId;
            this.parent = parent;
            existingFileHelper = parent.provider.getExistingFileHelper();
        }

        public CustomLoaderBuilder visibility(String partName, boolean show) {
            this.visibility.put(partName, show);
            return this;
        }

        public SimpleItemModelBuilder end() {
            return parent;
        }

        protected void build(ItemModelBuilder builder) {
        }

        protected JsonObject toJson(JsonObject json) {
            json.addProperty("loader", loaderId.toString());

            if (!visibility.isEmpty()) {
                JsonObject visibilityObj = new JsonObject();

                for (Map.Entry<String, Boolean> entry : visibility.entrySet()) {
                    visibilityObj.addProperty(entry.getKey(), entry.getValue());
                }
                json.add("visibility", visibilityObj);
            }

            return json;
        }
    }
}
