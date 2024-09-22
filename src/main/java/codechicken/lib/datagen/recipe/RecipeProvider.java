package codechicken.lib.datagen.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 27/12/20.
 */
public abstract class RecipeProvider implements DataProvider {

    private final Map<ResourceLocation, RecipeBuilder> recipes = new HashMap<>();
    private final PackOutput.PathProvider recipePath;
    private final PackOutput.PathProvider advancementPath;
    protected final String modId;

    public RecipeProvider(PackOutput output, String modId) {
        this.modId = modId;
        recipePath = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
        advancementPath = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
    }

    @Override
    public final CompletableFuture<Void> run(CachedOutput cache) {
        registerRecipes();
        List<CompletableFuture<?>> futures = new LinkedList<>();
        for (Map.Entry<ResourceLocation, RecipeBuilder> entry : recipes.entrySet()) {
            ResourceLocation id = entry.getKey();
            RecipeBuilder.BuiltRecipe builtRecipe = entry.getValue().build();
            futures.add(DataProvider.saveStable(
                    cache,
                    Recipe.CONDITIONAL_CODEC,
                    Optional.of(new WithConditions<>(builtRecipe.conditions(), builtRecipe.recipe())),
                    recipePath.json(id)
            ));

            AdvancementHolder advancement = builtRecipe.advancement();
            if (advancement != null) {
                futures.add(DataProvider.saveStable(
                        cache,
                        Advancement.CONDITIONAL_CODEC,
                        Optional.of(new WithConditions<>(builtRecipe.conditions(), advancement.value())),
                        advancementPath.json(id)
                ));
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    protected abstract void registerRecipes();

    protected <T extends RecipeBuilder> T builder(T builder) {
        if (recipes.containsKey(builder.getId())) {
            throw new IllegalArgumentException("Recipe with id '" + builder.getId() + "' already exists.");
        }
        recipes.put(builder.getId(), builder);
        return builder;
    }

    //@formatter:off
    protected ShapedRecipeBuilder shapedRecipe(ItemLike result) { return builder(ShapedRecipeBuilder.builder(result, 1)); }
    protected ShapedRecipeBuilder shapedRecipe(ItemLike result, int count) { return builder(ShapedRecipeBuilder.builder(new ItemStack(result, count))); }
    protected ShapedRecipeBuilder shapedRecipe(ItemLike result, int count, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(new ItemStack(result, count), id)); }
    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result) { return builder(ShapedRecipeBuilder.builder(result.get(), 1)); }
    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result, int count) { return builder(ShapedRecipeBuilder.builder(new ItemStack(result.get(), count))); }
    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(new ItemStack(result.get(), count), id)); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result) { return builder(ShapedRecipeBuilder.builder(result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(result, id)); }

    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result) { return builder(ShapelessRecipeBuilder.builder(result, 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result) { return builder(ShapelessRecipeBuilder.builder(result.get(), 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, int count) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result.get(), count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result.get(), count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result) { return builder(ShapelessRecipeBuilder.builder(result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(result, id)); }

    protected FurnaceRecipeBuilder smelting(ItemLike result) { return builder(FurnaceRecipeBuilder.smelting(result, 1)); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.smelting(result.get(), 1)); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder smelting(ItemStack result) { return builder(FurnaceRecipeBuilder.smelting(result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smelting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(result, id)); }

    protected FurnaceRecipeBuilder blasting(ItemLike result) { return builder(FurnaceRecipeBuilder.blasting(result, 1)); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.blasting(result.get(), 1)); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder blasting(ItemStack result) { return builder(FurnaceRecipeBuilder.blasting(result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder blasting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(result, id)); }

    protected FurnaceRecipeBuilder smoking(ItemLike result) { return builder(FurnaceRecipeBuilder.smoking(result, 1)); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.smoking(result.get(), 1)); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder smoking(ItemStack result) { return builder(FurnaceRecipeBuilder.smoking(result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smoking(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(result, id)); }

    protected FurnaceRecipeBuilder campfire(ItemLike result) { return builder(FurnaceRecipeBuilder.campfire(result, 1)); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.campfire(result.get(), 1)); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder campfire(ItemStack result) { return builder(FurnaceRecipeBuilder.campfire(result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder campfire(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(result, id)); }

    protected ShapedRecipeBuilder customShaped(ItemLike result, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(result, 1, factory)); }
    protected ShapedRecipeBuilder customShaped(ItemLike result, int count, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(new ItemStack(result, count), factory)); }
    protected ShapedRecipeBuilder customShaped(ItemLike result, int count, ResourceLocation id, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(new ItemStack(result, count), id, factory)); }
    protected ShapedRecipeBuilder customShaped(Supplier<? extends ItemLike> result, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(result.get(), 1, factory)); }
    protected ShapedRecipeBuilder customShaped(Supplier<? extends ItemLike> result, int count, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(new ItemStack(result.get(), count), factory)); }
    protected ShapedRecipeBuilder customShaped(Supplier<? extends ItemLike> result, int count, ResourceLocation id, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(new ItemStack(result.get(), count), id, factory)); }
    protected ShapedRecipeBuilder customShaped(ItemStack result, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory)); }
    protected ShapedRecipeBuilder customShaped(ItemStack result, ResourceLocation id, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(result, id, factory)); }

    protected ShapelessRecipeBuilder customShapeless(ItemLike result, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(result, 1, factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemLike result, int count, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(new ItemStack(result, count), factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemLike result, int count, ResourceLocation id, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(new ItemStack(result, count), id, factory)); }
    protected ShapelessRecipeBuilder customShapeless(Supplier<? extends ItemLike> result, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(result.get(), 1, factory)); }
    protected ShapelessRecipeBuilder customShapeless(Supplier<? extends ItemLike> result, int count, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(new ItemStack(result.get(), count), factory)); }
    protected ShapelessRecipeBuilder customShapeless(Supplier<? extends ItemLike> result, int count, ResourceLocation id, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(new ItemStack(result.get(), count), id, factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemStack result, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemStack result, ResourceLocation id, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(result, id, factory)); }

    protected FurnaceRecipeBuilder customFurnace(ItemLike result, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(result, 1, factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemLike result, int count, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(new ItemStack(result, count), factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemLike result, int count, ResourceLocation id, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(new ItemStack(result, count), id, factory)); }
    protected FurnaceRecipeBuilder customFurnace(Supplier<? extends ItemLike> result, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(result.get(), 1, factory)); }
    protected FurnaceRecipeBuilder customFurnace(Supplier<? extends ItemLike> result, int count, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(new ItemStack(result.get(), count), factory)); }
    protected FurnaceRecipeBuilder customFurnace(Supplier<? extends ItemLike> result, int count, ResourceLocation id, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(new ItemStack(result.get(), count), id, factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemStack result, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemStack result, ResourceLocation id, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(result, id, factory)); }

    protected SpecialCraftingRecipeBuilder special(ItemLike id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    protected SpecialCraftingRecipeBuilder special(Supplier<? extends ItemLike> id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id.get(), factory)); }
    protected SpecialCraftingRecipeBuilder special(ItemStack id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    protected SpecialCraftingRecipeBuilder special(String id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    protected SpecialCraftingRecipeBuilder special(ResourceLocation id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    //@formatter:on

    protected Criterion<?> enteredBlock(Block blockIn) {
        return CriteriaTriggers.ENTER_BLOCK.createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(blockIn.builtInRegistryHolder()), Optional.empty()));
    }

    protected Criterion<?> hasItem(ItemLike itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected Criterion<?> hasItem(Supplier<? extends ItemLike> itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn.get()).build());
    }

    protected Criterion<?> hasItem(TagKey<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    protected Criterion<?> hasItem(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(predicates)
                )
        );
    }

    @Override
    public String getName() {
        return modId + " Recipes.";
    }
}
