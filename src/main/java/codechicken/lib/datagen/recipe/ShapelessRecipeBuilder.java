package codechicken.lib.datagen.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
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

    protected ShapelessRecipeBuilder(Identifier id, HolderGetter<Item> items, ItemStack result, Factory factory) {
        super(id, items, result);
        this.factory = factory;
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, ItemLike result) {
        return builder(items, result, 1);
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, ItemLike result, int count) {
        return builder(items, new ItemStack(result, count));
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, ItemLike result, int count, Identifier id) {
        return builder(items, new ItemStack(result, count), id);
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, Supplier<? extends ItemLike> result) {
        return builder(items, result.get(), 1);
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count) {
        return builder(items, new ItemStack(result.get(), count));
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count, Identifier id) {
        return builder(items, new ItemStack(result.get(), count), id);
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, ItemStack result) {
        return builder(items, result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static ShapelessRecipeBuilder builder(HolderGetter<Item> items, ItemStack result, Identifier id) {
        return new ShapelessRecipeBuilder(id, items, result, ShapelessRecipe::new);
    }

    // region Custom
    public static ShapelessRecipeBuilder custom(HolderGetter<Item> items, ItemLike result, Factory factory) {
        return custom(items, result, 1, factory);
    }

    public static ShapelessRecipeBuilder custom(HolderGetter<Item> items, ItemLike result, int count, Factory factory) {
        return custom(items, new ItemStack(result, count), factory);
    }

    public static ShapelessRecipeBuilder custom(HolderGetter<Item> items, ItemLike result, int count, Identifier id, Factory factory) {
        return custom(items, new ItemStack(result, count), id, factory);
    }

    public static ShapelessRecipeBuilder custom(HolderGetter<Item> items, ItemStack result, Factory factory) {
        return custom(items, result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory);
    }

    public static ShapelessRecipeBuilder custom(HolderGetter<Item> items, ItemStack result, Identifier id, Factory factory) {
        return new ShapelessRecipeBuilder(id, items, result, factory);
    }
    // endregion

    public ShapelessRecipeBuilder addIngredient(TagKey<Item> tag) {
        return addIngredient(tag, 1);
    }

    public ShapelessRecipeBuilder addIngredient(TagKey<Item> tag, int quantity) {
        addAutoCriteria(tag);
        Ingredient ingredient = Ingredient.of(items.getOrThrow(tag));
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
