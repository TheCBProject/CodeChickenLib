package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 27/12/20.
 */
public abstract class RecipeProvider implements DataProvider {

    private final Map<ResourceLocation, RecipeBuilder> recipes = new HashMap<>();
    private final PackOutput output;

    public RecipeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public final CompletableFuture<Void> run(CachedOutput cache) {
        Path path = output.getOutputFolder();
        registerRecipes();
        List<CompletableFuture<?>> futures = new LinkedList<>();
        for (Map.Entry<ResourceLocation, RecipeBuilder> entry : recipes.entrySet()) {
            ResourceLocation id = entry.getKey();
            FinishedRecipe finishedRecipe = entry.getValue().build();
            futures.add(DataProvider.saveStable(cache, finishedRecipe.serializeRecipe(), path.resolve("data/" + id.getNamespace() + "/recipes/" + id.getPath() + ".json")));

            JsonObject advancement = finishedRecipe.serializeAdvancement();
            if (advancement != null) {
                futures.add(DataProvider.saveStable(cache, advancement, path.resolve("data/" + id.getNamespace() + "/advancements/" + id.getPath() + ".json")));
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
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result) { return builder(ShapedRecipeBuilder.builder(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(result, id)); }

    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result) { return builder(ShapelessRecipeBuilder.builder(result, 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result) { return builder(ShapelessRecipeBuilder.builder(result.get(), 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, int count) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result.get(), count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result.get(), count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result) { return builder(ShapelessRecipeBuilder.builder(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(result, id)); }

    protected FurnaceRecipeBuilder smelting(ItemLike result) { return builder(FurnaceRecipeBuilder.smelting(result, 1)); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.smelting(result.get(), 1)); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder smelting(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder smelting(ItemStack result) { return builder(FurnaceRecipeBuilder.smelting(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smelting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(result, id)); }

    protected FurnaceRecipeBuilder blasting(ItemLike result) { return builder(FurnaceRecipeBuilder.blasting(result, 1)); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.blasting(result.get(), 1)); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder blasting(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder blasting(ItemStack result) { return builder(FurnaceRecipeBuilder.blasting(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder blasting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(result, id)); }

    protected FurnaceRecipeBuilder smoking(ItemLike result) { return builder(FurnaceRecipeBuilder.smoking(result, 1)); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.smoking(result.get(), 1)); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder smoking(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder smoking(ItemStack result) { return builder(FurnaceRecipeBuilder.smoking(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smoking(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(result, id)); }

    protected FurnaceRecipeBuilder campfire(ItemLike result) { return builder(FurnaceRecipeBuilder.campfire(result, 1)); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.campfire(result.get(), 1)); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder campfire(Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder campfire(ItemStack result) { return builder(FurnaceRecipeBuilder.campfire(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder campfire(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(result, id)); }

    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemLike result) { return builder(ShapedRecipeBuilder.custom(serializer, result, 1)); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemLike result, int count) { return builder(ShapedRecipeBuilder.custom(serializer, new ItemStack(result, count))); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemLike result, int count, ResourceLocation id) { return builder(ShapedRecipeBuilder.custom(serializer, new ItemStack(result, count), id)); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result) { return builder(ShapedRecipeBuilder.custom(serializer, result.get(), 1)); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result, int count) { return builder(ShapedRecipeBuilder.custom(serializer, new ItemStack(result.get(), count))); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(ShapedRecipeBuilder.custom(serializer, new ItemStack(result.get(), count), id)); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemStack result) { return builder(ShapedRecipeBuilder.custom(serializer, result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemStack result, ResourceLocation id) { return builder(ShapedRecipeBuilder.custom(serializer, result, id)); }

    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemLike result) { return builder(ShapelessRecipeBuilder.custom(serializer, result, 1)); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemLike result, int count) { return builder(ShapelessRecipeBuilder.custom(serializer, new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemLike result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.custom(serializer, new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result) { return builder(ShapelessRecipeBuilder.custom(serializer, result.get(), 1)); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result, int count) { return builder(ShapelessRecipeBuilder.custom(serializer, new ItemStack(result.get(), count))); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.custom(serializer, new ItemStack(result.get(), count), id)); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemStack result) { return builder(ShapelessRecipeBuilder.custom(serializer, result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemStack result, ResourceLocation id) { return builder(ShapelessRecipeBuilder.custom(serializer, result, id)); }

    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result) { return builder(FurnaceRecipeBuilder.custom(serializer, result, 1)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result, int count) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result) { return builder(FurnaceRecipeBuilder.custom(serializer, result.get(), 1)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result, int count) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result.get(), count))); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, Supplier<? extends ItemLike> result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result.get(), count), id)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemStack result) { return builder(FurnaceRecipeBuilder.custom(serializer, result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.custom(serializer, result, id)); }

    protected SpecialCraftingRecipeBuilder special(SimpleCraftingRecipeSerializer<?> serializer, String id) { return builder(SpecialCraftingRecipeBuilder.builder(serializer, id)); }
    protected SpecialCraftingRecipeBuilder special(SimpleCraftingRecipeSerializer<?> serializer, ResourceLocation id) { return builder(SpecialCraftingRecipeBuilder.builder(serializer, id)); }
    //@formatter:on

    protected EnterBlockTrigger.TriggerInstance enteredBlock(Block blockIn) {
        return new EnterBlockTrigger.TriggerInstance(ContextAwarePredicate.ANY, blockIn, StatePropertiesPredicate.ANY);
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(Supplier<? extends ItemLike> itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn.get()).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(TagKey<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
    }
}
