package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.*;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.match;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.relative;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * So the logic behind this element is as follows.
 * This element contains a base "Content Element" that holds all the scrollable content.
 * The content element's position is controlled by the {@link GuiScrolling}
 * But its {@link GeoParam#WIDTH} and {@link GeoParam#HEIGHT} constraints can be set by the user,
 * Or they can be set to dynamically adjust to the child elements added to it.
 * <p>
 * The bounds of the {@link GuiScrolling} represent the "view window"
 * When scrolling up/down, left/right the Content Element is effectively just moving around behind the view window
 * and everything outside the view window is scissored off.
 * Any events that occur outside the view window are not propagated to scroll element.
 * Calls to {@link #isMouseOver()} from an area of an element that is outside the view window will return false.
 * <p>
 * Elements that are completely outside the view window will not be rendered at all for efficiency.
 * <p>
 * Created by brandon3055 on 01/09/2023
 */
public class GuiScrolling extends GuiElement<GuiScrolling> implements ContentElement<GuiElement<?>> {

    /**
     * This is made available primarily for debugging purposes where it can be useful to see what's going on behind the scenes.
     */
    public boolean enableScissor = true;
    private GuiElement<?> contentElement;
    private double xScrollPos = 0;
    private double yScrollPos = 0;
    private double contentWidth = 0;
    private double contentHeight = 0;
    private boolean setup = false;

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiScrolling(@NotNull GuiParent<?> parent) {
        super(parent);
        installContainerElement(new ContentElement(this));
    }

    //=== Scroll element setup ===//

    /**
     * Retrieves the content element that holds all the scrolling elements.
     * You must add all of your scrolling content to this element.
     * Scrolling content must also be constrained relative to this element.
     * <p>
     * The {@link GeoParam#TOP} and {@link GeoParam#LEFT} constraints for this element are set by the {@link GuiScrolling} and must not be overridden.
     * These are used to control the 'scrolling' of the element.
     * <p>
     * By default, the {@link GeoParam#WIDTH} and {@link GeoParam#HEIGHT} (and therefor also BOTTOM, RIGHT) are dynamically constrained to match the outer bounds of the scrolling elements.
     * So attempting to constrain the content to any of these dynamic parameters would result in a stack overflow.
     * You can however override the WIDTH and HEIGHT constraints if you wish.
     * This can be useful if you wish to create something like a fixed width scrolling list where the width of each scrolling element is bound to the width of the list.
     * <p>
     * The most important thing to note, Especially when manually constraining the WIDTH and HEIGHT of the content element,
     * All scrolling elements must be withing the bounds of the content element. Anything outside the content element's bounds will not be visible.
     *
     * @return The content element.
     */
    @Override
    public GuiElement<?> getContentElement() {
        return contentElement;
    }

    /**
     * This allows you to install a custom container element.
     * The elements constraints will automatically be set by this method.
     * <p>
     * After calling this method you may override the container element WIDTH and HEIGHT constraints as described in the documentation for {@link #getContentElement()}
     * But you must not touch the TOP or LEFT constraints.
     * <p>
     * Important thing to note, By default the container element is preinstalled before any children can be added, meaning any children added to the {@link GuiScrolling}
     * will render on top of the scrolling content.
     * As this method allows you to set a new child as the container element, any children added before the new content element, will render under the content element.
     *
     * @param element The new container element.
     */
    public void installContainerElement(GuiElement<?> element) {
        if (element.getParent() != this) throw new IllegalStateException("Content element must be a child of the GuiScrollingBase it is being installed in");
        if (contentElement != null) removeChild(contentElement);
        setup = true;
        contentElement = element;
        contentElement.setRenderCull(getRectangle());
        contentElement.constrain(TOP, Constraint.relative(get(TOP), () -> yScrollPos * -hiddenSize(Axis.Y)));
        contentElement.constrain(LEFT, Constraint.relative(get(LEFT), () -> xScrollPos * -hiddenSize(Axis.X)));
        contentElement.constrain(WIDTH, Constraint.dynamic(() -> contentElement.getChildBounds().xMax() - contentElement.xMin()));
        contentElement.constrain(HEIGHT, Constraint.dynamic(() -> contentElement.getChildBounds().yMax() - contentElement.yMin()));
        setup = false;
    }

    /**
     * @return a {@link SliderState} that can be used to get or control the scroll position of the specified axis.
     */
    public SliderState scrollState(Axis axis) {
        return switch (axis) {
            case X -> SliderState.forScrollBar(() -> xScrollPos, e -> xScrollPos = e, () -> MathHelper.clip(xSize() / contentElement.xSize(), 0, 1));
            case Y -> SliderState.forScrollBar(() -> yScrollPos, e -> yScrollPos = e, () -> MathHelper.clip(ySize() / contentElement.ySize(), 0, 1));
        };
    }

    //=== Internal logic ===//

    /**
     * @return the total content size / length for the given axis
     */
    public double totalSize(Axis axis) {
        return switch (axis) {
            case X -> contentWidth;
            case Y -> contentHeight;
        };
    }

    /**
     * @return the hidden content size / length for the given axis (How much of the content is outside the view area)
     */
    public double hiddenSize(Axis axis) {
        return switch (axis) {
            case X -> Math.max(contentWidth - xSize(), 0);
            case Y -> Math.max(contentHeight - ySize(), 0);
        };
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        //These can not be generated dynamically, Doing so would result in a calculation loop, aka a stack overflow.
        contentWidth = contentElement.xSize();
        contentHeight = contentElement.ySize();
    }

    @Override
    public boolean blockMouseOver(GuiElement<?> element, double mouseX, double mouseY) {
        return super.blockMouseOver(element, mouseX, mouseY) || (element.isDescendantOf(contentElement) && !this.isMouseOver());
    }

    //=== Rendering ===//

    @Override
    protected boolean renderChild(GuiElement<?> child, GuiRender render, double mouseX, double mouseY, float partialTicks) {
        boolean scissor = child == contentElement && enableScissor;
        if (scissor) render.pushScissorRect(getRectangle());
        boolean ret = super.renderChild(child, render, mouseX, mouseY, partialTicks);
        if (scissor) render.popScissor();
        return ret;
    }

    private class ContentElement extends GuiElement<ContentElement> {
        /**
         * @param parent parent {@link GuiParent}.
         */
        public ContentElement(@NotNull GuiParent<?> parent) {
            super(parent);
        }

        @Override
        public ContentElement constrain(GeoParam param, @Nullable Constraint constraint) {
            if (!setup && (param == TOP || param == LEFT)) throw new IllegalStateException("Can not override TOP or LEFT constraints on content element, These are used to control the scrolling behavior!");
            return super.constrain(param, constraint);
        }
    }

    public static Assembly<? extends GuiElement<?>, GuiScrolling> simpleScrollWindow(@NotNull GuiParent<?> parent, boolean verticalScrollBar, boolean horizontalScrollBar) {
        GuiElement<?> container = new GuiElement<>(parent);
        GuiRectangle background = GuiRectangle.vanillaSlot(container)
                .constrain(TOP, match(container.get(TOP)))
                .constrain(LEFT, match(container.get(LEFT)))
                .constrain(BOTTOM, relative(container.get(BOTTOM), horizontalScrollBar ? -10 : 0))
                .constrain(RIGHT, relative(container.get(RIGHT), verticalScrollBar ? -10 : 0));

        GuiScrolling scroll = new GuiScrolling(background);
        Constraints.bind(scroll, background, 1);

        var result = new Assembly<>(container, scroll);

        if (verticalScrollBar) {
            var bar = GuiSlider.vanillaScrollBar(container, Axis.Y);
            bar.container
                    .constrain(TOP, match(container.get(TOP)))
                    .constrain(BOTTOM, relative(container.get(BOTTOM), horizontalScrollBar ? -10 : 0))
                    .constrain(RIGHT, match(container.get(RIGHT)))
                    .constrain(WIDTH, Constraint.literal(9));
            bar.primary
                    .setSliderState(scroll.scrollState(Axis.Y))
                    .setScrollableElement(scroll);
            result.addParts(bar.container, bar.primary, bar.getPart(0));
        }
        if (horizontalScrollBar) {
            var bar = GuiSlider.vanillaScrollBar(container, Axis.X);
            bar.container
                    .constrain(BOTTOM, match(container.get(BOTTOM)))
                    .constrain(LEFT, match(container.get(LEFT)))
                    .constrain(RIGHT, relative(container.get(RIGHT), verticalScrollBar ? -10 : 0))
                    .constrain(HEIGHT, Constraint.literal(9));
            bar.primary
                    .setSliderState(scroll.scrollState(Axis.X))
                    .setScrollableElement(scroll);
            result.addParts(bar.container, bar.primary, bar.getPart(0));
        }
        return result;
    }
}
