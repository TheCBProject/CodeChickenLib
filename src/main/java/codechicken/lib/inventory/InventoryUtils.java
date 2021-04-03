package codechicken.lib.inventory;

import codechicken.lib.util.ItemUtils;
import com.google.common.base.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class InventoryUtils {

    @CapabilityInject (IItemHandler.class)
    public static final Capability<IItemHandler> ITEM_HANDLER = null;

    /**
     * Static default implementation for IInventory method
     */
    @Nonnull
    public static ItemStack decrStackSize(IInventory inv, int slot, int size) {
        ItemStack item = inv.getItem(slot);

        if (!item.isEmpty()) {
            if (item.getCount() <= size) {
                inv.setItem(slot, ItemStack.EMPTY);
                inv.setChanged();
                return item;
            }
            ItemStack itemstack1 = item.split(size);
            if (item.getCount() == 0) {
                inv.setItem(slot, ItemStack.EMPTY);
            } else {
                inv.setItem(slot, item);
            }

            inv.setChanged();
            return itemstack1;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Static default implementation for IInventory method
     */
    public static ItemStack removeStackFromSlot(IInventory inv, int slot) {
        ItemStack stack = inv.getItem(slot);
        inv.setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    /**
     * @return The quantity of items from addition that can be added to base
     */
    public static int incrStackSize(@Nonnull ItemStack base, @Nonnull ItemStack addition) {
        if (canStack(base, addition)) {
            return incrStackSize(base, addition.getCount());
        }

        return 0;
    }

    /**
     * @return The quantity of items from addition that can be added to base
     */
    public static int incrStackSize(@Nonnull ItemStack base, int addition) {
        int totalSize = base.getCount() + addition;

        if (totalSize <= base.getMaxStackSize()) {
            return addition;
        } else if (base.getCount() < base.getMaxStackSize()) {
            return base.getMaxStackSize() - base.getCount();
        }

        return 0;
    }

    /**
     * NBT item saving function
     */
    public static ListNBT writeItemStacksToTag(ItemStack[] items) {
        return writeItemStacksToTag(items, 64);
    }

    /**
     * NBT item saving function with support for stack sizes > 32K
     */
    public static ListNBT writeItemStacksToTag(ItemStack[] items, int maxQuantity) {
        ListNBT tagList = new ListNBT();
        for (int i = 0; i < items.length; i++) {
            CompoundNBT tag = new CompoundNBT();
            tag.putShort("Slot", (short) i);
            items[i].save(tag);

            if (maxQuantity > Short.MAX_VALUE) {
                tag.putInt("Quantity", items[i].getCount());
            } else if (maxQuantity > Byte.MAX_VALUE) {
                tag.putShort("Quantity", (short) items[i].getCount());
            }

            tagList.add(tag);
        }
        return tagList;
    }

    /**
     * NBT item loading function with support for stack sizes > 32K
     */
    public static void readItemStacksFromTag(ItemStack[] items, ListNBT tagList) {
        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT tag = tagList.getCompound(i);
            int b = tag.getShort("Slot");
            items[b] = ItemStack.of(tag);
            INBT quant = tag.get("Quantity");
            if (quant instanceof NumberNBT) {
                items[b].setCount(((NumberNBT) quant).getAsInt());
            }
        }
    }

    /**
     * Gets the maximum quantity of an item that can be inserted into inv
     */
    public static int getInsertibleQuantity(InventoryRange inv, @Nonnull ItemStack stack) {
        int quantity = 0;
        stack = ItemUtils.copyStack(stack, Integer.MAX_VALUE);
        for (int slot : inv.slots) {
            quantity += fitStackInSlot(inv, slot, stack);
        }

        return quantity;
    }

    public static int getInsertibleQuantity(IInventory inv, @Nonnull ItemStack stack) {
        return getInsertibleQuantity(new InventoryRange(inv), stack);
    }

    public static int fitStackInSlot(InventoryRange inv, int slot, ItemStack stack) {
        ItemStack base = inv.inv.getItem(slot);
        if (!canStack(base, stack) || !inv.canInsertItem(slot, stack)) {
            return 0;
        }

        int fit = !base.isEmpty() ? incrStackSize(base, inv.inv.getMaxStackSize() - base.getCount()) : inv.inv.getMaxStackSize();
        return Math.min(fit, stack.getCount());
    }

    public static int fitStackInSlot(IInventory inv, int slot, @Nonnull ItemStack stack) {
        return fitStackInSlot(new InventoryRange(inv), slot, stack);
    }

    /**
     * @param simulate If set to true, no items will actually be inserted
     * @return The number of items unable to be inserted
     */
    public static int insertItem(InventoryRange inv, @Nonnull ItemStack stack, boolean simulate) {
        stack = stack.copy();
        for (int pass = 0; pass < 2; pass++) {
            for (int slot : inv.slots) {
                ItemStack base = inv.inv.getItem(slot);
                if ((pass == 0) == (base.isEmpty())) {
                    continue;
                }
                int fit = fitStackInSlot(inv, slot, stack);
                if (fit == 0) {
                    continue;
                }

                if (!base.isEmpty()) {
                    stack.shrink(fit);
                    if (!simulate) {
                        base.grow(fit);
                        inv.inv.setItem(slot, base);
                    }
                } else {
                    if (!simulate) {
                        inv.inv.setItem(slot, ItemUtils.copyStack(stack, fit));
                    }
                    stack.shrink(fit);
                }
                if (stack.getCount() == 0) {
                    return 0;
                }
            }
        }
        return stack.getCount();
    }

    public static int insertItem(IInventory inv, @Nonnull ItemStack stack, boolean simulate) {
        return insertItem(new InventoryRange(inv), stack, simulate);
    }

    /**
     * Gets the stack in slot if it can be extracted
     */
    public static ItemStack getExtractableStack(InventoryRange inv, int slot) {
        ItemStack stack = inv.inv.getItem(slot);
        if (stack.isEmpty() || !inv.canExtractItem(slot, stack)) {
            return ItemStack.EMPTY;
        }

        return stack;
    }

    public static ItemStack getExtractableStack(IInventory inv, int slot) {
        return getExtractableStack(new InventoryRange(inv), slot);
    }

    public static boolean areStacksIdentical(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        if (stack1.isEmpty() || stack2.isEmpty()) {
            return stack1 == stack2;
        }

        return stack1.getItem() == stack2.getItem() && stack1.getDamageValue() == stack2.getDamageValue() && stack1.getCount() == stack2.getCount() && Objects.equal(stack1.getTag(), stack2.getTag());
    }

    public static boolean canStack(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        return stack1.isEmpty() || stack2.isEmpty() || (stack1.getItem() == stack2.getItem() && (stack2.getDamageValue() == stack1.getDamageValue()) && ItemStack.tagMatches(stack2, stack1)) && stack1.isStackable();
    }

    /**
     * Consumes one item from slot in inv with support for containers.
     */
    public static void consumeItem(IInventory inv, int slot) {
        ItemStack stack = inv.getItem(slot);
        Item item = stack.getItem();
        if (item.hasContainerItem(stack)) {
            ItemStack container = item.getContainerItem(stack);
            inv.setItem(slot, container);
        } else {
            inv.removeItem(slot, 1);
        }
    }

    /**
     * Gets the size of the stack in a slot. Returns 0 on empty stacks
     */
    public static int stackSize(IInventory inv, int slot) {
        ItemStack stack = inv.getItem(slot);
        return stack.isEmpty() ? 0 : stack.getCount();
    }

    /**
     * Drops all items from inv using removeStackFromSlot
     */
    public static void dropOnClose(PlayerEntity player, IInventory inv) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.removeItemNoUpdate(i);
            if (!stack.isEmpty()) {
                player.drop(stack, false);
            }
        }
    }

    public static boolean canInsertStack(IItemHandler handler, int slot, ItemStack stack) {
        return handler.insertItem(slot, stack, true) != stack;
    }

    public static boolean canExtractStack(IItemHandler handler, int slot) {
        ItemStack stack = handler.getStackInSlot(slot);
        if (!stack.isEmpty()) {
            return !handler.extractItem(slot, stack.getMaxStackSize(), true).isEmpty();
        }
        return false;
    }

    public static ItemStack insertItem(IItemHandler handler, ItemStack insert, boolean simulate) {
        insert = insert.copy();
        for (int pass = 0; pass < 2; pass++) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (pass == 0 && stack.isEmpty()) {
                    continue;
                }
                if (insert.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                insert = handler.insertItem(slot, insert, simulate);
            }
        }

        return insert;
    }

    /**
     * Counts the matching stacks.
     * Checks for insertion or extraction.
     *
     * @param handler The inventory.
     * @param filter  What we are checking for.
     * @param insert  If we are checking for insertion or extraction.
     * @return The total number of items of the specified filter type.
     */
    public static int countMatchingStacks(IItemHandler handler, ItemStack filter, boolean insert) {

        int c = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty() && ItemUtils.areStacksSameType(filter, stack) && (insert ? canInsertStack(handler, slot, stack) : canExtractStack(handler, slot))) {
                c += stack.getCount();
            }
        }
        return c;
    }

    public static int getInsertableQuantity(IItemHandler handler, ItemStack stack) {
        ItemStack copy = ItemUtils.copyStack(stack, Integer.MAX_VALUE);
        int quantity = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (canInsertStack(handler, slot, copy)) {
                ItemStack left = handler.insertItem(slot, copy, true);
                if (left.isEmpty()) {
                    quantity += copy.getCount();
                } else {
                    quantity += copy.getCount() - left.getCount();
                }
            }
        }
        return quantity;
    }
    //
    //    //region hasItemHandler_Raw
    //
    //    /**
    //     * Checks if only the capability exists on the tile for the specified face.
    //     * Overloaded methods delegate to this in the end.
    //     *
    //     * @param tile The tile.
    //     * @param face The face.
    //     * @return If the tile has the cap.
    //     */
    //    public static boolean hasItemHandler_Raw(TileEntity tile, EnumFacing face) {
    //        return tile != null && tile.hasCapability(ITEM_HANDLER, face);
    //    }
    //
    //    public static boolean hasItemHandler_Raw(TileEntity tile, int face) {
    //        return hasItemHandler_Raw(tile, EnumFacing.VALUES[face]);
    //    }
    //
    //    public static boolean hasItemHandler_Raw(IBlockAccess world, BlockPos pos, EnumFacing face) {
    //        return hasItemHandler_Raw(world.getTileEntity(pos), face);
    //    }
    //
    //    public static boolean hasItemHandler_Raw(IBlockAccess world, BlockPos pos, int face) {
    //        return hasItemHandler_Raw(world.getTileEntity(pos), face);
    //    }
    //    //endregion
    //
    //    //region hasItemHandler
    //
    //    /**
    //     * Checks if the capability exists on the tile for the specified face,
    //     * Or if the tile is an instance of the Legacy, IInventory or ISidedInventory.
    //     * Overloaded methods delegate to this in the end.
    //     *
    //     * @param tile The tile.
    //     * @param face The face.
    //     * @return If the tile has the cap, or uses legacy interfaces.
    //     */
    //    public static boolean hasItemHandler(TileEntity tile, EnumFacing face) {
    //        return hasItemHandler_Raw(tile, face) || tile instanceof IInventory || tile instanceof ISidedInventory;
    //    }
    //
    //    public static boolean hasItemHandler(TileEntity tile, int face) {
    //        return hasItemHandler(tile, EnumFacing.VALUES[face]);
    //    }
    //
    //    public static boolean hasItemHandler(IBlockAccess world, BlockPos pos, EnumFacing face) {
    //        return hasItemHandler(world.getTileEntity(pos), face);
    //    }
    //
    //    public static boolean hasItemHandler(IBlockAccess world, BlockPos pos, int face) {
    //        return hasItemHandler(world.getTileEntity(pos), face);
    //    }
    //    //endregion
    //
    //    //region getItemHandler_Raw
    //
    //    /**
    //     * Grabs the IItemHandler capability for the tile,
    //     * Will wrap if the cap doesnt exist, If you care about only interacting with the cap,
    //     * Then use {@link #hasItemHandler_Raw} to check if the tile only has the cap before calling.
    //     *
    //     * @param tile The tile.
    //     * @param face The face.
    //     * @return The handler, wrapped if the tile uses legacy interfaces and no cap.
    //     */
    //    public static IItemHandler getItemHandler_Raw(TileEntity tile, EnumFacing face) {
    //        if (hasItemHandler(tile, face)) {
    //            if (hasItemHandler_Raw(tile, face)) {
    //                return tile.getCapability(ITEM_HANDLER, face);
    //            } else {
    //                if (tile instanceof ISidedInventory && face != null) {
    //                    return new SidedInvWrapper((ISidedInventory) tile, face);
    //                } else {
    //                    return new InvWrapper((IInventory) tile);
    //                }
    //            }
    //        }
    //        return null;
    //    }
    //
    //    public static IItemHandler getItemHandler_Raw(TileEntity tile, int face) {
    //        return getItemHandler_Raw(tile, EnumFacing.VALUES[face]);
    //    }
    //
    //    public static IItemHandler getItemHandler_Raw(IBlockAccess world, BlockPos pos, EnumFacing face) {
    //        return getItemHandler_Raw(world.getTileEntity(pos), face);
    //    }
    //
    //    public static IItemHandler getItemHandler_Raw(IBlockAccess world, BlockPos pos, int face) {
    //        return getItemHandler_Raw(world.getTileEntity(pos), face);
    //    }
    //    //endregion
    //
    //    //region getItemHandlerOr
    //
    //    /**
    //     * Grabs the IItemHandler capability for the tile or the default if none.
    //     * This method guards against tiles specifying a cap exists and returning null,
    //     * in that case the default is returned.
    //     *
    //     * @param tile     The tile.
    //     * @param face     The face.
    //     * @param _default The default.
    //     * @return The handler or default.
    //     */
    //    public static IItemHandler getItemHandlerOr(TileEntity tile, EnumFacing face, IItemHandler _default) {
    //        IItemHandler handler = hasItemHandler(tile, face) ? getItemHandler_Raw(tile, face) : null;
    //        return handler != null ? handler : _default;
    //    }
    //
    //    public static IItemHandler getItemHandlerOr(TileEntity tile, int face, IItemHandler _default) {
    //        return hasItemHandler(tile, face) ? getItemHandler_Raw(tile, face) : _default;
    //    }
    //
    //    public static IItemHandler getItemHandlerOr(IBlockAccess world, BlockPos pos, EnumFacing face, IItemHandler _default) {
    //        return getItemHandlerOr(world.getTileEntity(pos), face, _default);
    //    }
    //
    //    public static IItemHandler getItemHandlerOr(IBlockAccess world, BlockPos pos, int face, IItemHandler _default) {
    //        return getItemHandlerOr(world.getTileEntity(pos), face, _default);
    //    }
    //    //endregion
    //
    //    //region getItemHandler
    //
    //    /**
    //     * Grabs the IITemHandler capability for the tile or null if none.
    //     * Overloaded methods delegate to this in the end.
    //     *
    //     * @param tile The tile.
    //     * @param face The face.
    //     * @return The handler or null.
    //     */
    //    public static IItemHandler getItemHandler(TileEntity tile, EnumFacing face) {
    //        return getItemHandlerOr(tile, face, null);
    //    }
    //
    //    public static IItemHandler getItemHandler(TileEntity tile, int face) {
    //        return getItemHandlerOr(tile, face, null);
    //    }
    //
    //    public static IItemHandler getItemHandler(IBlockAccess world, BlockPos pos, EnumFacing face) {
    //        return getItemHandlerOr(world, pos, face, null);
    //    }
    //
    //    public static IItemHandler getItemHandler(IBlockAccess world, BlockPos pos, int face) {
    //        return getItemHandlerOr(world, pos, face, null);
    //    }
    //    //endregion
    //
    //    //region getItemHandlerOrEmpty
    //
    //    /**
    //     * Grabs the IITemHandler capability for the tile or EmptyHandler.INSTANCE if none.
    //     * This method guards against tiles specify a cap exists and returning null,
    //     * in that case the empty handler is returned.
    //     * Overloaded methods delegate to this in the end.
    //     *
    //     * @param tile The tile.
    //     * @param face The face.
    //     * @return The handler or EmptyHandler.INSTANCE.
    //     */
    //    public static IItemHandler getItemHandlerOrEmpty(TileEntity tile, EnumFacing face) {
    //        return getItemHandlerOr(tile, face, EmptyHandler.INSTANCE);
    //    }
    //
    //    public static IItemHandler getItemHandlerOrEmpty(TileEntity tile, int face) {
    //        return getItemHandlerOr(tile, face, EmptyHandler.INSTANCE);
    //    }
    //
    //    public static IItemHandler getItemHandlerOrEmpty(IBlockAccess world, BlockPos pos, EnumFacing face) {
    //        return getItemHandlerOr(world.getTileEntity(pos), face, EmptyHandler.INSTANCE);
    //    }
    //
    //    public static IItemHandler getItemHandlerOrEmpty(IBlockAccess world, BlockPos pos, int face) {
    //        return getItemHandlerOr(world.getTileEntity(pos), face, EmptyHandler.INSTANCE);
    //    }
    //    //endregion
}
