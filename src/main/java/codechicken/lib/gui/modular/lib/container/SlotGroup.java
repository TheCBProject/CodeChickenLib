package codechicken.lib.gui.modular.lib.container;

import codechicken.lib.gui.modular.elements.GuiSlots;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.inventory.container.modular.ModularGuiContainerMenu;
import codechicken.lib.inventory.container.modular.ModularSlot;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Used to configure slots a set of slots in a ContainerMenu.
 * The SlotGroup can then be passed to ModularGui elements such as {@link GuiSlots} in order to give the gui control over slot positioning and rendering.
 * <p>
 * Ideally you should use a separate SlotGroup for each 'group' of slots, Meaning the players main inventory and hot bar should be separate ranges.
 * That said, if you have multiple slots spread out across something like a machine gui, You can add them all to one SlotGroup then
 * pass each individual slot from that range to a {@link GuiSlots#singleSlot(GuiParent, ContainerScreenAccess, SlotGroup, int)}
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public class SlotGroup {

    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    public final int zone;
    public final List<Integer> quickMoveTo;

    private final ModularGuiContainerMenu containerMenu;
    private final List<ModularSlot> slots = new ArrayList<>();

    public SlotGroup(ModularGuiContainerMenu containerMenu, int zone, int... quickMoveTo) {
        this.zone = zone;
        this.containerMenu = containerMenu;
        this.quickMoveTo = Arrays.stream(quickMoveTo).boxed().toList();
    }

    public ModularSlot addSlot(ModularSlot slot) {
        slots.add(slot);
        containerMenu.addSlot(slot);
        containerMenu.mapSlot(slot, this);
        return slot;
    }

    /**
     * Convenient method for adding multiple slots.
     *
     * @param slotCount  The number of slots to be added.
     * @param startIndex Slot starting index.
     * @param makeSlot   Builder used to create the slots, Input integer will start at startIndex and increment by one for each slot.
     */
    public void addSlots(int slotCount, int startIndex, Function<Integer, ModularSlot> makeSlot) {
        for (int index = startIndex; index < startIndex + slotCount; index++) {
            addSlot(makeSlot.apply(index));
        }
    }

    public void addAllSlots(Container container) {
        addAllSlots(container, ModularSlot::new);
    }

    public void addAllSlots(Container container, BiFunction<Container, Integer, ModularSlot> makeSlot) {
        for (int index = 0; index < container.getContainerSize(); index++) {
            addSlot(makeSlot.apply(container, index));
        }
    }

    public void addPlayerMain(Inventory inventory) {
        addSlots(27, 9, index -> new ModularSlot(inventory, index));
    }

    public void addPlayerBar(Inventory inventory) {
        addSlots(9, 0, index -> new ModularSlot(inventory, index));
    }

    public void addPlayerArmor(Inventory inventory) {
        for (int i = 0; i < 4; ++i) {
            EquipmentSlot slot = ARMOR_SLOTS[i];
            addSlot(new ModularSlot(inventory, 39 - i)
                    .onSet((oldStack, newStack) -> onEquipItem(inventory, slot, newStack, oldStack))
                    .setStackLimit(stack -> 1)
                    .setValidator(stack -> slot == inventory.player.getEquipmentSlotForItem(stack))
                    .setCanRemove((player, stack) -> stack.isEmpty() || player.isCreative() || !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))
            );
        }
    }

    public void addPlayerOffhand(Inventory inventory) {
        addSlot(new ModularSlot(inventory, 40).onSet((oldStack, newStack) -> onEquipItem(inventory, EquipmentSlot.OFFHAND, newStack, oldStack)));
    }

    static void onEquipItem(Inventory inventory, EquipmentSlot slot, ItemStack newStack, ItemStack oldStack) {
        inventory.player.onEquipItem(slot, oldStack, newStack);
    }

    public int size() {
        return slots.size();
    }

    public ModularSlot getSlot(int index) {
        return slots.get(index);
    }

    public int indexOf(Slot slot) {
        return slots.indexOf(slot);
    }

    public List<ModularSlot> slots() {
        return Collections.unmodifiableList(slots);
    }
}
