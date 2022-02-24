package codechicken.lib.raytracer;

import codechicken.lib.vec.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

//TODO Copyable.
public class DistanceRayTraceResult extends BlockHitResult implements Comparable<DistanceRayTraceResult> {

    /**
     * The square distance from the start of the raytrace.
     */
    public double dist;
    public Object hitInfo;
    public int subHit;

    public DistanceRayTraceResult(Vector3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {
        this(false, hitVec.vec3(), faceIn, posIn, isInside, data, dist);
    }

    public DistanceRayTraceResult(Vec3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {
        this(false, hitVec, faceIn, posIn, isInside, data, dist);
    }

    protected DistanceRayTraceResult(boolean isMissIn, Vec3 hitVec, Direction faceIn, BlockPos posIn, boolean isInside, Object data, double dist) {
        super(isMissIn, hitVec, faceIn, posIn, isInside);
        setData(data);
        this.dist = dist;
    }

    public void setData(Object data) {
        if (data instanceof Integer) {
            subHit = (Integer) data;
        }
        hitInfo = data;
    }

    @Override
    public DistanceRayTraceResult withDirection(Direction newFace) {
        return new DistanceRayTraceResult(getType() == Type.MISS, getLocation(), getDirection(), getBlockPos(), isInside(), hitInfo, dist);
    }

    @Override
    public int compareTo(DistanceRayTraceResult o) {
        return Double.compare(dist, o.dist);
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", subHit=" + subHit + ", sqDist: " + dist + "}";
    }
}
