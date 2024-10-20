package codechicken.lib.raytracer;

import codechicken.lib.vec.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RayTracer {

    @Nullable
    public static BlockHitResult retraceBlock(BlockGetter level, Player player, BlockPos pos) {
        Vec3 startVec = getStartVec(player);
        Vec3 endVec = getEndVec(player);
        BlockState state = level.getBlockState(pos);
        VoxelShape baseShape = state.getShape(level, pos);
        BlockHitResult baseTraceResult = baseShape.clip(startVec, endVec, pos);
        if (baseTraceResult != null) {
            BlockHitResult raytraceTraceShape = state.getVisualShape(level, pos, CollisionContext.of(player)).clip(startVec, endVec, pos);
            if (raytraceTraceShape != null) {
                return raytraceTraceShape;
            }
        }
        return baseTraceResult;
    }

    public static BlockHitResult retrace(Player player) {
        return retrace(player, ClipContext.Block.OUTLINE);
    }

    public static BlockHitResult retrace(Player player, ClipContext.Block blockMode) {
        return retrace(player, player.blockInteractionRange(), blockMode, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult retrace(Player player, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return retrace(player, player.blockInteractionRange(), blockMode, fluidMode);
    }

    public static BlockHitResult retrace(Player player, double reach, ClipContext.Block blockMode) {
        return retrace(player, reach, blockMode, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult retrace(Player player, double reach, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        Vec3 startVec = getStartVec(player);
        Vec3 endVec = getEndVec(player, reach);
        return player.level().clip(new ClipContext(startVec, endVec, blockMode, fluidMode, player));
    }

    public static Vec3 getCorrectedHeadVec(Player player) {
        Vector3 v = Vector3.fromEntity(player).add(0, player.getEyeHeight(), 0);
        return v.vec3();
    }

    public static Vec3 getStartVec(Player player) {
        return getCorrectedHeadVec(player);
    }

    public static Vec3 getEndVec(Player player) {
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(1.0F);
        double reach = player.blockInteractionRange();
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vec3 getEndVec(Player player, double reach) {
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(1.0F);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }
}
