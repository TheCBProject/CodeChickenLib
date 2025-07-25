package codechicken.lib.gui.modular;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
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
     * */
    private boolean renderingSlots = false;

    public ModularGuiContainer(T containerMenu, Inventory inventory, ContainerGuiProvider<T> provider) {
        super(containerMenu, inventory, Component.empty());
        provider.setMenuAccess(this);
        this.modularGui = new ModularGui(provider);
        this.modularGui.setScreen(this);
    }

    public ModularGui getModularGui() {
        return modularGui;
    }

    @NotNull
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
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        GuiElement<?> root = modularGui.getRoot();
        topPos = (int) root.getValue(GeoParam.TOP);
        leftPos = (int) root.getValue(GeoParam.LEFT);
        imageWidth = (int) root.getValue(GeoParam.WIDTH);
        imageHeight = (int) root.getValue(GeoParam.HEIGHT);

        if (modularGui.renderBackground()) {
            //Called here to ensure background renders before modularGui elements.
            super.renderBackground(graphics, mouseX, mouseY, partialTicks);
        }
        GuiRender render = GuiRender.convert(graphics);
        modularGui.render(render, partialTicks);

        super.render(graphics, mouseX, mouseY, partialTicks);

        //Ensure we render overlay on top of things like JEI
        render.pose().pushPose();
        render.pose().translate(0, 0, 400);
        if (!handleFloatingItemRender(render, mouseX, mouseY) && !renderHoveredStackToolTip(render, mouseX, mouseY)) {
            modularGui.renderOverlay(render, partialTicks);
        }
        render.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        //Stubbed to avoid the above super.render() call from rendering the background again.
    }

    protected boolean handleFloatingItemRender(GuiRender render, int mouseX, int mouseY) {
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
            renderFloatingItem(render, stack, mouseX - 8, mouseY - yOffset, countOverride);
            ret = modularGui.doesFloatingItemDisableToolTips();
        }

        if (!this.snapbackItem.isEmpty()) {
            float anim = (float) (Util.getMillis() - this.snapbackTime) / 100.0F;
            if (anim >= 1.0F) {
                anim = 1.0F;
                this.snapbackItem = ItemStack.EMPTY;
            }

            int xDist = snapbackEnd.x - snapbackStartX;
            int yDist = snapbackEnd.y - snapbackStartY;
            int xPos = snapbackStartX + (int) ((float) xDist * anim);
            int yPos = snapbackStartY + (int) ((float) yDist * anim);
            renderFloatingItem(render, snapbackItem, xPos + leftPos, yPos + topPos, null);
            ret = modularGui.doesFloatingItemDisableToolTips();
        }

        return ret;
    }

    protected boolean renderHoveredStackToolTip(GuiRender guiGraphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            GuiElement<?> handler = modularGui.getSlotHandler(hoveredSlot);
            if (handler != null && (handler.blockMouseOver(handler, mouseX, mouseY) || !handler.isMouseOver())) {
                return false;
            }
            ItemStack itemStack = this.hoveredSlot.getItem();
            guiGraphics.toolTipWithImage(this.getTooltipFromContainerItem(itemStack), itemStack.getTooltipImage(), itemStack, mouseX, mouseY);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return modularGui.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return modularGui.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return modularGui.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        return modularGui.keyPressed(key, scancode, modifiers) || super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        return modularGui.keyReleased(key, scancode, modifiers) || super.keyReleased(key, scancode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return modularGui.charTyped(character, modifiers) || super.charTyped(character, modifiers);
    }

    //=== AbstractContainerMenu Overrides ===//

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (modularGui.vanillaSlotRendering()) {
            super.renderSlot(guiGraphics, slot);
        } else {
            renderingSlots = true;
        }
    }

    //Modular gui friendly version of the slot render
    @Override
    public void renderSlot(GuiRender render, Slot slot) {
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
                render.fill(slotX, slotY, slotX + 16, slotY + 16, 0x80ffffff);
            }
            render.renderItem(slotStack, slotX, slotY, 16, slot.x + (slot.y * this.imageWidth)); //TODO May want a random that does not change if the slot is moved.
            render.renderItemDecorations(slotStack, slotX, slotY, countString);
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

    @Override
    public void renderFloatingItem(GuiGraphics guiGraphics, ItemStack itemStack, int i, int j, String string) {
        if (modularGui.vanillaSlotRendering()) super.renderFloatingItem(guiGraphics, itemStack, i, j, string);
    }

    public void renderFloatingItem(GuiRender render, ItemStack itemStack, int x, int y, String string) {
        render.pose().pushPose();
        render.pose().translate(0.0F, 0.0F, 50F);
        render.renderItem(itemStack, x, y);
        render.renderItemDecorations(itemStack, x, y - (this.draggingItem.isEmpty() ? 0 : 8), string);
        render.pose().popPose();
    }

    @Nullable
    @Override
    public Slot findSlot(double mouseX, double mouseY) {
        Slot slot = super.findSlot(mouseX, mouseY);
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
