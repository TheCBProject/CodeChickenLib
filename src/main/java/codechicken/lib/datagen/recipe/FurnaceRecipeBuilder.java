package codechicken.lib.datagen.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    protected FurnaceRecipeBuilder(ResourceLocation id, ItemStack result, Factory factory) {
        super(id, result);
        this.factory = factory;
    }

    //region Smelting
    public static FurnaceRecipeBuilder smelting(ItemLike result) {
        return smelting(result, 1);
    }

    public static FurnaceRecipeBuilder smelting(ItemLike result, int count) {
        return smelting(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder smelting(ItemLike result, int count, ResourceLocation id) {
        return smelting(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result) {
        return smelting(result.get(), 1);
    }

    public static FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count) {
        return smelting(new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count, ResourceLocation id) {
        return smelting(new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder smelting(ItemStack result) {
        return smelting(result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder smelting(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(id, result, SmeltingRecipe::new)
                .cookingTime(200);
    }
    //endregion

    //region Blasting
    public static FurnaceRecipeBuilder blasting(ItemLike result) {
        return blasting(result, 1);
    }

    public static FurnaceRecipeBuilder blasting(ItemLike result, int count) {
        return blasting(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder blasting(ItemLike result, int count, ResourceLocation id) {
        return blasting(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result) {
        return blasting(result.get(), 1);
    }

    public static FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count) {
        return blasting(new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count, ResourceLocation id) {
        return blasting(new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder blasting(ItemStack result) {
        return blasting(result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder blasting(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(id, result, BlastingRecipe::new)
                .cookingTime(100);
    }
    //endregion

    //region Smoking
    public static FurnaceRecipeBuilder smoking(ItemLike result) {
        return smoking(result, 1);
    }

    public static FurnaceRecipeBuilder smoking(ItemLike result, int count) {
        return smoking(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder smoking(ItemLike result, int count, ResourceLocation id) {
        return smoking(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result) {
        return smoking(result.get(), 1);
    }

    public static FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count) {
        return smoking(new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count, ResourceLocation id) {
        return smoking(new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder smoking(ItemStack result) {
        return smoking(result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder smoking(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(id, result, SmokingRecipe::new)
                .cookingTime(100);
    }
    //endregion

    //region Campfire
    public static FurnaceRecipeBuilder campfire(ItemLike result) {
        return campfire(result, 1);
    }

    public static FurnaceRecipeBuilder campfire(ItemLike result, int count) {
        return campfire(new ItemStack(result, count));
    }

    public static FurnaceRecipeBuilder campfire(ItemLike result, int count, ResourceLocation id) {
        return campfire(new ItemStack(result, count), id);
    }

    public static FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result) {
        return campfire(result.get(), 1);
    }

    public static FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count) {
        return campfire(new ItemStack(result.get(), count));
    }

    public static FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count, ResourceLocation id) {
        return campfire(new ItemStack(result.get(), count), id);
    }

    public static FurnaceRecipeBuilder campfire(ItemStack result) {
        return campfire(result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static FurnaceRecipeBuilder campfire(ItemStack result, ResourceLocation id) {
        return new FurnaceRecipeBuilder(id, result, CampfireCookingRecipe::new)
                .cookingTime(600);
    }
    //endregion

    //region Custom
    public static FurnaceRecipeBuilder custom(ItemLike result, Factory factory) {
        return custom(result, 1, factory);
    }

    public static FurnaceRecipeBuilder custom(ItemLike result, int count, Factory factory) {
        return custom(new ItemStack(result, count), factory);
    }

    public static FurnaceRecipeBuilder custom(ItemLike result, int count, ResourceLocation id, Factory factory) {
        return custom(new ItemStack(result, count), id, factory);
    }

    public static FurnaceRecipeBuilder custom(Supplier<? extends ItemLike> result, Factory factory) {
        return custom(result.get(), 1, factory);
    }

    public static FurnaceRecipeBuilder custom(Supplier<? extends ItemLike> result, int count, Factory factory) {
        return custom(new ItemStack(result.get(), count), factory);
    }

    public static FurnaceRecipeBuilder custom(Supplier<? extends ItemLike> result, int count, ResourceLocation id, Factory factory) {
        return custom(new ItemStack(result.get(), count), id, factory);
    }

    public static FurnaceRecipeBuilder custom(ItemStack result, Factory factory) {
        return custom(result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory);
    }

    public static FurnaceRecipeBuilder custom(ItemStack result, ResourceLocation id, Factory factory) {
        return new FurnaceRecipeBuilder(id, result, factory);
    }
    //endregion

    public FurnaceRecipeBuilder category(CookingBookCategory category) {
        this.category = category;
        return this;
    }

    public FurnaceRecipeBuilder ingredient(TagKey<Item> tag) {
        addAutoCriteria(tag);
        this.ingredient = Ingredient.of(tag);
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
