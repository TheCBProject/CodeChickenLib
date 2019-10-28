//package codechicken.lib.inventory.container;
//
//import codechicken.lib.packet.PacketCustom;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.entity.player.InventoryPlayer;
//import net.minecraft.inventory.*;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.NonNullList;
//
//import javax.annotation.Nonnull;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//
//public abstract class ContainerExtended extends Container implements IContainerListener {
//
//    public LinkedList<EntityPlayerMP> playerCrafters = new LinkedList<>();
//
//    public ContainerExtended() {
//        listeners.add(this);
//    }
//
//    @Override
//    public void addListener(@Nonnull IContainerListener listener) {
//        if (listener instanceof EntityPlayerMP) {
//            playerCrafters.add((EntityPlayerMP) listener);
//            sendContainerAndContentsToPlayer(this, getInventory(), Collections.singletonList((EntityPlayerMP) listener));
//            detectAndSendChanges();
//        } else {
//            super.addListener(listener);
//        }
//    }
//
//    @Override
//    public void removeListener(@Nonnull IContainerListener listener) {
//        if (listener instanceof EntityPlayerMP) {
//            playerCrafters.remove(listener);
//        } else {
//            super.removeListener(listener);
//        }
//    }
//
//    @Override
//    public void sendAllContents(@Nonnull Container container, @Nonnull NonNullList<ItemStack> list) {
//        sendContainerAndContentsToPlayer(container, list, playerCrafters);
//    }
//
//    @Override
//    public void sendAllWindowProperties(@Nonnull Container container, @Nonnull IInventory inventory) {
//    }
//
//    public void sendContainerAndContentsToPlayer(Container container, NonNullList<ItemStack> list, List<EntityPlayerMP> playerCrafters) {
//        LinkedList<ItemStack> largeStacks = new LinkedList<>();
//        for (int i = 0; i < list.size(); i++) {
//            ItemStack stack = list.get(i);
//            if (!stack.isEmpty() && stack.getCount() > Byte.MAX_VALUE) {
//                list.set(i, ItemStack.EMPTY);
//                largeStacks.add(stack);
//            } else {
//                largeStacks.add(ItemStack.EMPTY);
//            }
//        }
//
//        for (EntityPlayerMP player : playerCrafters) {
//            player.sendAllContents(container, list);
//        }
//
//        for (int i = 0; i < largeStacks.size(); i++) {
//            ItemStack stack = largeStacks.get(i);
//            if (!stack.isEmpty()) {
//                sendLargeStack(stack, i, playerCrafters);
//            }
//        }
//    }
//
//    public void sendLargeStack(ItemStack stack, int slot, List<EntityPlayerMP> players) {
//    }
//
//    @Override
//    public void sendWindowProperty(@Nonnull Container container, int i, int j) {
//        for (EntityPlayerMP player : playerCrafters) {
//            player.sendWindowProperty(container, i, j);
//        }
//    }
//
//    @Override
//    public void sendSlotContents(@Nonnull Container container, int slot, @Nonnull ItemStack stack) {
//        if (!stack.isEmpty() && stack.getCount() > Byte.MAX_VALUE) {
//            sendLargeStack(stack, slot, playerCrafters);
//        } else {
//            for (EntityPlayerMP player : playerCrafters) {
//                player.sendSlotContents(container, slot, stack);
//            }
//        }
//    }
//
//    @Override
//    @Nonnull
//    public ItemStack slotClick(int slot, int dragType, @Nonnull ClickType clickType, @Nonnull EntityPlayer player) {
//        if (slot >= 0 && slot < inventorySlots.size()) {
//            Slot actualSlot = getSlot(slot);
//            if (actualSlot instanceof SlotHandleClicks) {
//                return ((SlotHandleClicks) actualSlot).slotClick(this, player, dragType, clickType);
//            }
//        }
//        return super.slotClick(slot, dragType, clickType, player);
//    }
//
//    @Override
//    @Nonnull
//    public ItemStack transferStackInSlot(@Nonnull EntityPlayer par1EntityPlayer, int slotIndex) {
//        ItemStack transferredStack = ItemStack.EMPTY;
//        Slot slot = inventorySlots.get(slotIndex);
//
//        if (slot != null && slot.getHasStack()) {
//            ItemStack stack = slot.getStack();
//            transferredStack = stack.copy();
//
//            if (!doMergeStackAreas(slotIndex, stack)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (stack.getCount() == 0) {
//                slot.putStack(ItemStack.EMPTY);
//            } else {
//                slot.onSlotChanged();
//            }
//        }
//
//        return transferredStack;
//    }
//
//    @Override
//    public boolean mergeItemStack(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean reverse) {
//        boolean merged = false;
//        int slotIndex = reverse ? endIndex - 1 : startIndex;
//
//        if (stack.isEmpty()) {
//            return false;
//        }
//
//        if (stack.isStackable())//search for stacks to increase
//        {
//            while (stack.getCount() > 0 && (reverse ? slotIndex >= startIndex : slotIndex < endIndex)) {
//                Slot slot = inventorySlots.get(slotIndex);
//                ItemStack slotStack = slot.getStack();
//
//                if (!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, slotStack)) {
//                    int totalStackSize = slotStack.getCount() + stack.getCount();
//                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
//                    if (totalStackSize <= maxStackSize) {
//                        stack.setCount(0);
//                        slotStack.setCount(totalStackSize);
//                        slot.onSlotChanged();
//                        merged = true;
//                    } else if (slotStack.getCount() < maxStackSize) {
//                        stack.shrink(maxStackSize - slotStack.getCount());
//                        slotStack.setCount(maxStackSize);
//                        slot.onSlotChanged();
//                        merged = true;
//                    }
//                }
//
//                slotIndex += reverse ? -1 : 1;
//            }
//        }
//
//        if (stack.getCount() > 0)//normal transfer :)
//        {
//            slotIndex = reverse ? endIndex - 1 : startIndex;
//
//            while (stack.getCount() > 0 && (reverse ? slotIndex >= startIndex : slotIndex < endIndex)) {
//                Slot slot = this.inventorySlots.get(slotIndex);
//
//                if (!slot.getHasStack() && slot.isItemValid(stack)) {
//                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
//                    if (stack.getCount() <= maxStackSize) {
//                        slot.putStack(stack.copy());
//                        slot.onSlotChanged();
//                        stack.setCount(0);
//                        merged = true;
//                    } else {
//                        slot.putStack(stack.splitStack(maxStackSize));
//                        slot.onSlotChanged();
//                        merged = true;
//                    }
//                }
//
//                slotIndex += reverse ? -1 : 1;
//            }
//        }
//
//        return merged;
//    }
//
//    public boolean doMergeStackAreas(int slotIndex, ItemStack stack) {
//        return false;
//    }
//
//    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
//        bindPlayerInventory(inventoryPlayer, 8, 84);
//    }
//
//    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int x, int y) {
//        for (int row = 0; row < 3; row++) {
//            for (int col = 0; col < 9; col++) {
//                addSlotToContainer(new Slot(inventoryPlayer, col + row * 9 + 9, x + col * 18, y + row * 18));
//            }
//        }
//        for (int slot = 0; slot < 9; slot++) {
//            addSlotToContainer(new Slot(inventoryPlayer, slot, x + slot * 18, y + 58));
//        }
//    }
//
//    @Override
//    public boolean canInteractWith(@Nonnull EntityPlayer var1) {
//        return true;
//    }
//
//    public void sendContainerPacket(PacketCustom packet) {
//        for (EntityPlayerMP player : playerCrafters) {
//            packet.sendToPlayer(player);
//        }
//    }
//
//    /**
//     * May be called from a client packet handler to handle additional info
//     */
//    public void handleOutputPacket(PacketCustom packet) {
//    }
//
//    /**
//     * May be called from a server packet handler to handle additional info
//     */
//    public void handleInputPacket(PacketCustom packet) {
//    }
//
//    /**
//     * May be called from a server packet handler to handle client input
//     */
//    public void handleGuiChange(int ID, int value) {
//    }
//
//    public void sendProgressBarUpdate(int barID, int value) {
//        for (IContainerListener crafting : listeners) {
//            crafting.sendWindowProperty(this, barID, value);
//        }
//    }
//}
