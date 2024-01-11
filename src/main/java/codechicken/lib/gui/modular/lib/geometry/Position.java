package codechicken.lib.gui.modular.lib.geometry;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 24/08/2023
 */
public interface Position {

    double x();

    double y();

    default Position offset(double x, double y) {
        return create(x() + x, y() + y);
    }

    /**
     * Returns the position value for the given axis.
     */
    default double get(Axis axis) {
        return axis == Axis.X ? x() : y();
    }

    static Position create(double x, double y) {
        return new Immutable(x, y);
    }

    static Position create(Supplier<Double> getX, Supplier<Double> getY) {
        return new Dynamic(getX, getY);
    }

    /**
     * Creates a new position, bound to the specified parent's position.
     * */
    static Position create(GuiParent<?> parent) {
        return new Bound(parent);
    }

    record Immutable(@Override double x, @Override double y) implements Position { }

    record Bound(GuiParent<?> parent) implements Position {
        @Override
        public double x() {
            return parent.xMin();
        }

        @Override
        public double y() {
            return parent.yMin();
        }

        @Override
        public String toString() {
            return "Bound{" +
                    "x=" + x() +
                    ", y=" + y() +
                    '}';
        }
    }

    record Dynamic(Supplier<Double> getX, Supplier<Double> getY) implements Position {
        @Override
        public double x() {
            return getX.get();
        }

        @Override
        public double y() {
            return getY.get();
        }

        @Override
        public String toString() {
            return "Dynamic{" +
                    "x=" + x() +
                    ", y=" + y() +
                    '}';
        }
    }

    static class Mutable implements Position {
        private double x;
        private double y;

        public Mutable(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public double x() {
            return x;
        }

        @Override
        public double y() {
            return y;
        }

        @Override
        public Position offset(double x, double y) {
            this.x += x;
            this.y += y;
            return this;
        }

        public Position set(double x, double y) {
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public String toString() {
            return "Mutable{" +
                    "x=" + x() +
                    ", y=" + y() +
                    '}';
        }
    }

}
