package codechicken.lib.gui.modular.lib.geometry;

import codechicken.lib.gui.modular.elements.GuiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * This is the base class used to define the size and position of a GuiElement.
 * <p>
 * The geometry system is designed to be very user-friendly, yet very powerful, with the ability to do all sorts of fun and interesting things.
 * But if all you want is the ability to set simple size and position values, then there is no need to read any further.
 * Just use the basic setters setXPos, setYPos, setWidth, setHeight and everything should just work.
 * If you want to dive deeper, read on.
 * <p>
 * The geometry system is based around 6 core parameters: xMin, xMax, xSize, yMin, yMax and ySize. See: {@link GeoParam}<br>
 * These parameters are defined using 'Constraints' See: {@link Constraint}<br>
 * When properly constrained, it will be possible to request values for any of these 6 parameters.<br>
 * For each axis (x and y) there are 3 constraints: min, max and size.<br>
 * In order for an axis to be properly defined 2 of these constraints need to be set.<br>
 * e.g. (xMin and xSize) or (xMin and xMax) or (xMax and xSize)<br>
 * Using any of these combinations the position and size of an axis can be computed.
 * <p>
 * Look at {@link AxisConfig} for details on the values that get returned when too few constraints are set.<br>
 * TLDR: We will always try to return "reasonable" default values. e.g. if only size is constrained, xMin will default
 * to 0 and xMax will default to 0 + xSize.<br>
 * If all three constraints are set then the size constraint will be ignored.<br>
 * You can use {@link #strictMode(boolean)} to change this behavior.
 * <p>
 * There are a number of different types of constraints that can be used, including completely custom constraints.<br>
 * Take a look at the {@link Constraint} class for a detailed list with explanations.
 * <p>
 * Note:
 * All position and size values in ModularGui use doubles. This is because floating point position and size values
 * can be very useful at times. But they can also cause a lot of ugly visual artifacts when not used properly.
 * So by default, all the builtin constraints will cast their outputs to an integer value.
 * If you need floating point precision, you can enable it by calling .precise() on any of the builtin constraints.
 * <p>
 * Created by brandon3055 on 29/06/2023
 */
public abstract class ConstrainedGeometry<T extends ConstrainedGeometry<T>> implements GuiParent<T> {

    private Constraint xMin = null;
    private Constraint xMax = null;
    private Constraint xSize = null;
    private AxisConfig xAxis = AxisConfig.NONE;

    private Constraint yMin = null;
    private Constraint yMax = null;
    private Constraint ySize = null;
    private AxisConfig yAxis = AxisConfig.NONE;

    //Permanently bound immutable position and rectangle elements.
    private final Position position = Position.create(this);
    private final Rectangle rectangle = Rectangle.create(this);
    private final Rectangle.Mutable childBounds = getRectangle().mutable();

    private boolean strictMode = false;

    @NotNull
    public abstract GuiParent<?> getParent();

    public GeoRef getParent(GeoParam param) {
        return getParent().get(param);
    }

    //=== Simple Setters ===//

    /**
     * Simple method for setting the x position of this element.
     * <p>
     * Constrains the left side of this element, to the left side of the parent element,
     * with an offset that is calculated by subtracting parent's current x pos from the given x pos.
     * <p>
     * In other words, if the parent element's position changes, this element will move with it.
     *
     * @param x The X position in screen space.
     * @return This Element.
     */
    public T setXPos(double x) {
        GeoRef parentLeft = getParent(LEFT);
        return constrain(LEFT, Constraint.relative(parentLeft, x - parentLeft.get()));
    }

    /**
     * Simple method for setting the y position of this element.
     * <p>
     * Constrains the top side of this element, to the top side of the parent element,
     * with an offset that is calculated by subtracting parent's current y pos from the given y pos.
     * <p>
     * In other words, if the parent element's position changes, this element will move with it.
     *
     * @param y The Y position in screen space.
     * @return This Element.
     */
    public T setYPos(double y) {
        GeoRef parentTop = getParent(LEFT);
        return constrain(LEFT, Constraint.relative(parentTop, y - parentTop.get()));
    }

    /**
     * Convenience method for setting both x and y positions.
     *
     * @param x The X position in screen space.
     * @param y The Y position in screen space.
     * @return This Element.
     * @see #setXPos(double)
     * @see #setYPos(double)
     */
    public T setPos(double x, double y) {
        return setXPos(x).setYPos(y);
    }

    /**
     * Simple method for setting the width of this element.
     *
     * @param width The width to apply.
     * @return This Element.
     */
    public T setWidth(double width) {
        return constrain(WIDTH, Constraint.literal(width));
    }

    /**
     * Simple method for setting the height of this element.
     *
     * @param height The height to apply.
     * @return This Element.
     */
    public T setHeight(double height) {
        return constrain(HEIGHT, Constraint.literal(height));
    }

    /**
     * Convenience method for setting both width and height.
     *
     * @param width  The width to apply.
     * @param height The height to apply.
     * @return This Element.
     * @see #setWidth(double)
     * @see #setHeight(double)
     */
    public T setSize(double width, double height) {
        return setWidth(width).setHeight(height);
    }

    //=== Everything related to constraint based geometry ===//

    /**
     * @return The position of the Left edge of this element.
     */
    @Override
    public double xMin() {
        return xAxis.min.apply(xMin, xMax, xSize);
    }

    /**
     * @return The position of the Right edge of this element.
     */
    @Override
    public double xMax() {
        return xAxis.max.apply(xMin, xMax, xSize);
    }

    /**
     * @return The Width of this element.
     */
    @Override
    public double xSize() {
        return xAxis.size.apply(xMin, xMax, xSize);
    }

    /**
     * @return The position of the Top edge of this element.
     */
    @Override
    public double yMin() {
        return yAxis.min.apply(yMin, yMax, ySize);
    }

    /**
     * @return The position of the Bottom edge of this element.
     */
    @Override
    public double yMax() {
        return yAxis.max.apply(yMin, yMax, ySize);
    }

    /**
     * @return The Height of this element.
     */
    @Override
    public double ySize() {
        return yAxis.size.apply(yMin, yMax, ySize);
    }

    /**
     * Returns a reference to the specified geometry parameter.
     * This is primarily used when defining geometry constraints.
     * But it can also be used as a simple {@link Supplier<Integer>}
     * that will return the current parameter value when requested.
     * <p>
     * Note: The returned geometry reference will always be valid
     *
     * @param param The geometry parameter.
     * @return A Geometry Reference
     */
    @Override
    public GeoRef get(GeoParam param) {
        return new GeoRef(this, param);
    }

    /**
     * @param param      The geometry parameter to be constrained.
     * @param constraint The constraint to apply
     * @return This Element.
     */
    @SuppressWarnings ("unchecked")
    public T constrain(GeoParam param, @Nullable Constraint constraint) {
        if (constraint != null && constraint.axis() != null && constraint.axis() != param.axis) {
            throw new IllegalStateException("Attempted to apply constraint for axis: " + constraint.axis() + ", to Parameter: " + param);
        }
        if (param.axis == Axis.X) {
            constrainX(param, constraint);
        } else if (param.axis == Axis.Y) {
            constrainY(param, constraint);
        }
        return (T) this;
    }

    /**
     * Clear any configured constraints and reset this element to default unconstrained state.
     * Convenient when reconfiguring an elements constraints or applying constraints to an element
     * with an existing, unknown constraint configuration.
     */
    @SuppressWarnings ("unchecked")
    public T clearConstraints() {
        xMin = xMax = xSize = yMin = yMax = ySize = null;
        xAxis = yAxis = AxisConfig.NONE;
        return (T) this;
    }

    private void constrainX(GeoParam param, @Nullable Constraint constraint) {
        if (param == GeoParam.LEFT) {
            xMin = constraint;
        } else if (param == GeoParam.RIGHT) {
            xMax = constraint;
        } else if (param == WIDTH) {
            xSize = constraint;
        }
        xAxis = AxisConfig.getConfigFor(xMin, xMax, xSize);
        validate();
    }

    private void constrainY(GeoParam param, @Nullable Constraint constraint) {
        if (param == GeoParam.TOP) {
            yMin = constraint;
        } else if (param == GeoParam.BOTTOM) {
            yMax = constraint;
        } else if (param == GeoParam.HEIGHT) {
            ySize = constraint;
        }
        yAxis = AxisConfig.getConfigFor(yMin, yMax, ySize);
        validate();
    }

    /**
     * Strict mode is intended to help catch potential mistakes when writing modular GUIs
     * <p>
     * Enforces a strict requirement for each exist to have two and only two constraints.
     * Any attempt to over-constrain an axis will throw an immediate fatal exception.
     * If an axis is under-constrained then a fatal exception will be thrown when a value from the axis is queried.
     * <p>
     * Strict mode applies to this element, and recursively to all children of this element.
     *
     * @param strictMode Enable strict mode.
     * @return the geometry object.
     */
    @SuppressWarnings ("unchecked")
    public T strictMode(boolean strictMode) {
        this.strictMode = strictMode;
        //TODO Propagate to children (Will be handled in the base GuiElement)
        return (T) this;
    }

    //TODO This needs to be called from the parent element somewhere. Possibly on tick or render
    //Ideally i would like to find a way to only call it once. we need to account for constraints being modified after initial element construction.
    public void validate() {
        if (strictMode) {
            if (xAxis.constraints != 2) {
                throw new IllegalStateException(String.format("X axis of element: %s is %s constrained!", getParent(), xAxis.constraints < 2 ? "under" : "over"));
            } else if (yAxis.constraints != 2) {
                throw new IllegalStateException(String.format("Y axis of element: %s is %s constrained!", getParent(), yAxis.constraints < 2 ? "under" : "over"));
            }
        }
    }

    public void clearGeometryCache() {
        if (xMin != null) xMin.markDirty();
        if (xMax != null) xMax.markDirty();
        if (xSize != null) xSize.markDirty();
        if (yMin != null) yMin.markDirty();
        if (yMax != null) yMax.markDirty();
        if (ySize != null) ySize.markDirty();
        getChildren().forEach(ConstrainedGeometry::clearGeometryCache);
    }

    //=== Geometry Utilities ===//

    /**
     * Returns a {@link Position} that is permanently bound to this element.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Returns a {@link Rectangle} that is permanently bound to this element.
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    public double xCenter() {
        return xMin() + (xSize() / 2);
    }

    public double yCenter() {
        return yMin() + (ySize() / 2);
    }

    /**
     * Returns a new {@link Rectangle} the bounds of which will enclose this element and all of its child
     * elements recursively.
     */
    public Rectangle.Mutable getEnclosingRect() {
        return addBoundsToRect(getRectangle().mutable());
    }

    /**
     * Expands the bounds of the given rectangle (if needed) so that they enclose this element.
     * And all of its child elements recursively.
     */
    public Rectangle.Mutable addBoundsToRect(Rectangle.Mutable enclosingRect) {
        enclosingRect.combine(getRectangle());
        for (GuiElement<?> element : getChildren()) {
            if (element.isEnabled()) {
                element.addBoundsToRect(enclosingRect);
            }
        }
        return enclosingRect;
    }

    /**
     * @return a rectangle, the bounds of which enclose all enabled child elements.
     * If there are no enabled child elements the returned rect will have the position of this element, with zero size.
     */
    public Rectangle.Mutable getChildBounds() {
        boolean set = false;
        for (GuiElement<?> element : getChildren()) {
            if (element.isEnabled()) {
                if (!set) {
                    childBounds.set(element.getRectangle());
                    set = true;
                } else {
                    element.addBoundsToRect(childBounds);
                }
            }
        }
        if (!set) childBounds.setPos(xMin(), yMin()).setSize(0, 0);
        return childBounds;
    }
}
