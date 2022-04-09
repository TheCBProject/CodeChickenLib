package codechicken.lib.vec;

import codechicken.lib.util.Copyable;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract supertype for any VectorN transformation
 *
 * @param <Vector>         The vector type
 * @param <Transformation> The transformation type
 */
public abstract class ITransformation<Vector, Transformation extends ITransformation<Vector, Transformation>> implements Copyable<Transformation> {

    /**
     * Applies this transformation to vec
     */
    public abstract void apply(Vector vec);

    /**
     * @param point The point to apply this transformation around
     * @return Wraps this transformation in a translation to point and then back from point
     */
    public abstract Transformation at(Vector point);

    /**
     * Creates a TransformationList composed of this transformation followed by t
     * If this is a TransformationList, the transformation will be appended and this returned
     */
    public abstract Transformation with(Transformation t);

    /**
     * Returns a simplified transformation that performs this, followed by next. If such a transformation does not exist, returns null
     */
    @Nullable
    public Transformation merge(Transformation next) {
        return null;
    }

    /**
     * Returns true if this transformation is redundant, eg. Scale(1, 1, 1), Translation(0, 0, 0) or Rotation(0, a, b, c)
     */
    public boolean isRedundant() {
        return false;
    }

    /**
     * Attempts to invert the Transformation.
     * <p>
     * The transformations inverse may be itself, or the transform
     * may not have an inverse. In that case a {@link IrreversibleTransformationException} is thrown.
     *
     * @return The inverse transform.
     * @throws IrreversibleTransformationException If the transform does not have an inverse.
     */
    public abstract Transformation inverse() throws IrreversibleTransformationException;

    /**
     * Scala ++ operator
     */
    public Transformation $plus$plus(Transformation t) {
        return with(t);
    }
}
