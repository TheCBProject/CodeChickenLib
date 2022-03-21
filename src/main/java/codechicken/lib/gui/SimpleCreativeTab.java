package codechicken.lib.gui;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Provides a Simple way to create a {@link CreativeModeTab}
 * <p>
 * Created by covers1624 on 7/11/2016.
 */
public class SimpleCreativeTab extends CreativeModeTab {

    private final Supplier<ItemStack> stackSupplier;

    public SimpleCreativeTab(String label, Supplier<ItemStack> stackSupplier) {
        super(label);
        this.stackSupplier = stackSupplier;
    }

    @Nonnull
    @Override
    public ItemStack makeIcon() {
        return stackSupplier.get();
    }
}
