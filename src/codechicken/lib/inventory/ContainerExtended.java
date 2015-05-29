package codechicken.lib.inventory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IClientPacketHandler;
import codechicken.lib.packet.PacketCustom.IServerPacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import org.apache.logging.log4j.LogManager;

/**
 * Clean container implementation with a few extra features.
 * Easy shift-click handling.
 * Hooks for slot clicks, large stack sizes and some networking.
 */
public abstract class ContainerExtended extends Container implements ICrafting
{
    private static final String netChannel = "CCL:Container";
    private static int nextNetworkID = 0;
    static {
        PacketCustom.assignHandler(netChannel, new IClientPacketHandler()
        {
            @Override
            public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler) {
                Container cont = mc.thePlayer.openContainer;
                if(!(cont instanceof ContainerExtended))
                    return;

                ContainerExtended c = (ContainerExtended)cont;
                if(packet.getType() == 1)
                    c.netID = packet.readInt();
                else if(c.netID == packet.readInt())
                    c.handleClientPacket(packet);
            }
        });
        PacketCustom.assignHandler(netChannel, new IServerPacketHandler()
        {
            @Override
            public void handlePacket(PacketCustom packet, EntityPlayerMP sender, INetHandlerPlayServer handler) {
                Container cont = sender.openContainer;
                if(!(cont instanceof ContainerExtended))
                    return;

                ContainerExtended c = (ContainerExtended)cont;
                if(c.netID == packet.readInt())
                    c.handleServerPacket(packet);
            }
        });
    }

    public LinkedList<EntityPlayerMP> playerCrafters = new LinkedList<EntityPlayerMP>();
    private int netID;

    public ContainerExtended() {
        crafters.add(this);
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            netID = ++nextNetworkID;
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        if (icrafting instanceof EntityPlayerMP) {
            playerCrafters.add((EntityPlayerMP) icrafting);
            sendNetID((EntityPlayerMP) icrafting);
            sendContainerAndContentsToPlayer(this, getInventory(), Arrays.asList((EntityPlayerMP) icrafting));
            detectAndSendChanges();
        } else
            super.addCraftingToCrafters(icrafting);
    }

    private void sendNetID(EntityPlayerMP player) {
        if(netID == 0)
            LogManager.getLogger("CodeChickenLib").error("Player added to container with 0 network ID");
        else
            new PacketCustom(netChannel, 1).writeInt(netID).sendToPlayer(player);
    }

    @Override
    public void removeCraftingFromCrafters(ICrafting icrafting) {
        if (icrafting instanceof EntityPlayerMP)
            playerCrafters.remove(icrafting);
        else
            super.removeCraftingFromCrafters(icrafting);
    }

    @Override
    public void sendContainerAndContentsToPlayer(Container container, List list) {
        sendContainerAndContentsToPlayer(container, list, playerCrafters);
    }

    public void sendContainerAndContentsToPlayer(Container container, List<ItemStack> list, List<EntityPlayerMP> playerCrafters) {
        LinkedList<ItemStack> largeStacks = new LinkedList<ItemStack>();
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack != null && stack.stackSize > Byte.MAX_VALUE) {
                list.set(i, null);
                largeStacks.add(stack);
            } else
                largeStacks.add(null);
        }

        for (EntityPlayerMP player : playerCrafters)
            player.sendContainerAndContentsToPlayer(container, list);

        for (int i = 0; i < largeStacks.size(); i++) {
            ItemStack stack = largeStacks.get(i);
            if (stack != null)
                sendLargeStack(stack, i, playerCrafters);
        }
    }

    public void sendLargeStack(ItemStack stack, int slot, List<EntityPlayerMP> players) {
    }

    @Override
    public void sendProgressBarUpdate(Container container, int i, int j) {
        for (EntityPlayerMP player : playerCrafters)
            player.sendProgressBarUpdate(container, i, j);
    }

    @Override
    public void sendSlotContents(Container container, int slot, ItemStack stack) {
        if (stack != null && stack.stackSize > Byte.MAX_VALUE)
            sendLargeStack(stack, slot, playerCrafters);
        else
            for (EntityPlayerMP player : playerCrafters)
                player.sendSlotContents(container, slot, stack);
    }

    @Override
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer player) {
        if (par1 >= 0 && par1 < inventorySlots.size()) {
            Slot slot = getSlot(par1);
            if (slot instanceof SlotHandleClicks)
                return ((SlotHandleClicks) slot).slotClick(this, player, par2, par3);
        }
        return super.slotClick(par1, par2, par3, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotIndex) {
        ItemStack transferredStack = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            transferredStack = stack.copy();

            if (!doMergeStackAreas(slotIndex, stack))
                return null;

            if (stack.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }

        return transferredStack;
    }

    @Override
    public boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverse) {
        boolean merged = false;
        int slotIndex = reverse ? endIndex - 1 : startIndex;

        if (stack == null)
            return false;

        if (stack.isStackable())//search for stacks to increase
        {
            while (stack.stackSize > 0 && (reverse ? slotIndex >= startIndex : slotIndex < endIndex)) {
                Slot slot = (Slot) inventorySlots.get(slotIndex);
                ItemStack slotStack = slot.getStack();

                if (slotStack != null && slotStack.getItem() == stack.getItem() &&
                        (!stack.getHasSubtypes() || stack.getItemDamage() == slotStack.getItemDamage()) &&
                        ItemStack.areItemStackTagsEqual(stack, slotStack)) {
                    int totalStackSize = slotStack.stackSize + stack.stackSize;
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
                    if (totalStackSize <= maxStackSize) {
                        stack.stackSize = 0;
                        slotStack.stackSize = totalStackSize;
                        slot.onSlotChanged();
                        merged = true;
                    } else if (slotStack.stackSize < maxStackSize) {
                        stack.stackSize -= maxStackSize - slotStack.stackSize;
                        slotStack.stackSize = maxStackSize;
                        slot.onSlotChanged();
                        merged = true;
                    }
                }

                slotIndex += reverse ? -1 : 1;
            }
        }

        if (stack.stackSize > 0)//normal transfer :)
        {
            slotIndex = reverse ? endIndex - 1 : startIndex;

            while (stack.stackSize > 0 && (reverse ? slotIndex >= startIndex : slotIndex < endIndex)) {
                Slot slot = (Slot) this.inventorySlots.get(slotIndex);

                if (!slot.getHasStack() && slot.isItemValid(stack)) {
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
                    if (stack.stackSize <= maxStackSize) {
                        slot.putStack(stack.copy());
                        slot.onSlotChanged();
                        stack.stackSize = 0;
                        merged = true;
                    } else {
                        slot.putStack(stack.splitStack(maxStackSize));
                        slot.onSlotChanged();
                        merged = true;
                    }
                }

                slotIndex += reverse ? -1 : 1;
            }
        }

        return merged;
    }

    /**
     * Called when slotIndex is shift clicked on. Recommended implementation is to call mergeItemStack based on slotIndex
     *
     * @param stack The stack in the clicked slot
     * @return True if one or more items were moved from this slots into other slots
     */
    public boolean doMergeStackAreas(int slotIndex, ItemStack stack) {
        return false;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        bindPlayerInventory(inventoryPlayer, 8, 84);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int x, int y) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlotToContainer(new Slot(inventoryPlayer, col + row * 9 + 9, x + col * 18, y + row * 18));
        for (int slot = 0; slot < 9; slot++)
            addSlotToContainer(new Slot(inventoryPlayer, slot, x + slot * 18, y + 58));
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }

    public void sendContainerPacket(PacketCustom packet) {
        for (EntityPlayerMP player : playerCrafters)
            packet.sendToPlayer(player);
    }

    /**
     * @param type An identifying number for the packet type between 2 and 0x79 inclusive. 1 is reserved for synchronising networkIDs
     * @return A packet on the CCL inventory channel that will be recieved by this container on the other network side
     */
    public PacketCustom getPacket(int type) {
        if(netID == 0)
            LogManager.getLogger("CodeChickenLib").error("Tried to get packet for container with 0 network ID");
        if(type == 1)
            throw new IllegalArgumentException("Packet type 1 is reserved for network synchronisation in ContainerExtended");

        return new PacketCustom(netChannel, type).writeInt(netID);
    }

    /**
     * Handle a packet from the server obtained by getPacket.
     */
    public void handleClientPacket(PacketCustom packet) {}

    /**
     * Handle a packet from the client obtained by getPacket.
     */
    public void handleServerPacket(PacketCustom packet) {}

    public void sendProgressBarUpdate(int barID, int value) {
        for (ICrafting crafting : (List<ICrafting>) crafters)
            crafting.sendProgressBarUpdate(this, barID, value);
    }
}
