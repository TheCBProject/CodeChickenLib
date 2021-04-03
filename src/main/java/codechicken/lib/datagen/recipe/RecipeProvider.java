package codechicken.lib.datagen.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
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
@SuppressWarnings ("UnstableApiUsage")
public abstract class RecipeProvider implements IDataProvider {

    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<ResourceLocation, RecipeBuilder> recipes = new HashMap<>();
    private final DataGenerator generator;

    public RecipeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public final void run(DirectoryCache cache) throws IOException {
        Path path = generator.getOutputFolder();
        registerRecipes();
        for (Map.Entry<ResourceLocation, RecipeBuilder> entry : recipes.entrySet()) {
            ResourceLocation id = entry.getKey();
            IFinishedRecipe finishedRecipe = entry.getValue().build();
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
    protected ShapedRecipeBuilder shapedRecipe(IItemProvider result) { return builder(ShapedRecipeBuilder.builder(result, 1)); }
    protected ShapedRecipeBuilder shapedRecipe(IItemProvider result, int count) { return builder(ShapedRecipeBuilder.builder(new ItemStack(result, count))); }
    protected ShapedRecipeBuilder shapedRecipe(IItemProvider result, int count, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(new ItemStack(result, count), id)); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result) { return builder(ShapedRecipeBuilder.builder(result, result.getItem().getRegistryName())); }
    protected ShapedRecipeBuilder shapedRecipe(ItemStack result, ResourceLocation id) { return builder(ShapedRecipeBuilder.builder(result, id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(IItemProvider result) { return builder(ShapelessRecipeBuilder.builder(result, 1)); }
    protected ShapelessRecipeBuilder shapelessRecipe(IItemProvider result, int count) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count))); }
    protected ShapelessRecipeBuilder shapelessRecipe(IItemProvider result, int count, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(new ItemStack(result, count), id)); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result) { return builder(ShapelessRecipeBuilder.builder(result, result.getItem().getRegistryName())); }
    protected ShapelessRecipeBuilder shapelessRecipe(ItemStack result, ResourceLocation id) { return builder(ShapelessRecipeBuilder.builder(result, id)); }
    protected FurnaceRecipeBuilder smelting(IItemProvider result) { return builder(FurnaceRecipeBuilder.smelting(result, 1)); }
    protected FurnaceRecipeBuilder smelting(IItemProvider result, int count) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smelting(IItemProvider result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smelting(ItemStack result) { return builder(FurnaceRecipeBuilder.smelting(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder smelting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smelting(result, id)); }
    protected FurnaceRecipeBuilder blasting(IItemProvider result) { return builder(FurnaceRecipeBuilder.blasting(result, 1)); }
    protected FurnaceRecipeBuilder blasting(IItemProvider result, int count) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder blasting(IItemProvider result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder blasting(ItemStack result) { return builder(FurnaceRecipeBuilder.blasting(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder blasting(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.blasting(result, id)); }
    protected FurnaceRecipeBuilder smoking(IItemProvider result) { return builder(FurnaceRecipeBuilder.smoking(result, 1)); }
    protected FurnaceRecipeBuilder smoking(IItemProvider result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder smoking(IItemProvider result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder smoking(ItemStack result) { return builder(FurnaceRecipeBuilder.smoking(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder smoking(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.smoking(result, id)); }
    protected FurnaceRecipeBuilder campfire(IItemProvider result) { return builder(FurnaceRecipeBuilder.campfire(result, 1)); }
    protected FurnaceRecipeBuilder campfire(IItemProvider result, int count) { return builder(FurnaceRecipeBuilder.smoking(new ItemStack(result, count))); }
    protected FurnaceRecipeBuilder campfire(IItemProvider result, int count, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(new ItemStack(result, count), id)); }
    protected FurnaceRecipeBuilder campfire(ItemStack result) { return builder(FurnaceRecipeBuilder.campfire(result, result.getItem().getRegistryName())); }
    protected FurnaceRecipeBuilder campfire(ItemStack result, ResourceLocation id) { return builder(FurnaceRecipeBuilder.campfire(result, id)); }
    protected SpecialRecipeBuilder special(SpecialRecipeSerializer<?> serializer, String id) { return builder(SpecialRecipeBuilder.builder(serializer, id)); }
    protected SpecialRecipeBuilder special(SpecialRecipeSerializer<?> serializer, ResourceLocation id) { return builder(SpecialRecipeBuilder.builder(serializer, id)); }
    //@formatter:on

    private void saveRecipe(DirectoryCache cache, JsonObject recipeJson, Path path) {
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

    private void saveRecipeAdvancement(DirectoryCache cache, JsonObject advancementJson, Path path) {
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

    protected EnterBlockTrigger.Instance enteredBlock(Block blockIn) {
        return new EnterBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY, blockIn, StatePropertiesPredicate.ANY);
    }

    protected InventoryChangeTrigger.Instance hasItem(IItemProvider itemIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected InventoryChangeTrigger.Instance hasItem(ITag<Item> tagIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    protected InventoryChangeTrigger.Instance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, predicates);
    }
}
