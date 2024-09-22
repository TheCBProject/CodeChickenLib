package codechicken.lib.inventory.container;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.util.ItemUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class SlotDummy extends SlotHandleClicks {

    public final int stackLimit;

    public SlotDummy(Container inv, int slot, int x, int y) {
        this(inv, slot, x, y, 64);
    }

    public SlotDummy(Container inv, int slot, int x, int y, int limit) {
        super(inv, slot, x, y);
        stackLimit = limit;
    }

    @Override
    public void slotClick(ContainerExtended container, Player player, int button, ClickType clickType) {
        ItemStack held = player.getInventory().player.containerMenu.getCarried();
        boolean shift = clickType == ClickType.QUICK_MOVE;
        slotClick(held, button, shift);
    }

    public void slotClick(ItemStack held, int button, boolean shift) {
        ItemStack tstack = getItem();
        if (!held.isEmpty() && (tstack.isEmpty() || !InventoryUtils.canStack(held, tstack))) {
            int quantity = Math.min(held.getCount(), stackLimit);
            if (shift) {
                quantity = Math.min(stackLimit, held.getMaxStackSize() * 16);
            }
            if (button == 1) {
                quantity = 1;
            }
            set(ItemUtils.copyStack(held, quantity));
        } else if (!tstack.isEmpty()) {
            int inc;
            if (!held.isEmpty()) {
                inc = button == 1 ? -held.getCount() : held.getCount();
                if (shift) {
                    inc *= 16;
                }
            } else {
                inc = button == 1 ? -1 : 1;
                if (shift) {
                    inc *= 16;
                }
            }
            int quantity = tstack.getCount() + inc;
            if (quantity <= 0) {
                set(ItemStack.EMPTY);
            } else {
                set(ItemUtils.copyStack(tstack, quantity));
            }
        }
    }

    @Override
    public void set(ItemStack stack) {
        if (!stack.isEmpty() && stack.getCount() > stackLimit) {
            stack = ItemUtils.copyStack(stack, stackLimit);
        }
        super.set(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }
}
