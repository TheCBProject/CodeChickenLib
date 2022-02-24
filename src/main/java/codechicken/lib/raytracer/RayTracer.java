package codechicken.lib.raytracer;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RayTracer {

    /**
     * @param start   The vector to start RayTracing from.
     * @param end     The Vector to stop RayTracing at.
     * @param cuboids The cuboids to check for a hit.
     * @param pos     The position offset for the start and end vector.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vec3 start, Vec3 end, BlockPos pos, List<IndexedCuboid6> cuboids) {
        return rayTraceCuboidsClosest(new Vector3(start), new Vector3(end), pos, cuboids);
    }

    /**
     * @param start   The vector to start RayTracing from.
     * @param end     The Vector to stop RayTracing at.
     * @param cuboids The cuboids to check for a hit.
     * @param pos     The position offset for the start and end vector.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3 start, Vector3 end, BlockPos pos, List<IndexedCuboid6> cuboids) {
        List<CuboidRayTraceResult> results = new ArrayList<>();
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
     * This method goes by the assumption you don't care about IndexedCuboids and their extra data.
     * Useful for adding hitboxes to blocks that don't actually do anything but visuals.
     *
     * @param start The vector to start RayTracing from.
     * @param end   The vector to stop RayTracing at.
     * @param pos   The position offset for the start and enc vector.
     * @param boxes The cuboids to trace.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3 start, Vector3 end, BlockPos pos, AABB... boxes) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (boxes != null) {
            for (AABB box : boxes) {
                cuboidList.add(new IndexedCuboid6(0, box));
            }
        }
        return rayTraceCuboidsClosest(start, end, pos, cuboidList);
    }

    /**
     * This method goes by the assumption you don't care about IndexedCuboids and their extra data.
     * Useful for adding hitboxes to blocks that don't actually do anything but visuals.
     *
     * @param start The vector to start RayTracing from.
     * @param end   The vector to stop RayTracing at.
     * @param pos   The position offset for the start and enc vector.
     * @param boxes The cuboids to trace.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vec3 start, Vec3 end, BlockPos pos, AABB... boxes) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (boxes != null) {
            for (AABB box : boxes) {
                cuboidList.add(new IndexedCuboid6(0, box));
            }
        }
        return rayTraceCuboidsClosest(start, end, pos, cuboidList);
    }

    /**
     * This method goes by the assumption you don't care about IndexedCuboids and their extra data.
     * Useful for adding hitboxes to blocks that don't actually do anything but visuals.
     *
     * @param start   The vector to start RayTracing from.
     * @param end     The vector to stop RayTracing at.
     * @param pos     The position offset for the start and enc vector.
     * @param cuboids The cuboids to trace.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3 start, Vector3 end, BlockPos pos, Cuboid6... cuboids) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (cuboids != null) {
            for (Cuboid6 cuboid : cuboids) {
                cuboidList.add(new IndexedCuboid6(0, cuboid));
            }
        }
        return rayTraceCuboidsClosest(start, end, pos, cuboidList);
    }

    /**
     * This method goes by the assumption you don't care about IndexedCuboids and their extra data.
     * Useful for adding hitboxes to blocks that don't actually do anything but visuals.
     *
     * @param start   The vector to start RayTracing from.
     * @param end     The vector to stop RayTracing at.
     * @param pos     The position offset for the start and enc vector.
     * @param cuboids The cuboids to trace.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vec3 start, Vec3 end, BlockPos pos, Cuboid6... cuboids) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (cuboids != null) {
            for (Cuboid6 cuboid : cuboids) {
                cuboidList.add(new IndexedCuboid6(0, cuboid));
            }
        }
        return rayTraceCuboidsClosest(start, end, pos, cuboidList);
    }

    /**
     * @param start   The vector to start RayTracing from.
     * @param end     The Vector to stop RayTracing at.
     * @param pos     The position offset for the start and end vector.
     * @param cuboids The cuboids to check for a hit.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3 start, Vector3 end, BlockPos pos, IndexedCuboid6... cuboids) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (cuboids != null) {
            Collections.addAll(cuboidList, cuboids);
        }
        return rayTraceCuboidsClosest(start, end, pos, cuboidList);
    }

    /**
     * @param start   The vector to start RayTracing from.
     * @param end     The Vector to stop RayTracing at.
     * @param pos     The position offset for the start and end vector.
     * @param cuboids The cuboids to check for a hit.
     * @return The closest hit to the start vector.
     */
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vec3 start, Vec3 end, BlockPos pos, IndexedCuboid6... cuboids) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (cuboids != null) {
            Collections.addAll(cuboidList, cuboids);
        }
        return rayTraceCuboidsClosest(start, end, pos, cuboidList);
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
        BlockHitResult bbResult = Shapes.create(cuboid.aabb()).clip(start.vec3(), end.vec3(), pos);

        if (bbResult != null) {
            Vector3 hitVec = new Vector3(bbResult.getLocation());
            Direction sideHit = bbResult.getDirection();
            double dist = hitVec.copy().subtract(start).magSquared();
            return new CuboidRayTraceResult(hitVec, sideHit, pos, bbResult.isInside(), cuboid, dist);
        }
        return null;
    }

    public static BlockHitResult retraceBlock(LevelReader world, Player player, BlockPos pos) {
        Vec3 startVec = getStartVec(player);
        Vec3 endVec = getEndVec(player);
        BlockState state = world.getBlockState(pos);
        VoxelShape baseShape = state.getShape(world, pos);
        BlockHitResult baseTraceResult = baseShape.clip(startVec, endVec, pos);
        if (baseTraceResult != null) {
            BlockHitResult raytraceTraceShape = state.getVisualShape(world, pos, CollisionContext.of(player)).clip(startVec, endVec, pos);
            if (raytraceTraceShape != null) {
                return raytraceTraceShape;
            }
        }
        return baseTraceResult;
    }

    private static double getBlockReachDistance_server(ServerPlayer player) {
        return player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
    }

    @OnlyIn (Dist.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getInstance().gameMode.getPickRange();
    }

    public static BlockHitResult retrace(Player player) {
        return retrace(player, ClipContext.Block.OUTLINE);
    }

    public static BlockHitResult retrace(Player player, ClipContext.Block blockMode) {
        return retrace(player, getBlockReachDistance(player), blockMode, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult retrace(Player player, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return retrace(player, getBlockReachDistance(player), blockMode, fluidMode);
    }

    public static BlockHitResult retrace(Player player, double reach, ClipContext.Block blockMode) {
        return retrace(player, reach, blockMode, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult retrace(Player player, double reach, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        Vec3 startVec = getStartVec(player);
        Vec3 endVec = getEndVec(player, reach);
        return player.level.clip(new ClipContext(startVec, endVec, blockMode, fluidMode, player));
    }

    public static Vec3 getCorrectedHeadVec(Player player) {
        Vector3 v = Vector3.fromEntity(player).add(0, player.getEyeHeight(), 0);
        return v.vec3();
    }

    public static Vec3 getStartVec(Player player) {
        return getCorrectedHeadVec(player);
    }

    @Deprecated // Use attribute directly? avoid all this nonsense.
    public static double getBlockReachDistance(Player player) {
        return player.level.isClientSide ? getBlockReachDistance_client() : player instanceof ServerPlayer ? getBlockReachDistance_server((ServerPlayer) player) : 5D;
    }

    public static Vec3 getEndVec(Player player) {
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(1.0F);
        double reach = getBlockReachDistance(player);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vec3 getEndVec(Player player, double reach) {
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(1.0F);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }
}
