package codechicken.lib.inventory;

import codechicken.lib.util.ArrayUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Objects;

/**
 * IInventory implementation which saves and loads from an NBT tag
 */
public class InventoryNBT implements Container {

    protected final RegistryAccess registries;
    protected ItemStack[] items;
    protected CompoundTag tag;

    public InventoryNBT(RegistryAccess registries, int size, CompoundTag tag) {
        this.registries = registries;
        this.tag = tag;
        items = new ItemStack[size];
        ArrayUtils.fillArray(items, ItemStack.EMPTY, (Objects::isNull));
        readNBT();
    }

    private void writeNBT() {
        tag.put("items", InventoryUtils.writeItemStacksToTag(registries, items, getMaxStackSize()));
    }

    private void readNBT() {
        if (tag.contains("items")) {
            InventoryUtils.readItemStacksFromTag(registries, items, tag.getList("items", 10));
        }
    }

    @Override
    public int getContainerSize() {
        return items.length;
    }

    @Override
    public boolean isEmpty() {
        return ArrayUtils.count(items, (stack -> !stack.isEmpty())) <= 0;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items[slot];
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return InventoryUtils.decrStackSize(this, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return InventoryUtils.removeStackFromSlot(this, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items[slot] = stack;
        setChanged();
    }

    @Override
    public void setChanged() {
        writeNBT();
    }

    @Override
    public void clearContent() {
        Arrays.fill(items, ItemStack.EMPTY);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
