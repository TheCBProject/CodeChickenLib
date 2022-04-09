package codechicken.lib.datagen.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
    public final void run(HashCache cache) throws IOException {
        Path path = generator.getOutputFolder();
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
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result) { return builder(ShapedRecipeBuilder.builder(result, result.getItem().getRegistryName())); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(result, id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result) { return builder(ShapelessRecipeBuilder.builder(result, 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemLike result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result) { return builder(ShapelessRecipeBuilder.builder(result, result.getItem().getRegistryName())); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(result, id)); }
    protected FurnaceRecipeBuilder smelting(ItemLike result) { return builder(FurnaceRecipeBuilder.smelting(result, 1)); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smelting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smelting(ItemStack result) { return builder(FurnaceRecipeBuilder.smelting(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder smelting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(result, id)); }
    protected FurnaceRecipeBuilder blasting(ItemLike result) { return builder(FurnaceRecipeBuilder.blasting(result, 1)); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder blasting(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder blasting(ItemStack result) { return builder(FurnaceRecipeBuilder.blasting(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder blasting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(result, id)); }
    protected FurnaceRecipeBuilder smoking(ItemLike result) { return builder(FurnaceRecipeBuilder.smoking(result, 1)); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smoking(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smoking(ItemStack result) { return builder(FurnaceRecipeBuilder.smoking(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder smoking(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(result, id)); }
    protected FurnaceRecipeBuilder campfire(ItemLike result) { return builder(FurnaceRecipeBuilder.campfire(result, 1)); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder campfire(ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder campfire(ItemStack result) { return builder(FurnaceRecipeBuilder.campfire(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder campfire(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(result, id)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result) { return builder(FurnaceRecipeBuilder.custom(serializer, result, 1)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result, int count) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemLike result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.custom(serializer, new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemStack result) { return builder(FurnaceRecipeBuilder.custom(serializer, result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder customFurnace(RecipeSerializer<?> serializer, ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.custom(serializer, result, id)); }
    protected SpecialRecipeBuilder special(SimpleRecipeSerializer<?> serializer, String id) { return builder(SpecialRecipeBuilder.builder(serializer, id)); }
    protected SpecialRecipeBuilder special(SimpleRecipeSerializer<?> serializer, ResourceLocation id) { return builder(SpecialRecipeBuilder.builder(serializer, id)); }
    //@formatter:on

    private void saveRecipe(HashCache cache, JsonObject recipeJson, Path path) {
        try {
            String json = GSON.toJson(recipeJson);
            String hash = SHA1.hashUnencodedChars(json).toString();
            if (!hash.equals(cache.getHash(path)) || !Files.exists(path)) {
                Files.createDirectories(path.getParent());

                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(json);
                }
            }

            cache.putNew(path, hash);
        } catch (IOException e) {
            logger.error("Couldn't save recipe {}", path, e);
        }

    }

    private void saveRecipeAdvancement(HashCache cache, JsonObject advancementJson, Path path) {
        try {
            String json = GSON.toJson(advancementJson);
            String hash = SHA1.hashUnencodedChars(json).toString();
            if (!hash.equals(cache.getHash(path)) || !Files.exists(path)) {
                Files.createDirectories(path.getParent());

                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(json);
                }
            }

            cache.putNew(path, hash);
        } catch (IOException e) {
            logger.error("Couldn't save recipe advancement {}", path, e);
        }
    }

    protected EnterBlockTrigger.TriggerInstance enteredBlock(Block blockIn) {
        return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, blockIn, StatePropertiesPredicate.ANY);
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(TagKey<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
    }
}
