package codechicken.lib.gui.modular.lib.geometry;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Constraints are used to define an elements position and shape by constraining the elements geometry parameters. {@link GeoParam}
 * Constraints can be as simple as literal values representing an elements exact position and / or size in screen space,
 * They can be relative coordinates, (Relative to another element's Geometry)
 * Or they can be used to supply completely custom dynamic values.
 * <p>
 * All the built-in constraints are implemented and documented in this class.
 * <p>
 * Created by brandon3055 on 30/06/2023
 *
 * @see ConstrainedGeometry
 */
public sealed interface Constraint permits ConstraintImpl {

    /**
     * This method will return the current value of this constraint.
     * This could be a fixed stored value, or a dynamically computed value
     * depending on the type of constraint.
     * <p>
     * All position and size values in ModularGui are doubles.
     * However, by default, all the builtin constraints will cast their outputs to integer values.
     * This avoids a lot of random visual artifacts that can occur when using floating point values in MC Screens.
     * <p>
     * If you need floating-point precision, it can be enabled calling .precise() on any of the builtin constraints.
     *
     * @return The computed or stored value of this constraint.
     */
    double get();

    /**
     * @return the axis this constraint applies to, Ether X, Y or null for undefined.
     */
    @Nullable
    Axis axis();

    /**
     * This is part of a late addition to improve performance.
     * Rather than computing a constraint value every single time it is queried, which can be many, many times per render frame,
     * We now cache the constraint value, and that cache is cleared at the start of each render frame.
     */
    void markDirty();

    /**
     * This is the most basic constraint. It constrains a parameter to a single fixed value.
     *
     * @param value The fixed value that will be returned by this constraint.
     */
    static ConstraintImpl.Literal literal(double value) {
        return new ConstraintImpl.Literal(value);
    }

    /**
     * Constrains a parameter to the value provided by the given supplier.
     *
     * @param valueSupplier The dynamic value supplier.
     */
    static ConstraintImpl.Dynamic dynamic(Supplier<Double> valueSupplier) {
        return new ConstraintImpl.Dynamic(valueSupplier);
    }

    /**
     * Contains a parameter to the exact value of the given reference.
     * This is effectively a relative constraint with no offset.
     *
     * @param relativeTo The relative geometry.
     */
    static ConstraintImpl.Relative match(GeoRef relativeTo) {
        return new ConstraintImpl.Relative(relativeTo, 0);
    }

    /**
     * Contains a parameter to the given reference plus the provided fixed offset.
     *
     * @param relativeTo The relative geometry.
     * @param offset     The offset to apply.
     */
    static ConstraintImpl.Relative relative(GeoRef relativeTo, double offset) {
        return new ConstraintImpl.Relative(relativeTo, offset);
    }

    /**
     * Contains a parameter to the given reference plus the provided dynamic offset.
     *
     * @param relativeTo The relative geometry.
     * @param offset     The dynamic offset to apply.
     */
    static ConstraintImpl.RelativeDynamic relative(GeoRef relativeTo, Supplier<Double> offset) {
        return new ConstraintImpl.RelativeDynamic(relativeTo, offset);
    }

    /**
     * Contains a parameter to a fixed position between the two provided references.
     * Note: it is possible to go outside the given range if the given position is greater than 1 or less than 0.
     * To prevent this call .clamp() on the returned constraint.
     *
     * @param start    The Start position.
     * @param end      The End position.
     * @param position The position between start and end. (0=start to 1=end)
     */
    static ConstraintImpl.Between between(GeoRef start, GeoRef end, double position) {
        return new ConstraintImpl.Between(start, end, position);
    }

    /**
     * Contains a parameter to a fixed position between the two provided references.
     * Note: it is possible to go outside the given range if the given position is greater than 1 or less than 0.
     * To prevent this call .clamp() on the returned constraint.
     * <p>
     * This variant also allows a pixel offset.
     *
     * @param start    The Start position.
     * @param end      The End position.
     * @param position The position between start and end. (0=start to 1=end)
     * @param offset   position offset in pixels
     */
    static ConstraintImpl.BetweenOffset between(GeoRef start, GeoRef end, double position, double offset) {
        return new ConstraintImpl.BetweenOffset(start, end, position, offset);
    }

    /**
     * Contains a parameter to a dynamic position between the two provided references.
     * Note: it is possible to go outside the given range if the given position is greater than 1 or less than 0.
     * To prevent this call .clamp() on the returned constraint.
     *
     * @param start    The Start position.
     * @param end      The End position.
     * @param position The dynamic position between start and end. (0=start to 1=end)
     */
    static ConstraintImpl.BetweenDynamic between(GeoRef start, GeoRef end, Supplier<Double> position) {
        return new ConstraintImpl.BetweenDynamic(start, end, position);
    }

    /**
     * Contains a parameter to a dynamic position between the two provided references.
     * Note: it is possible to go outside the given range if the given position is greater than 1 or less than 0.
     * To prevent this call .clamp() on the returned constraint.
     * <p>
     * This variant also allows a pixel offset.
     *
     * @param start    The Start position.
     * @param end      The End position.
     * @param position The dynamic position between start and end. (0=start to 1=end)
     * @param offset   Dynamic position offset in pixels
     */
    static ConstraintImpl.BetweenDynamic between(GeoRef start, GeoRef end, Supplier<Double> position, Supplier<Double> offset) {
        return new ConstraintImpl.BetweenOffsetDynamic(start, end, position, offset);
    }

    /**
     * Contains a parameter to the mid-point between the two provided references.
     *
     * @param start The Start position.
     * @param end   The End position.
     */
    static ConstraintImpl.MidPoint midPoint(GeoRef start, GeoRef end) {
        return new ConstraintImpl.MidPoint(start, end, 0);
    }

    /**
     * Contains a parameter to the mid-point between the two provided references with a fixed offset.
     *
     * @param start  The Start position.
     * @param end    The End position.
     * @param offset offset distance.
     */
    static ConstraintImpl.MidPoint midPoint(GeoRef start, GeoRef end, double offset) {
        return new ConstraintImpl.MidPoint(start, end, offset);
    }

    /**
     * Contains a parameter to the mid-point between the two provided references with a dynamic offset.
     *
     * @param start  The Start position.
     * @param end    The End position.
     * @param offset offset distance suppler.
     */
    static ConstraintImpl.MidPointDynamic midPoint(GeoRef start, GeoRef end, Supplier<Double> offset) {
        return new ConstraintImpl.MidPointDynamic(start, end, offset);
    }
}
