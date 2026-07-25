package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.container.SlotGroup;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.lib.geometry.Position;
import codechicken.lib.gui.modular.SpriteSupplier;
import codechicken.lib.math.MathHelper;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static codechicken.lib.CodeChickenLib.MOD_ID;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.match;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.relative;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

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

    public static final SpriteSupplier[] ARMOR_SLOTS = new SpriteSupplier[] {
            SpriteSupplier.of(AtlasIds.ITEMS, Identifier.withDefaultNamespace("item/empty_armor_slot_helmet")),
            SpriteSupplier.of(AtlasIds.ITEMS, Identifier.withDefaultNamespace("item/empty_armor_slot_chestplate")),
            SpriteSupplier.of(AtlasIds.ITEMS, Identifier.withDefaultNamespace("item/empty_armor_slot_leggings")),
            SpriteSupplier.of(AtlasIds.ITEMS, Identifier.withDefaultNamespace("item/empty_armor_slot_boots"))
    };
    public static final SpriteSupplier OFF_HAND_SLOT = SpriteSupplier.of(AtlasIds.ITEMS, Identifier.withDefaultNamespace("item/empty_armor_slot_shield"));

    private final int firstSlot;
    private final int slotCount;
    private final int columns;
    private final SlotGroup slots;
    private final ContainerScreenAccess<?> screenAccess;

    private Function<Slot, SpriteSupplier> slotTexture = slot -> SpriteSupplier.gui(Identifier.fromNamespaceAndPath(MOD_ID, "widgets/slot"));
    private Function<Slot, SpriteSupplier> slotIcons = slot -> SpriteSupplier.EMPTY;
    private Function<Slot, Integer> highlightColour = slot -> 0x80ffffff;
    private @Nullable TriConsumer<Slot, Position, GuiGraphics> slotOverlay = null;
    private int xSlotSpacing = 0;
    private int ySlotSpacing = 0;

    /**
     * @param slots       The slot group containing the slots that this element will manage.
     * @param gridColumns The width of the inventory grid (Typically 9 for standard player or chest inventories)
     */
    public GuiSlots(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots, int gridColumns) {
        this(parent, screenAccess, slots, 0, slots.size(), gridColumns);
    }

    /**
     * @param slots       The slot group containing the slots that this element will manage.
     * @param firstSlot   Index of the fist slot within the slot group.
     * @param slotCount   The number of slots that this element will manage.
     * @param gridColumns The width of the inventory grid (Typically 9 for standard player or chest inventories)
     */
    public GuiSlots(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots, int firstSlot, int slotCount, int gridColumns) {
        super(parent);
        this.screenAccess = screenAccess;
        this.slots = slots;
        this.firstSlot = firstSlot;
        this.slotCount = slotCount;
        this.columns = gridColumns;
        if (firstSlot + slotCount > slots.size()) {
            throw new IllegalStateException("Specified slot range is out of bounds, Last slot in group is at index " + (slots.size() - 1) + " Specified range is from index " + firstSlot + " to " + (firstSlot + slotCount - 1));
        }
        int columns = Math.min(gridColumns, slotCount);
        this.constrain(WIDTH, Constraint.dynamic(() -> (double) (columns * 18) + ((columns - 1) * xSlotSpacing)));
        int rows = Math.max(1, slotCount / gridColumns);
        this.constrain(GeoParam.HEIGHT, Constraint.dynamic(() -> (double) (rows * 18) + ((rows - 1) * ySlotSpacing)));
        for (int index = 0; index < slotCount; index++) {
            Slot slot = slots.getSlot(index + firstSlot);
            getModularGui().setSlotHandler(slot, this);
        }

        updateSlots(parent.getModularGui().getRoot());
    }

    //=== Construction Helpers ===//

    public static GuiSlots singleSlot(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots) {
        return singleSlot(parent, screenAccess, slots, 0);
    }

    public static GuiSlots singleSlot(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup slots, int index) {
        return new GuiSlots(parent, screenAccess, slots, index, 1, 1);
    }

    public static Player player(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots) {
        return player(parent, screenAccess, mainSlots, hotBarSlots, 3);
    }

    public static Player player(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, int hotBarSpacing) {
        int width = 18 * 9;
        int height = 18 * 4 + hotBarSpacing;
        GuiElement<?> container = new GuiElement<>(parent)
                .constrain(WIDTH, Constraint.literal(width))
                .constrain(HEIGHT, Constraint.literal(height));

        GuiSlots main = new GuiSlots(container, screenAccess, mainSlots, 9)
                .constrain(TOP, Constraint.midPoint(container.get(TOP), container.get(BOTTOM), height / -2D))
                .constrain(LEFT, Constraint.midPoint(container.get(LEFT), container.get(RIGHT), width / -2D));

        GuiSlots bar = new GuiSlots(container, screenAccess, hotBarSlots, 9)
                .constrain(TOP, relative(main.get(BOTTOM), hotBarSpacing))
                .constrain(LEFT, match(main.get(LEFT)));
        return new Player(container, main, bar);
    }

    public static PlayerWithArmor playerWithArmor(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots) {
        return playerWithArmor(parent, screenAccess, mainSlots, hotBarSlots, armorSlots, 3, true);
    }

    public static PlayerWithArmor playerWithArmor(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots, int groupSpacing, boolean slotIcons) {
        int width = 18 * 10 + groupSpacing;
        int height = 18 * 4 + groupSpacing;
        GuiElement<?> container = new GuiElement<>(parent)
                .constrain(WIDTH, Constraint.literal(width))
                .constrain(HEIGHT, Constraint.literal(height));

        GuiSlots armor = new GuiSlots(container, screenAccess, armorSlots, 1)
                .setYSlotSpacing(groupSpacing / 3)
                .setEmptyIconI(index -> slotIcons ? ARMOR_SLOTS[index] : null)
                .constrain(TOP, Constraint.midPoint(container.get(TOP), container.get(BOTTOM), height / -2D))
                .constrain(LEFT, Constraint.midPoint(container.get(LEFT), container.get(RIGHT), width / -2D));

        GuiSlots main = new GuiSlots(container, screenAccess, mainSlots, 9)
                .constrain(TOP, match(armor.get(TOP)))
                .constrain(LEFT, relative(armor.get(RIGHT), groupSpacing));

        GuiSlots bar = new GuiSlots(container, screenAccess, hotBarSlots, 9)
                .constrain(TOP, relative(main.get(BOTTOM), groupSpacing))
                .constrain(LEFT, match(main.get(LEFT)));

        return new PlayerWithArmor(container, main, bar, armor);
    }

    public static PlayerAll playerAllSlots(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots, SlotGroup offhandSlots) {
        return playerAllSlots(parent, screenAccess, mainSlots, hotBarSlots, armorSlots, offhandSlots, 3, true);
    }

    public static PlayerAll playerAllSlots(GuiParent<?> parent, ContainerScreenAccess<?> screenAccess, SlotGroup mainSlots, SlotGroup hotBarSlots, SlotGroup armorSlots, SlotGroup offhandSlots, int groupSpacing, boolean slotIcons) {
        int width = 18 * 11 + groupSpacing * 2;
        int height = 18 * 4 + groupSpacing;
        GuiElement<?> container = new GuiElement<>(parent)
                .constrain(WIDTH, Constraint.literal(width))
                .constrain(HEIGHT, Constraint.literal(height));

        GuiSlots armor = new GuiSlots(container, screenAccess, armorSlots, 1)
                .setYSlotSpacing(groupSpacing / 3)
                .setEmptyIconI(index -> slotIcons ? ARMOR_SLOTS[index] : null)
                .constrain(TOP, Constraint.midPoint(container.get(TOP), container.get(BOTTOM), height / -2D))
                .constrain(LEFT, Constraint.midPoint(container.get(LEFT), container.get(RIGHT), width / -2D));

        GuiSlots main = new GuiSlots(container, screenAccess, mainSlots, 9)
                .constrain(TOP, match(armor.get(TOP)))
                .constrain(LEFT, relative(armor.get(RIGHT), groupSpacing));

        GuiSlots bar = new GuiSlots(container, screenAccess, hotBarSlots, 9)
                .constrain(TOP, relative(main.get(BOTTOM), groupSpacing))
                .constrain(LEFT, match(main.get(LEFT)));

        GuiSlots offHand = new GuiSlots(container, screenAccess, offhandSlots, 1)
                .setEmptyIconI(index -> slotIcons ? OFF_HAND_SLOT : null)
                .constrain(TOP, match(bar.get(TOP)))
                .constrain(LEFT, relative(bar.get(RIGHT), groupSpacing));

        return new PlayerAll(container, main, bar, armor, offHand);
    }

    //=== Slots Setup ===//

    /**
     * Allows you to use a custom slot texture, The default is the standard vanilla slot.
     */
    public GuiSlots setSlotTexture(SpriteSupplier slotTexture) {
        this.slotTexture = e -> slotTexture;
        return this;
    }

    /**
     * Allows you to use a custom per-slot slot textures, The default is the standard vanilla texture for all slots.
     */
    public GuiSlots setSlotTexture(Function<Slot, SpriteSupplier> slotTexture) {
        this.slotTexture = slotTexture;
        return this;
    }

    /**
     * Allows you to use a custom per-slot slot textures, The default is the standard vanilla texture for all slots.
     * <p>
     * Similar to {@link #setSlotTexture(Function)} except you are given the index of the slot within the {@link GuiSlots} element.
     */
    public GuiSlots setSlotTextureI(Function<Integer, SpriteSupplier> slotTexture) {
        this.slotTexture = slot -> slotTexture.apply(slots.indexOf(slot) - firstSlot);
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
    public GuiSlots setHighlightColour(Function<Slot, Integer> highlightColour) {
        this.highlightColour = highlightColour;
        return this;
    }

    /**
     * Allows you to set per-slot highlight colours, The integer passed to the function is the
     * index of the slot within the {@link SlotGroup}
     * <p>
     * Similar to {@link #setHighlightColour(Function)} except you are given the index of the slot within the {@link GuiSlots} element.
     */
    public GuiSlots setHighlightColourI(Function<Integer, Integer> highlightColour) {
        this.highlightColour = slot -> highlightColour.apply(slots.indexOf(slot) - firstSlot);
        return this;
    }

    /**
     * Applies a single empty slot icon to all slots.
     * Recommended texture size is 16x16
     */
    public GuiSlots setEmptyIcon(SpriteSupplier texture) {
        return setEmptyIcon(index -> texture);
    }

    /**
     * Allows you to provide a texture to be rendered in each slot when the slot is empty.
     * Recommended texture size is 16x16
     *
     * @param slotIcons A function that is given the slot index within the {@link SlotGroup}, and should return a material or null.
     */
    public GuiSlots setEmptyIcon(Function<Slot, SpriteSupplier> slotIcons) {
        this.slotIcons = slotIcons;
        return this;
    }

    /**
     * Allows you to provide a texture to be rendered in each slot when the slot is empty.
     * Recommended texture size is 16x16
     * <p>
     * Similar to {@link #setEmptyIcon(Function)} except you are given the index of the slot within the {@link GuiSlots} element.
     *
     * @param slotIcons A function that is given the slot index within the {@link SlotGroup}, and should return a material or null.
     */
    public GuiSlots setEmptyIconI(Function<Integer, SpriteSupplier> slotIcons) {
        this.slotIcons = slot -> slotIcons.apply(slots.indexOf(slot) - firstSlot);
        return this;
    }

    /**
     * Allows you to attach an overlay renderer that will get called for each slot, after all slots have been rendered.
     * This can be used to render pretty much anything you want to overtop the slot.
     *
     * @param slotOverlay Render callback providing the slot, screen position of the slot (top-left corner) and the active GuiRender.
     */
    public GuiSlots setSlotOverlay(TriConsumer<Slot, Position, GuiGraphics> slotOverlay) {
        this.slotOverlay = slotOverlay;
        return this;
    }

    /**
     * Allows you to attach an overlay renderer that will get called for each slot, after all slots have been rendered.
     * This can be used to render pretty much anything you want to overtop the slot.
     * <p>
     * Similar to {@link #setSlotOverlay(TriConsumer)} except you are given the index of the slot within the {@link GuiSlots} element.
     *
     * @param slotOverlay Render callback providing the slot, screen position of the slot (top-left corner) and the active GuiRender.
     */
    public GuiSlots setSlotOverlayI(TriConsumer<Integer, Position, GuiGraphics> slotOverlay) {
        this.slotOverlay = (slot, position, render) -> slotOverlay.accept(slots.indexOf(slot) - firstSlot, position, render);
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

    private void updateSlots(GuiElement<?> root) {
        int columns = Math.min(this.columns, slotCount);
        int rows = Math.max(1, slotCount / columns);
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
    public void renderBehind(GuiGraphics render, double mouseX, double mouseY, float partialTicks) {
        GuiElement<?> root = getModularGui().getRoot();
        updateSlots(root);

        Slot highlightSlot = null;

        for (int index = 0; index < slotCount; index++) {
            Slot slot = slots.getSlot(index + firstSlot);
            slotTexture.apply(slot).ifPresent(icon -> {
                render.cc$blitSprite(RenderPipelines.GUI_TEXTURED, icon, slot.x + root.xMin() - 1, slot.y + root.yMin() - 1, 18, 18);
            });
        }

        for (int index = 0; index < slotCount; index++) {
            Slot slot = slots.getSlot(index + firstSlot);
            if (!slot.isActive()) continue;
            if (!slot.hasItem()) {
                slotIcons.apply(slot).ifPresent(icon -> {
                    render.cc$blitSprite(RenderPipelines.GUI_TEXTURED, icon, slot.x + root.xMin(), slot.y + root.yMin(), 16, 16);
                });
            }

            screenAccess.doRenderSlot(render, slot);
            if (MathHelper.isInRect(slot.x + root.xMin(), slot.y + root.yMin(), 16, 16, mouseX, mouseY) && !blockMouseOver(this, mouseX, mouseY) && isMouseOver()) {
                highlightSlot = slot;
            }
        }

        if (slotOverlay != null) {
            for (int index = 0; index < slotCount; index++) {
                Slot slot = slots.getSlot(index + firstSlot);
                if (!slot.isActive()) continue;
                slotOverlay.accept(slot, Position.create(slot.x + root.xMin(), slot.y + root.yMin()), render);
            }
        }

        if (highlightSlot != null) {
            render.cc$fill(highlightSlot.x + root.xMin(), highlightSlot.y + root.yMin(), 16, 16, highlightColour.apply(highlightSlot));
        }
    }

    public record Player(GuiElement<?> container, GuiSlots main, GuiSlots hotBar) {

        public FastStream<GuiSlots> stream() {
            return FastStream.of(main, hotBar);
        }
    }

    public record PlayerWithArmor(GuiElement<?> container, GuiSlots main, GuiSlots hotBar, GuiSlots armor) {

        public FastStream<GuiSlots> stream() {
            return FastStream.of(main, hotBar, armor);
        }
    }

    public record PlayerAll(GuiElement<?> container, GuiSlots main, GuiSlots hotBar, GuiSlots armor, GuiSlots offHand) {

        public FastStream<GuiSlots> stream() {
            return FastStream.of(main, hotBar, armor, offHand);
        }
    }
}
