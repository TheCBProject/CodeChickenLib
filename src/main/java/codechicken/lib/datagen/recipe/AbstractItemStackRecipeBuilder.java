package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractItemStackRecipeBuilder<T extends AbstractRecipeBuilder<ItemStack, T>> extends AbstractRecipeBuilder<ItemStack, T> {

    protected AbstractItemStackRecipeBuilder(RecipeSerializer<?> serializer, ResourceLocation id, ItemStack result) {
        super(serializer, id, result);
    }

    @Override
    protected ResourceLocation getAdvancementId() {
        return new ResourceLocation(id.getNamespace(), "recipes/__advancement/" + id.getPath());
    }

    @Override
    public abstract AbstractItemStackFinishedRecipe _build();

    public abstract class AbstractItemStackFinishedRecipe extends AbstractFinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);
            JsonObject result = new JsonObject();
            ItemStack resultStack = AbstractItemStackRecipeBuilder.this.result;
            result.addProperty("item", ForgeRegistries.ITEMS.getKey(resultStack.getItem()).toString());
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
