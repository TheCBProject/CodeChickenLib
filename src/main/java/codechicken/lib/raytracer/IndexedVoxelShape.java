package codechicken.lib.raytracer;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

/**
 * A VoxelShape implementation, produces a {@link VoxelShapeRayTraceResult} when ray traced.
 * {@link IndexedVoxelShape#data} will be passed through to {@link RayTraceResult#hitInfo} and
 * to {@link RayTraceResult#subHit} if its an integer.
 * <p>
 * Created by covers1624 on 5/12/20.
 */
public class IndexedVoxelShape extends VoxelShape {

    private final VoxelShape parent;
    private final Object data;

    public IndexedVoxelShape(VoxelShape parent, Object data) {
        super(parent.shape);
        this.parent = parent;
        this.data = data;
    }

    @Override
    public DoubleList getCoords(Direction.Axis axis) {
        return parent.getCoords(axis);
    }

    @Nullable
    @Override
    public VoxelShapeRayTraceResult clip(Vector3d start, Vector3d end, BlockPos pos) {
        BlockRayTraceResult result = parent.clip(start, end, pos);
        if (result == null) return null;
        double dist = result.getLocation().distanceToSqr(start);
        return new VoxelShapeRayTraceResult(result, this, dist);
    }

    public Object getData() {
        return data;
    }
}
