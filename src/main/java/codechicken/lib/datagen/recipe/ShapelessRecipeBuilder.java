package codechicken.lib.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 28/12/20.
 */
public class ShapelessRecipeBuilder extends AbstractItemStackRecipeBuilder<ShapelessRecipeBuilder> {

    private static final Logger logger = LogManager.getLogger();

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
        return new ShapelessRecipeBuilder(IRecipeSerializer.SHAPELESS_RECIPE, id, result);
    }

    public ShapelessRecipeBuilder addIngredient(ITag<Item> tag) {
        return addIngredient(tag, 1);
    }

    public ShapelessRecipeBuilder addIngredient(ITag<Item> tag, int quantity) {
        addAutoCriteria(tag);
        Ingredient ingredient = Ingredient.of(tag);
        for (int i = 0; i < quantity; ++i) {
            ingredients.add(ingredient);
        }
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(IItemProvider item) {
        return addIngredient(item, 1);
    }

    public ShapelessRecipeBuilder addIngredient(IItemProvider item, int quantity) {
        addAutoCriteria(item);
        Ingredient ingredient = Ingredient.of(item);
        for (int i = 0; i < quantity; ++i) {
            ingredients.add(ingredient);
        }
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(Ingredient ingredient) {
        return this.addIngredient(ingredient, 1);
    }

    public ShapelessRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
        if (generateCriteria) {
            logger.warn("Criteria not automatically generated for raw ingredient.", new Throwable("Here, have a stack trace"));
        }
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
        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);

            JsonArray ingredients = new JsonArray();
            for (Ingredient ingredient : ShapelessRecipeBuilder.this.ingredients) {
                ingredients.add(ingredient.toJson());
            }
            json.add("ingredients", ingredients);
        }
    }
}
