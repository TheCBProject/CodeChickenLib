package codechicken.lib.render.particle;

import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlock;

/**
 * Trait like interface for adding CustomParticle support to any block, Simply implement :D
 *
 * Created by covers1624 on 31/10/19.
 */
public interface ICustomParticleBlock extends IForgeBlock {

    @Override
    default boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        CustomParticleHandler.handleLandingEffects(worldserver, pos, entity, numberOfParticles);
        return true;
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    default boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        return world.isRemote && CustomParticleHandler.handleRunningEffects(world, pos, state, entity);
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    default boolean addHitEffects(BlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return CustomParticleHandler.handleHitEffects(state, worldObj, target, manager);
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    default boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return CustomParticleHandler.handleDestroyEffects(world, pos, state, manager);
    }
}
