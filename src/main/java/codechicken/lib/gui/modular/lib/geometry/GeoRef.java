package codechicken.lib.gui.modular.lib.geometry;

import java.util.function.Supplier;

/**
 * Used to access one of the 6 core parameters that make up an element's geometry.
 * <p>
 * The primary purpose of this class is to provide a convenient way to reference a geometry
 * parameter when defining constraints.
 * It also helps make the code more debuggable.
 * I could just make literally everything a lambda, but that makes debugging kinda painful when things break.
 * <p>
 * Created by brandon3055 on 30/06/2023
 */
public class GeoRef implements Supplier<Double> {
    public final GuiParent<?> geometry;
    public final GeoParam parameter;

    public GeoRef(GuiParent<?> geometry, GeoParam parameter) {
        this.geometry = geometry;
        this.parameter = parameter;
    }

    @Override
    public Double get() {
        return geometry.getValue(parameter);
    }

    @Override
    public String toString() {
        return "GeoReference{" +
                "geometry=" + geometry +
                ", parameter=" + parameter +
                '}';
    }
}
