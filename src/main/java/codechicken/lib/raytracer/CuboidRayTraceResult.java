package codechicken.lib.raytracer;

import codechicken.lib.vec.Vector3;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Created by covers1624 on 8/9/2016.
 */
@Deprecated
public class CuboidRayTraceResult extends DistanceRayTraceResult {

    public IndexedCuboid6 cuboid6;

    public CuboidRayTraceResult(Vector3 hit, Direction side, BlockPos pos, boolean isInside, IndexedCuboid6 cuboid, double dist) {
        super(hit, side, pos, isInside, cuboid.data, dist);
        this.cuboid6 = cuboid;
    }

    public CuboidRayTraceResult(Vector3 hit, Direction side, boolean isInside, IndexedCuboid6 cuboid, double dist) {
        super(hit, side, BlockPos.ZERO, isInside, cuboid.data, dist);
        this.cuboid6 = cuboid;
    }

    protected CuboidRayTraceResult(boolean isMissIn, Vector3d hit, Direction side, BlockPos pos, boolean isInside, IndexedCuboid6 cuboid, double dist) {
        super(isMissIn, hit, side, pos, isInside, cuboid, dist);
        this.cuboid6 = cuboid;
    }

    @Override
    public DistanceRayTraceResult withDirection(Direction newFace) {
        return new CuboidRayTraceResult(getType() == Type.MISS, getLocation(), newFace, getBlockPos(), isInside(), cuboid6, dist);
    }

    public DistanceRayTraceResult getAsDistanceResult() {
        return new DistanceRayTraceResult(getType() == Type.MISS, getLocation(), getDirection(), getBlockPos(), isInside(), hitInfo, dist);
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", cuboid=" + cuboid6.toString() + "}";
    }
}
