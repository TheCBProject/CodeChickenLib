package codechicken.lib.inventory;

import codechicken.lib.util.ArrayUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * Simple IInventory implementation with an array of items, name and maximum stack size
 */
public class InventorySimple implements IInventory/*, ICapabilityProvider*/ {

    public ItemStack[] items;
    public int limit;
    public String name;

    public InventorySimple(ItemStack[] items, int limit, String name) {
        this.items = items;
        ArrayUtils.fillArray(items, ItemStack.EMPTY, (Objects::isNull));
        this.limit = limit;
        this.name = name;
    }

    public InventorySimple(ItemStack[] items, String name) {
        this(items, 64, name);
    }

    public InventorySimple(ItemStack[] items, int limit) {
        this(items, limit, "inv");
    }

    public InventorySimple(ItemStack[] items) {
        this(items, 64, "inv");
    }

    public InventorySimple(int size, int limit, String name) {
        this(new ItemStack[size], limit, name);
    }

    public InventorySimple(int size, int limit) {
        this(size, limit, "inv");
    }

    public InventorySimple(int size, String name) {
        this(size, 64, name);
    }

    public InventorySimple(int size) {
        this(size, 64, "inv");
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
        return limit;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public void setChanged() {
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
        Arrays.fill(items, ItemStack.EMPTY);
    }

    //    @Override
    //    @Nonnull
    //    public String getName() {
    //        return name;
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
    //
    //    @Override
    //    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
    //        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    //    }
    //
    //    @SuppressWarnings ("unchecked")
    //    @Override
    //    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
    //        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    //            return (T) new InvWrapper(this);
    //        }
    //        return null;
    //    }
}
