package codechicken.lib.render.particle;

import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.VoxelShapeBlockHitResult;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static codechicken.lib.internal.network.CCLNetwork.C_ADD_LANDING_EFFECTS;

/**
 * Created by covers1624 on 21/11/2016.
 */
public class CustomParticleHandler {

    /**
     * Call from {@link Block#addLandingEffects}
     * Provided the model bound is an instance of IModelParticleProvider, you will have landing particles just handled for you.
     *
     * @param world        The world.
     * @param pos          The position of the block.
     * @param entity       The entity.
     * @param numParticles The number of particles to spawn.
     * @return Always true for this, basically just return the result of this method inside {@link Block#addLandingEffects}
     */
    public static boolean handleLandingEffects(ServerLevel world, BlockPos pos, LivingEntity entity, int numParticles) {
        PacketCustom packet = new PacketCustom(CCLNetwork.NET_CHANNEL, C_ADD_LANDING_EFFECTS);
        packet.writePos(pos);
        packet.writeVector(Vector3.fromEntity(entity));
        packet.writeVarInt(numParticles);
        packet.sendToPlayer((ServerPlayer) entity);
        return true;
    }

    @OnlyIn (Dist.CLIENT)
    public static boolean handleRunningEffects(Level world, BlockPos pos, BlockState state, Entity entity) {
        //Spoof a raytrace from the feet.
        BlockHitResult traceResult = new BlockHitResult(entity.position().add(0, 1, 0), Direction.UP, pos, false);
        BlockModelShaper modelShapes = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        BakedModel model = modelShapes.getBlockModel(state);
        if (model instanceof IModelParticleProvider) {
            ModelData modelData = world.getModelDataManager().getAt(pos);
            ParticleEngine particleManager = Minecraft.getInstance().particleEngine;
            List<TextureAtlasSprite> sprites = new ArrayList<>(((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos, modelData));
            TextureAtlasSprite rolledSprite = sprites.get(world.random.nextInt(sprites.size()));
            double x = entity.getX() + (world.random.nextFloat() - 0.5D) * entity.getBbWidth();
            double y = entity.getBoundingBox().minY + 0.1D;
            double z = entity.getZ() + (world.random.nextFloat() - 0.5D) * entity.getBbWidth();
            particleManager.add(new CustomBreakingParticle((ClientLevel) world, x, y, z, -entity.getDeltaMovement().x * 4.0D, 1.5D, -entity.getDeltaMovement().z * 4.0D, rolledSprite));
            return true;
        }

        return false;
    }

    /**
     * Call from {@link Block#addLandingEffects}
     * Provided the model bound is an instance of IModelParticleProvider, you will have landing particles just handled for you.
     * Use the default PerspectiveModel implementations in CCL, Hit effects will be polled from your model based on your bounding box hit.
     *
     * @param state       The state.
     * @param world       The world.
     * @param traceResult The trace result.
     * @param manager     The ParticleManager.
     * @return True if particles were added, basically just return the result of this method inside {@link Block#addLandingEffects}
     */
    @OnlyIn (Dist.CLIENT)
    public static boolean handleHitEffects(BlockState state, Level world, HitResult traceResult, ParticleEngine manager) {
        if (traceResult instanceof BlockHitResult hit) {
            BlockPos pos = hit.getBlockPos();
            BlockModelShaper modelShapes = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
            BakedModel model = modelShapes.getBlockModel(state);
            if (model instanceof IModelParticleProvider) {
                ModelData modelData = world.getModelDataManager().getAt(pos);
                Cuboid6 bounds = new Cuboid6();
                if (hit instanceof VoxelShapeBlockHitResult) {
                    bounds.set(((VoxelShapeBlockHitResult) hit).shape.bounds());
                } else {
                    bounds.set(state.getShape(world, pos).bounds());
                }
                bounds = bounds.copy().add(pos);
                Set<TextureAtlasSprite> hitSprites = ((IModelParticleProvider) model).getHitEffects(hit, state, world, pos, modelData);
                List<TextureAtlasSprite> sprites = new ArrayList<>(hitSprites);
                addBlockHitEffects(world, bounds, hit.getDirection(), sprites.get(world.random.nextInt(sprites.size())), manager);
                return true;
            }
        }
        return false;
    }

    /**
     * {@link Block#addHitEffects}
     * Provided the model bound is an instance of IModelParticleProvider, you will have landing particles just handled for you.
     * Use the default PerspectiveModel implementations inside CCL, Destroy effects will just be handled for you.
     *
     * @param world   The world.
     * @param pos     The position of the block.
     * @param manager The ParticleManager.
     * @return True if particles were added, basically just return the result of this method inside {@link Block#addHitEffects}
     */
    @OnlyIn (Dist.CLIENT)
    public static boolean handleDestroyEffects(Level world, BlockPos pos, BlockState state, ParticleEngine manager) {
        BlockModelShaper modelShapes = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        BakedModel model = modelShapes.getBlockModel(state);
        if (model instanceof IModelParticleProvider) {
            ModelData modelData = world.getModelDataManager().getAt(pos);
            Cuboid6 bounds = new Cuboid6(state.getShape(world, pos).bounds());
            addBlockDestroyEffects(world, bounds.add(pos), new ArrayList<>(((IModelParticleProvider) model).getDestroyEffects(state, world, pos, modelData)), manager);
            return true;
        }
        return false;
    }

    @OnlyIn (Dist.CLIENT)
    public static void addLandingEffects(Level world, BlockPos pos, BlockState state, Vector3 entityPos, int numParticles) {
        //Spoof a raytrace from the feet.
        BlockHitResult traceResult = new BlockHitResult(new Vec3(entityPos.x, pos.getY() + 1, entityPos.z), Direction.UP, pos, false);
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        Random randy = new Random();
        BlockModelShaper modelShapes = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        BakedModel model = modelShapes.getBlockModel(state);
        if (model instanceof IModelParticleProvider) {
            ModelData modelData = world.getModelDataManager().getAt(pos);
            List<TextureAtlasSprite> sprites = new ArrayList<>(((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos, modelData));

            double speed = 0.15000000596046448D;
            if (numParticles != 0) {
                for (int i = 0; i < numParticles; i++) {
                    double mX = randy.nextGaussian() * speed;
                    double mY = randy.nextGaussian() * speed;
                    double mZ = randy.nextGaussian() * speed;
                    manager.add(CustomBreakingParticle.newLandingParticle((ClientLevel) world, entityPos.x, entityPos.y, entityPos.z, mX, mY, mZ, sprites.get(randy.nextInt(sprites.size()))));
                }
            }
        }
    }

    @OnlyIn (Dist.CLIENT)
    public static void addBlockHitEffects(Level world, Cuboid6 bounds, Direction side, TextureAtlasSprite icon, ParticleEngine particleManager) {
        float border = 0.1F;
        Vector3 diff = bounds.max.copy().subtract(bounds.min).add(-2 * border);
        diff.x *= world.random.nextDouble();
        diff.y *= world.random.nextDouble();
        diff.z *= world.random.nextDouble();
        Vector3 pos = diff.add(bounds.min).add(border);

        if (side == Direction.DOWN) {
            diff.y = bounds.min.y - border;
        }
        if (side == Direction.UP) {
            diff.y = bounds.max.y + border;
        }
        if (side == Direction.NORTH) {
            diff.z = bounds.min.z - border;
        }
        if (side == Direction.SOUTH) {
            diff.z = bounds.max.z + border;
        }
        if (side == Direction.WEST) {
            diff.x = bounds.min.x - border;
        }
        if (side == Direction.EAST) {
            diff.x = bounds.max.x + border;
        }

        particleManager.add(new CustomBreakingParticle((ClientLevel) world, pos.x, pos.y, pos.z, 0, 0, 0, icon).setPower(0.2F).scale(0.6F));
    }

    @OnlyIn (Dist.CLIENT)
    public static void addBlockDestroyEffects(Level world, Cuboid6 bounds, List<TextureAtlasSprite> icons, ParticleEngine particleManager) {
        Vector3 diff = bounds.max.copy().subtract(bounds.min);
        Vector3 center = bounds.min.copy().add(bounds.max).multiply(0.5);
        Vector3 density = diff.copy().multiply(4).ceil();

        for (int i = 0; i < density.x; ++i) {
            for (int j = 0; j < density.y; ++j) {
                for (int k = 0; k < density.z; ++k) {
                    double x = bounds.min.x + (i + 0.5) * diff.x / density.x;
                    double y = bounds.min.y + (j + 0.5) * diff.y / density.y;
                    double z = bounds.min.z + (k + 0.5) * diff.z / density.z;
                    particleManager.add(new CustomBreakingParticle((ClientLevel) world, x, y, z, x - center.x, y - center.y, z - center.z, icons.get(world.random.nextInt(icons.size()))));
                }
            }
        }
    }
}
