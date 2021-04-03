package codechicken.lib.inventory.container;

import codechicken.lib.packet.PacketCustom;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class ContainerExtended extends Container implements IContainerListener {

    public LinkedList<ServerPlayerEntity> playerCrafters = new LinkedList<>();

    protected ContainerExtended(ContainerType<?> type, int id) {
        super(type, id);
        containerListeners.add(this);
    }

    @Override
    public void addSlotListener(@Nonnull IContainerListener listener) {
        if (listener instanceof ServerPlayerEntity) {
            playerCrafters.add((ServerPlayerEntity) listener);
            sendContainerAndContentsToPlayer(this, getItems(), Collections.singletonList((ServerPlayerEntity) listener));
            broadcastChanges();
        } else {
            super.addSlotListener(listener);
        }
    }

    @Override
    public void removeSlotListener(@Nonnull IContainerListener listener) {
        if (listener instanceof ServerPlayerEntity) {
            playerCrafters.remove(listener);
        } else {
            super.removeSlotListener(listener);
        }
    }

    @Override
    public void refreshContainer(@Nonnull Container container, @Nonnull NonNullList<ItemStack> list) {
        sendContainerAndContentsToPlayer(container, list, playerCrafters);
    }

    public void sendContainerAndContentsToPlayer(Container container, NonNullList<ItemStack> list, List<ServerPlayerEntity> playerCrafters) {
        LinkedList<ItemStack> largeStacks = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (!stack.isEmpty() && stack.getCount() > Byte.MAX_VALUE) {
                list.set(i, ItemStack.EMPTY);
                largeStacks.add(stack);
            } else {
                largeStacks.add(ItemStack.EMPTY);
            }
        }

        for (ServerPlayerEntity player : playerCrafters) {
            player.refreshContainer(container, list);
        }

        for (int i = 0; i < largeStacks.size(); i++) {
            ItemStack stack = largeStacks.get(i);
            if (!stack.isEmpty()) {
                sendLargeStack(stack, i, playerCrafters);
            }
        }
    }

    public void sendLargeStack(ItemStack stack, int slot, List<ServerPlayerEntity> players) {
    }

    @Override
    public void setContainerData(@Nonnull Container container, int i, int j) {
        for (ServerPlayerEntity player : playerCrafters) {
            player.setContainerData(container, i, j);
        }
    }

    @Override
    public void slotChanged(@Nonnull Container container, int slot, @Nonnull ItemStack stack) {
        if (!stack.isEmpty() && stack.getCount() > Byte.MAX_VALUE) {
            sendLargeStack(stack, slot, playerCrafters);
        } else {
            for (ServerPlayerEntity player : playerCrafters) {
                player.slotChanged(container, slot, stack);
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack clicked(int slot, int dragType, @Nonnull ClickType clickType, @Nonnull PlayerEntity player) {
        if (slot >= 0 && slot < slots.size()) {
            Slot actualSlot = getSlot(slot);
            if (actualSlot instanceof SlotHandleClicks) {
                return ((SlotHandleClicks) actualSlot).slotClick(this, player, dragType, clickType);
            }
        }
        return super.clicked(slot, dragType, clickType, player);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull PlayerEntity par1EntityPlayer, int slotIndex) {
        ItemStack transferredStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            transferredStack = stack.copy();

            if (!doMergeStackAreas(slotIndex, stack)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return transferredStack;
    }

    @Override
    public boolean moveItemStackTo(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean reverse) {
        boolean merged = false;
        int slotIndex = reverse ? endIndex - 1 : startIndex;

        if (stack.isEmpty()) {
            return false;
        }

        if (stack.isStackable()) {//search for stacks to increase
            while (stack.getCount() > 0 && (reverse ? slotIndex >= startIndex : slotIndex < endIndex)) {
                Slot slot = slots.get(slotIndex);
                ItemStack slotStack = slot.getItem();

                if (!slotStack.isEmpty() && consideredTheSameItem(stack, slotStack)) {
                    int totalStackSize = slotStack.getCount() + stack.getCount();
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    if (totalStackSize <= maxStackSize) {
                        stack.setCount(0);
                        slotStack.setCount(totalStackSize);
                        slot.setChanged();
                        merged = true;
                    } else if (slotStack.getCount() < maxStackSize) {
                        stack.shrink(maxStackSize - slotStack.getCount());
                        slotStack.setCount(maxStackSize);
                        slot.setChanged();
                        merged = true;
                    }
                }
                slotIndex += reverse ? -1 : 1;
            }
        }

        if (stack.getCount() > 0) {//normal transfer :)
            slotIndex = reverse ? endIndex - 1 : startIndex;
            while (stack.getCount() > 0 && (reverse ? slotIndex >= startIndex : slotIndex < endIndex)) {
                Slot slot = this.slots.get(slotIndex);

                if (!slot.hasItem() && slot.mayPlace(stack)) {
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    if (stack.getCount() <= maxStackSize) {
                        slot.set(stack.copy());
                        slot.setChanged();
                        stack.setCount(0);
                    } else {
                        slot.set(stack.split(maxStackSize));
                        slot.setChanged();
                    }
                    merged = true;
                }
                slotIndex += reverse ? -1 : 1;
            }
        }

        return merged;
    }

    public boolean doMergeStackAreas(int slotIndex, ItemStack stack) {
        return false;
    }

    protected void bindPlayerInventory(PlayerInventory inventoryPlayer) {
        bindPlayerInventory(inventoryPlayer, 8, 84);
    }

    protected void bindPlayerInventory(PlayerInventory inventoryPlayer, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventoryPlayer, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        for (int slot = 0; slot < 9; slot++) {
            addSlot(new Slot(inventoryPlayer, slot, x + slot * 18, y + 58));
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity var1) {
        return true;
    }

    public void sendContainerPacket(PacketCustom packet) {
        for (ServerPlayerEntity player : playerCrafters) {
            packet.sendToPlayer(player);
        }
    }

    /**
     * May be called from a client packet handler to handle additional info
     */
    public void handleOutputPacket(PacketCustom packet) {
    }

    /**
     * May be called from a server packet handler to handle additional info
     */
    public void handleInputPacket(PacketCustom packet) {
    }

    /**
     * May be called from a server packet handler to handle client input
     */
    public void handleGuiChange(int ID, int value) {
    }

    public void sendProgressBarUpdate(int barID, int value) {
        for (IContainerListener crafting : containerListeners) {
            crafting.setContainerData(this, barID, value);
        }
    }
}
