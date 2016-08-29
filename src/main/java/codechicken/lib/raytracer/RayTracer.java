package codechicken.lib.raytracer;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class RayTracer {
    @Deprecated
    private Vector3 vec = new Vector3();
    @Deprecated
    private Vector3 vec2 = new Vector3();
    @Deprecated
    private Vector3 s_vec = new Vector3();
    @Deprecated
    private double s_dist;
    @Deprecated
    private int s_side;
    @Deprecated
    private IndexedCuboid6 c_cuboid;
    @Deprecated
    private static ThreadLocal<RayTracer> t_inst = new ThreadLocal<RayTracer>();

    @Deprecated
    public RayTracer() {
    }

    @Deprecated
    public static RayTracer instance() {
        RayTracer inst = t_inst.get();
        if (inst == null) {
            t_inst.set(inst = new RayTracer());
        }
        return inst;
    }

    @Deprecated
    private void traceSide(int side, Vector3 start, Vector3 end, Cuboid6 cuboid) {
        vec.set(start);
        Vector3 hit = null;
        switch (side) {
        case 0:
            hit = vec.XZintercept(end, cuboid.min.y);
            break;
        case 1:
            hit = vec.XZintercept(end, cuboid.max.y);
            break;
        case 2:
            hit = vec.XYintercept(end, cuboid.min.z);
            break;
        case 3:
            hit = vec.XYintercept(end, cuboid.max.z);
            break;
        case 4:
            hit = vec.YZintercept(end, cuboid.min.x);
            break;
        case 5:
            hit = vec.YZintercept(end, cuboid.max.x);
            break;
        }
        if (hit == null) {
            return;
        }

        switch (side) {
        case 0:
        case 1:
            if (!MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) {
                return;
            }
            break;
        case 2:
        case 3:
            if (!MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y)) {
                return;
            }
            break;
        case 4:
        case 5:
            if (!MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y) || !MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) {
                return;
            }
            break;
        }

        double dist = vec2.set(hit).subtract(start).magSquared();
        if (dist < s_dist) {
            s_side = side;
            s_dist = dist;
            s_vec.set(vec);
        }
    }

    @Deprecated
    private boolean rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid) {
        s_dist = Double.MAX_VALUE;
        s_side = -1;

        for (int i = 0; i < 6; i++) {
            traceSide(i, start, end, cuboid);
        }

        return s_side >= 0;
    }

    @Deprecated
    public IndexedCuboid6 rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids) {
        double c_dist = Double.MAX_VALUE;
        int c_side = 0;
        Vector3 c_vec = Vector3.zero;
        IndexedCuboid6 c_hit = null;

        for (IndexedCuboid6 cuboid : cuboids) {
            if (rayTraceCuboid(start, end, cuboid) && s_dist < c_dist) {
                c_dist = s_dist;
                c_side = s_side;
                c_vec = s_vec;
                c_hit = cuboid;
            }
        }

        if (c_hit != null) {
            s_dist = c_dist;
            s_side = c_side;
            s_vec = c_vec;
        }

        return c_hit;
    }

    @Deprecated
    public DistanceRayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, BlockCoord pos, Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new DistanceRayTraceResult(s_vec, s_side, pos, data, s_dist) : null;
    }

    @Deprecated
    public DistanceRayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, Entity entity, Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new DistanceRayTraceResult(entity, s_vec, data, s_dist) : null;
    }

    @Deprecated
    public DistanceRayTraceResult rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids, BlockCoord pos) {
        IndexedCuboid6 hit = rayTraceCuboids(start, end, cuboids);
        return hit != null ? new DistanceRayTraceResult(s_vec, s_side, pos, hit.data, s_dist) : null;
    }

    @Deprecated
    public DistanceRayTraceResult rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids, Entity entity) {
        IndexedCuboid6 hit = rayTraceCuboids(start, end, cuboids);
        return hit != null ? new DistanceRayTraceResult(entity, s_vec, hit.data, s_dist) : null;
    }

    /**
     * @param start   The vector to start RayTracing from.
     * @param end     The Vector to stop RayTracing at.
     * @param cuboids The cuboids to check for a hit.
     * @param pos     The position offset for the start and end vector.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vec3d start, Vec3d end, List<IndexedCuboid6> cuboids, BlockPos pos) {
        return rayTraceCuboidsClosest(new Vector3(start), new Vector3(end), cuboids, pos);
    }

    /**
     * @param start   The vector to start RayTracing from.
     * @param end     The Vector to stop RayTracing at.
     * @param cuboids The cuboids to check for a hit.
     * @param pos     The position offset for the start and end vector.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids, BlockPos pos) {
        List<CuboidRayTraceResult> results = new ArrayList<CuboidRayTraceResult>();
        for (IndexedCuboid6 cuboid6 : cuboids) {
            CuboidRayTraceResult hit = rayTrace(pos, start, end, cuboid6);
            results.add(hit);
        }
        CuboidRayTraceResult closestHit = null;
        double curClosest = Double.MAX_VALUE;
        for (CuboidRayTraceResult hit : results) {
            if (hit != null) {
                if (curClosest > hit.dist) {
                    closestHit = hit;
                    curClosest = hit.dist;
                }
            }
        }
        return closestHit;
    }

    /**
     * Ray traces from start to end, if the ray intercepts the cuboid it returns a new CuboidRayTraceResult.
     *
     * @param pos    The BlockPosition to subtract from the start and end vector.
     * @param start  The vector to start RayTracing from.
     * @param end    The vector to end RayTracing at.
     * @param cuboid The cuboid to check for an intercept.
     * @return A new CuboidRayTraceResult if successful, null if fail.
     */
    public static CuboidRayTraceResult rayTrace(BlockPos pos, Vector3 start, Vector3 end, IndexedCuboid6 cuboid) {
        Vector3 startRay = start.copy().sub(pos);
        Vector3 endRay = end.copy().sub(pos);
        RayTraceResult bbResult = cuboid.aabb().calculateIntercept(startRay.vec3(), endRay.vec3());

        if (bbResult != null) {
            Vector3 hitVec = new Vector3(bbResult.hitVec).add(pos);
            EnumFacing sideHit = bbResult.sideHit;
            double dist = hitVec.copy().sub(start).magSquared();
            return new CuboidRayTraceResult(hitVec, pos, sideHit, cuboid, dist);
        }
        return null;
    }

    public static RayTraceResult retraceBlock(World world, EntityPlayer player, BlockPos pos) {
        Vec3d startVec = getStartVec(player);
        Vec3d endVec = getEndVec(player);
        return world.getBlockState(pos).collisionRayTrace(world, pos, startVec, endVec);
    }

    private static double getBlockReachDistance_server(EntityPlayerMP player) {
        return player.interactionManager.getBlockReachDistance();
    }

    @SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }

    public static RayTraceResult retrace(EntityPlayer player) {
        return retrace(player, getBlockReachDistance(player));
    }

    public static RayTraceResult retrace(EntityPlayer player, double reach) {
        Vec3d startVec = getStartVec(player);
        Vec3d endVec = getEndVec(player);
        return player.worldObj.rayTraceBlocks(startVec, endVec, true, false, true);
    }

    public static Vec3d getCorrectedHeadVec(EntityPlayer player) {
        Vector3 v = Vector3.fromEntity(player).add(0, player.getEyeHeight(), 0);
        return v.vec3();
    }

    public static Vec3d getStartVec(EntityPlayer player) {
        return getCorrectedHeadVec(player);
    }

    public static double getBlockReachDistance(EntityPlayer player) {
        return player.worldObj.isRemote ? getBlockReachDistance_client() : player instanceof EntityPlayerMP ? getBlockReachDistance_server((EntityPlayerMP) player) : 5D;
    }

    public static Vec3d getEndVec(EntityPlayer player) {
        Vec3d headVec = getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0F);
        double reach = getBlockReachDistance(player);
        return headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
    }
}
