package codechicken.lib.item.filtering;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This is an item filter.
 * Implement it and do stuff, Its pretty simple.
 * There are some default filters provided as well.
 */
public interface IItemFilter {

    /**
     * Checks to see if the item matches the filter.
     * True means the item is allowed, false the item is not allowed.
     *
     * @param item Item to check.
     * @return True if the item matches the filter.
     */
    boolean matches(@Nonnull ItemStack item);
}
