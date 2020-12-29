package codechicken.lib.datagen.recipe;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 27/12/20.
 */
public interface RecipeBuilder {

    ResourceLocation getId();

    IFinishedRecipe build();

    //Compat with vanilla's Recipe Provider.
    default void build(Consumer<IFinishedRecipe> consumer) {
        consumer.accept(build());
    }

}
