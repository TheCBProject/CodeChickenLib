package codechicken.lib.gui.modular;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Container screen implementation for {@link ModularGui}.
 *
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public class ModularGuiContainer<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements ContainerScreenAccess<T> {

    public final ModularGui modularGui;
    /**
     * Flag used to disable vanilla slot highlight rendering.
     *
     */
    private boolean renderingSlots = false;

    public ModularGuiContainer(T containerMenu, Inventory inventory, ContainerGuiProvider<T> provider) {
        super(containerMenu, inventory, Component.empty());
        provider.setMenuAccess(this);
        this.modularGui = new ModularGui(provider);
        this.modularGui.setScreen(this);
        addRenderableOnly(this::renderModularGui);
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        addRenderableOnly(this::renderModularGui);
    }

    public ModularGui getModularGui() {
        return modularGui;
    }

    @Override
    public Component getTitle() {
        return modularGui.getGuiTitle();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return modularGui.closeOnEscape();
    }

    @Override
    protected void init() {
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        GuiElement<?> root = modularGui.getRoot();
        topPos = (int) root.getValue(GeoParam.TOP);
        leftPos = (int) root.getValue(GeoParam.LEFT);
        imageWidth = (int) root.getValue(GeoParam.WIDTH);
        imageHeight = (int) root.getValue(GeoParam.HEIGHT);

        super.render(graphics, mouseX, mouseY, partialTicks);

        if (!handleFloatingItemRender(graphics, mouseX, mouseY) && !renderHoveredStackToolTip(graphics, mouseX, mouseY)) {
            modularGui.renderOverlay(graphics, partialTicks);
        }
    }

    private void renderModularGui(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        modularGui.render(graphics, partialTicks);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (modularGui.renderBackground()) {
            super.renderBackground(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderCarriedItem(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (modularGui.vanillaSlotRendering()) return;
        super.renderCarriedItem(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void renderSnapbackItem(GuiGraphics guiGraphics) {
        if (modularGui.vanillaSlotRendering()) return;
        super.renderSnapbackItem(guiGraphics);
    }

    protected boolean handleFloatingItemRender(GuiGraphics graphics, int mouseX, int mouseY) {
        if (modularGui.vanillaSlotRendering()) return false;
        boolean ret = false;

        ItemStack stack = draggingItem.isEmpty() ? menu.getCarried() : draggingItem;
        if (!stack.isEmpty()) {
            int yOffset = draggingItem.isEmpty() ? 8 : 16;
            String countOverride = null;
            if (!draggingItem.isEmpty() && isSplittingStack) {
                stack = stack.copyWithCount(Mth.ceil((float) stack.getCount() / 2.0F));
            } else if (isQuickCrafting && quickCraftSlots.size() > 1) {
                stack = stack.copyWithCount(this.quickCraftingRemainder);
                if (stack.isEmpty()) {
                    countOverride = ChatFormatting.YELLOW + "0";
                }
            }
            renderFloatingItem(graphics, stack, mouseX - 8, mouseY - yOffset, countOverride);
            ret = modularGui.doesFloatingItemDisableToolTips();
        }

        if (snapbackData != null) {
            float anim = Mth.clamp((float) (Util.getMillis() - snapbackData.time()) / 100.0F, 0F, 1F);
            int xDist = snapbackData.end().x - snapbackData.start().x;
            int yDist = snapbackData.end().y - snapbackData.start().y;
            int xPos = snapbackData.start().x + (int) ((float) xDist * anim);
            int yPos = snapbackData.start().y + (int) ((float) yDist * anim);
            renderFloatingItem(graphics, snapbackData.item(), xPos + leftPos, yPos + topPos, null);
            if (anim >= 1.0F) {
                this.snapbackData = null;
            }
            ret = modularGui.doesFloatingItemDisableToolTips();
        }

        return ret;
    }

    protected boolean renderHoveredStackToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem() && showTooltipWithItemInHand(hoveredSlot.getItem())) {
            GuiElement<?> handler = modularGui.getSlotHandler(hoveredSlot);
            if (handler != null && (handler.blockMouseOver(handler, mouseX, mouseY) || !handler.isMouseOver())) {
                return false;
            }
            ItemStack itemStack = this.hoveredSlot.getItem();
            guiGraphics.setTooltipForNextFrame(
                    this.font,
                    this.getTooltipFromContainerItem(itemStack),
                    itemStack.getTooltipImage(),
                    itemStack,
                    mouseX,
                    mouseY,
                    itemStack.get(DataComponents.TOOLTIP_STYLE)
            );
            return true;
        }
        return false;
    }

    @Override
    protected void containerTick() {
        modularGui.tick();
    }

    @Override
    public void removed() {
        super.removed();
        modularGui.onGuiClose();
    }

    //=== Input Pass-though ===//

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        modularGui.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return modularGui.mouseClicked(event) || super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return modularGui.mouseReleased(event) || super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return modularGui.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return modularGui.keyPressed(event) || super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return modularGui.keyReleased(event) || super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return modularGui.charTyped(event) || super.charTyped(event);
    }

    //=== AbstractContainerMenu Overrides ===//

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        if (modularGui.vanillaSlotRendering()) {
            super.renderSlot(guiGraphics, slot, mouseX, mouseY);
        } else {
            renderingSlots = true;
        }
    }

    //Modular gui friendly version of the slot render
    @Override
    public void doRenderSlot(GuiGraphics graphics, Slot slot) {
        if (modularGui.vanillaSlotRendering()) return;
        int slotX = slot.x + leftPos;
        int slotY = slot.y + topPos;
        ItemStack slotStack = slot.getItem();
        boolean dragingToSlot = false;
        boolean dontRenderItem = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;

        ItemStack carriedStack = this.menu.getCarried();
        String countString = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !slotStack.isEmpty()) {
            slotStack = slotStack.copyWithCount(slotStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !carriedStack.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (AbstractContainerMenu.canItemQuickReplace(slot, carriedStack, true) && this.menu.canDragTo(slot)) {
                dragingToSlot = true;
                int k = Math.min(carriedStack.getMaxStackSize(), slot.getMaxStackSize(carriedStack));
                int l = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
                int m = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, carriedStack) + l;
                if (m > k) {
                    m = k;
                    countString = ChatFormatting.YELLOW.toString() + k;
                }

                slotStack = carriedStack.copyWithCount(m);
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining();
            }
        }

        if (!dontRenderItem) {
            if (dragingToSlot) {
                //Highlights slots when doing a drag place operation.
                graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0x80ffffff);
            }
            renderSlotContents(graphics, slotStack, slot, countString);
        }
    }

    @Override
    public boolean isHovering(Slot pSlot, double pMouseX, double pMouseY) {
        boolean ret = super.isHovering(pSlot, pMouseX, pMouseY);
        //Override the isHovering check before renderSlotHighlight is called.
        if (ret && renderingSlots && pSlot.isActive()) {
            //This breaks the default hoveredSlot assignment, so we need to handle that here.
            hoveredSlot = pSlot;
            return false;
        }
        return ret;
    }

    @Override //Disable vanilla title and inventory name rendering
    protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
        renderingSlots = false;
    }

    @Nullable
    @Override
    public Slot getHoveredSlot(double mouseX, double mouseY) {
        Slot slot = super.getHoveredSlot(mouseX, mouseY);
        if (slot == null) return null;

        GuiElement<?> handler = modularGui.getSlotHandler(slot);
        if (handler != null && (!handler.isEnabled() || !handler.isMouseOver())) {
            return null;
        }
        return slot;
    }

    @Override
    protected void slotClicked(Slot slot, int i, int j, ClickType clickType) {
        if (slot != null) {
            GuiElement<?> handler = modularGui.getSlotHandler(slot);
            if (handler != null && !handler.isEnabled()) return;
        }
        super.slotClicked(slot, i, j, clickType);
    }
}
