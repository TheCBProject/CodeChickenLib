package codechicken.lib.inventory.container.modular;

import codechicken.lib.gui.modular.elements.GuiSlots;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.container.DataSync;
import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The base abstract ContainerMenu for all modular gui containers.
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public abstract class ModularGuiContainerMenu extends AbstractContainerMenu {
    private static final Logger LOGGER = LogManager.getLogger();

    public final Inventory inventory;
    public final List<SlotGroup> slotGroups = new ArrayList<>();
    public final Map<Slot, SlotGroup> slotGroupMap = new HashMap<>();
    public final Map<Integer, List<Slot>> zonedSlots = new HashMap<>();
    public final List<DataSync<?>> dataSyncs = new ArrayList<>();
    private BiConsumer<ServerPlayer, Consumer<FriendlyByteBuf>> serverToClientPacketHandler;
    private Consumer<Consumer<FriendlyByteBuf>> clientToServerPacketHandler;

    protected ModularGuiContainerMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory) {
        super(menuType, containerId);
        this.inventory = inventory;
    }

    /**
     * Creates and returns a new slot group for this container.
     * You can then add your inventory slots to this slot group, similar to how you would normally add slots to the container.
     * With one big exception! You do not need to worry about setting slot positions! (Just use 0, 0)
     * <p>
     * Make sure to save your slot groups to accessible fields in your container menu class.
     * You will need to pass these to appropriate {@link GuiSlots} / {@link GuiSlots#singleSlot(GuiParent, ContainerScreenAccess, SlotGroup, int)} elements.
     * The gui elements will handle positioning and rendering the slots.
     * <p>
     * As far as splitting a containers slots into multiple groups, Typically the players main inventory and hot bar would be added as two separate groups.
     * How you handle the containers slots is up to you, For something like a machine with several spread out slots,
     * you can still add all the slots to a single group, then pass each individual slot from the group to a single {@link GuiSlots#singleSlot(GuiParent, ContainerScreenAccess, SlotGroup, int)} element.
     *
     * @param zoneId      Used for quick-move (shift click) operations. Each group has a zone id, and you can specify which zones a group can quick-move to.
     *                    Multiple groups can have the same zone id. Quick move work though the groups in a zone in the order the groups here added.
     * @param quickMoveTo List of zones this group can quick-move to.
     */
    protected SlotGroup createSlotGroup(int zoneId, int... quickMoveTo) {
        SlotGroup group = new SlotGroup(this, zoneId, quickMoveTo);
        slotGroups.add(group);
        return group;
    }

    /**
     * Convenience method to create a slot group for player slots.
     * Configured to quick-move to {@link #remoteSlotGroup()} groups.
     *
     * @see #createSlotGroup(int, int[])
     */
    protected SlotGroup playerSlotGroup() {
        return createSlotGroup(0, 1);
    }

    /**
     * Convenience method to create a slot group for the 'other side' of the inventory
     * So the Block/tile or whatever this inventory is attached to.
     * Configured to quick-move to {@link #playerSlotGroup()} groups.
     *
     * @see #createSlotGroup(int, int[])
     */
    protected SlotGroup remoteSlotGroup() {
        return createSlotGroup(1, 0);
    }

    //=== Network ===//

    /**
     * Set the server to client packet handler.
     * As polylib does not have its own network implementation, the implementor of ModularGuiContainerMenu must provide their own if
     * they wish to use the network functionality built into ModularGuiContainerMenu.
     * <p>
     * An example imeplementation may look something like:
     * <pre>
     * setServerToClientPacketHandler((player, packetWriter) -> {
     *     FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
     *     packetWriter.accept(buf);
     *     MyNetwork.sendModularGuiMenuPacketToPlayer(player, buf);
     * });
     * </pre>
     * Then, in your client side packet handler you would call {@link ModularGuiContainerMenu#handlePacketFromServer(Player, FriendlyByteBuf)}
     * The player can be the client side player.
     *
     * <p>
     * A server to client packet handler is required for the {@link DataSync} system to work.
     */
    public void setServerToClientPacketHandler(BiConsumer<ServerPlayer, Consumer<FriendlyByteBuf>> serverToClientPacketHandler) {
        this.serverToClientPacketHandler = serverToClientPacketHandler;
    }

    /**
     * This should be implemented similar to {@link #setServerToClientPacketHandler(BiConsumer)}
     * The difference being this will be sending packets in the other direction.
     */
    public void setClientToServerPacketHandler(Consumer<Consumer<FriendlyByteBuf>> clientToServerPacketHandler) {
        this.clientToServerPacketHandler = clientToServerPacketHandler;
    }

    /**
     * Send a packet to the client side container.
     * Requires a server to client packet handler to be installed via {@link #setServerToClientPacketHandler(BiConsumer)}
     *
     * @param packetId     message id, Can be any value from 0 to 254, 255 is used by the {@link DataSync} system.
     * @param packetWriter Use this callback to write your data to the packet.
     */
    public void sendPacketToClient(int packetId, Consumer<FriendlyByteBuf> packetWriter) {
        if (serverToClientPacketHandler != null && inventory.player instanceof ServerPlayer serverPlayer) {
            serverToClientPacketHandler.accept(serverPlayer, buf -> {
                buf.writeByte(containerId);
                buf.writeByte((byte) packetId);
                packetWriter.accept(buf);
            });
        }
    }

    /**
     * Send a packet to the server side container.
     * Requires a client to server packet handler to be installed via {@link #setClientToServerPacketHandler(Consumer)}
     *
     * @param packetId     message id, Can be any value from 0 to 255
     * @param packetWriter Use this callback to write your data to the packet.
     */
    public void sendPacketToServer(int packetId, Consumer<FriendlyByteBuf> packetWriter) {
        if (clientToServerPacketHandler != null) {
            clientToServerPacketHandler.accept(buf -> {
                buf.writeByte(containerId);
                buf.writeByte((byte) packetId);
                packetWriter.accept(buf);
            });
        }
    }

    public static void handlePacketFromClient(Player player, FriendlyByteBuf packet) {
        int containerId = packet.readByte();
        int packetId = packet.readByte() & 0xFF;
        if (player.containerMenu instanceof ModularGuiContainerMenu menu && menu.containerId == containerId) {
            menu.handlePacketFromClient(player, packetId, packet);
        }
    }

    /**
     * Override this in your container menu implementation in order to receive packets sent via {@link #sendPacketToServer(int, Consumer)}
     * Requires a client to server packet handler to be installed via {@link #setClientToServerPacketHandler(Consumer)}
     */
    public void handlePacketFromClient(Player player, int packetId, FriendlyByteBuf packet) {

    }

    public static void handlePacketFromServer(Player player, FriendlyByteBuf packet) {
        int containerId = packet.readByte();
        int packetId = packet.readByte() & 0xFF;
        if (player.containerMenu instanceof ModularGuiContainerMenu menu && menu.containerId == containerId) {
            menu.handlePacketFromServer(player, packetId, packet);
        }
    }

    /**
     * Override this in your container menu implementation in order to receive packets sent via {@link #sendPacketToServer(int, Consumer)}
     * Requires a server to client packet handler to be installed via {@link #setServerToClientPacketHandler(BiConsumer)}
     * <p>
     * Don't forget to call super if you plan on using the {@link DataSync} system.
     */
    public void handlePacketFromServer(Player player, int packetId, FriendlyByteBuf packet) {
        if (packetId == 255) {
            int index = packet.readByte() & 0xFF;
            if (dataSyncs.size() > index) {
                dataSyncs.get(index).handleSyncPacket(packet);
            }
        }
    }

    //=== Quick Move ===///

    /**
     * Determines if two @link {@link ItemStack} match and can be merged into a single slot
     */
    public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() || stack2.isEmpty()) return false;
        return ItemStack.matches(stack1, stack2);
    }

    /**
     * Transfers to the next zone in order, and will loop around to the lowest zone.
     * TODO, Would be nice to have better control over quick-move
     *  Maybe just the ability to specify which zones each group quick-moves to...
     */
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot slot = getSlot(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        SlotGroup group = slotGroupMap.get(slot);
        if (group == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();

        boolean movedAnything = false;
        for (Integer zone : group.quickMoveTo) {
            if (!zonedSlots.containsKey(zone)) {
                LOGGER.warn("Attempted to quick move to zone id {} but there are no slots assigned to this zone! This is a bug!", zone);
                continue;
            }
            if (moveItemStackTo(stack, zonedSlots.get(zone), false)) {
                movedAnything = true;
                break;
            }
        }

        if (!movedAnything) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, stack);
        return result;
    }

    protected boolean moveItemStackTo(ItemStack stack, List<Slot> targets, boolean reverse) {
        int start = 0;
        int end = targets.size();
        boolean moved = false;
        int position = start;
        if (reverse) {
            position = end - 1;
        }

        Slot slot;
        ItemStack itemStack2;
        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverse) {
                    if (position < start) {
                        break;
                    }
                } else if (position >= end) {
                    break;
                }

                slot = targets.get(position);
                itemStack2 = slot.getItem();
                if (!itemStack2.isEmpty() && ItemStack.isSameItemSameTags(stack, itemStack2)) {
                    int l = itemStack2.getCount() + stack.getCount();
                    if (l <= stack.getMaxStackSize()) {
                        stack.setCount(0);
                        itemStack2.setCount(l);
                        slot.setChanged();
                        moved = true;
                    } else if (itemStack2.getCount() < stack.getMaxStackSize()) {
                        stack.shrink(stack.getMaxStackSize() - itemStack2.getCount());
                        itemStack2.setCount(stack.getMaxStackSize());
                        slot.setChanged();
                        moved = true;
                    }
                }

                if (reverse) {
                    --position;
                } else {
                    ++position;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverse) {
                position = end - 1;
            } else {
                position = start;
            }

            while (true) {
                if (reverse) {
                    if (position < start) {
                        break;
                    }
                } else if (position >= end) {
                    break;
                }

                slot = targets.get(position);
                itemStack2 = slot.getItem();
                if (itemStack2.isEmpty() && slot.mayPlace(stack)) {
                    if (stack.getCount() > slot.getMaxStackSize()) {
                        slot.set(stack.split(slot.getMaxStackSize()));
                    } else {
                        slot.set(stack.split(stack.getCount()));
                    }

                    slot.setChanged();
                    moved = true;
                    break;
                }

                if (reverse) {
                    --position;
                } else {
                    ++position;
                }
            }
        }

        return moved;
    }

    //=== Internal Methods ===//

    public void mapSlot(Slot slot, SlotGroup slotGroup) {
        slotGroupMap.put(slot, slotGroup);
        zonedSlots.computeIfAbsent(slotGroup.zone, e -> new ArrayList<>()).add(slot);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        dataSyncs.forEach(DataSync::detectAndSend);
    }
}
