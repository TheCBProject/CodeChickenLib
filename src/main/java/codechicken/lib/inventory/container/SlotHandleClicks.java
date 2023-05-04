package codechicken.lib.inventory.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public abstract class SlotHandleClicks extends Slot {

    public SlotHandleClicks(Container inv, int slot, int x, int y) {
        super(inv, slot, x, y);
    }

    public abstract void slotClick(ContainerExtended container, Player player, int button, ClickType clickType);
}
