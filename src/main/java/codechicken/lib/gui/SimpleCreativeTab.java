package codechicken.lib.gui;

import codechicken.lib.internal.CCLLog;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 7/11/2016.
 * Provides a Simple way to create a CreativeTab
 */
public class SimpleCreativeTab extends CreativeTabs {

    private static final boolean IGNORE_INVALID = System.getProperty("ccl.ignoreInvalidTabItem", "false").equalsIgnoreCase("true");

    private Supplier<ItemStack> stackSupplier;

    /**
     * Create a SimpleCreativeTab!
     * The tab icon will be "baked" when the icon is requested the first time.
     * If the item cannot be found it will throw a RuntimeException, This can be avoided by setting "-Dccl.ignoreInvalidTabItem=true" in your command line args, this will default the tab to Redstone.
     *
     * @param tabLabel The label to be displayed, This WILL be localized by minecraft.
     * @param tabIcon  The item to find in the Item Registry e.g "minecraft:redstone" or "chickenchunks:chickenChunkLoader"
     */
    @Deprecated//I Recommend using The Supplier variant.
    public SimpleCreativeTab(String tabLabel, String tabIcon) {
        this(tabLabel, tabIcon, 0);
    }

    /**
     * Create a SimpleCreativeTab!
     * The tab icon will be "baked" when the icon is requested the first time.
     * If the item cannot be found it will throw a RuntimeException, This can be avoided by setting "-Dccl.ignoreInvalidTabItem=true" in your command line args, this will default the tab to Redstone.
     *
     * @param tabLabel The label to be displayed, This WILL be localized by minecraft.
     * @param name     The item to find in the Item Registry e.g "minecraft:redstone" or "chickenchunks:chickenChunkLoader"
     * @param meta     The metadata of the item.
     */
    @Deprecated//I Recommend using The Supplier variant.
    public SimpleCreativeTab(String tabLabel, String name, int meta) {
        this(tabLabel, () -> {
            ItemStack stack = new ItemStack(Items.REDSTONE);
            ResourceLocation location = new ResourceLocation(name);
            Item item = ForgeRegistries.ITEMS.getValue(location);
            if (item == null) {
                String error = String.format("Error creating SimpleCreativeTab with name %s, unable to find \"%s\" in the Item Registry. Please ensure the name of the item is correct.", tabLabel, name);
                if (IGNORE_INVALID) {
                    CCLLog.big(Level.WARN, error);
                } else {
                    throw new IllegalArgumentException(error);
                }
            } else {
                stack = new ItemStack(item, 1, meta);
            }
            return stack;
        });
    }

    public SimpleCreativeTab(String label, Supplier<ItemStack> stackSupplier) {
        super(label);
        this.stackSupplier = stackSupplier;
    }

    @Override
    @Nonnull
    public ItemStack getTabIconItem() {
        return stackSupplier.get();
    }
}
