package codechicken.lib.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 28/12/20.
 */
public class ShapelessRecipeBuilder extends AbstractItemStackRecipeBuilder<ShapelessRecipeBuilder> {

    private final List<Ingredient> ingredients = new ArrayList<>();

    protected ShapelessRecipeBuilder(IRecipeSerializer<?> serializer, ResourceLocation id, ItemStack result) {
        super(serializer, id, result);
    }

    public static ShapelessRecipeBuilder builder(IItemProvider result) {
        return builder(result, 1);
    }

    public static ShapelessRecipeBuilder builder(IItemProvider result, int count) {
        return builder(new ItemStack(result, count));
    }

    public static ShapelessRecipeBuilder builder(IItemProvider result, int count, ResourceLocation id) {
        return builder(new ItemStack(result, count), id);
    }

    public static ShapelessRecipeBuilder builder(ItemStack result) {
        return builder(result, result.getItem().getRegistryName());
    }

    public static ShapelessRecipeBuilder builder(ItemStack result, ResourceLocation id) {
        return new ShapelessRecipeBuilder(IRecipeSerializer.CRAFTING_SHAPELESS, id, result);
    }

    public ShapelessRecipeBuilder addIngredient(Tag<Item> tag) {
        return addIngredient(tag, 1);
    }

    public ShapelessRecipeBuilder addIngredient(Tag<Item> tag, int quantity) {
        return addIngredient(Ingredient.fromTag(tag), quantity);
    }

    public ShapelessRecipeBuilder addIngredient(IItemProvider item) {
        return addIngredient(item, 1);
    }

    public ShapelessRecipeBuilder addIngredient(IItemProvider item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            addIngredient(Ingredient.fromItems(item));
        }

        return this;
    }

    public ShapelessRecipeBuilder addIngredient(Ingredient ingredient) {
        return this.addIngredient(ingredient, 1);
    }

    public ShapelessRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            ingredients.add(ingredient);
        }

        return this;
    }

    @Override
    public AbstractItemStackFinishedRecipe _build() {
        return new FinishedShapelessRecipe();
    }

    public class FinishedShapelessRecipe extends AbstractItemStackFinishedRecipe {

        @Override
        public void serialize(JsonObject json) {
            super.serialize(json);

            JsonArray ingredients = new JsonArray();
            for (Ingredient ingredient : ShapelessRecipeBuilder.this.ingredients) {
                ingredients.add(ingredient.serialize());
            }
            json.add("ingredients", ingredients);
        }
    }
}
