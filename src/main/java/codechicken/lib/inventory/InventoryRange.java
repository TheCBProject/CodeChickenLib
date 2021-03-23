package codechicken.lib.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

/**
 * Inventory wrapper for unified ISided/IInventory access
 */
public class InventoryRange {

    public IInventory inv;
    public Direction face;
    public ISidedInventory sidedInv;
    public int[] slots;

    @Deprecated// Use EnumFacing version.
    public InventoryRange(IInventory inv, int side) {
        this(inv, Direction.BY_3D_DATA[side]);
    }

    public InventoryRange(IInventory inv, Direction side) {
        this.inv = inv;
        this.face = side;
        if (inv instanceof ISidedInventory) {
            sidedInv = (ISidedInventory) inv;
            slots = sidedInv.getSlotsForFace(face);
        } else {
            slots = new int[inv.getContainerSize()];
            for (int i = 0; i < slots.length; i++) {
                slots[i] = i;
            }
        }
    }

    public InventoryRange(IInventory inv) {
        this(inv, Direction.DOWN);
    }

    public InventoryRange(IInventory inv, int fslot, int lslot) {
        this.inv = inv;
        slots = new int[lslot - fslot];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = fslot + i;
        }
    }

    public InventoryRange(IInventory inv, InventoryRange access) {
        this.inv = inv;
        this.slots = access.slots;
        this.face = access.face;
        if (inv instanceof ISidedInventory) {
            sidedInv = (ISidedInventory) inv;
        }
    }

    public boolean canInsertItem(int slot, @Nonnull ItemStack item) {
        return sidedInv == null ? inv.canPlaceItem(slot, item) : sidedInv.canPlaceItemThroughFace(slot, item, face);
    }

    public boolean canExtractItem(int slot, @Nonnull ItemStack item) {
        return sidedInv == null ? inv.canPlaceItem(slot, item) : sidedInv.canTakeItemThroughFace(slot, item, face);
    }

    public int lastSlot() {
        int last = 0;
        for (int slot : slots) {
            if (slot > last) {
                last = slot;
            }
        }
        return last;
    }
}
