package codechicken.lib.gui.modular.lib.geometry;

import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

/**
 * Denies how each of the three axis parameters are computed based on the available constraints.
 * Note: If both min and max are defined, size is ignored.
 */
public enum AxisConfig {
    //@formatter:off
    //              |           Compute Axis Min              |              Compute Axis Max             |                Compute Axis Size             |
    /**
     * If nothing is constrained.
     * min=0, max=0, size=0
     * */
    NONE        (0, (min, max, size) -> 0D,                     (min, max, size) -> 0D,                     (min, max, size) -> 0D),
    /**
     * If only Min is constrained.
     * min=min, max=min, size=0
     * */
    MIN_ONLY    (1, (min, max, size) -> min.get(),              (min, max, size) -> min.get(),              (min, max, size) -> 0D),
    /**
     * If only Max is constrained.
     * min=max, max=max, size=0
     * */
    MAX_ONLY    (1, (min, max, size) -> max.get(),              (min, max, size) -> max.get(),              (min, max, size) -> 0D),
    /**
     * If only Size is constrained.
     * min=0, max=size, size=size
     * */
    SIZE_ONLY   (1, (min, max, size) -> 0D,                     (min, max, size) -> size.get(),             (min, max, size) -> size.get()),
    /**
     * If Min and Size are constrained .
     * min=min, max=min+size, size=size
     * */
    MIN_SIZE    (2, (min, max, size) -> min.get(),              (min, max, size) -> min.get() + size.get(), (min, max, size) -> size.get()),
    /**
     * If Max and Size are constrained.
     * min=max-size, max=max, size=size
     * */
    MAX_SIZE    (2, (min, max, size) -> max.get() - size.get(), (min, max, size) -> max.get(),              (min, max, size) -> size.get()),
    /**
     * If Min and Max are constrained.
     * min=min, max=max, size=max-min
     * */
    MIN_MAX     (2, (min, max, size) -> min.get(),              (min, max, size) -> max.get(),              (min, max, size) -> max.get() - min.get()),
    /**
     * If Min, Max and Size are constrained the Size is ignored.
     * min=min, max=max, size=max-min
     * */
    MIN_MAX_SIZE(3, (min, max, size) -> min.get(),              (min, max, size) -> max.get(),              (min, max, size) -> max.get() - min.get());
    //@formatter:on

    public final int constraints;
    public final TriFunction<Constraint, Constraint, Constraint, Double> min;
    public final TriFunction<Constraint, Constraint, Constraint, Double> max;
    public final TriFunction<Constraint, Constraint, Constraint, Double> size;
    //[min][max][size] TODO, I Clean this up once i confirm everything works correctly.
    private static final AxisConfig[][][] LOOKUP = new AxisConfig[][][]{
            { //Min = 0
                    { //Max = 0
                            NONE,       //Size = 0
                            SIZE_ONLY   //Size = 1
                    },
                    { //Max = 1
                            MAX_ONLY,   //Size = 0
                            MAX_SIZE    //Size = 1
                    }
            },
            { //Min = 1
                    { //Max = 0
                            MIN_ONLY,   //Size = 0
                            MIN_SIZE    //Size = 1
                    },
                    { //Max = 1
                            MIN_MAX,    //Size = 0
                            MIN_MAX_SIZE//Size = 1
                    }
            }
    };

    AxisConfig(int constraints,
               TriFunction<Constraint, Constraint, Constraint, Double> min,
               TriFunction<Constraint, Constraint, Constraint, Double> max,
               TriFunction<Constraint, Constraint, Constraint, Double> size) {
        this.constraints = constraints;
        this.min = min;
        this.max = max;
        this.size = size;
    }

    public static AxisConfig getConfigFor(@Nullable Constraint min, @Nullable Constraint max, @Nullable Constraint size) {
        return LOOKUP[min != null ? 1 : 0][max != null ? 1 : 0][size != null ? 1 : 0];
    }
}
