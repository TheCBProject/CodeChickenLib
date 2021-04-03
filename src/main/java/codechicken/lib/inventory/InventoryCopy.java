package codechicken.lib.inventory;

import codechicken.lib.util.ArrayUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Creates a copy of an IInventory for extended simulation
 */
public class InventoryCopy implements IInventory {

    public boolean[] accessible;
    public ItemStack[] items;
    public IInventory inv;

    public InventoryCopy(IInventory inv) {
        items = new ItemStack[inv.getContainerSize()];
        ArrayUtils.fillArray(items, ItemStack.EMPTY, (Objects::isNull));
        accessible = new boolean[inv.getContainerSize()];
        this.inv = inv;
        update();
    }

    public void update() {
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                items[i] = stack.copy();
            }
        }
    }

    public InventoryCopy open(InventoryRange access) {
        int lslot = access.lastSlot();
        if (lslot > accessible.length) {
            boolean[] l_accessible = new boolean[lslot];
            ItemStack[] l_items = new ItemStack[lslot];
            System.arraycopy(accessible, 0, l_accessible, 0, accessible.length);
            System.arraycopy(items, 0, l_items, 0, items.length);
            accessible = l_accessible;
            items = l_items;
        }

        for (int slot : access.slots) {
            accessible[slot] = true;
        }
        return this;
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
    @Nonnull
    public ItemStack getItem(int slot) {
        return items[slot];
    }

    @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        return InventoryUtils.decrStackSize(this, slot, amount);
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        return InventoryUtils.removeStackFromSlot(this, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items[slot] = stack;
        setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return inv.canPlaceItem(i, itemstack);
    }

    @Override
    public void startOpen(PlayerEntity player) {
    }

    @Override
    public void stopOpen(PlayerEntity player) {
    }

    //    @Override
    //    public int getField(int id) {
    //        return 0;
    //    }
    //
    //    @Override
    //    public void setField(int id, int value) {
    //    }
    //
    //    @Override
    //    public int getFieldCount() {
    //        return 0;
    //    }

    @Override
    public void clearContent() {
    }

    //    @Override
    //    @Nonnull
    //    public String getName() {
    //        return "copy";
    //    }
    //
    //    @Override
    //    public boolean hasCustomName() {
    //        return true;
    //    }
    //
    //    @Override
    //    @Nonnull
    //    public ITextComponent getDisplayName() {
    //        return new TextComponentString(getName());
    //    }
}
