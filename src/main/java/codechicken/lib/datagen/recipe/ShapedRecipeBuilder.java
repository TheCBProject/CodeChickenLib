package codechicken.lib.datagen.recipe;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 28/12/20.
 */
public class ShapedRecipeBuilder extends AbstractItemStackRecipeBuilder<ShapedRecipeBuilder> {

    private static final Logger logger = LogManager.getLogger();

    private final Factory factory;
    private final List<String> patternLines = new ArrayList<>();
    private final Char2ObjectMap<Ingredient> keys = new Char2ObjectOpenHashMap<>();

    private CraftingBookCategory category = CraftingBookCategory.MISC;
    private boolean showNotification = true;

    protected ShapedRecipeBuilder(ResourceLocation id, ItemStack result, Factory factory) {
        super(id, result);
        this.factory = factory;
    }

    public static ShapedRecipeBuilder builder(ItemLike result) {
        return builder(result, 1);
    }

    public static ShapedRecipeBuilder builder(ItemLike result, int count) {
        return builder(new ItemStack(result, count));
    }

    public static ShapedRecipeBuilder builder(ItemLike result, int count, ResourceLocation id) {
        return builder(new ItemStack(result, count), id);
    }

    public static ShapedRecipeBuilder builder(Supplier<? extends ItemLike> result) {
        return builder(result.get(), 1);
    }

    public static ShapedRecipeBuilder builder(Supplier<? extends ItemLike> result, int count) {
        return builder(new ItemStack(result.get(), count));
    }

    public static ShapedRecipeBuilder builder(Supplier<? extends ItemLike> result, int count, ResourceLocation id) {
        return builder(new ItemStack(result.get(), count), id);
    }

    public static ShapedRecipeBuilder builder(ItemStack result) {
        return builder(result, BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    public static ShapedRecipeBuilder builder(ItemStack result, ResourceLocation id) {
        return new ShapedRecipeBuilder(id, result, ShapedRecipe::new);
    }

    // region Custom
    public static ShapedRecipeBuilder custom(ItemLike result, Factory factory) {
        return custom(result, 1, factory);
    }

    public static ShapedRecipeBuilder custom(ItemLike result, int count, Factory factory) {
        return custom(new ItemStack(result, count), factory);
    }

    public static ShapedRecipeBuilder custom(ItemLike result, int count, ResourceLocation id, Factory factory) {
        return custom(new ItemStack(result, count), id, factory);
    }

    public static ShapedRecipeBuilder custom(ItemStack result, Factory factory) {
        return custom(result, BuiltInRegistries.ITEM.getKey(result.getItem()), factory);
    }

    public static ShapedRecipeBuilder custom(ItemStack result, ResourceLocation id, Factory factory) {
        return new ShapedRecipeBuilder(id, result, factory);
    }
    // endregion

    public ShapedRecipeBuilder key(char key, TagKey<Item> item) {
        addAutoCriteria(item);
        return keyInternal(key, Ingredient.of(item));
    }

    public ShapedRecipeBuilder key(char key, ItemLike item) {
        addAutoCriteria(item);
        return keyInternal(key, Ingredient.of(item));
    }

    public ShapedRecipeBuilder key(char key, Supplier<? extends ItemLike> item) {
        addAutoCriteria(item.get());
        return keyInternal(key, Ingredient.of(item.get()));
    }

    public ShapedRecipeBuilder key(char key, Ingredient ingredient) {
        if (generateCriteria) {
            logger.warn("Criteria not automatically generated for raw ingredient.", new Throwable("Here, have a stack trace"));
        }
        return keyInternal(key, ingredient);
    }

    private ShapedRecipeBuilder keyInternal(char key, Ingredient ingredient) {
        if (keys.containsKey(key)) {
            throw new IllegalArgumentException("Symbol '" + key + "' is already defined!");
        }
        if (key == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        keys.put(key, ingredient);
        return this;
    }

    public ShapedRecipeBuilder patternLine(String patternIn) {
        if (!patternLines.isEmpty() && patternIn.length() != patternLines.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.patternLines.add(patternIn);
        return this;
    }

    public ShapedRecipeBuilder category(CraftingBookCategory category) {
        this.category = category;
        return this;
    }

    public ShapedRecipeBuilder showNotifications(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public Recipe<?> _build() {
        return factory.build(
                group,
                category,
                ShapedRecipePattern.of(keys, patternLines),
                result,
                showNotification
        );
    }

    @Override
    protected void validate() {
        super.validate();
        if (patternLines.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!", created);
        }
        if (patternLines.size() == 1 && patternLines.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?", created);
        }

        CharSet chars = new CharOpenHashSet(keys.keySet());
        chars.remove(' ');

        for (String line : this.patternLines) {
            for (char c : line.toCharArray()) {
                if (c == ' ') continue;
                if (!keys.containsKey(c)) {
                    throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c + "'", created);
                }
                chars.remove(c);
            }
        }

        if (!chars.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id, created);
        }
    }

    public interface Factory {

        Recipe<?> build(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification);
    }
}
