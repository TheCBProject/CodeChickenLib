package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.GeoParam.HEIGHT;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.WIDTH;

/**
 * A simple gui element that renders an item stack.
 * This width and height of this element should be constrained to the same value,
 * The stack size is based on the element size.
 * constrain size to 16x16 for the standard gui stack size.
 * <p>
 * Created by brandon3055 on 03/09/2023
 */
public class GuiItemStack extends GuiElement<GuiItemStack> implements BackgroundRender {
    private Supplier<ItemStack> stack;
    private Supplier<Boolean> decorate = () -> true;
    private Supplier<Boolean> toolTip = () -> true;

    public GuiItemStack(GuiParent<?> parent) {
        this(parent, () -> ItemStack.EMPTY);
    }

    public GuiItemStack(GuiParent<?> parent, ItemStack itemStack) {
        super(parent);
        setStack(itemStack);
    }

    public GuiItemStack(GuiParent<?> parent, Supplier<ItemStack> provider) {
        super(parent);
        setStack(provider);
    }

    public GuiItemStack setStack(Supplier<ItemStack> stackProvider) {
        this.stack = stackProvider;
        return this;
    }

    public GuiItemStack setStack(ItemStack stack) {
        this.stack = () -> stack;
        return this;
    }

    /**
     * Enable item stack decorations.
     * Meaning, Damage bar, Stack size, Item cool down, etc. (Default Enabled)
     */
    public GuiItemStack enableStackDecoration(boolean enableDecoration) {
        return enableStackDecoration(() -> enableDecoration);
    }

    /**
     * Enable item stack decorations.
     * Meaning, Damage bar, Stack size, Item cool down, etc. (Default Enabled)
     */
    public GuiItemStack enableStackDecoration(Supplier<Boolean> enableDecoration) {
        this.decorate = enableDecoration;
        return this;
    }

    /**
     * Enable the default item stack tooltip. (Default Enabled)
     * Note: If the {@link GuiItemStack} element has a tooltip applied via one of the element #setTooltip methods,
     * That will override the item stack tool tip.
     */
    public GuiItemStack enableStackToolTip(boolean enableToolTip) {
        return enableStackToolTip(() -> enableToolTip);
    }

    /**
     * Enable the default item stack tooltip. (Default Enabled)
     * Note: If the {@link GuiItemStack} element has a tooltip applied via one of the element #setTooltip methods,
     * That will override the item stack tool tip.
     */
    public GuiItemStack enableStackToolTip(Supplier<Boolean> enableToolTip) {
        this.toolTip = enableToolTip;
        return this;
    }

    //=== Internal methods ===//

    private double getStackSize() {
        return Math.max(getValue(WIDTH), getValue(HEIGHT));
    }

    @Override
    public void renderBehind(GuiGraphics render, double mouseX, double mouseY, float partialTicks) {
        ItemStack stack = this.stack.get();
        if (stack.isEmpty()) return;

        // TODO size needs to be applied via pose transform
        render.cc$renderItem(stack, xMin(), yMin(), (int) (xMin() + (xSize() * yMin())));
        if (decorate.get()) {
            render.cc$renderItemDecorations(Minecraft.getInstance().font, stack, xMin(), yMin());
        }
    }

    @Override
    public boolean renderOverlay(GuiGraphics graphics, double mouseX, double mouseY, float partialTicks, boolean consumed) {
        if (super.renderOverlay(graphics, mouseX, mouseY, partialTicks, consumed)) return true;
        var stack = this.stack.get();
        if (isMouseOver() && !stack.isEmpty() && toolTip.get()) {
            graphics.setTooltipForNextFrame(
                    font(),
                    Screen.getTooltipFromItem(Minecraft.getInstance(), stack),
                    stack.getTooltipImage(),
                    stack,
                    (int) mouseX,
                    (int) mouseY,
                    stack.get(DataComponents.TOOLTIP_STYLE)
            );
            return true;
        }
        return false;
    }
}
