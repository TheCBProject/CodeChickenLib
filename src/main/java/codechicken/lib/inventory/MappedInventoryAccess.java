package codechicken.lib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappedInventoryAccess implements Container {

    public interface InventoryAccessor {

        boolean canAccessSlot(int slot);
    }

    public static final InventoryAccessor fullAccess = slot -> true;

    private final ArrayList<Integer> slotMap = new ArrayList<>();
    private final Container inv;
    private final ArrayList<InventoryAccessor> accessors = new ArrayList<>();

    public MappedInventoryAccess(Container inv, InventoryAccessor... accessors) {
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
    public ItemStack getItem(int slot) {
        return inv.getItem(slotMap.get(slot));
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inv.removeItem(slotMap.get(slot), amount);
    }

    @Override
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
    public boolean stillValid(Player player) {
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
    public void startOpen(Player player) {
        inv.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        inv.stopOpen(player);
    }

    @Override
    public void clearContent() {
        inv.clearContent();
    }

    public List<InventoryAccessor> accessors() {
        return accessors;
    }
}
