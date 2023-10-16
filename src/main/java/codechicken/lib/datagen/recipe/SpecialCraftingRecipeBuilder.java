package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

/**
 * Created by covers1624 on 25/3/21.
 */
public class SpecialCraftingRecipeBuilder implements RecipeBuilder {

    protected final SimpleCraftingRecipeSerializer<?> serializer;
    protected final ResourceLocation id;

    public SpecialCraftingRecipeBuilder(SimpleCraftingRecipeSerializer<?> serializer, ResourceLocation id) {
        this.serializer = serializer;
        this.id = id;
    }

    public static SpecialCraftingRecipeBuilder builder(SimpleCraftingRecipeSerializer<?> serializer, String id) {
        return builder(serializer, new ResourceLocation(id));
    }

    public static SpecialCraftingRecipeBuilder builder(SimpleCraftingRecipeSerializer<?> serializer, ResourceLocation id) {
        return new SpecialCraftingRecipeBuilder(serializer, id);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public FinishedRecipe build() {
        return new SpecialFinishedRecipe();
    }

    //@formatter:off
    protected class SpecialFinishedRecipe implements FinishedRecipe {
        @Override public void serializeRecipeData(JsonObject p_218610_1_) { }
        @Override public ResourceLocation getId() { return id; }
        @Override public RecipeSerializer<?> getType() { return serializer; }
        @Override public JsonObject serializeAdvancement() { return null; }
        @Override public ResourceLocation getAdvancementId() { return null; }
    }
    //@formatter:on
}
