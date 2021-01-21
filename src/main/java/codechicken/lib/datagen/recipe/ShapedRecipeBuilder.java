package codechicken.lib.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 28/12/20.
 */
public class ShapedRecipeBuilder extends AbstractItemStackRecipeBuilder<ShapedRecipeBuilder> {

    private final List<String> patternLines = new ArrayList<>();
    private final Char2ObjectMap<Ingredient> keys = new Char2ObjectOpenHashMap<>();

    protected ShapedRecipeBuilder(IRecipeSerializer<?> serializer, ResourceLocation id, ItemStack result) {
        super(serializer, id, result);
    }

    public static ShapedRecipeBuilder builder(IItemProvider result) {
        return builder(result, 1);
    }

    public static ShapedRecipeBuilder builder(IItemProvider result, int count) {
        return builder(new ItemStack(result, count));
    }

    public static ShapedRecipeBuilder builder(IItemProvider result, int count, ResourceLocation id) {
        return builder(new ItemStack(result, count), id);
    }

    public static ShapedRecipeBuilder builder(ItemStack result) {
        return builder(result, result.getItem().getRegistryName());
    }

    public static ShapedRecipeBuilder builder(ItemStack result, ResourceLocation id) {
        return new ShapedRecipeBuilder(IRecipeSerializer.CRAFTING_SHAPED, id, result);
    }

    public ShapedRecipeBuilder key(char key, Tag<Item> item) {
        return key(key, Ingredient.fromTag(item));
    }

    public ShapedRecipeBuilder key(char key, IItemProvider item) {
        return key(key, Ingredient.fromItems(item));
    }

    public ShapedRecipeBuilder key(char key, Ingredient ingredient) {
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

    @Override
    public AbstractItemStackFinishedRecipe _build() {
        return new FinishedShapedRecipe();
    }

    @Override
    protected void validate() {
        super.validate();
        if (patternLines.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        }
        if (patternLines.size() == 1 && patternLines.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
        }

        CharSet chars = new CharOpenHashSet(keys.keySet());
        chars.remove(' ');

        for (String line : this.patternLines) {
            for (char c : line.toCharArray()) {
                if (c == ' ') { continue; }
                if (!keys.containsKey(c)) {
                    throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c + "'");
                }
                chars.remove(c);
            }
        }

        if (!chars.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
        }
    }

    public class FinishedShapedRecipe extends AbstractItemStackFinishedRecipe {

        @Override
        public void serialize(JsonObject json) {
            super.serialize(json);
            JsonArray pattern = new JsonArray();
            patternLines.forEach(pattern::add);
            json.add("pattern", pattern);

            JsonObject key = new JsonObject();
            for (Char2ObjectMap.Entry<Ingredient> entry : keys.char2ObjectEntrySet()) {
                key.add(String.valueOf(entry.getCharKey()), entry.getValue().serialize());
            }
            json.add("key", key);
        }
    }
}
