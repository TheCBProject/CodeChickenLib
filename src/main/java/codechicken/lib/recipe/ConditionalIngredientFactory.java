package codechicken.lib.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;

/**
 * Simple conditional ingredient.
 *
 * Created by covers1624 on 18/10/2017.
 */
public class ConditionalIngredientFactory implements IIngredientFactory {

    @Nonnull
    @Override
    public Ingredient parse(JsonContext context, JsonObject json) {
        if (!CraftingHelper.processConditions(JsonUtils.getJsonArray(json, "conditions"), context)) {
            if (json.has("fail")) {
                return CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "fail"), context);
            }
            return Ingredient.EMPTY;
        }
        return CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "pass"), context);
    }
}
