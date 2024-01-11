package codechicken.lib.gui.modular.lib.geometry;

/**
 * Created by brandon3055 on 04/09/2023
 */
public enum Direction {
    UP(Axis.Y),
    LEFT(Axis.X),
    DOWN(Axis.Y),
    RIGHT(Axis.X);

    private static Direction[] VALUES = values();

    private final Axis axis;

    Direction(Axis axis) {
        this.axis = axis;
    }

    public Axis getAxis() {
        return axis;
    }

    public Direction opposite() {
        if (axis == Axis.X) return this == LEFT ? RIGHT : LEFT;
        else return this == UP ? DOWN : UP;
    }

    public Direction rotateCW() {
        return values()[(ordinal() + VALUES.length - 1) % VALUES.length];
    }

    public Direction rotateCCW() {
        return values()[(ordinal() + 1) % VALUES.length];
    }

    public double rotationTo(Direction other) {
        return (this.ordinal() - other.ordinal()) * 90;
    }
}
