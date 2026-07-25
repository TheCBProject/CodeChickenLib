package codechicken.lib.datagen.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 27/12/20.
 */
public abstract class RecipeProvider implements DataProvider {

    private final Map<Identifier, RecipeBuilder> recipes = new HashMap<>();
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final PackOutput.PathProvider recipePath;
    private final PackOutput.PathProvider advancementPath;
    protected final String modId;

    private @Nullable HolderGetter<Item> items;

    public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modId) {
        this.registries = registries;
        this.modId = modId;
        recipePath = output.createRegistryElementsPathProvider(Registries.RECIPE);
        advancementPath = output.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
    }

    @Override
    public final CompletableFuture<Void> run(CachedOutput cache) {
        return registries.thenCompose(registries -> run(cache, registries));
    }

    private CompletableFuture<Void> run(CachedOutput cache, HolderLookup.Provider registries) {
        registerRecipes();
        List<CompletableFuture<?>> futures = new LinkedList<>();
        for (Map.Entry<Identifier, RecipeBuilder> entry : recipes.entrySet()) {
            Identifier id = entry.getKey();
            RecipeBuilder.BuiltRecipe builtRecipe = entry.getValue().build();
            futures.add(DataProvider.saveStable(
                    cache,
                    registries,
                    Recipe.CONDITIONAL_CODEC,
                    Optional.of(new WithConditions<>(builtRecipe.conditions(), builtRecipe.recipe())),
                    recipePath.json(id)
            ));

            AdvancementHolder advancement = builtRecipe.advancement();
            if (advancement != null) {
                futures.add(DataProvider.saveStable(
                        cache,
                        registries,
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

    protected HolderGetter<Item> items() {
        if (items == null) {
            if (!registries.isDone()) throw new RuntimeException("Only available inside registerRecipes() callback.");

            items = registries.join().lookupOrThrow(Registries.ITEM);
        }
        return items;
    }

    //@formatter:off
    protected ShapedRecipeBuilder shapedRecipe(ItemLike result) { return builder(ShapedRecipeBuilder.builder(items(), result, 1)); }
    protected ShapedRecipeBuilder shapedRecipe(ItemLike result, int count) { return builder(ShapedRecipeBuilder.builder(items(), new ItemStack(result, count))); }
    protected ShapedRecipeBuilder shapedRecipe(ItemLike result, int count, Identifier id) { return builder(ShapedRecipeBuilder.builder(items(), new ItemStack(result, count), id)); }
    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result) { return builder(ShapedRecipeBuilder.builder(items(), result.get(), 1)); }
    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result, int count) { return builder(ShapedRecipeBuilder.builder(items(), new ItemStack(result.get(), count))); }
    protected ShapedRecipeBuilder shapedRecipe(Supplier<? extends ItemLike> result, int count, Identifier id) { return builder(ShapedRecipeBuilder.builder(items(), new ItemStack(result.get(), count), id)); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result) { return builder(ShapedRecipeBuilder.builder(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result, Identifier id) { return builder(ShapedRecipeBuilder.builder(items(), result, id)); }

    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result) { return builder(ShapelessRecipeBuilder.builder(items(), result, 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count) { return builder(ShapelessRecipeBuilder.builder(items(), new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count, Identifier id) { return builder(ShapelessRecipeBuilder.builder(items(), new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result) { return builder(ShapelessRecipeBuilder.builder(items(), result.get(), 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, int count) { return builder(ShapelessRecipeBuilder.builder(items(), new ItemStack(result.get(), count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, int count, Identifier id) { return builder(ShapelessRecipeBuilder.builder(items(), new ItemStack(result.get(), count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result) { return builder(ShapelessRecipeBuilder.builder(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result, Identifier id) { return builder(ShapelessRecipeBuilder.builder(items(), result, id)); }

    protected FurnaceRecipeBuilder smelting(ItemLike result) { return builder(FurnaceRecipeBuilder.smelting(items(), result, 1)); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smelting(items(), new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.smelting(items(), new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.smelting(items(), result.get(), 1)); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.smelting(items(), new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.smelting(items(), new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder smelting(ItemStack result) { return builder(FurnaceRecipeBuilder.smelting(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smelting(ItemStack result, Identifier id) { return builder(FurnaceRecipeBuilder.smelting(items(), result, id)); }

    protected FurnaceRecipeBuilder blasting(ItemLike result) { return builder(FurnaceRecipeBuilder.blasting(items(), result, 1)); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.blasting(items(), new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.blasting(items(), new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.blasting(items(), result.get(), 1)); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.blasting(items(), new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.blasting(items(), new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder blasting(ItemStack result) { return builder(FurnaceRecipeBuilder.blasting(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder blasting(ItemStack result, Identifier id) { return builder(FurnaceRecipeBuilder.blasting(items(), result, id)); }

    protected FurnaceRecipeBuilder smoking(ItemLike result) { return builder(FurnaceRecipeBuilder.smoking(items(), result, 1)); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smoking(items(), new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.smoking(items(), new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.smoking(items(), result.get(), 1)); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.smoking(items(), new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.smoking(items(), new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder smoking(ItemStack result) { return builder(FurnaceRecipeBuilder.smoking(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smoking(ItemStack result, Identifier id) { return builder(FurnaceRecipeBuilder.smoking(items(), result, id)); }

    protected FurnaceRecipeBuilder campfire(ItemLike result) { return builder(FurnaceRecipeBuilder.campfire(items(), result, 1)); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.campfire(items(), new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.campfire(items(), new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.campfire(items(), result.get(), 1)); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.campfire(items(), new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count, Identifier id) { return builder(FurnaceRecipeBuilder.campfire(items(), new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder campfire(ItemStack result) { return builder(FurnaceRecipeBuilder.campfire(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder campfire(ItemStack result, Identifier id) { return builder(FurnaceRecipeBuilder.campfire(items(), result, id)); }

    protected ShapedRecipeBuilder customShaped(ItemLike result, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), result, 1, factory)); }
    protected ShapedRecipeBuilder customShaped(ItemLike result, int count, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), new ItemStack(result, count), factory)); }
    protected ShapedRecipeBuilder customShaped(ItemLike result, int count, Identifier id, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), new ItemStack(result, count), id, factory)); }
    protected ShapedRecipeBuilder customShaped(Supplier<? extends ItemLike> result, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), result.get(), 1, factory)); }
    protected ShapedRecipeBuilder customShaped(Supplier<? extends ItemLike> result, int count, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), new ItemStack(result.get(), count), factory)); }
    protected ShapedRecipeBuilder customShaped(Supplier<? extends ItemLike> result, int count, Identifier id, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), new ItemStack(result.get(), count), id, factory)); }
    protected ShapedRecipeBuilder customShaped(ItemStack result, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory)); }
    protected ShapedRecipeBuilder customShaped(ItemStack result, Identifier id, ShapedRecipeBuilder.Factory factory) { return builder(ShapedRecipeBuilder.custom(items(), result, id, factory)); }

    protected ShapelessRecipeBuilder customShapeless(ItemLike result, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), result, 1, factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemLike result, int count, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), new ItemStack(result, count), factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemLike result, int count, Identifier id, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), new ItemStack(result, count), id, factory)); }
    protected ShapelessRecipeBuilder customShapeless(Supplier<? extends ItemLike> result, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), result.get(), 1, factory)); }
    protected ShapelessRecipeBuilder customShapeless(Supplier<? extends ItemLike> result, int count, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), new ItemStack(result.get(), count), factory)); }
    protected ShapelessRecipeBuilder customShapeless(Supplier<? extends ItemLike> result, int count, Identifier id, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), new ItemStack(result.get(), count), id, factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemStack result, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory)); }
    protected ShapelessRecipeBuilder customShapeless(ItemStack result, Identifier id, ShapelessRecipeBuilder.Factory factory) { return builder(ShapelessRecipeBuilder.custom(items(), result, id, factory)); }

    protected FurnaceRecipeBuilder customFurnace(ItemLike result, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), result, 1, factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemLike result, int count, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), new ItemStack(result, count), factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemLike result, int count, Identifier id, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), new ItemStack(result, count), id, factory)); }
    protected FurnaceRecipeBuilder customFurnace(Supplier<? extends ItemLike> result, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), result.get(), 1, factory)); }
    protected FurnaceRecipeBuilder customFurnace(Supplier<? extends ItemLike> result, int count, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), new ItemStack(result.get(), count), factory)); }
    protected FurnaceRecipeBuilder customFurnace(Supplier<? extends ItemLike> result, int count, Identifier id, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), new ItemStack(result.get(), count), id, factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemStack result, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory)); }
    protected FurnaceRecipeBuilder customFurnace(ItemStack result, Identifier id, FurnaceRecipeBuilder.Factory factory) { return builder(FurnaceRecipeBuilder.custom(items(), result, id, factory)); }

    protected SpecialCraftingRecipeBuilder special(ItemLike id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    protected SpecialCraftingRecipeBuilder special(Supplier<? extends ItemLike> id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id.get(), factory)); }
    protected SpecialCraftingRecipeBuilder special(ItemStack id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    protected SpecialCraftingRecipeBuilder special(String id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    protected SpecialCraftingRecipeBuilder special(Identifier id, SpecialCraftingRecipeBuilder.Factory factory) { return builder(SpecialCraftingRecipeBuilder.builder(id, factory)); }
    //@formatter:on

    protected Criterion<?> enteredBlock(Block blockIn) {
        return CriteriaTriggers.ENTER_BLOCK.createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(blockIn.builtInRegistryHolder()), Optional.empty()));
    }

    protected Criterion<?> hasItem(ItemLike itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(items(), itemIn).build());
    }

    protected Criterion<?> hasItem(Supplier<? extends ItemLike> itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(items(), itemIn.get()).build());
    }

    protected Criterion<?> hasItem(TagKey<Item> tag) {
        return hasItem(ItemPredicate.Builder.item().of(items(), tag).build());
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
