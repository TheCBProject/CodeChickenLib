package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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

    public GuiItemStack(@NotNull GuiParent<?> parent) {
        this(parent, () -> ItemStack.EMPTY);
    }

    public GuiItemStack(@NotNull GuiParent<?> parent, ItemStack itemStack) {
        super(parent);
        setStack(itemStack);
    }

    public GuiItemStack(@NotNull GuiParent<?> parent, Supplier<ItemStack> provider) {
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

    public double getStackSize() {
        return Math.max(getValue(WIDTH), getValue(HEIGHT));
    }

    @Override
    public double getBackgroundDepth() {
        return getStackSize();
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        ItemStack stack = this.stack.get();
        if (stack.isEmpty()) return;

        render.renderItem(stack, xMin(), yMin(), getStackSize(), (int) (xMin() + (xSize() * yMin())));
        if (decorate.get()) {
            render.renderItemDecorations(stack, xMin(), yMin(), getStackSize());
        }
    }

    @Override
    public boolean renderOverlay(GuiRender render, double mouseX, double mouseY, float partialTicks, boolean consumed) {
        if (super.renderOverlay(render, mouseX, mouseY, partialTicks, consumed)) return true;
        if (isMouseOver() && !stack.get().isEmpty()) {
            render.renderTooltip(stack.get(), mouseX, mouseY);
            return true;
        }
        return false;
    }
}
