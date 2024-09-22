package codechicken.lib.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


/**
 * Inventory wrapper for unified ISided/IInventory access
 */
public class InventoryRange {

    public final Container inv;
    public @Nullable final Direction face;
    public @Nullable WorldlyContainer sidedInv;
    public final int[] slots;

    public InventoryRange(Container inv, Direction side) {
        this.inv = inv;
        this.face = side;
        if (inv instanceof WorldlyContainer) {
            sidedInv = (WorldlyContainer) inv;
            slots = sidedInv.getSlotsForFace(face);
        } else {
            slots = new int[inv.getContainerSize()];
            for (int i = 0; i < slots.length; i++) {
                slots[i] = i;
            }
        }
    }

    public InventoryRange(Container inv) {
        this(inv, Direction.DOWN);
    }

    public InventoryRange(Container inv, int fslot, int lslot) {
        this.inv = inv;
        this.face = null;
        slots = new int[lslot - fslot];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = fslot + i;
        }
    }

    public InventoryRange(Container inv, InventoryRange access) {
        this.inv = inv;
        this.slots = access.slots;
        this.face = access.face;
        if (inv instanceof WorldlyContainer) {
            sidedInv = (WorldlyContainer) inv;
        }
    }

    public boolean canInsertItem(int slot, ItemStack item) {
        return sidedInv == null ? inv.canPlaceItem(slot, item) : sidedInv.canPlaceItemThroughFace(slot, item, face);
    }

    public boolean canExtractItem(int slot, ItemStack item) {
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
