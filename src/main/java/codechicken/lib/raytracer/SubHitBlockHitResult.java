package codechicken.lib.raytracer;

import codechicken.lib.vec.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SubHitBlockHitResult extends BlockHitResult implements Comparable<SubHitBlockHitResult> {

    /**
     * The square distance from the start of the raytrace.
     */
    public final double dist;
    public final Object hitInfo;
    public final int subHit;

    public SubHitBlockHitResult(Vector3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {
        this(false, hitVec.vec3(), faceIn, posIn, isInside, data, dist);
    }

    public SubHitBlockHitResult(Vec3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {
        this(false, hitVec, faceIn, posIn, isInside, data, dist);
    }

    protected SubHitBlockHitResult(boolean isMissIn, Vec3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {
        super(isMissIn, hitVec, faceIn, posIn, isInside);
        if (data instanceof Integer d) {
            subHit = d;
        } else {
            subHit = -1;
        }
        hitInfo = data;
        this.dist = dist;
    }

    @Override
    public SubHitBlockHitResult withDirection(Direction newFace) {
        return new SubHitBlockHitResult(getType() == Type.MISS, getLocation(), getDirection(), getBlockPos(), isInside(), hitInfo, dist);
    }

    @Override
    public int compareTo(SubHitBlockHitResult o) {
        return Double.compare(dist, o.dist);
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", subHit=" + subHit + ", sqDist: " + dist + "}";
    }
}
