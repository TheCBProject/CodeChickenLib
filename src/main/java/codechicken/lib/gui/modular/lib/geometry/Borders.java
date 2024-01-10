package codechicken.lib.gui.modular.lib.geometry;

import java.util.Objects;

/**
 * Created by brandon3055 on 28/08/2023
 */
public final class Borders {
    private double top;
    private double left;
    private double bottom;
    private double right;

    public Borders(double top, double left, double bottom, double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public static Borders create(double borders) {
        return create(borders, borders);
    }

    public static Borders create(double leftRight, double topBottom) {
        return create(topBottom, leftRight, topBottom, leftRight);
    }

    public static Borders create(double top, double left, double bottom, double right) {
        return new Borders(top, left, bottom, right);
    }

    public double top() {
        return top;
    }

    public double left() {
        return left;
    }

    public double bottom() {
        return bottom;
    }

    public double right() {
        return right;
    }

    public Borders setTop(double top) {
        this.top = top;
        return this;
    }

    public Borders setLeft(double left) {
        this.left = left;
        return this;
    }

    public Borders setBottom(double bottom) {
        this.bottom = bottom;
        return this;
    }

    public Borders setRight(double right) {
        this.right = right;
        return this;
    }

    public Borders setTopBottom(double topBottom) {
        return setTop(topBottom).setBottom(topBottom);
    }

    public Borders setLeftRight(double leftRight) {
        return setLeft(leftRight).setLeftRight(leftRight);
    }

    public Borders setBorders(double borders) {
        return setBorders(borders, borders);
    }

    public Borders setBorders(double topBottom, double leftRight) {
        return setBorders(topBottom, leftRight, topBottom, leftRight);
    }

    public Borders setBorders(double top, double left, double bottom, double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Borders) obj;
        return Double.doubleToLongBits(this.top) == Double.doubleToLongBits(that.top) &&
                Double.doubleToLongBits(this.left) == Double.doubleToLongBits(that.left) &&
                Double.doubleToLongBits(this.bottom) == Double.doubleToLongBits(that.bottom) &&
                Double.doubleToLongBits(this.right) == Double.doubleToLongBits(that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, left, bottom, right);
    }

    @Override
    public String toString() {
        return "Borders[" +
                "top=" + top + ", " +
                "left=" + left + ", " +
                "bottom=" + bottom + ", " +
                "right=" + right + ']';
    }

}
