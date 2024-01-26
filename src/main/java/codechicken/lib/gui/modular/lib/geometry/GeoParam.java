package codechicken.lib.gui.modular.lib.geometry;

/**
 * Used to define the 6 core parameters that make up an elements geometry
 * These are named Left, Right, Width, Top, Bottom, Height.
 * These names were chosen for ease of use, and to make it clear what they represent.
 * Internally they are known as xMin, xMax, xSize, yMin, yMax, ySize.
 * <p>
 * Created by brandon3055 on 30/06/2023
 */
public enum GeoParam {
    /** X_MIN */
    LEFT(Axis.X),
    /** X_MAX */
    RIGHT(Axis.X),
    /** X_SIZE */
    WIDTH(Axis.X),

    /** Y_MIN */
    TOP(Axis.Y),
    /** Y_MAX */
    BOTTOM(Axis.Y),
    /** Y_SIZE */
    HEIGHT(Axis.Y);

    public final Axis axis;

    GeoParam(Axis axis) {
        this.axis = axis;
    }

}
