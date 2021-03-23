package codechicken.lib.inventory;

import codechicken.lib.util.ArrayUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * IInventory implementation which saves and loads from an NBT tag
 */
public class InventoryNBT implements IInventory {

    protected ItemStack[] items;
    protected CompoundNBT tag;

    public InventoryNBT(int size, CompoundNBT tag) {
        this.tag = tag;
        items = new ItemStack[size];
        ArrayUtils.fillArray(items, ItemStack.EMPTY, (Objects::isNull));
        readNBT();
    }

    private void writeNBT() {
        tag.put("items", InventoryUtils.writeItemStacksToTag(items, getMaxStackSize()));
    }

    private void readNBT() {
        if (tag.contains("items")) {
            InventoryUtils.readItemStacksFromTag(items, tag.getList("items", 10));
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
    @Nonnull
    public ItemStack getItem(int slot) {
        return items[slot];
    }

    @Override
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
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        writeNBT();
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.length; i++) {
            items[i] = ItemStack.EMPTY;
        }
        setChanged();
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
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void startOpen(PlayerEntity player) {
    }

    @Override
    public void stopOpen(PlayerEntity player) {
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return true;
    }

    //    @Override
    //    @Nonnull
    //    public String getName() {
    //        return "NBT";
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
