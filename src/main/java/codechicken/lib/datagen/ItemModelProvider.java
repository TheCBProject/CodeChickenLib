package codechicken.lib.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static codechicken.lib.CodeChickenLib.MOD_ID;
import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 15/10/20.
 */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected static final ModelFile.UncheckedModelFile GENERATED = new ModelFile.UncheckedModelFile("item/generated");
    protected static final ModelFile.UncheckedModelFile HANDHELD = new ModelFile.UncheckedModelFile("item/handheld");

    public ItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, ITEM_FOLDER, WrappedItemModelBuilder::new, existingFileHelper);
    }

    //region Location helpers
    protected String name(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
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
        return blockTexture(block, "");
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

    protected SimpleItemModelBuilder clazz(Supplier<? extends Item> item, Class<? extends BakedModel> clazz) {
        return clazz(item.get(), clazz);
    }

    protected SimpleItemModelBuilder clazz(ItemLike item, Class<? extends BakedModel> clazz) {
        return generated(item)
                .noTexture()
                .customLoader(ClassCustomLoaderBuilder::new)
                .clazz(clazz)
                .end();
    }
    //endregion

    private SimpleItemModelBuilder makeNested(ItemLike item) {
        WrappedItemModelBuilder builder = (WrappedItemModelBuilder) nested();
        builder.simpleBuilder = new SimpleItemModelBuilder(this, builder, item.asItem());
        return builder.simpleBuilder;
    }

    private ExistingFileHelper getExistingFileHelper() {
        return existingFileHelper;
    }

    @Override
    public String getName() {
        return modid + " Item models.";
    }

    public static class SimpleItemModelBuilder {

        private final ItemModelProvider provider;
        private final ItemModelBuilder builder;
        private final Item item;
        private final Map<String, ResourceLocation> layers = new HashMap<>();
        private @Nullable ModelFile parent;
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
            this.parent = requireNonNull(parent);
            return this;
        }

        public SimpleItemModelBuilder texture(@Nullable ResourceLocation texture) {
            if (texture == null) throw new IllegalStateException("Use '.noTexture()' instead of '.texture(null)'");
            return texture("layer0", texture);
        }

        public SimpleItemModelBuilder texture(String layer, ResourceLocation texture) {
            if (noTexture) throw new IllegalStateException("Unable to set texture. NoTexture set.");
            if (!StringUtils.isEmpty(folder)) throw new IllegalArgumentException("Adding texture would ignore existing folder.");

            ResourceLocation existing = layers.put(layer, requireNonNull(texture));
            if (existing != null) {
                LOGGER.warn("Overwriting layer '{}' texture '{}' with '{}'", layer, existing, texture);
            }
            return this;
        }

        public SimpleItemModelBuilder folder(String folder) {
            if (!layers.isEmpty()) throw new IllegalStateException("Textures set, folder would be ignored.");
            if (noTexture) throw new IllegalStateException("No Texture set, folder would be ignored.");

            this.folder = requireNonNull(folder);
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
                builder.parent(requireNonNull(parent, "Model requires a parent."));
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

        private @Nullable SimpleItemModelBuilder simpleBuilder;

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

    public static class ClassCustomLoaderBuilder extends CustomLoaderBuilder {

        private @Nullable Class<? extends BakedModel> clazz;

        protected ClassCustomLoaderBuilder(SimpleItemModelBuilder parent) {
            super(ResourceLocation.fromNamespaceAndPath(MOD_ID, "class"), parent);
        }

        public ClassCustomLoaderBuilder clazz(Class<? extends BakedModel> clazz) {
            try {
                Constructor<?> ctor = clazz.getConstructor();
                if (!Modifier.isPublic(ctor.getModifiers())) {
                    throw new IllegalArgumentException("Expected single no-args public constructor.");
                }
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Expected single no-args public constructor.", ex);
            }
            this.clazz = clazz;
            return this;
        }

        @Override
        protected JsonObject toJson(JsonObject json) {
            super.toJson(json);
            json.addProperty("class", requireNonNull(clazz).getName());
            return json;
        }
    }

    public static class CompositeLoaderBuilder extends CustomLoaderBuilder {

        private final Map<String, SimpleItemModelBuilder> children = new LinkedHashMap<>();
        private final List<String> order = new ArrayList<>();

        protected CompositeLoaderBuilder(ResourceLocation loader, SimpleItemModelBuilder parent) {
            super(loader, parent);
        }

        public static CompositeLoaderBuilder forge(SimpleItemModelBuilder parent) {
            return new CompositeLoaderBuilder(ResourceLocation.fromNamespaceAndPath("forge", "composite"), parent);
        }

        public static CompositeLoaderBuilder ccl(SimpleItemModelBuilder parent) {
            return new CompositeLoaderBuilder(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_composite"), parent);
        }

        public CompositeLoaderBuilder nested(String name, Consumer<SimpleItemModelBuilder> cons) {
            if (children.containsKey(name)) throw new IllegalArgumentException("Child with name " + name + " is already registered.");

            SimpleItemModelBuilder nested = parent.provider.makeNested(parent.item);
            cons.accept(nested);
            children.put(name, nested);
            order.add(name);
            return this;
        }

        public CompositeLoaderBuilder order(String... names) {
            for (String name : names) {
                if (!children.containsKey(name)) {
                    throw new IllegalArgumentException("Child with name " + name + " does not exist.");
                }
            }
            order.clear();
            Collections.addAll(order, names);
            return this;
        }

        @Override
        protected JsonObject toJson(JsonObject json) {
            super.toJson(json);

            JsonObject children = new JsonObject();
            this.children.forEach((name, child) -> children.add(name, child.builder.toJson()));
            json.add("children", children);

            JsonArray order = new JsonArray();
            this.order.forEach(order::add);
            json.add("item_render_order", order);

            return json;
        }
    }
}
