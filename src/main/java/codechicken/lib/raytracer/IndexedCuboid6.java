package codechicken.lib.raytracer;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class IndexedCuboid6 extends Cuboid6 {

    public Object data;

    public IndexedCuboid6(Object data, Cuboid6 cuboid) {
        super(cuboid);
        this.data = data;
    }

    public IndexedCuboid6(Object data, AABB box) {
        super(box);
        this.data = data;
    }

    @Deprecated
    public CuboidRayTraceResult calculateIntercept(Vector3 start, Vector3 end) {
        Vector3 hit = null;
        Direction sideHit = null;
        double dist = Double.MAX_VALUE;

        for (Direction face : Direction.BY_3D_DATA) {
            Vector3 suspectHit = switch (face) {
                case DOWN -> start.copy().XZintercept(end, min.y);
                case UP -> start.copy().XZintercept(end, max.y);
                case NORTH -> start.copy().XYintercept(end, min.z);
                case SOUTH -> start.copy().XYintercept(end, max.z);
                case WEST -> start.copy().YZintercept(end, min.x);
                case EAST -> start.copy().YZintercept(end, max.x);
            };

            if (suspectHit == null) {
                continue;
            }

            switch (face) {

                case DOWN:
                case UP:
                    if (!MathHelper.between(min.x, suspectHit.x, max.x) || !MathHelper.between(min.z, suspectHit.z, max.z)) {
                        continue;
                    }
                    break;
                case NORTH:
                case SOUTH:
                    if (!MathHelper.between(min.x, suspectHit.x, max.x) || !MathHelper.between(min.y, suspectHit.y, max.y)) {
                        continue;
                    }
                    break;
                case WEST:
                case EAST:
                    if (!MathHelper.between(min.y, suspectHit.y, max.y) || !MathHelper.between(min.z, suspectHit.z, max.z)) {
                        continue;
                    }
                    break;
            }
            double suspectDist = suspectHit.copy().subtract(start).magSquared();
            if (suspectDist < dist) {
                sideHit = face;
                dist = suspectDist;
                hit = suspectHit;
            }
        }

        if (sideHit != null && hit != null) {
            return new CuboidRayTraceResult(hit, sideHit, false, this, dist);
        }
        return null;
    }

    @Override
    public IndexedCuboid6 copy() {
        return new IndexedCuboid6(data, this);
    }
}
