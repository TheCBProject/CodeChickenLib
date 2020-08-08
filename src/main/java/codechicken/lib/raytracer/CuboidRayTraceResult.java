package codechicken.lib.raytracer;

import codechicken.lib.vec.Vector3;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Created by covers1624 on 8/9/2016.
 * This class is kind of special as if you return this in Block.collisionRayTrace, when the blocks HitBox is rendered it will cancel and use the cuboid provided here.
 * This is to get around the fact that it is currently impossible to determine from Block.getSelectedBoundingBox what sub box to render as you have absolutely no player context to do a trace.
 */
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
    public DistanceRayTraceResult withFace(Direction newFace) {
        return new CuboidRayTraceResult(getType() == Type.MISS, getHitVec(), newFace, getPos(), isInside(), cuboid6, dist);
    }

    public DistanceRayTraceResult getAsDistanceResult() {
        return new DistanceRayTraceResult(getType() == Type.MISS, getHitVec(), getFace(), getPos(), isInside(), hitInfo, dist);
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", cuboid=" + cuboid6.toString() + "}";
    }
}
