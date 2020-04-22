package codechicken.lib.gui;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Provides a Simple way to create an ItemGroup
 *
 * Created by covers1624 on 7/11/2016.
 */
public class SimpleItemGroup extends ItemGroup {

    private final Supplier<ItemStack> stackSupplier;

    public SimpleItemGroup(String label, Supplier<ItemStack> stackSupplier) {
        super(label);
        this.stackSupplier = stackSupplier;
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return stackSupplier.get();
    }
}
