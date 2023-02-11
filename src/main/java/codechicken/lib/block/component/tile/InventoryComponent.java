package codechicken.lib.block.component.tile;

import codechicken.lib.block.ModularBlockEntity;
import codechicken.lib.block.ModularTileBlock;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.util.ArrayUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedList;
import java.util.List;

/**
 * A component containing a sequence of item slots.
 * <p>
 * Created by covers1624 on 24/7/22.
 */
@ApiStatus.Experimental
public class InventoryComponent extends ModularTileBlock.TileComponent<InventoryComponent.Data> {

    private final int size;
    private final int limit;
    private final List<Listener> changeListeners = new LinkedList<>();

    private InventoryComponent(int size, int limit) {
        this.size = size;
        this.limit = limit;
    }

    /**
     * Receive a callback when this inventory is modified.
     *
     * @param listener The listener.
     */
    public void onChanged(Listener listener) {
        changeListeners.add(listener);
    }

    @Override
    protected Data createData(ModularBlockEntity ent) {
        return new Data(ent, this, size, limit);
    }

    public interface Listener {

        void onChanged(Data data);
    }

    public class Data extends ModularBlockEntity.DataComponent implements Container {

        private final ItemStack[] items;
        private final int limit;

        protected Data(ModularBlockEntity tile, ModularTileBlock.TileComponent<?> tileComponent, int size, int limit) {
            super(tile, tileComponent);
            this.items = ArrayUtils.fill(new ItemStack[size], ItemStack.EMPTY);
            this.limit = limit;
        }

        @Override
        public void clearContent() {
            ArrayUtils.fill(items, ItemStack.EMPTY);
        }

        @Override
        public int getContainerSize() {
            return items.length;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack item : items) {
                if (!item.isEmpty()) {
                    return false;
                }
            }
            return true;
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
            tile.setChanged();
            for (Listener listener : changeListeners) {
                listener.onChanged(this);
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public int getMaxStackSize() {
            return limit;
        }

        @Override
        protected void save(CompoundTag tag) {
            tag.put("Items", InventoryUtils.writeItemStacksToTag(items));
        }

        @Override
        protected void load(CompoundTag tag) {
            InventoryUtils.readItemStacksFromTag(items, tag.getList("Items", Tag.TAG_COMPOUND));
        }
    }
}
