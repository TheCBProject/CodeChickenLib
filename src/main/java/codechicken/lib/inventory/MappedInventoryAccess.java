package codechicken.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappedInventoryAccess implements IInventory {

    public interface InventoryAccessor {

        boolean canAccessSlot(int slot);
    }

    public static final InventoryAccessor fullAccess = slot -> true;

    private ArrayList<Integer> slotMap = new ArrayList<>();
    private IInventory inv;
    private ArrayList<InventoryAccessor> accessors = new ArrayList<>();

    public MappedInventoryAccess(IInventory inv, InventoryAccessor... accessors) {
        this.inv = inv;
        Collections.addAll(this.accessors, accessors);
        reset();
    }

    public void reset() {
        slotMap.clear();
        nextslot:
        for (int i = 0; i < inv.getContainerSize(); i++) {
            for (InventoryAccessor a : accessors) {
                if (!a.canAccessSlot(i)) {
                    continue nextslot;
                }
            }

            slotMap.add(i);
        }
    }

    @Override
    public int getContainerSize() {
        return slotMap.size();
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack getItem(int slot) {
        return inv.getItem(slotMap.get(slot));
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        return inv.removeItem(slotMap.get(slot), amount);
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        return inv.removeItemNoUpdate(slotMap.get(slot));
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inv.setItem(slotMap.get(slot), stack);
    }

    @Override
    public int getMaxStackSize() {
        return inv.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        inv.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return inv.stillValid(player);
    }

    public void addAccessor(InventoryAccessor accessor) {
        accessors.add(accessor);
        reset();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return inv.canPlaceItem(slotMap.get(slot), stack);
    }

    @Override
    public void startOpen(PlayerEntity player) {
        inv.startOpen(player);
    }

    @Override
    public void stopOpen(PlayerEntity player) {
        inv.stopOpen(player);
    }

    //    @Override
    //    public int getField(int id) {
    //        return inv.getField(id);
    //    }
    //
    //    @Override
    //    public void setField(int id, int value) {
    //        inv.setField(id, value);
    //    }
    //
    //    @Override
    //    public int getFieldCount() {
    //        return inv.getFieldCount();
    //    }

    @Override
    public void clearContent() {
        inv.clearContent();
    }

    //    @Override
    //    @Nonnull
    //    public String getName() {
    //        return inv.getName();
    //    }
    //
    //    @Override
    //    public boolean hasCustomName() {
    //        return inv.hasCustomName();
    //    }
    //
    //    @Override
    //    @Nonnull
    //    public ITextComponent getDisplayName() {
    //        return inv.getDisplayName();
    //    }

    public List<InventoryAccessor> accessors() {
        return accessors;
    }
}
