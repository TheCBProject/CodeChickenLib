package codechicken.lib.model.bakery.key;

import net.minecraft.item.ItemStack;

/**
 * Created by covers1624 on 2/11/2016.
 * This is used so the bakery has some idea of the difference between generated models.
 * This is used specifically as the cache key, and is called frequently, Make sure it's well optimized.
 */
public interface IItemStackKeyGenerator {

    /**
     * Used to tell the bakery what ItemStacks are the same.
     * The bakery implementation is global, so make sure you prefix your blocks registry name and meta.
     * E.g. "thermalexpansion:machine|6"
     * Just remember, this key defines a model in the cache, if a model with the provided key is not found, your bakery will be called to generate one.
     *
     * The stack is always as follows ModelBakery > Bakery > Gen Key > Hey does this key exist in cache ? ret model : Call item specific bakery to gen model > cache > ret.
     *
     * @param stack The stack to generate a key for.
     * @return The uniquely identifiable key for the given stack.
     */
    String generateKey(ItemStack stack);

}
