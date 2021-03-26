package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by covers1624 on 25/3/21.
 */
public class SpecialRecipeBuilder implements RecipeBuilder {

    protected final SpecialRecipeSerializer<?> serializer;
    protected final ResourceLocation id;

    public SpecialRecipeBuilder(SpecialRecipeSerializer<?> serializer, ResourceLocation id) {
        this.serializer = serializer;
        this.id = id;
    }

    public static SpecialRecipeBuilder builder(SpecialRecipeSerializer<?> serializer, String id) {
        return builder(serializer, new ResourceLocation(id));
    }

    public static SpecialRecipeBuilder builder(SpecialRecipeSerializer<?> serializer, ResourceLocation id) {
        return new SpecialRecipeBuilder(serializer, id);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IFinishedRecipe build() {
        return new SpecialFinishedRecipe();
    }

    //@formatter:off
    protected class SpecialFinishedRecipe implements IFinishedRecipe {
        @Override public void serializeRecipeData(JsonObject p_218610_1_) { }
        @Override public ResourceLocation getId() { return id; }
        @Override public IRecipeSerializer<?> getType() { return serializer; }
        @Override public JsonObject serializeAdvancement() { return null; }
        @Override public ResourceLocation getAdvancementId() { return null; }
    }
    //@formatter:on
}
