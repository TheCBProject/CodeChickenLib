package codechicken.lib.gui.modular.lib.geometry;

import net.minecraft.client.renderer.Rect2i;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 14/08/2023
 */
public interface Rectangle {

    Position pos();

    default double x() {
        return pos().x();
    }

    default double y() {
        return pos().y();
    }

    double width();

    double height();

    default double xMax() {
        return x() + width();
    }

    default double yMax() {
        return y() + height();
    }

    /**
     * Returns a new rectangle with this operation applied
     */
    default Rectangle offsetPos(double xAmount, double yAmount) {
        return create(x() + xAmount, y() + yAmount, width(), height());
    }

    /**
     * Returns a new rectangle with this operation applied
     */
    default Rectangle setPos(double newX, double newY) {
        return create(newX, newY, width(), height());
    }

    /**
     * Returns a new rectangle with this operation applied
     */
    default Rectangle setSize(double width, double height) {
        return create(x(), y(), width, height);
    }

    /**
     * Returns a new rectangle with this operation applied
     */
    default Rectangle offsetSize(double xAmount, double yAmount) {
        return create(x(), y(), width() + xAmount, height() + yAmount);
    }

    default Rect2i toRect2i() {
        return new Rect2i((int) x(), (int) y(), (int) width(), (int) height());
    }

    default boolean intersects(double x, double y, double w, double h) {
        double x0 = x();
        double y0 = y();
        return (x + w > x0 && y + h > y0 && x < x0 + width() && y < y0 + height());
    }

    default boolean intersects(Rectangle other) {
        double x0 = x();
        double y0 = y();
        return (other.x() + other.width() > x0 && other.y() + other.height() > y0 && other.x() < x0 + width() && other.y() < y0 + height());
    }

    /**
     * Returns a new rectangle that represents the intersection area between the two inputs
     */
    default Rectangle intersect(Rectangle other) {
        double x = Math.max(x(), other.x());
        double y = Math.max(y(), other.y());
        double width = Math.max(0, Math.min(xMax(), other.xMax()) - x());
        double height = Math.max(0, Math.min(yMax(), other.yMax()) - y());
        return create(x, y, width, height);
    }

    /**
     * Returns a new rectangle, the bounds of which enclose all the input rectangles.
     *
     * @param combineWith Rectangles to combine with the start rectangle
     */
    default Rectangle combine(Rectangle... combineWith) {
        double x = x();
        double y = y();
        double maxX = xMax();
        double maxY = yMax();
        for (Rectangle other : combineWith) {
            x = Math.min(x, other.x());
            y = Math.min(y, other.y());
            maxX = Math.max(maxX, other.xMax());
            maxY = Math.max(maxY, other.yMax());
        }
        return create(x, y, maxX - x, maxY - y);
    }

    default boolean contains(double x, double y) {
        return x >= x() && x <= x() + width() && y >= y() && y <= y() + height();
    }

    /**
     * @return the size of this rectangle on the given axis.
     */
    default double size(Axis axis) {
        return axis == Axis.X ? width() : height();
    }

    /**
     * @return the min value the specified axis (meaning x() or y())
     */
    default double min(Axis axis) {
        return axis == Axis.X ? x() : y();
    }

    /**
     * @return the max value the specified axis (meaning x() + width() or y() + height())
     */
    default double max(Axis axis) {
        return axis == Axis.X ? xMax() : yMax();
    }

    /**
     * Return the distance the given position is from this rectangle on the specified axis.
     * Will return 0 if position is inside this rectangle on the given axis.
     */
    default double distance(Axis axis, Position position) {
        double pos = position.get(axis);
        double min = min(axis);
        double max = max(axis);
        return pos < min ? min - pos : pos > max ? pos - max : 0;
    }

    static Rectangle create(Position position, double width, double height) {
        return new Immutable(position, width, height);
    }

    static Rectangle create(double x, double y, double width, double height) {
        return new Immutable(Position.create(x, y), width, height);
    }

    /**
     * Returns a new rectangle bound to the specified parent's geometry.
     */
    static Rectangle create(GuiParent<?> parent) {
        return new Dynamic(Position.create(parent), parent::xSize, parent::ySize);
    }

    static Rectangle create(Position position, Supplier<Double> getWidth, Supplier<Double> getHeight) {
        return new Dynamic(position, getWidth, getHeight);
    }

    default Mutable mutable() {
        return new Mutable(new Position.Mutable(x(), y()), width(), height());
    }

    /**
     * Should not be created directly
     */
    record Immutable(Position position, double xSize, double ySize) implements Rectangle {
        @Override
        public Position pos() {
            return position;
        }

        @Override
        public double width() {
            return xSize;
        }

        @Override
        public double height() {
            return ySize;
        }

        @Override
        public String toString() {
            return "Immutable{" +
                    "pos=" + pos() +
                    ", width=" + width() +
                    ", height=" + height() +
                    '}';
        }
    }

    record Dynamic(Position position, Supplier<Double> getWidth, Supplier<Double> getHeight) implements Rectangle {
        @Override
        public Position pos() {
            return position;
        }

        @Override
        public double width() {
            return getWidth.get();
        }

        @Override
        public double height() {
            return getHeight.get();
        }

        @Override
        public String toString() {
            return "Dynamic{" +
                    "pos=" + pos() +
                    ", width=" + width() +
                    ", height=" + height() +
                    '}';
        }
    }

    class Mutable implements Rectangle {
        private Position.Mutable pos;
        private double width;
        private double height;

        public Mutable(Position.Mutable pos, double width, double height) {
            this.pos = pos;
            this.width = width;
            this.height = height;
        }

        @Override
        public Position pos() {
            return pos;
        }

        @Override
        public double width() {
            return width;
        }

        @Override
        public double height() {
            return height;
        }

        @Override
        public Rectangle offsetPos(double xAmount, double yAmount) {
            pos.offset(xAmount, yAmount);
            return this;
        }

        @Override
        public Rectangle setPos(double newX, double newY) {
            pos.set(newX, newY);
            return this;
        }

        @Override
        public Rectangle setSize(double width, double height) {
            this.width = width;
            this.height = height;
            return this;
        }

        @Override
        public Rectangle offsetSize(double xAmount, double yAmount) {
            this.width += xAmount;
            this.height += yAmount;
            return this;
        }

        @Override
        public Rectangle intersect(Rectangle other) {
            double x = Math.max(x(), other.x());
            double y = Math.max(y(), other.y());
            width = Math.max(0, Math.min(xMax(), other.xMax()) - x());
            height = Math.max(0, Math.min(yMax(), other.yMax()) - y());
            return setPos(x, y);
        }

        public void set(Rectangle rectangle) {
            this.pos.set(rectangle.x(), rectangle.y());
            this.width = rectangle.width();
            this.height = rectangle.height();
        }

        @Override
        public Rectangle combine(Rectangle... combineWith) {
            double x = x();
            double y = y();
            double maxX = xMax();
            double maxY = yMax();
            for (Rectangle other : combineWith) {
                x = Math.min(x, other.x());
                y = Math.min(y, other.y());
                maxX = Math.max(maxX, other.xMax());
                maxY = Math.max(maxY, other.yMax());
            }
            return setPos(x, y).setSize(maxX - x, maxY - y);
        }

        public Immutable immutable() {
            return new Immutable(Position.create(x(), y()), width(), height());
        }
    }
}
