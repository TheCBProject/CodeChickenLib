package codechicken.lib.raytracer;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
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
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3d start, Vector3d end, BlockPos pos, List<IndexedCuboid6> cuboids) {
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
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3 start, Vector3 end, BlockPos pos, AxisAlignedBB... boxes) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (boxes != null) {
            for (AxisAlignedBB box : boxes) {
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
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3d start, Vector3d end, BlockPos pos, AxisAlignedBB... boxes) {
        List<IndexedCuboid6> cuboidList = new LinkedList<>();
        if (boxes != null) {
            for (AxisAlignedBB box : boxes) {
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
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3d start, Vector3d end, BlockPos pos, Cuboid6... cuboids) {
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
    public static CuboidRayTraceResult rayTraceCuboidsClosest(Vector3d start, Vector3d end, BlockPos pos, IndexedCuboid6... cuboids) {
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
        BlockRayTraceResult bbResult = VoxelShapes.create(cuboid.aabb()).clip(start.vec3(), end.vec3(), pos);

        if (bbResult != null) {
            Vector3 hitVec = new Vector3(bbResult.getLocation());
            Direction sideHit = bbResult.getDirection();
            double dist = hitVec.copy().subtract(start).magSquared();
            return new CuboidRayTraceResult(hitVec, sideHit, pos, bbResult.isInside(), cuboid, dist);
        }
        return null;
    }

    public static BlockRayTraceResult retraceBlock(IBlockReader world, PlayerEntity player, BlockPos pos) {
        Vector3d startVec = getStartVec(player);
        Vector3d endVec = getEndVec(player);
        BlockState state = world.getBlockState(pos);
        VoxelShape baseShape = state.getShape(world, pos);
        BlockRayTraceResult baseTraceResult = baseShape.clip(startVec, endVec, pos);
        if (baseTraceResult != null) {
            BlockRayTraceResult raytraceTraceShape = state.getVisualShape(world, pos, ISelectionContext.of(player)).clip(startVec, endVec, pos);
            if (raytraceTraceShape != null) {
                return raytraceTraceShape;
            }
        }
        return baseTraceResult;
    }

    private static double getBlockReachDistance_server(ServerPlayerEntity player) {
        return player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
    }

    @OnlyIn (Dist.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getInstance().gameMode.getPickRange();
    }

    public static BlockRayTraceResult retrace(PlayerEntity player) {
        return retrace(player, RayTraceContext.BlockMode.OUTLINE);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.BlockMode blockMode) {
        return retrace(player, getBlockReachDistance(player), blockMode, RayTraceContext.FluidMode.NONE);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {
        return retrace(player, getBlockReachDistance(player), blockMode, fluidMode);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, double reach, RayTraceContext.BlockMode blockMode) {
        return retrace(player, reach, blockMode, RayTraceContext.FluidMode.NONE);
    }

    public static BlockRayTraceResult retrace(PlayerEntity player, double reach, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {
        Vector3d startVec = getStartVec(player);
        Vector3d endVec = getEndVec(player, reach);
        return player.level.clip(new RayTraceContext(startVec, endVec, blockMode, fluidMode, player));
    }

    public static Vector3d getCorrectedHeadVec(PlayerEntity player) {
        Vector3 v = Vector3.fromEntity(player).add(0, player.getEyeHeight(), 0);
        return v.vec3();
    }

    public static Vector3d getStartVec(PlayerEntity player) {
        return getCorrectedHeadVec(player);
    }

    @Deprecated // Use attribute directly? avoid all this nonsense.
    public static double getBlockReachDistance(PlayerEntity player) {
        return player.level.isClientSide ? getBlockReachDistance_client() : player instanceof ServerPlayerEntity ? getBlockReachDistance_server((ServerPlayerEntity) player) : 5D;
    }

    public static Vector3d getEndVec(PlayerEntity player) {
        Vector3d headVec = getCorrectedHeadVec(player);
        Vector3d lookVec = player.getViewVector(1.0F);
        double reach = getBlockReachDistance(player);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vector3d getEndVec(PlayerEntity player, double reach) {
        Vector3d headVec = getCorrectedHeadVec(player);
        Vector3d lookVec = player.getViewVector(1.0F);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }
}
