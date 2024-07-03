package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.lib.geometry.Borders;
import codechicken.lib.gui.modular.lib.geometry.ConstrainedGeometry;
import net.covers1624.quack.annotation.ReplaceWithExpr;

import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * This class contains a bunch of static helper methods that can be used to quickly apply common constraints.
 * The plan is to keep adding common constraints to this class as they pop up.
 * <p>
 * Created by brandon3055 on 28/08/2023
 */
public class Constraints {

    /**
     * Bind an elements geometry to a reference element.
     *
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     */
    public static void bind(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference) {
        bind(element, reference, 0.0);
    }

    /**
     * Bind an elements geometry to a reference element with border offsets. (Border offsets may be positive or negative)
     *
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     */
    public static void bind(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference, double borders) {
        bind(element, reference, borders, borders, borders, borders);
    }

    /**
     * Bind an elements geometry to a reference element with border offsets. (Border offsets may be positive or negative)
     *
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     */
    public static void bind(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference, double top, double left, double bottom, double right) {
        element.constrain(TOP, relative(reference.get(TOP), top));
        element.constrain(LEFT, relative(reference.get(LEFT), left));
        element.constrain(BOTTOM, relative(reference.get(BOTTOM), -bottom));
        element.constrain(RIGHT, relative(reference.get(RIGHT), -right));
    }

    /**
     * Bind an elements geometry to a reference element with border offsets. (Border offsets may be positive or negative)
     * The border offsets are dynamic, meaning if the values stored in the {@link Borders} object are updated, this binding will reflect those changes automatically.
     *
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     * @param borders   Border offsets.
     */
    public static void bind(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference, Borders borders) {
        element.constrain(TOP, relative(reference.get(TOP), borders::top));
        element.constrain(LEFT, relative(reference.get(LEFT), borders::left));
        element.constrain(BOTTOM, relative(reference.get(BOTTOM), () -> -borders.bottom()));
        element.constrain(RIGHT, relative(reference.get(RIGHT), () -> -borders.right()));
    }

    public static void size(ConstrainedGeometry<?> element, double width, double height) {
        element.constrain(WIDTH, literal(width));
        element.constrain(HEIGHT, literal(height));
    }

    public static void size(ConstrainedGeometry<?> element, Supplier<Double> width, Supplier<Double> height) {
        element.constrain(WIDTH, dynamic(width));
        element.constrain(HEIGHT, dynamic(height));
    }

    public static void pos(ConstrainedGeometry<?> element, double left, double top) {
        element.constrain(LEFT, literal(left));
        element.constrain(TOP, literal(top));
    }

    public static void pos(ConstrainedGeometry<?> element, Supplier<Double> left, Supplier<Double> top) {
        element.constrain(LEFT, dynamic(left));
        element.constrain(TOP, dynamic(top));
    }

    public static void center(ConstrainedGeometry<?> element, ConstrainedGeometry<?> centerOn) {
        element.constrain(TOP, midPoint(centerOn.get(TOP), centerOn.get(BOTTOM), () -> element.ySize() / -2));
        element.constrain(LEFT, midPoint(centerOn.get(LEFT), centerOn.get(RIGHT), () -> element.xSize() / -2));
    }

    public static void center(ConstrainedGeometry<?> element, ConstrainedGeometry<?> centerOn, double xOffset, double yOffset) {
        element.constrain(TOP, midPoint(centerOn.get(TOP), centerOn.get(BOTTOM), () -> (element.ySize() / -2) + yOffset));
        element.constrain(LEFT, midPoint(centerOn.get(LEFT), centerOn.get(RIGHT), () -> (element.xSize() / -2) + xOffset));
    }

    /**
     * Constrain the specified element to a position inside the specified targetElement.
     * See the following image for an example of what each LayoutPos does:
     * <a href="https://ss.brandon3055.com/e89a6">https://ss.brandon3055.com/e89a6</a>
     *
     * @param target    The element whose position we are setting.
     * @param reference The reference element, the element that target will be placed inside.
     * @param position  The layout position.
     */
    @Deprecated
    @ReplaceWithExpr ("target.placeInside")
    public static void placeInside(ConstrainedGeometry<?> target, ConstrainedGeometry<?> reference, LayoutPos position) {
        target.placeInside(reference, position);
    }

    /**
     * Constrain the specified element to a position inside the specified targetElement.
     * See the following image for an example of what each LayoutPos does:
     * <a href="https://ss.brandon3055.com/e89a6">https://ss.brandon3055.com/e89a6</a>
     *
     * @param target    The element whose position we are setting.
     * @param reference The reference element, the element that target will be placed inside.
     * @param position  The layout position.
     * @param xOffset   Optional X offset to be applied to the final position.
     * @param yOffset   Optional Y offset to be applied to the final position.
     */
    @Deprecated
    @ReplaceWithExpr ("target.placeInside")
    public static void placeInside(ConstrainedGeometry<?> target, ConstrainedGeometry<?> reference, LayoutPos position, double xOffset, double yOffset) {
        target.placeInside(reference, position, xOffset, yOffset);
    }

    /**
     * Constrain the specified element to a position outside the specified targetElement.
     * See the following image for an example of what each LayoutPos does:
     * <a href="https://ss.brandon3055.com/baa7c">https://ss.brandon3055.com/baa7c</a>
     *
     * @param target    The element whose position we are setting.
     * @param reference The reference element, the element that target will be placed outside of.
     * @param position  The layout position.
     */
    @Deprecated
    @ReplaceWithExpr ("target.placeOutside")
    public static void placeOutside(ConstrainedGeometry<?> target, ConstrainedGeometry<?> reference, LayoutPos position) {
        target.placeOutside(reference, position);
    }

    /**
     * Constrain the specified element to a position outside the specified targetElement.
     * See the following image for an example of what each LayoutPos does:
     * <a href="https://ss.brandon3055.com/baa7c">https://ss.brandon3055.com/baa7c</a>
     *
     * @param target    The element whose position we are setting.
     * @param reference The reference element, the element that target will be placed outside of.
     * @param position  The layout position.
     * @param xOffset   Optional X offset to be applied to the final position.
     * @param yOffset   Optional Y offset to be applied to the final position.
     */
    @Deprecated
    @ReplaceWithExpr ("target.placeOutside")
    public static void placeOutside(ConstrainedGeometry<?> target, ConstrainedGeometry<?> reference, LayoutPos position, double xOffset, double yOffset) {
        target.placeOutside(reference, position, xOffset, yOffset);
    }

    public enum LayoutPos {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_RIGHT,
        MIDDLE_LEFT,
        BOTTOM_RIGHT,
        BOTTOM_CENTER,
        BOTTOM_LEFT
    }
}
