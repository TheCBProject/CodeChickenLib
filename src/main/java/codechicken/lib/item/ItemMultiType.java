package codechicken.lib.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 3/27/2016.
 */
public class ItemMultiType extends Item {

    protected HashMap<Integer, String> names = new HashMap<>();
    private int nextVariant = 0;
    private boolean hasRegistered = false;

    /**
     * @param registryName The name to be registered with."
     */
    public ItemMultiType(CreativeTabs tab, String registryName) {
        setRegistryName(registryName);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(tab);
        setUnlocalizedName(new ResourceLocation(registryName).getResourcePath());
        setMaxStackSize(64);
    }

    @Override
    public ItemMultiType setUnlocalizedName(String unlocalizedName) {
        super.setUnlocalizedName(unlocalizedName);
        return this;
    }

    @Nonnull
    public ItemStack registerItem(int meta, String name) {
        if (names.containsKey(meta)) {
            FMLLog.warning("[ItemMultiType.%s]: Variant %s with meta %s is already registered to %s with meta %s", getRegistryName(), name, meta, names.get(meta), meta);
        }
        names.put(meta, name);
        ItemStack stack = new ItemStack(this, 1, meta);
        incVariant();
        return stack;
    }

    @Nonnull
    public ItemStack registerOreDict(int meta, String name) {
        ItemStack stack = registerItem(meta, name);
        OreDictionary.registerOre(name, stack);
        return stack;
    }

    @Nonnull
    public ItemStack registerItem(String name) {
        return registerItem(nextVariant, name);
    }

    @Nonnull
    public ItemStack registerOreDict(String name) {
        ItemStack stack = registerItem(nextVariant, name);
        OreDictionary.registerOre(name, stack);
        return stack;
    }

    private void incVariant() {
        if (names.containsKey(nextVariant)) {
            nextVariant++;
            while (names.containsKey(nextVariant)) {
                nextVariant++;
            }
        }
    }

    @SuppressWarnings ("ConstantConditions")
    @SideOnly (Side.CLIENT)
    public void registerModels() {
        if (!hasRegistered) {
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "type=" + entry.getValue().toLowerCase());
                ModelLoader.setCustomModelResourceLocation(this, entry.getKey(), location);
            }
            hasRegistered = true;
        }
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (Map.Entry<Integer, String> entry : names.entrySet()) {
            subItems.add(new ItemStack(itemIn, 1, entry.getKey()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        return getUnlocalizedName() + "." + names.get(meta);
    }

}
