package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.Assembly;
import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.sprite.CCGuiTextures;
import codechicken.lib.gui.modular.sprite.Material;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.match;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.relative;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;
import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

/**
 * This element is used to manage and render a grid of inventory slots in a GUI.
 * The width and height of this element are automatically constrained based on the slot configuration.
 * However, you can override those constraints, The slot grid will always render in the center of the element nomater the element size.
 * <p>
 * This can be used to render all slots in a {@link SlotGroup} or a sub-set of slots within a group.
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public class GuiSlots extends GuiElement<GuiSlots> implements BackgroundRender {
    public static final Material[] ARMOR_SLOTS = new Material[]{Material.fromAtlas(BLOCK_ATLAS, "item/empty_armor_slot_helmet"), Material.fromAtlas(BLOCK_ATLAS, "item/empty_armor_slot_chestplate"), Material.fromAtlas(BLOCK_ATLAS, "item/empty_armor_slot_leggings"), Material.fromAtlas(BLOCK_ATLAS, "item/empty_armor_slot_boots")};
    public static final Material OFF_HAND_SLOT = Material.fromAtlas(BLOCK_ATLAS, "item/empty_armor_slot_shield");

    private final int firstSlot;
    private final int slotCount;
    private final int columns;
    private final SlotGroup slots;
    private final ContainerScreenAccess<?> screenAccess;

    private Material slotTexture = CCGuiTextures.getUncached("widgets/slot");
    private Function<Integer, Material> slotIcons = slot -> null;
    private Function<Integer, Integer> highlightColour = slot -> 0x80ffffff;
    private int xSlotSpacing = 0;
    private int ySlotSpacing = 0;

    /**
     * @param slots       The slot group containing the slots that this element will manage.
     * @param gridColumns The width of the inventory grid (Typically 9 for standard player or chest inventories)
     */
    public GuiSlots(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots, int gridColumns) {
        this(parent, screenAccess, slots, 0, slots.size(), gridColumns);
    }

    /**
     * @param slots       The slot group containing the slots that this element will manage.
     * @param firstSlot   Index of the fist slot within the slot group.
     * @param slotCount   The number of slots that this element will manage.
     * @param gridColumns The width of the inventory grid (Typically 9 for standard player or chest inventories)
     */
    public GuiSlots(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots, int firstSlot, int slotCount, int gridColumns) {
        super(parent);
        this.screenAccess = screenAccess;
        this.slots = slots;
        this.firstSlot = firstSlot;
        this.slotCount = slotCount;
        this.columns = gridColumns;
        if (firstSlot + slotCount > slots.size()) {
            throw new IllegalStateException("Specified slot range is out of bounds, Last slot in group is at index " + (slots.size() - 1) + " Specified range is from index " + firstSlot + " to " + (firstSlot + slotCount - 1));
        }
        int columns = Math.min(gridColumns, slots.size());
        this.constrain(WIDTH, Constraint.dynamic(() -> (double) (columns * 18) + ((columns - 1) * xSlotSpacing)));
        int rows = Math.max(1, slots.size() / gridColumns);
        this.constrain(GeoParam.HEIGHT, Constraint.dynamic(() -> (double) (rows * 18) + ((rows - 1) * ySlotSpacing)));
        for (int index = 0; index < slotCount; index++) {
            Slot slot = slots.getSlot(index + firstSlot);
            getModularGui().setSlotHandler(slot, this);
        }

        updateSlots(parent.getModularGui().getRoot());
    }

    //=== Slots Setup ===//

    /**
     * Allows you to use a custom slot texture, The default is the standard vanilla slot.
     */
    public GuiSlots setSlotTexture(Material slotTexture) {
        this.slotTexture = slotTexture;
        return this;
    }

    /**
     * Sets a custom slot highlight colour (The highlight you get when your cursor is over a slot.)
     */
    public GuiSlots setHighlightColour(int highlightColour) {
        return setHighlightColour(slot -> highlightColour);
    }

    /**
     * Allows you to set per-slot highlight colours, The integer passed to the function is the
     * index of the slot within the {@link SlotGroup}
     */
    public GuiSlots setHighlightColour(Function<Integer, Integer> highlightColour) {
        this.highlightColour = highlightColour;
        return this;
    }

    /**
     * Applies a single empty slot icon to all slots.
     * Recommended texture size is 16x16
     */
    public GuiSlots setEmptyIcon(Material texture) {
        return setEmptyIcon(index -> texture);
    }

    /**
     * Allows you to provide a texture to be rendered in each slot when the slot is empty.
     * Recommended texture size is 16x16
     *
     * @param slotIcons A function that is given the slot index within the {@link SlotGroup}, and should return a material or null.
     */
    public GuiSlots setEmptyIcon(Function<Integer, Material> slotIcons) {
        this.slotIcons = slotIcons;
        return this;
    }

    public GuiSlots setXSlotSpacing(int xSlotSpacing) {
        this.xSlotSpacing = xSlotSpacing;
        return this;
    }

    public GuiSlots setYSlotSpacing(int ySlotSpacing) {
        this.ySlotSpacing = ySlotSpacing;
        return this;
    }

    public GuiSlots setSlotSpacing(int xSlotSpacing, int ySlotSpacing) {
        this.xSlotSpacing = xSlotSpacing;
        this.ySlotSpacing = ySlotSpacing;
        return this;
    }

    //=== Internal Methods ===//

    @Override
    public double getBackgroundDepth() {
        return 33;
    }

    private void updateSlots(GuiElement<?> root) {
        int columns = Math.min(this.columns, slots.size());
        int rows = Math.max(1, slots.size() / columns);
        double width = (columns * 18) + (columns - 1) * xSlotSpacing;
        double height = (rows * 18) + (rows - 1) * ySlotSpacing;
        int top = (int) (yCenter() - (height / 2) - root.yMin());
        int left = (int) (xCenter() - (width / 2) - root.xMin());

        for (int index = 0; index < slotCount; index++) {
            Slot slot = slots.getSlot(index + firstSlot);
            int x = index % columns;
            int y = index / columns;
            slot.x = left + (x * 18) + 1 + (x * xSlotSpacing);
            slot.y = top + (y * 18) + 1 + (y * ySlotSpacing);
        }
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        GuiElement<?> root = getModularGui().getRoot();
        updateSlots(root);

        Slot highlightSlot = null;
        render.pose().pushPose();

        for (int index = 0; index < slotCount; index++) {
            Slot slot = slots.getSlot(index + firstSlot);
            render.texRect(slotTexture, slot.x + root.xMin() - 1, slot.y + root.yMin() - 1, 18, 18);
        }

        render.pose().translate(0, 0, 0.4);

        for (int index = 0; index < slotCount; index++) {
            Slot slot = slots.getSlot(index + firstSlot);
            if (!slot.isActive()) continue;
            if (!slot.hasItem()) {
                Material icon = slotIcons.apply(index + firstSlot);
                if (icon != null) {
                    render.texRect(icon, slot.x + root.xMin(), slot.y + root.yMin(), 16, 16);
                }
            }

            screenAccess.renderSlot(render, slot);
            if (GuiRender.isInRect(slot.x + root.xMin(), slot.y + root.yMin(), 16, 16, mouseX, mouseY) && !blockMouseOver(this, mouseX, mouseY)) {
                highlightSlot = slot;
            }
        }

        if (highlightSlot != null) {
            render.pose().translate(0, 0, getBackgroundDepth() - 0.8);
            render.rect(highlightSlot.x + root.xMin(), highlightSlot.y + root.yMin(), 16, 16, highlightColour.apply(slots.indexOf(highlightSlot)));
        }

        render.pose().popPose();
    }

    //=== Construction Helpers ===//

    public static GuiSlots singleSlot(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots) {
        return singleSlot(parent, screenAccess, slots, 0);
    }

    public static GuiSlots singleSlot(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots, int index) {
        return new GuiSlots(parent, screenAccess, slots, index, 1, 1);
    }

    public static Assembly<? extends GuiElement<?>, GuiSlots> player(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots) {
        return player(parent, screenAccess, mainSlots, hotBarSlots, 3);
    }

    public static Assembly<? extends GuiElement<?>, GuiSlots> player(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, int hotBarSpacing) {
        int width = 18 * 9;
        int height = 18 * 4 + hotBarSpacing;
        GuiElement<?> container = new GuiElement<>(parent)
                .setZStacking(false)
                .constrain(WIDTH, Constraint.literal(width))
                .constrain(HEIGHT, Constraint.literal(height));

        GuiSlots main = new GuiSlots(container, screenAccess, mainSlots, 9)
                .constrain(TOP, Constraint.midPoint(container.get(TOP), container.get(BOTTOM), height / -2D))
                .constrain(LEFT, Constraint.midPoint(container.get(LEFT), container.get(RIGHT), width / -2D));

        GuiSlots bar = new GuiSlots(container, screenAccess, hotBarSlots, 9)
                .constrain(TOP, relative(main.get(BOTTOM), hotBarSpacing))
                .constrain(LEFT, match(main.get(LEFT)));
        return new Assembly<>(container, main).addParts(bar);
    }

    public static Assembly<? extends GuiElement<?>, GuiSlots> playerWithArmor(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots) {
        return playerWithArmor(parent, screenAccess, mainSlots, hotBarSlots, armorSlots, 3, true);
    }

    public static Assembly<? extends GuiElement<?>, GuiSlots> playerWithArmor(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots, int groupSpacing, boolean slotIcons) {
        int width = 18 * 10 + groupSpacing;
        int height = 18 * 4 + groupSpacing;
        GuiElement<?> container = new GuiElement<>(parent)
                .setZStacking(false)
                .constrain(WIDTH, Constraint.literal(width))
                .constrain(HEIGHT, Constraint.literal(height));

        GuiSlots armor = new GuiSlots(container, screenAccess, armorSlots, 1)
                .setYSlotSpacing(groupSpacing / 3)
                .setEmptyIcon(index -> slotIcons ? ARMOR_SLOTS[index] : null)
                .constrain(TOP, Constraint.midPoint(container.get(TOP), container.get(BOTTOM), height / -2D))
                .constrain(LEFT, Constraint.midPoint(container.get(LEFT), container.get(RIGHT), width / -2D));

        GuiSlots main = new GuiSlots(container, screenAccess, mainSlots, 9)
                .constrain(TOP, match(armor.get(TOP)))
                .constrain(LEFT, relative(armor.get(RIGHT), groupSpacing));

        GuiSlots bar = new GuiSlots(container, screenAccess, hotBarSlots, 9)
                .constrain(TOP, relative(main.get(BOTTOM), groupSpacing))
                .constrain(LEFT, match(main.get(LEFT)));

        return new Assembly<>(container, main).addParts(bar, armor);
    }

    public static Assembly<? extends GuiElement<?>, GuiSlots> playerAllSlots(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots, SlotGroup offhandSlots) {
        return playerAllSlots(parent, screenAccess, mainSlots, hotBarSlots, armorSlots, offhandSlots, 3, true);
    }

    public static Assembly<? extends GuiElement<?>, GuiSlots> playerAllSlots(@NotNull GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots, SlotGroup offhandSlots, int groupSpacing, boolean slotIcons) {
        int width = 18 * 11 + groupSpacing * 2;
        int height = 18 * 4 + groupSpacing;
        GuiElement<?> container = new GuiElement<>(parent)
                .setZStacking(false)
                .constrain(WIDTH, Constraint.literal(width))
                .constrain(HEIGHT, Constraint.literal(height));

        GuiSlots armor = new GuiSlots(container, screenAccess, armorSlots, 1)
                .setYSlotSpacing(groupSpacing / 3)
                .setEmptyIcon(index -> slotIcons ? ARMOR_SLOTS[index] : null)
                .constrain(TOP, Constraint.midPoint(container.get(TOP), container.get(BOTTOM), height / -2D))
                .constrain(LEFT, Constraint.midPoint(container.get(LEFT), container.get(RIGHT), width / -2D));

        GuiSlots main = new GuiSlots(container, screenAccess, mainSlots, 9)
                .constrain(TOP, match(armor.get(TOP)))
                .constrain(LEFT, relative(armor.get(RIGHT), groupSpacing));

        GuiSlots bar = new GuiSlots(container, screenAccess, hotBarSlots, 9)
                .constrain(TOP, relative(main.get(BOTTOM), groupSpacing))
                .constrain(LEFT, match(main.get(LEFT)));

        GuiSlots offHand = new GuiSlots(container, screenAccess, offhandSlots, 1)
                .setEmptyIcon(index -> slotIcons ? OFF_HAND_SLOT : null)
                .constrain(TOP, match(bar.get(TOP)))
                .constrain(LEFT, relative(bar.get(RIGHT), groupSpacing));

        return new Assembly<>(container, main).addParts(bar, armor, offHand);
    }
}
