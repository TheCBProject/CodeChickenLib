package codechicken.lib.datagen;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 15/10/20.
 */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    protected static final ModelFile.UncheckedModelFile GENERATED = new ModelFile.UncheckedModelFile("item/generated");
    protected static final ModelFile.UncheckedModelFile HANDHELD = new ModelFile.UncheckedModelFile("item/handheld");

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, WrappedItemModelBuilder::new, existingFileHelper);
    }

    //region Location helpers
    protected String name(IItemProvider item) {
        return item.asItem().getRegistryName().getPath();
    }

    protected ResourceLocation itemTexture(IItemProvider item) {
        return itemTexture(item, "");
    }

    protected ResourceLocation itemTexture(Supplier<? extends Item> item) {
        return itemTexture(item, "");
    }

    protected ResourceLocation itemTexture(IItemProvider item, String folder) {
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
    protected ItemModelBuilder getBuilder(IItemProvider item) {
        return getBuilder(name(item));
    }

    protected ItemModelBuilder getBuilder(Supplier<? extends Item> item) {
        return getBuilder(name(item.get()));
    }
    //endregion

    //region Simple builder
    protected SimpleItemModelBuilder getSimple(IItemProvider item) {
        WrappedItemModelBuilder builder = (WrappedItemModelBuilder) getBuilder(item);
        if (builder.simpleBuilder == null) {
            builder.simpleBuilder = new SimpleItemModelBuilder(builder, item.asItem());
        }
        return builder.simpleBuilder;
    }

    protected SimpleItemModelBuilder getSimple(Supplier<? extends Item> item) {
        return getSimple(item.get());
    }

    protected SimpleItemModelBuilder generated(IItemProvider item) {
        return getSimple(item)
                .parent(GENERATED);
    }

    protected SimpleItemModelBuilder generated(Supplier<? extends Item> item) {
        return generated(item.get());
    }

    protected SimpleItemModelBuilder handheld(IItemProvider item) {
        return getSimple(item)
                .parent(HANDHELD);
    }

    protected SimpleItemModelBuilder handheld(Supplier<? extends Item> item) {
        return handheld(item.get());
    }

    protected void simpleItemBlock(Block block) {
        getSimple(block)
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + name(block))))
                .texture(null);
    }
    //endregion

    public class SimpleItemModelBuilder {

        private final ItemModelBuilder builder;
        private final Item item;
        private ModelFile parent;
        private ResourceLocation texture;
        private boolean noTexture = false;
        private String folder = "";

        protected SimpleItemModelBuilder(ItemModelBuilder builder, Item item) {
            this.builder = builder;
            this.item = item;
        }

        public SimpleItemModelBuilder parent(ModelFile parent) {
            this.parent = Objects.requireNonNull(parent);
            return this;
        }

        public SimpleItemModelBuilder texture(ResourceLocation texture) {
            this.texture = texture;
            this.noTexture = texture == null;
            if (!StringUtils.isEmpty(folder)) {
                throw new IllegalArgumentException("Adding texture would ignore existing folder.");
            }
            return this;
        }

        public SimpleItemModelBuilder folder(String folder) {
            this.folder = Objects.requireNonNull(folder);
            if (texture != null && !noTexture) {
                throw new IllegalArgumentException("Folder would be ignored, remove parameter");
            }
            return this;
        }

        private boolean built = false;

        private void build() {
            if (!built) {
                ResourceLocation texture = this.texture;
                if (texture == null && !noTexture) {
                    texture = itemTexture(item, folder);
                }

                builder.parent(parent);
                if (texture != null) {
                    builder.texture("layer0", texture);
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
            }
            return super.toJson();
        }
    }
}
