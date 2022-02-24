package codechicken.lib.datagen.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 27/12/20.
 */
public interface RecipeBuilder {

    ResourceLocation getId();

    FinishedRecipe build();

    //Compat with vanilla's Recipe Provider.
    default void build(Consumer<FinishedRecipe> consumer) {
        consumer.accept(build());
    }

}
