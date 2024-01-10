package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.elements.GuiElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 01/09/2023
 */
public interface ToolTipHandler<T extends ToolTipHandler<T>> {

    Supplier<List<Component>> getTooltip();

    /**
     * Set a delay before element tooltip is displayed.
     * Default delay is 10 ticks.
     */
    T setTooltipDelay(int tooltipDelay);

    int getTooltipDelay();

    /**
     * Add hover text that is to be displayed when the user hovers their cursor over this element. (with a delay of 10 ticks)
     * If you have multiple stacked elements with tooltips, only the top most element under the cursor will display its hover text.
     *
     * @param Tooltip A single tooltip text component supplier.
     * @see #setTooltipDelay(int)
     */
    default T setTooltipSingle(@Nullable Component Tooltip) {
        return setTooltip(Tooltip == null ? null : () -> Collections.singletonList(Tooltip));
    }

    /**
     * Add hover text that is to be displayed when the user hovers their cursor over this element. (with a delay of 10 ticks)
     * If you have multiple stacked elements with tooltips, only the top most element under the cursor will display its hover text.
     *
     * @param Tooltip A single tooltip text component supplier.
     * @see #setTooltipDelay(int)
     */
    default T setTooltip(Component... Tooltip) {
        return setTooltip(Tooltip == null ? null : () -> List.of(Tooltip));
    }

    /**
     * Add hover text that is to be displayed when the user hovers their cursor over this element. (with a delay of 10 ticks)
     * If you have multiple stacked elements with tooltips, only the top most element under the cursor will display its hover text.
     *
     * @param tooltip A single line tooltip component supplier.
     * @see #setTooltipDelay(int)
     */
    default T setTooltipSingle(@Nullable Supplier<Component> tooltip) {
        return setTooltip(tooltip == null ? null : () -> Collections.singletonList(tooltip.get()));
    }

    /**
     * Add hover text that is to be displayed when the user hovers their cursor over this element. (with a delay of 10 ticks)
     * If you have multiple stacked elements with tooltips, only the top most element under the cursor will display its hover text.
     *
     * @param tooltip The tooltip lines. If null or empty, hover text will be disabled
     * @see #setTooltipDelay(int)
     */
    default T setTooltip(@Nullable List<Component> tooltip) {
        return setTooltip(tooltip == null ? null : () -> tooltip);
    }

    /**
     * Add hover text that is to be displayed when the user hovers their cursor over this element. (with a delay of 10 ticks)
     * If you have multiple stacked elements with tooltips, only the top most element under the cursor will display its hover text.
     *
     * @param tooltip The tooltip lines. If null or the returned list is empty, hover text will be disabled
     * @see #setTooltipDelay(int)
     */
    T setTooltip(@Nullable Supplier<List<Component>> tooltip);

    /**
     * Add hover text that is to be displayed when the user hovers their cursor over this element.
     * If you have multiple stacked elements with tooltips, only the top most element under the cursor will display its hover text.
     *
     * @param tooltip      The tooltip lines. If null or the returned list is empty, hover text will be disabled
     * @param tooltipDelay Delay before hover text is shown.
     */
    default T setTooltip(@Nullable Supplier<List<Component>> tooltip, int tooltipDelay) {
        setTooltip(tooltip);
        setTooltipDelay(tooltipDelay);
        return (T) this;
    }

    /**
     * The method responsible for rendering element tool tips.
     * Called from {@link GuiElement#renderOverlay(GuiRender, double, double, float, boolean)}
     */
    default boolean renderTooltip(GuiRender render, double mouseX, double mouseY) {
        Supplier<List<Component>> supplier = getTooltip();
        if (supplier == null) return false;
        List<Component> list = supplier.get();
        if (list.isEmpty()) return false;
        render.componentTooltip(list, mouseX, mouseY);
        return true;
    }
}
