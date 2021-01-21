package codechicken.lib.raytracer;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

import static codechicken.lib.util.SneakyUtils.unsafeCast;

/**
 * A VoxelShape implementation, produces a {@link VoxelShapeRayTraceResult} when ray traced.
 * Whilst similar to {@link IndexedVoxelShape}, will ray trace each sub-component provided, returning the closest.
 * <p>
 * The sub-component will have its outline automatically rendered appropriately.
 * <p>
 * Created by covers1624 on 5/12/20.
 */
public class MultiIndexedVoxelShape extends VoxelShape {

    private final VoxelShape merged;
    private final ImmutableSet<IndexedVoxelShape> shapes;

    /**
     * Construct a MultiIndexedVoxelShape, using the combination of all the sub-components
     * as this VoxelShape.
     *
     * @param shapes The sub-components.
     */
    public MultiIndexedVoxelShape(ImmutableSet<IndexedVoxelShape> shapes) {
        this(VoxelShapeCache.merge(unsafeCast(shapes)), shapes);//Generics die in a hole pls, kthx.
    }

    /**
     * Constructs a MultiIndexedVoxelShape, using the provided VoxelShape as this shape,
     * whilst still RayTracing against all the sub-components.
     *
     * @param merged The base shape.
     * @param shapes The sub-components.
     */
    public MultiIndexedVoxelShape(VoxelShape merged, ImmutableSet<IndexedVoxelShape> shapes) {
        super(merged.part);
        this.merged = merged;
        this.shapes = shapes;
    }

    @Override
    public DoubleList getValues(Direction.Axis axis) {
        return merged.getValues(axis);
    }

    @Nullable
    @Override
    public VoxelShapeRayTraceResult rayTrace(Vector3d start, Vector3d end, BlockPos pos) {
        VoxelShapeRayTraceResult closest = null;
        double dist = Double.MAX_VALUE;
        for (IndexedVoxelShape shape : shapes) {
            VoxelShapeRayTraceResult hit = shape.rayTrace(start, end, pos);
            if (hit != null && dist >= hit.dist) {
                closest = hit;
                dist = hit.dist;
            }
        }

        return closest;
    }
}
