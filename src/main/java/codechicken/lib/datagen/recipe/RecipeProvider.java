package codechicken.lib.datagen.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;

/**
 * Created by covers1624 on 27/12/20.
 */
public abstract class RecipeProvider implements DataProvider {

    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<ResourceLocation, RecipeBuilder> recipes = new HashMap<>();
    private final DataGenerator generator;

    public RecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public @NotNull CompletableFuture<?> run(final @NotNull CachedOutput cache) {
        return CompletableFuture.supplyAsync(() -> {
            Path path = generator.getPackOutput().getOutputFolder();
            registerRecipes();
            for (Map.Entry<ResourceLocation, RecipeBuilder> entry : recipes.entrySet()) {
                ResourceLocation id = entry.getKey();
                FinishedRecipe finishedRecipe = entry.getValue().build();
                saveRecipe(cache, finishedRecipe.serializeRecipe(), path.resolve("data/" + id.getNamespace() + "/recipes/" + id.getPath() + ".json"));

                JsonObject advancement = finishedRecipe.serializeAdvancement();
                if (advancement != null) {
                    saveRecipeAdvancement(cache, advancement, path.resolve("data/" + id.getNamespace() + "/advancements/" + id.getPath() + ".json"));
                }
            }
            return CompletableFuture.completedFuture(this);
        }, Executors.newCachedThreadPool());
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
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result) { return builder(ShapedRecipeBuilder.builder(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(result, id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result) { return builder(ShapelessRecipeBuilder.builder(result, 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result) { return builder(ShapelessRecipeBuilder.builder(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(result, id)); }
    protected FurnaceRecipeBuilder smelting(ItemLike result) { return builder(FurnaceRecipeBuilder.smelting(result, 1)); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smelting(ItemStack result) { return builder(FurnaceRecipeBuilder.smelting(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smelting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(result, id)); }
    protected FurnaceRecipeBuilder blasting(ItemLike result) { return builder(FurnaceRecipeBuilder.blasting(result, 1)); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder blasting(ItemStack result) { return builder(FurnaceRecipeBuilder.blasting(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder blasting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(result, id)); }
    protected FurnaceRecipeBuilder smoking(ItemLike result) { return builder(FurnaceRecipeBuilder.smoking(result, 1)); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smoking(ItemStack result) { return builder(FurnaceRecipeBuilder.smoking(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder smoking(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(result, id)); }
    protected FurnaceRecipeBuilder campfire(ItemLike result) { return builder(FurnaceRecipeBuilder.campfire(result, 1)); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder campfire(ItemStack result) { return builder(FurnaceRecipeBuilder.campfire(result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder campfire(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(result, id)); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemLike result) { return builder(ShapedRecipeBuilder.custom(serializer, result, 1)); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemLike result, int count) { return builder(ShapedRecipeBuilder.custom(serializer, new ItemStack(result, count))); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemLike result, int count, ResourceLocation id) { return builder(ShapedRecipeBuilder.custom(serializer, new ItemStack(result, count), id)); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemStack result) { return builder(ShapedRecipeBuilder.custom(serializer, result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapedRecipeBuilder customShaped(RecipeSerializer<?> serializer, ItemStack result, ResourceLocation id) { return builder(ShapedRecipeBuilder.custom(serializer, result, id)); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemLike result) { return builder(ShapelessRecipeBuilder.custom(serializer, result, 1)); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemLike result, int count) { return builder(ShapelessRecipeBuilder.custom(serializer, new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemLike result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.custom(serializer, new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemStack result) { return builder(ShapelessRecipeBuilder.custom(serializer, result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected ShapelessRecipeBuilder customShapeless(RecipeSerializer<?> serializer, ItemStack result, ResourceLocation id) { return builder(ShapelessRecipeBuilder.custom(serializer, result, id)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result) { return builder(FurnaceRecipeBuilder.custom(serializer, result, 1)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result, int count) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemStack result) { return builder(FurnaceRecipeBuilder.custom(serializer, result, ForgeRegistries.ITEMS.getKey(result.getItem()))); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.custom(serializer, result, id)); }
    protected SpecialRecipeBuilder special(SimpleCraftingRecipeSerializer<?> serializer, String id) { return builder(SpecialRecipeBuilder.builder(serializer, id)); }
    protected SpecialRecipeBuilder special(SimpleCraftingRecipeSerializer<?> serializer, ResourceLocation id) { return builder(SpecialRecipeBuilder.builder(serializer, id)); }
    //@formatter:on

    private void saveRecipe(CachedOutput cache, JsonObject recipeJson, Path path) {
        DataProvider.saveStable(cache, recipeJson, path);

    }

    private void saveRecipeAdvancement(CachedOutput cache, JsonObject advancementJson, Path path) {
        DataProvider.saveStable(cache, advancementJson, path);
    }

    protected EnterBlockTrigger.TriggerInstance enteredBlock(Block blockIn) {
        return new EnterBlockTrigger.TriggerInstance(ContextAwarePredicate.ANY, blockIn, StatePropertiesPredicate.ANY);
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(TagKey<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
    }
}
