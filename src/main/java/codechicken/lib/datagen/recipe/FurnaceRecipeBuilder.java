package codechicken.lib.datagen.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 28/12/20.
 */
public class FurnaceRecipeBuilder extends AbstractItemStackRecipeBuilder<FurnaceRecipeBuilder> {

    private static final Logger logger = LogManager.getLogger();

    private final Factory factory;

    private CookingBookCategory category = CookingBookCategory.MISC;
    private @Nullable Ingredient ingredient;
    private float experience = 0.0F;
    private int cookingTime = 200;

    protected FurnaceRecipeBuilder(Identifier id, HolderGetter<Item> items, ItemStack result, Factory factory) {
        super(id, items, result);
        this.factory = factory;
    }

    //region Smelting
    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, ItemLike result) {
        return smelting(items, result, 1);
    }

    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, ItemLike result, int count) {
        return smelting(items, new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, ItemLike result, int count, Identifier id) {
        return smelting(items, new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, Supplier<? extends ItemLike> result) {
        return smelting(items, result.get(), 1);
    }

    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count) {
        return smelting(items, new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count, Identifier id) {
        return smelting(items, new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, ItemStack result) {
        return smelting(items, result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder smelting(HolderGetter<Item> items, ItemStack result, Identifier id) {
        return new FurnaceRecipeBuilder(id, items, result, SmeltingRecipe::new)
                .cookingTime(200);
    }
    //endregion

    //region Blasting
    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, ItemLike result) {
        return blasting(items, result, 1);
    }

    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, ItemLike result, int count) {
        return blasting(items, new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, ItemLike result, int count, Identifier id) {
        return blasting(items, new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, Supplier<? extends ItemLike> result) {
        return blasting(items, result.get(), 1);
    }

    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count) {
        return blasting(items, new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count, Identifier id) {
        return blasting(items, new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, ItemStack result) {
        return blasting(items, result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder blasting(HolderGetter<Item> items, ItemStack result, Identifier id) {
        return new FurnaceRecipeBuilder(id, items, result, BlastingRecipe::new)
                .cookingTime(100);
    }
    //endregion

    //region Smoking
    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, ItemLike result) {
        return smoking(items, result, 1);
    }

    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, ItemLike result, int count) {
        return smoking(items, new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, ItemLike result, int count, Identifier id) {
        return smoking(items, new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, Supplier<? extends ItemLike> result) {
        return smoking(items, result.get(), 1);
    }

    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count) {
        return smoking(items, new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count, Identifier id) {
        return smoking(items, new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, ItemStack result) {
        return smoking(items, result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder smoking(HolderGetter<Item> items, ItemStack result, Identifier id) {
        return new FurnaceRecipeBuilder(id, items, result, SmokingRecipe::new)
                .cookingTime(100);
    }
    //endregion

    //region Campfire
    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, ItemLike result) {
        return campfire(items, result, 1);
    }

    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, ItemLike result, int count) {
        return campfire(items, new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, ItemLike result, int count, Identifier id) {
        return campfire(items, new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, Supplier<? extends ItemLike> result) {
        return campfire(items, result.get(), 1);
    }

    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count) {
        return campfire(items, new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count, Identifier id) {
        return campfire(items, new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, ItemStack result) {
        return campfire(items, result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder campfire(HolderGetter<Item> items, ItemStack result, Identifier id) {
        return new FurnaceRecipeBuilder(id, items, result, CampfireCookingRecipe::new)
                .cookingTime(600);
    }
    //endregion

    //region Custom
    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, ItemLike result, Factory factory) {
        return custom(items, result, 1, factory);
    }

    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, ItemLike result, int count, Factory factory) {
        return custom(items, new ItemStack(result, count), factory);
    }

    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, ItemLike result, int count, Identifier id, Factory factory) {
        return custom(items, new ItemStack(result, count), id, factory);
    }

    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, Supplier<? extends ItemLike> result, Factory factory) {
        return custom(items, result.get(), 1, factory);
    }

    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count, Factory factory) {
        return custom(items, new ItemStack(result.get(), count), factory);
    }

    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, Supplier<? extends ItemLike> result, int count, Identifier id, Factory factory) {
        return custom(items, new ItemStack(result.get(), count), id, factory);
    }

    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, ItemStack result, Factory factory) {
        return custom(items, result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory);
    }

    public static FurnaceRecipeBuilder custom(HolderGetter<Item> items, ItemStack result, Identifier id, Factory factory) {
        return new FurnaceRecipeBuilder(id, items, result, factory);
    }
    //endregion

    public FurnaceRecipeBuilder category(CookingBookCategory category) {
        this.category = category;
        return this;
    }

    public FurnaceRecipeBuilder ingredient(TagKey<Item> tag) {
        addAutoCriteria(tag);
        this.ingredient = Ingredient.of(items.getOrThrow(tag));
        return this;
    }

    public FurnaceRecipeBuilder ingredient(ItemLike item) {
        addAutoCriteria(item);
        this.ingredient = Ingredient.of(item);
        return this;
    }

    public FurnaceRecipeBuilder ingredient(Supplier<? extends ItemLike> item) {
        addAutoCriteria(item.get());
        this.ingredient = Ingredient.of(item.get());
        return this;
    }

    public FurnaceRecipeBuilder ingredient(Ingredient ingredient) {
        if (generateCriteria) {
            logger.warn("Criteria not automatically generated for raw ingredient.", new Throwable("Here, have a stack trace"));
        }
        this.ingredient = ingredient;
        return this;
    }

    public FurnaceRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    public FurnaceRecipeBuilder cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    @Override
    public Recipe<?> _build() {
        return factory.build(group, category, requireNonNull(ingredient), result, experience, cookingTime);
    }

    @Override
    protected void validate() {
        super.validate();
        if (ingredient == null) {
            throw new IllegalStateException("No ingredient set.");
        }
    }

    public interface Factory {

        Recipe<?> build(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}
