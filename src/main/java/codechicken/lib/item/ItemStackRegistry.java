package codechicken.lib.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 22/10/2016.
 */
public class ItemStackRegistry {

    private static final Map<ResourceLocation, ItemStack> stackRegistry = new HashMap<ResourceLocation, ItemStack>();

    public static void registerCustomItemStack(String name, ItemStack stack) {
        stackRegistry.put(new ResourceLocation(Loader.instance().activeModContainer().getModId(), name), stack);
    }

    public static ItemStack findItemStack(String modId, String name, int stackSize){
        ItemStack foundStack = findItemStack(modId, name);
        if (foundStack != null){
            ItemStack copy = foundStack.copy();
            copy.stackSize = Math.min(stackSize, copy.getMaxStackSize());
            return copy;
        }
        return null;
    }

    public static ItemStack findItemStack(String modId, String name) {
        ResourceLocation stackLocation = new ResourceLocation(modId, name);
        ItemStack stack = stackRegistry.get(stackLocation);
        if (stack == null) {
            Item item = ForgeRegistries.ITEMS.getValue(stackLocation);
            if (item != null) {
                stack = new ItemStack(item, 0, 0);
            }
        }
        if (stack == null){
            Block block = ForgeRegistries.BLOCKS.getValue(stackLocation);
            if (block != null){
                stack = new ItemStack(block, 0, Short.MAX_VALUE);
            }
        }
        return stack;
    }

}
