package codechicken.lib.gui.modular.lib.geometry;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 04/07/2023
 */
public abstract non-sealed class ConstraintImpl<T extends ConstraintImpl<?>> implements Constraint {

    protected boolean precise = false;
    protected Axis axis = null;
    private double value;
    private boolean isDirty = true;

    /**
     * @return True if precise mode is enabled.
     * @see #precise()
     */
    public boolean isPrecise() {
        return precise;
    }

    /**
     * By default, all constraint values are cast to (int).
     * This helps avoid a lot of visual artifacts that can occur when using floating point positions in MC Screens.
     * <p>
     * Calling this enables precise mode, which allows floating point precision to be used.
     *
     * @return The Constraint.
     */
    @SuppressWarnings("unchecked")
    public T precise() {
        this.precise = true;
        return (T) this;
    }

    @Override
    public double get() {
        if (isDirty) {
            value = isPrecise() ? getImpl() : (int) getImpl();
            isDirty = false;
        }
        return value;
    }

    @Override
    public @Nullable Axis axis() {
        return axis;
    }

    @SuppressWarnings("unchecked")
    public T setAxis(@Nullable Axis axis) {
        this.axis = axis;
        return (T) this;
    }

    @Override
    public void markDirty() {
        isDirty = true;
    }

    protected abstract double getImpl();

    public static class Literal extends ConstraintImpl<Literal> {
        protected final double value;

        /**
         * Returns the literal value supplied.
         */
        public Literal(double value) {
            this.value = value;
        }

        @Override
        protected double getImpl() {
            return value;
        }
    }

    public static class Dynamic extends ConstraintImpl<Dynamic> {
        protected final Supplier<Double> value;

        /**
         * Returns a dynamic value that will be retrieved from the provided supplier.
         */
        public Dynamic(Supplier<Double> value) {
            this.value = value;
        }

        @Override
        protected double getImpl() {
            return value.get();
        }
    }

    public static class Relative extends ConstraintImpl<Relative> {
        protected final GeoRef relTo;
        protected final double offset;

        /**
         * Returns the value of the provided reference plus the given offset.
         */
        public Relative(GeoRef relTo, double offset) {
            this.setAxis(relTo.parameter.axis);
            this.relTo = relTo;
            this.offset = offset;
        }

        @Override
        protected double getImpl() {
            return relTo.get() + getOffset();
        }

        public double getOffset() {
            return offset;
        }
    }

    public static class RelativeDynamic extends ConstraintImpl<RelativeDynamic> {
        protected final GeoRef relTo;
        protected final Supplier<Double> offset;

        /**
         * Returns the value of the provided reference plus the given dynamic offset.
         */
        public RelativeDynamic(GeoRef relTo, Supplier<Double> offset) {
            this.setAxis(relTo.parameter.axis);
            this.relTo = relTo;
            this.offset = offset;
        }

        @Override
        protected double getImpl() {
            return relTo.get() + getOffset();
        }

        public double getOffset() {
            return offset.get();
        }
    }

    public static class Between extends ConstraintImpl<Between> {
        protected final GeoRef start;
        protected final GeoRef end;
        protected final double pos;
        protected boolean clamp = false;

        public Between(GeoRef start, GeoRef end, double pos) {
            this.setAxis(start.parameter.axis);
            if (start.parameter.axis != end.parameter.axis) {
                throw new IllegalStateException("Attempted to define a 'Between' Constraint with parameters on different axes.");
            }
            this.start = start;
            this.end = end;
            this.pos = pos;
        }

        @Override
        protected double getImpl() {
            return start.get() + ((end.get() - start.get()) * getPos());
        }

        public double getPos() {
            return clamp ? Mth.clamp(pos, 0, 1) : pos;
        }

        public double getStart() {
            return start.get();
        }

        public double getEnd() {
            return end.get();
        }

        /**
         * Ensure the output can not go bellow the min reference or above the max reference.
         */
        public Between clamp() {
            this.clamp = true;
            return this;
        }
    }

    public static class BetweenDynamic extends ConstraintImpl<BetweenDynamic> {
        protected final GeoRef start;
        protected final GeoRef end;
        protected final Supplier<Double> pos;
        protected boolean clamp = false;

        public BetweenDynamic(GeoRef start, GeoRef end, Supplier<Double> pos) {
            this.setAxis(start.parameter.axis);
            if (start.parameter.axis != end.parameter.axis) {
                throw new IllegalStateException("Attempted to define a 'Between' Constraint with parameters on different axes.");
            }
            this.start = start;
            this.end = end;
            this.pos = pos;
        }

        @Override
        protected double getImpl() {
            return start.get() + ((end.get() - start.get()) * getPos());
        }

        public double getPos() {
            return clamp ? Mth.clamp(pos.get(), 0, 1) : pos.get();
        }

        public double getStart() {
            return start.get();
        }

        public double getEnd() {
            return end.get();
        }

        /**
         * Ensure the output can not go bellow the min reference or above the max reference.
         */
        public BetweenDynamic clamp() {
            this.clamp = true;
            return this;
        }
    }

    public static class MidPoint extends ConstraintImpl<MidPoint> {
        protected final GeoRef start;
        protected final GeoRef end;
        protected final double offset;

        public MidPoint(GeoRef start, GeoRef end, double offset) {
            this.setAxis(start.parameter.axis);
            if (start.parameter.axis != end.parameter.axis) {
                throw new IllegalStateException("Attempted to define a 'MidPoint' Constraint with parameters on different axes.");
            }
            this.start = start;
            this.end = end;
            this.offset = offset;
        }

        @Override
        protected double getImpl() {
            return start.get() + ((end.get() - start.get()) / 2) + getOffset();
        }

        public double getOffset() {
            return offset;
        }

        public double getStart() {
            return start.get();
        }

        public double getEnd() {
            return end.get();
        }
    }

    public static class MidPointDynamic extends ConstraintImpl<MidPointDynamic> {
        protected final GeoRef start;
        protected final GeoRef end;
        protected final Supplier<Double> offset;

        public MidPointDynamic(GeoRef start, GeoRef end, Supplier<Double> offset) {
            this.setAxis(start.parameter.axis);
            if (start.parameter.axis != end.parameter.axis) {
                throw new IllegalStateException("Attempted to define a 'MidPoint' Constraint with parameters on different axes.");
            }
            this.start = start;
            this.end = end;
            this.offset = offset;
        }

        @Override
        protected double getImpl() {
            return start.get() + ((end.get() - start.get()) / 2) + getOffset();
        }

        public double getOffset() {
            return offset.get();
        }

        public double getStart() {
            return start.get();
        }

        public double getEnd() {
            return end.get();
        }
    }
}
