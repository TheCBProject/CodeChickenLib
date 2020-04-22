package codechicken.lib.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class SlotHandleClicks extends Slot {

    public SlotHandleClicks(IInventory inv, int slot, int x, int y) {
        super(inv, slot, x, y);
    }

    public abstract ItemStack slotClick(ContainerExtended container, PlayerEntity player, int button, ClickType clickType);
}
