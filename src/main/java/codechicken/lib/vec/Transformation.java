package codechicken.lib.vec;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Abstract supertype for any 3D vector transformation
 */
public abstract class Transformation extends ITransformation<Vector3, Transformation> implements IVertexOperation {

    public static final int operationIndex = IVertexOperation.registerOperation();

    /**
     * Applies this transformation to a normal (doesn't translate)
     *
     * @param normal The normal to transform
     */
    public abstract void applyN(Vector3 normal);

    /**
     * Applies this transformation to a matrix as a multiplication on the right hand side.
     *
     * @param mat The matrix to combine this transformation with
     */
    public abstract void apply(Matrix4 mat);

    public Transformation at(Vector3 point) {
        return new TransformationList(new Translation(-point.x, -point.y, -point.z), this, point.translation());
    }

    public TransformationList with(Transformation t) {
        return new TransformationList(this, t);
    }

    @OnlyIn (Dist.CLIENT)
    public abstract void glApply();

    @Override
    public boolean load(CCRenderState ccrs) {
        ccrs.pipeline.addRequirement(ccrs.normalAttrib.operationID());
        return !isRedundant();
    }

    @Override
    public void operate(CCRenderState ccrs) {
        apply(ccrs.vert.vec);
        if (ccrs.normalAttrib.active) {
            applyN(ccrs.normal);
        }
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}
