package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractItemStackRecipeBuilder<T extends AbstractRecipeBuilder<ItemStack, T>> extends AbstractRecipeBuilder<ItemStack, T> {

    protected AbstractItemStackRecipeBuilder(IRecipeSerializer<?> serializer, ResourceLocation id, ItemStack result) {
        super(serializer, id, result);
    }

    @Override
    protected ResourceLocation getAdvancementId() {
        return new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getGroup().getPath() + "/" + id.getPath());
    }

    @Override
    public abstract AbstractItemStackFinishedRecipe _build();

    public abstract class AbstractItemStackFinishedRecipe extends AbstractFinishedRecipe {

        @Override
        public void serialize(JsonObject json) {
            super.serialize(json);
            JsonObject result = new JsonObject();
            ItemStack resultStack = AbstractItemStackRecipeBuilder.this.result;
            result.addProperty("item", resultStack.getItem().getRegistryName().toString());
            if (resultStack.getCount() > 1) {
                result.addProperty("count", resultStack.getCount());
            }
            //TODO, caps?
            if (resultStack.hasTag()) {
                result.addProperty("nbt", resultStack.getTag().toString());
            }
            json.add("result", result);
        }
    }
}
