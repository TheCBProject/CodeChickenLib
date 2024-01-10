package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.*;
import codechicken.lib.gui.modular.lib.geometry.*;
import codechicken.lib.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * This can be used as the base for anything that requires the linear movement of an element between two position.
 * e.g. Scroll bars, Slide controls and slide indicators.
 * <p>
 * Implementation is simple, Simply install a "Slide Element", this will be the moving element,
 * The movement of this element is confined to the bounds og the {@link GuiSlider}
 * <p>
 * The position of the slider is managed via the installed {@link SliderState}
 * <p>
 * Created by brandon3055 on 02/09/2023
 */
public class GuiSlider extends GuiElement<GuiSlider> {
    private final Axis axis;
    private SliderState state = SliderState.create(0.1);
    private GuiElement<?> slider;
    private double outOfBoundsDist = 50;
    private GuiElement<?> scrollableElement;

    private int dragButton = GuiButton.LEFT_CLICK;
    private int scrollDragButton = GuiButton.MIDDLE_CLICK;
    private boolean middleClickScroll = false;
    /**
     * This should theoretically never be needed, But just in case...
     */
    public boolean invertDragScroll = false;

    private boolean dragging = false;
    private double slideStartPos = 0;
    private Position clickPos = Position.create(0, 0);
    private boolean scrollableDragging = false;

    /**
     * Creates a basic gui slider that moves along the specified axis.
     * This includes a default slider element the width of which is bound to the GuiSlider,
     * And the length of which is controlled by {@link SliderState#sliderRatio()}
     */
    public GuiSlider(@NotNull GuiParent<?> parent, Axis axis) {
        super(parent);
        this.axis = axis;
        installSlider(new GuiElement<>(this));
        bindSliderLength();
        bindSliderWidth();
    }

    public GuiSlider(@NotNull GuiParent<?> parent, Axis axis, GuiElement<?> slider) {
        super(parent);
        this.axis = axis;
        installSlider(slider);
    }

    /**
     * Set the slider state used by this slider element.
     * The slider state is used to get and set the slider position.
     * It also controls scroll speed.
     */
    public GuiSlider setSliderState(SliderState state) {
        this.state = state;
        return this;
    }

    /**
     * For use cases where this slider is controlling something like a scroll element.
     * This enables scrolling when the cursor is over the scrollable element.
     * It can also enable scrolling via middle-click + drag.
     */
    public GuiSlider setScrollableElement(GuiElement<?> scrollableElement) {
        return setScrollableElement(scrollableElement, true);
    }

    /**
     * For use cases where this slider is controlling something like a scroll element.
     * This enables scrolling when the cursor is over the scrollable element.
     * It can also enable scrolling via middle-click + drag.
     */
    public GuiSlider setScrollableElement(GuiElement<?> scrollableElement, boolean middleClickScroll) {
        this.scrollableElement = scrollableElement;
        this.middleClickScroll = middleClickScroll;
        return this;
    }

    /**
     * Install an element to be used as the sliding element.
     * The sliders minimum position (meaning either LEFT or TOP) on the moving axis will be constrained the {@link GuiSlider}
     * Attempting to override this constraint  after installing the slider element will break the slider.
     * <p>
     * The size constraints, and position constraint for the non-moving axis need to be set by the implementor.
     *
     * @see #bindSliderLength()
     * @see #bindSliderWidth()
     */
    public GuiSlider installSlider(GuiElement<?> slider) {
        if (slider.getParent() != this) throw new IllegalStateException("slider element must be a child of the GuiSlider it is being installed in");
        if (this.slider != null) removeChild(this.slider);
        this.slider = slider;
        switch (axis) {
            case X -> slider.constrain(LEFT, Constraint.relative(get(LEFT), () -> (getValue(WIDTH) - slider.getValue(WIDTH)) * state.getPos()));
            case Y -> slider.constrain(TOP, Constraint.relative(get(TOP), () -> (getValue(HEIGHT) - slider.getValue(HEIGHT)) * state.getPos()));
        }
        return this;
    }

    /**
     * Sets up constraints to automatically control the slider element length.
     * The slider element length will be controlled by {@link SliderState#sliderRatio()}
     * This is used for things like gui scroll bars where the bar length changes based on the ratio of content in view.
     */
    public GuiSlider bindSliderLength() {
        switch (axis) {//Ensure we don't accidentally over-constrain
            case X -> slider.constrain(RIGHT, null).constrain(WIDTH, Constraint.dynamic(() -> getValue(WIDTH) * state.sliderRatio()));
            case Y -> slider.constrain(BOTTOM, null).constrain(HEIGHT, Constraint.dynamic(() -> getValue(HEIGHT) * state.sliderRatio()));
        }
        return this;
    }

    /**
     * Binds the sliders position and size on the non-moving axis to the width and pos of the {@link GuiSlider}
     */
    public GuiSlider bindSliderWidth() {
        switch (axis) {//Ensure we don't accidentally over-constrain
            case X -> slider.constrain(HEIGHT, null).constrain(TOP, Constraint.match(get(TOP))).constrain(BOTTOM, Constraint.match(get(BOTTOM)));
            case Y -> slider.constrain(WIDTH, null).constrain(LEFT, Constraint.match(get(LEFT))).constrain(RIGHT, Constraint.match(get(RIGHT)));
        }
        return this;
    }

    /**
     * @return the installed slider element.
     */
    public GuiElement<?> getSlider() {
        return slider;
    }

    /**
     * @return True if the slider is currently being dragged by the user.
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     * Set the out-of-bounds distance,
     * If the cursor is dragged more than this distance from the slider bounds on the no-moving axis,
     * the slider will snap back to its original position until the cursor moves back into bounds.
     * Default is 50, -1 will disable the snap-back functionality.
     */
    public GuiSlider setOutOfBoundsDist(double outOfBoundsDist) {
        this.outOfBoundsDist = outOfBoundsDist;
        return this;
    }

    /**
     * @param dragButton The mouse button used to drag this slider (Default {@link GuiButton#LEFT_CLICK})
     */
    public GuiSlider setDragButton(int dragButton) {
        this.dragButton = dragButton;
        return this;
    }

    /**
     * @param scrollDragButton The button used to scroll by clicking and dragging the defined scrollableElement (Default {@link GuiButton#MIDDLE_CLICK})
     * @see #setScrollableElement(GuiElement, boolean)
     */
    public GuiSlider setScrollDragButton(int scrollDragButton) {
        this.scrollDragButton = scrollDragButton;
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        dragging = false;
        clickPos = Position.create(mouseX, mouseY);
        slideStartPos = state.getPos();
        if (button == dragButton && isMouseOver()) {
            if (!slider.isMouseOver()) {
                clickPos = Position.create(slider.xCenter(), slider.yCenter());
                handleDrag(mouseX, mouseY);
            }
            dragging = true;
            return true;
        }
        if (button == scrollDragButton && scrollableElement != null && scrollableElement.isMouseOver()) {
            scrollableDragging = true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
        dragging = scrollableDragging = false;
        return super.mouseReleased(mouseX, mouseY, button, consumed);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (dragging || (scrollableDragging && scrollableElement != null)) {
            handleDrag(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    private void handleDrag(double mouseX, double mouseY) {
        Position mousePos = Position.create(mouseX, mouseY);
        Rectangle rect = dragging || scrollableElement == null ? getRectangle() : scrollableElement.getRectangle();

        if (dragging && outOfBoundsDist >= -1 && rect.distance(axis.opposite(), mousePos) > outOfBoundsDist) {
            state.setPos(slideStartPos);
            return;
        }

        double travel = rect.size(axis) - slider.getRectangle().size(axis);
        if (travel <= 0) return;
        double clickPos = this.clickPos.get(axis);
        double currentPos = mousePos.get(axis);
        double movement = (currentPos - clickPos) / travel;

        if (scrollableDragging) {
            movement *= invertDragScroll ? state.sliderRatio() : -state.sliderRatio();
        }

        state.setPos(MathHelper.clip(slideStartPos + movement, 0, 1));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (isMouseOver() || (scrollableElement != null && scrollableElement.isMouseOver())) {
            if (!state.canScroll(axis)) return false;
            state.setPos(MathHelper.clip(state.getPos() + (state.scrollSpeed() * -scroll), 0, 1));
            return true;
        }
        return false;
    }

    /**
     * Vanilla does not really seem to have a standard for its scroll bars,
     * But this is something that should at least fit in to a typical vanilla gui.
     */
    public static Assembly<GuiRectangle, GuiSlider> vanillaScrollBar(GuiElement<?> parent, Axis axis) {
        GuiRectangle background = GuiRectangle.vanillaSlot(parent);

        GuiSlider slider = new GuiSlider(background, axis);
        Constraints.bind(slider, background, 1);

        slider.installSlider(GuiRectangle.planeButton(slider))
                .bindSliderLength()
                .bindSliderWidth();

        GuiRectangle sliderHighlight = new GuiRectangle(slider.getSlider())
                .fill(0x5000b6FF)
                .setEnabled(() -> slider.getSlider().isMouseOver());

        Constraints.bind(sliderHighlight, slider.getSlider());

        return new Assembly<>(background, slider).addParts(sliderHighlight);
    }
}
