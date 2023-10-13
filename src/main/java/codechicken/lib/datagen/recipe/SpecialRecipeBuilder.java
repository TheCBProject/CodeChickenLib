package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Created by covers1624 on 25/3/21.
 */
public class SpecialRecipeBuilder implements RecipeBuilder {

    protected final SimpleCraftingRecipeSerializer<?> serializer;
    protected final ResourceLocation id;

    public SpecialRecipeBuilder(SimpleCraftingRecipeSerializer<?> serializer, ResourceLocation id) {
        this.serializer = serializer;
        this.id = id;
    }

    public static SpecialRecipeBuilder builder(SimpleCraftingRecipeSerializer<?> serializer, String id) {
        return builder(serializer, new ResourceLocation(id));
    }

    public static SpecialRecipeBuilder builder(SimpleCraftingRecipeSerializer<?> serializer, ResourceLocation id) {
        return new SpecialRecipeBuilder(serializer, id);
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
        @Override public void serializeRecipeData(@NotNull JsonObject data) { }
        @Override public@NotNull ResourceLocation getId() { return id; }
        @Override public@NotNull RecipeSerializer<?> getType() { return serializer; }
        @Override public JsonObject serializeAdvancement() { return null; }
        @Override public ResourceLocation getAdvancementId() { return null; }
    }
    //@formatter:on
}
