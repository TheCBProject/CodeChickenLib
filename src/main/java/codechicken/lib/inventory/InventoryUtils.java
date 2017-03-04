package codechicken.lib.inventory;

import codechicken.lib.util.ItemUtils;
import com.google.common.base.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;

public class InventoryUtils {

    public static boolean hasItemHandlerCap(TileEntity tileEntity, EnumFacing face) {
        return tileEntity != null && (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face) || tileEntity instanceof ISidedInventory || tileEntity instanceof IInventory);
    }

    public static IItemHandler getItemHandlerCap(TileEntity tileEntity, EnumFacing face) {
        if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)) {
            return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
        } else if (tileEntity instanceof ISidedInventory) {
            return new SidedInvWrapper(((ISidedInventory) tileEntity), face);
        } else if (tileEntity instanceof IInventory) {
            return new InvWrapper(((IInventory) tileEntity));
        }
        return new EmptyHandler();
    }

    /**
     * Constructor for ItemStack with tag
     */
    @Nonnull
    public static ItemStack newItemStack(Item item, int size, int damage, NBTTagCompound tag) {
        ItemStack stack = new ItemStack(item, size, damage);
        stack.setTagCompound(tag);
        return stack;
    }

    /**
     * Gets the actual damage of an item without asking the Item
     */
    public static int actualDamage(@Nonnull ItemStack stack) {
        return Items.DIAMOND.getDamage(stack);
    }

    /**
     * Static default implementation for IInventory method
     */
    @Nonnull
    public static ItemStack decrStackSize(IInventory inv, int slot, int size) {
        ItemStack item = inv.getStackInSlot(slot);

        if (!item.isEmpty()) {
            if (item.getCount() <= size) {
                inv.setInventorySlotContents(slot, ItemStack.EMPTY);
                inv.markDirty();
                return item;
            }
            ItemStack itemstack1 = item.splitStack(size);
            if (item.getCount() == 0) {
                inv.setInventorySlotContents(slot, ItemStack.EMPTY);
            } else {
                inv.setInventorySlotContents(slot, item);
            }

            inv.markDirty();
            return itemstack1;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Static default implementation for IInventory method
     */
    public static ItemStack removeStackFromSlot(IInventory inv, int slot) {
        ItemStack stack = inv.getStackInSlot(slot);
        inv.setInventorySlotContents(slot, ItemStack.EMPTY);
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
    public static NBTTagList writeItemStacksToTag(ItemStack[] items) {
        return writeItemStacksToTag(items, 64);
    }

    /**
     * NBT item saving function with support for stack sizes > 32K
     */
    public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity) {
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < items.length; i++) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setShort("Slot", (short) i);
            items[i].writeToNBT(tag);

            if (maxQuantity > Short.MAX_VALUE) {
                tag.setInteger("Quantity", items[i].getCount());
            } else if (maxQuantity > Byte.MAX_VALUE) {
                tag.setShort("Quantity", (short) items[i].getCount());
            }

            tagList.appendTag(tag);
        }
        return tagList;
    }

    /**
     * NBT item loading function with support for stack sizes > 32K
     */
    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList) {
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            int b = tag.getShort("Slot");
            items[b] = new ItemStack(tag);
            if (tag.hasKey("Quantity")) {
                items[b].setCount(((NBTPrimitive) tag.getTag("Quantity")).getInt());
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
        ItemStack base = inv.inv.getStackInSlot(slot);
        if (!canStack(base, stack) || !inv.canInsertItem(slot, stack)) {
            return 0;
        }

        int fit = !base.isEmpty() ? incrStackSize(base, inv.inv.getInventoryStackLimit() - base.getCount()) : inv.inv.getInventoryStackLimit();
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
                ItemStack base = inv.inv.getStackInSlot(slot);
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
                        inv.inv.setInventorySlotContents(slot, base);
                    }
                } else {
                    if (!simulate) {
                        inv.inv.setInventorySlotContents(slot, ItemUtils.copyStack(stack, fit));
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
        ItemStack stack = inv.inv.getStackInSlot(slot);
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

        return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && stack1.getCount() == stack2.getCount() && Objects.equal(stack1.getTagCompound(), stack2.getTagCompound());
    }

    /**
     * Gets an IInventory from a coordinate with support for double chests
     */
    public static IInventory getInventory(World world, BlockPos pos) {
        return getInventory(world.getTileEntity(pos));
    }

    public static IInventory getInventory(TileEntity tile) {
        if (!(tile instanceof IInventory)) {
            return null;
        }

        if (tile instanceof TileEntityChest) {
            return getChest((TileEntityChest) tile);
        }
        return (IInventory) tile;
    }

    public static IInventory getChest(TileEntityChest chest) {
        for (EnumFacing fside : Plane.HORIZONTAL) {
            if (chest.getWorld().getBlockState(chest.getPos().offset(fside)).getBlock() == chest.getBlockType()) {
                return new InventoryLargeChest("container.chestDouble", (TileEntityChest) chest.getWorld().getTileEntity(chest.getPos().offset(fside)), chest);
            }
        }
        return chest;
    }

    public static boolean canStack(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        return stack1.isEmpty() || stack2.isEmpty() || (stack1.getItem() == stack2.getItem() && (!stack2.getHasSubtypes() || stack2.getItemDamage() == stack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack2, stack1)) && stack1.isStackable();
    }

    /**
     * Consumes one item from slot in inv with support for containers.
     */
    public static void consumeItem(IInventory inv, int slot) {
        ItemStack stack = inv.getStackInSlot(slot);
        Item item = stack.getItem();
        if (item.hasContainerItem(stack)) {
            ItemStack container = item.getContainerItem(stack);
            inv.setInventorySlotContents(slot, container);
        } else {
            inv.decrStackSize(slot, 1);
        }
    }

    /**
     * Gets the size of the stack in a slot. Returns 0 on empty stacks
     */
    public static int stackSize(IInventory inv, int slot) {
        ItemStack stack = inv.getStackInSlot(slot);
        return stack.isEmpty() ? 0 : stack.getCount();
    }

    /**
     * Drops all items from inv using removeStackFromSlot
     */
    public static void dropOnClose(EntityPlayer player, IInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.removeStackFromSlot(i);
            if (!stack.isEmpty()) {
                player.dropItem(stack, false);
            }
        }
    }

    public static NBTTagCompound savePersistant(ItemStack stack, NBTTagCompound tag) {
        stack.writeToNBT(tag);
        tag.removeTag("id");
        tag.setString("name", stack.getItem().getRegistryName().toString());
        return tag;
    }

    public static ItemStack loadPersistant(NBTTagCompound tag) {
        String name = tag.getString("name");
        Item item = Item.REGISTRY.getObject(new ResourceLocation(name));
        if (item == null) {
            return ItemStack.EMPTY;
        }
        int count = tag.hasKey("Count") ? tag.getByte("Count") : 1;
        int damage = tag.hasKey("Damage") ? tag.getShort("Damage") : 0;
        ItemStack stack = new ItemStack(item, count, damage);
        if (tag.hasKey("tag", 10)) {
            stack.setTagCompound(tag.getCompoundTag("tag"));
        }
        return stack;
    }
}
