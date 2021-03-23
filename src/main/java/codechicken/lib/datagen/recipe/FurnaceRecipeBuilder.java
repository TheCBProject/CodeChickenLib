package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by covers1624 on 28/12/20.
 */
public class FurnaceRecipeBuilder extends AbstractItemStackRecipeBuilder<FurnaceRecipeBuilder> {

    private static final Logger logger = LogManager.getLogger();

    private Ingredient ingredient;
    private float experience = 0.0F;
    private int cookingTime = 200;

    protected FurnaceRecipeBuilder(IRecipeSerializer<?> serializer, ResourceLocation id, ItemStack result) {
        super(serializer, id, result);
    }

    //region Smelting
    public static FurnaceRecipeBuilder smelting(IItemProvider result) {
        return smelting(result, 1);
    }

    public static FurnaceRecipeBuilder smelting(IItemProvider result, int count) {
        return smelting(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder smelting(IItemProvider result, int count, ResourceLocation id) {
        return smelting(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder smelting(ItemStack result) {
        return smelting(result, result.getItem().getRegistryName());
    }

    public static FurnaceRecipeBuilder smelting(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(IRecipeSerializer.SMELTING_RECIPE, id, result)
                .cookingTime(200);
    }
    //endregion

    //region Blasting
    public static FurnaceRecipeBuilder blasting(IItemProvider result) {
        return blasting(result, 1);
    }

    public static FurnaceRecipeBuilder blasting(IItemProvider result, int count) {
        return blasting(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder blasting(IItemProvider result, int count, ResourceLocation id) {
        return blasting(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder blasting(ItemStack result) {
        return blasting(result, result.getItem().getRegistryName());
    }

    public static FurnaceRecipeBuilder blasting(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(IRecipeSerializer.BLASTING_RECIPE, id, result)
                .cookingTime(100);
    }
    //endregion

    //region Smoking
    public static FurnaceRecipeBuilder smoking(IItemProvider result) {
        return smoking(result, 1);
    }

    public static FurnaceRecipeBuilder smoking(IItemProvider result, int count) {
        return smoking(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder smoking(IItemProvider result, int count, ResourceLocation id) {
        return smoking(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder smoking(ItemStack result) {
        return smoking(result, result.getItem().getRegistryName());
    }

    public static FurnaceRecipeBuilder smoking(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(IRecipeSerializer.SMOKING_RECIPE, id, result)
                .cookingTime(100);
    }
    //endregion

    //region Campfire
    public static FurnaceRecipeBuilder campfire(IItemProvider result) {
        return campfire(result, 1);
    }

    public static FurnaceRecipeBuilder campfire(IItemProvider result, int count) {
        return campfire(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder campfire(IItemProvider result, int count, ResourceLocation id) {
        return campfire(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder campfire(ItemStack result) {
        return campfire(result, result.getItem().getRegistryName());
    }

    public static FurnaceRecipeBuilder campfire(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, id, result)
                .cookingTime(600);
    }
    //endregion

    public FurnaceRecipeBuilder ingredient(Tag<Item> tag) {
        addAutoCriteria(tag);
        this.ingredient = Ingredient.of(tag);
        return this;
    }

    public FurnaceRecipeBuilder ingredient(IItemProvider item) {
        addAutoCriteria(item);
        this.ingredient = Ingredient.of(item);
        return this;
    }

    public FurnaceRecipeBuilder ingredient(Ingredient ingredient) {
        if (generateCriteria) {
            logger.warn("Criteria not automatically generated for raw ingredient.", new Throwable("Here, have a stack trace"));
        }
        this.ingredient = ingredient;
        return this;
    }

    public FurnaceRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    public FurnaceRecipeBuilder cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    @Override
    public AbstractItemStackFinishedRecipe _build() {
        return new FinishedFurnaceRecipe();
    }

    public class FinishedFurnaceRecipe extends AbstractItemStackFinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);
            json.add("ingredient", ingredient.toJson());
            json.addProperty("experience", experience);
            json.addProperty("cookingtime", cookingTime);
        }
    }
}
