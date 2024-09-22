package codechicken.lib.datagen.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * Created by covers1624 on 28/12/20.
 */
public class ShapelessRecipeBuilder extends AbstractItemStackRecipeBuilder<ShapelessRecipeBuilder> {

    private static final Logger logger = LogManager.getLogger();

    private final Factory factory;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();

    private CraftingBookCategory category = CraftingBookCategory.MISC;

    protected ShapelessRecipeBuilder(ResourceLocation id, ItemStack result, Factory factory) {
        super(id, result);
        this.factory = factory;
    }

    public static ShapelessRecipeBuilder builder(ItemLike result) {
        return builder(result, 1);
    }

    public static ShapelessRecipeBuilder builder(ItemLike result, int count) {
        return builder(new ItemStack(result, count));
    }

    public static ShapelessRecipeBuilder builder(ItemLike result, int count, ResourceLocation id) {
        return builder(new ItemStack(result, count), id);
    }

    public static ShapelessRecipeBuilder builder(Supplier<? extends ItemLike> result) {
        return builder(result.get(), 1);
    }

    public static ShapelessRecipeBuilder builder(Supplier<? extends ItemLike> result, int count) {
        return builder(new ItemStack(result.get(), count));
    }

    public static ShapelessRecipeBuilder builder(Supplier<? extends ItemLike> result, int count, ResourceLocation id) {
        return builder(new ItemStack(result.get(), count), id);
    }

    public static ShapelessRecipeBuilder builder(ItemStack result) {
        return builder(result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static ShapelessRecipeBuilder builder(ItemStack result, ResourceLocation id) {
        return new ShapelessRecipeBuilder(id, result, ShapelessRecipe::new);
    }

    // region Custom
    public static ShapelessRecipeBuilder custom(ItemLike result, Factory factory) {
        return custom(result, 1, factory);
    }

    public static ShapelessRecipeBuilder custom(ItemLike result, int count, Factory factory) {
        return custom(new ItemStack(result, count), factory);
    }

    public static ShapelessRecipeBuilder custom(ItemLike result, int count, ResourceLocation id, Factory factory) {
        return custom(new ItemStack(result, count), id, factory);
    }

    public static ShapelessRecipeBuilder custom(ItemStack result, Factory factory) {
        return custom(result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory);
    }

    public static ShapelessRecipeBuilder custom(ItemStack result, ResourceLocation id, Factory factory) {
        return new ShapelessRecipeBuilder(id, result, factory);
    }
    // endregion

    public ShapelessRecipeBuilder addIngredient(TagKey<Item> tag) {
        return addIngredient(tag, 1);
    }

    public ShapelessRecipeBuilder addIngredient(TagKey<Item> tag, int quantity) {
        addAutoCriteria(tag);
        Ingredient ingredient = Ingredient.of(tag);
        for (int i = 0; i < quantity; ++i) {
            ingredients.add(ingredient);
        }
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(Supplier<? extends ItemLike> item) {
        return addIngredient(item.get(), 1);
    }

    public ShapelessRecipeBuilder addIngredient(Supplier<? extends ItemLike> item, int quantity) {
        addAutoCriteria(item.get());
        Ingredient ingredient = Ingredient.of(item.get());
        for (int i = 0; i < quantity; ++i) {
            ingredients.add(ingredient);
        }
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(ItemLike item) {
        return addIngredient(item, 1);
    }

    public ShapelessRecipeBuilder addIngredient(ItemLike item, int quantity) {
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

    public ShapelessRecipeBuilder category(CraftingBookCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public Recipe<?> _build() {
        return factory.build(
                group,
                category,
                result,
                ingredients
        );
    }

    public interface Factory {

        Recipe<?> build(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients);
    }
}
