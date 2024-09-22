package codechicken.lib.datagen.recipe;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by covers1624 on 27/12/20.
 */
public interface RecipeBuilder {

    ResourceLocation getId();

    BuiltRecipe build();

    // Compat with vanilla's Recipe Provider.
    default void build(RecipeOutput output) {
        BuiltRecipe built = build();
        output.accept(getId(), built.recipe, built.advancement, built.conditions.toArray(ICondition[]::new));
    }

    record BuiltRecipe(
            Recipe<?> recipe,
            @Nullable AdvancementHolder advancement,
            List<ICondition> conditions
    ) { }
}
