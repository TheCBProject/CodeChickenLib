package codechicken.lib.raytracer;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * A VoxelShape implementation, produces a {@link VoxelShapeBlockHitResult} when ray traced.
 * {@link IndexedVoxelShape#data} will be passed through to {@link VoxelShapeBlockHitResult#hitInfo} and
 * to {@link VoxelShapeBlockHitResult#subHit} if it's an integer.
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
    public VoxelShapeBlockHitResult clip(Vec3 start, Vec3 end, BlockPos pos) {
        BlockHitResult result = parent.clip(start, end, pos);
        if (result == null) return null;
        double dist = result.getLocation().distanceToSqr(start);
        return new VoxelShapeBlockHitResult(result, this, dist);
    }

    public Object getData() {
        return data;
    }
}
