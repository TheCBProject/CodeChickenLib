package codechicken.lib.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by covers1624 on 15/10/20.
 */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
    }

    protected String name(Item item) {
        return item.getRegistryName().getPath();
    }

    protected ResourceLocation itemTexture(Item item) {
        return modLoc("item/" + name(item));
    }

    protected ResourceLocation itemTexture(Item item, String folder) {
        return modLoc("item/" + StringUtils.appendIfMissing(folder, "/") + name(item));
    }

    protected ItemModelBuilder generated(Item item, String folder) {
        return generated(item, itemTexture(item, folder));
    }

    protected ItemModelBuilder generated(Item item) {
        return generated(item, itemTexture(item));
    }

    protected ItemModelBuilder generated(Item item, ResourceLocation texture) {
        return getBuilder(name(item))//
                .parent(new ModelFile.UncheckedModelFile("item/generated"))//
                .texture("layer0", texture);
    }

    protected ItemModelBuilder noTexture(Item item) {
        return getBuilder(name(item))//
                .parent(new ModelFile.UncheckedModelFile("item/generated"));
    }
}
