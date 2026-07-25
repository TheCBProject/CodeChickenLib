package codechicken.lib.datagen.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 25/3/21.
 */
public class SpecialCraftingRecipeBuilder implements RecipeBuilder {

    protected final Identifier id;
    protected final ResourceKey<Recipe<?>> key;

    private final Factory factory;

    public SpecialCraftingRecipeBuilder(Identifier id, Factory factory) {
        this.id = id;
        this.key = ResourceKey.create(Registries.RECIPE, id);
        this.factory = factory;
    }

    public static SpecialCraftingRecipeBuilder builder(ItemLike id, Factory factory) {
        return builder(BuiltInRegistries.ITEM.getKey(id.asItem()), factory);
    }

    public static SpecialCraftingRecipeBuilder builder(Supplier<? extends ItemLike> id, Factory factory) {
        return builder(id.get(), factory);
    }

    public static SpecialCraftingRecipeBuilder builder(ItemStack id, Factory factory) {
        return builder(id.getItem(), factory);
    }

    public static SpecialCraftingRecipeBuilder builder(String id, Factory factory) {
        return builder(Identifier.parse(id), factory);
    }

    public static SpecialCraftingRecipeBuilder builder(Identifier id, Factory factory) {
        return new SpecialCraftingRecipeBuilder(id, factory);
    }

    @Override
    public final Identifier getId() {
        return id;
    }

    @Override
    public final ResourceKey<Recipe<?>> getKey() {
        return key;
    }

    @Override
    public BuiltRecipe build() {
        return new BuiltRecipe(
                factory.build(),
                null,
                List.of()
        );
    }

    public interface Factory {

        Recipe<?> build();
    }
}
