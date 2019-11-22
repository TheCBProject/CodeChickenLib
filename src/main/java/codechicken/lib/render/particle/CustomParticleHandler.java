package codechicken.lib.render.particle;

import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static codechicken.lib.internal.network.CCLNetwork.C_ADD_LANDING_EFFECTS;
import static codechicken.lib.internal.network.CCLNetwork.C_OPEN_CONTAINER;

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
    public static boolean handleLandingEffects(ServerWorld world, BlockPos pos, LivingEntity entity, int numParticles) {
        PacketCustom packet = new PacketCustom(CCLNetwork.NET_CHANNEL, C_ADD_LANDING_EFFECTS);
        packet.writePos(pos);
        packet.writeVector(Vector3.fromEntity(entity));
        packet.writeVarInt(numParticles);
        packet.sendToPlayer((ServerPlayerEntity) entity);
        return true;
    }

    @OnlyIn (Dist.CLIENT)
    public static boolean handleRunningEffects(World world, BlockPos pos, BlockState state, Entity entity) {
        //Spoof a raytrace from the feet.
        BlockRayTraceResult traceResult = new BlockRayTraceResult(new Vec3d(entity.posX, pos.getY() + 1, entity.posZ), Direction.UP, pos, false);
        BlockModelShapes modelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
        IBakedModel model = modelShapes.getModel(state);
        if (model instanceof IModelParticleProvider) {
            IModelData modelData = ModelDataManager.getModelData(world, pos);
            ParticleManager particleManager = Minecraft.getInstance().particles;
            List<TextureAtlasSprite> sprites = new ArrayList<>(((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos, modelData));
            TextureAtlasSprite rolledSprite = sprites.get(world.rand.nextInt(sprites.size()));
            double x = entity.posX + (world.rand.nextFloat() - 0.5D) * entity.getWidth();
            double y = entity.getBoundingBox().minY + 0.1D;
            double z = entity.posZ + (world.rand.nextFloat() - 0.5D) * entity.getWidth();
            particleManager.addEffect(new CustomBreakingParticle(world, x, y, z, -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D, rolledSprite));
            return true;
        }

        return false;
    }

    /**
     * Call from {@link Block#addLandingEffects}
     * Provided the model bound is an instance of IModelParticleProvider, you will have landing particles just handled for you.
     * Use the default PerspectiveModel implementations in CCL, Hit effects will be polled from your model based on your bounding box hit.
     *
     * @param state   The state.
     * @param world   The world.
     * @param hit     The trace result.
     * @param manager The ParticleManager.
     * @return True if particles were added, basically just return the result of this method inside {@link Block#addLandingEffects}
     */
    @OnlyIn (Dist.CLIENT)
    public static boolean handleHitEffects(BlockState state, World world, RayTraceResult traceResult, ParticleManager manager) {
        if (traceResult instanceof BlockRayTraceResult) {
            BlockRayTraceResult hit = (BlockRayTraceResult) traceResult;
            BlockPos pos = hit.getPos();
            BlockModelShapes modelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
            IBakedModel model = modelShapes.getModel(state);
            if (model instanceof IModelParticleProvider) {
                IModelData modelData = ModelDataManager.getModelData(world, pos);
                Cuboid6 bounds;
                if (hit instanceof CuboidRayTraceResult) {
                    bounds = ((CuboidRayTraceResult) hit).cuboid6;
                } else {
                    bounds = new Cuboid6(state.getShape(world, pos).getBoundingBox());
                }
                bounds = bounds.copy().add(pos);
                Set<TextureAtlasSprite> hitSprites = ((IModelParticleProvider) model).getHitEffects(hit, state, world, pos, modelData);
                List<TextureAtlasSprite> sprites = new ArrayList<>(hitSprites);
                addBlockHitEffects(world, bounds, hit.getFace(), sprites.get(world.rand.nextInt(sprites.size())), manager);
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
    public static boolean handleDestroyEffects(World world, BlockPos pos, BlockState state, ParticleManager manager) {
        BlockModelShapes modelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
        IBakedModel model = modelShapes.getModel(state);
        if (model instanceof IModelParticleProvider) {
            IModelData modelData = ModelDataManager.getModelData(world, pos);
            Cuboid6 bounds = new Cuboid6(state.getShape(world, pos).getBoundingBox());
            addBlockDestroyEffects(world, bounds.add(pos), new ArrayList<>(((IModelParticleProvider) model).getDestroyEffects(state, world, pos, modelData)), manager);
            return true;
        }
        return false;
    }

    @OnlyIn (Dist.CLIENT)
    public static void addLandingEffects(World world, BlockPos pos, BlockState state, Vector3 entityPos, int numParticles) {
        //Spoof a raytrace from the feet.
        BlockRayTraceResult traceResult = new BlockRayTraceResult(new Vec3d(entityPos.x, pos.getY() + 1, entityPos.z), Direction.UP, pos, false);
        ParticleManager manager = Minecraft.getInstance().particles;
        Random randy = new Random();
        BlockModelShapes modelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
        IBakedModel model = modelShapes.getModel(state);
        if (model instanceof IModelParticleProvider) {
            IModelData modelData = ModelDataManager.getModelData(world, pos);
            List<TextureAtlasSprite> sprites = new ArrayList<>(((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos, modelData));

            double speed = 0.15000000596046448D;
            if (numParticles != 0) {
                for (int i = 0; i < numParticles; i++) {
                    double mX = randy.nextGaussian() * speed;
                    double mY = randy.nextGaussian() * speed;
                    double mZ = randy.nextGaussian() * speed;
                    manager.addEffect(CustomBreakingParticle.newLandingParticle(world, entityPos.x, entityPos.y, entityPos.z, mX, mY, mZ, sprites.get(randy.nextInt(sprites.size()))));
                }
            }
        }
    }

    @OnlyIn (Dist.CLIENT)
    public static void addBlockHitEffects(World world, Cuboid6 bounds, Direction side, TextureAtlasSprite icon, ParticleManager particleManager) {
        float border = 0.1F;
        Vector3 diff = bounds.max.copy().subtract(bounds.min).add(-2 * border);
        diff.x *= world.rand.nextDouble();
        diff.y *= world.rand.nextDouble();
        diff.z *= world.rand.nextDouble();
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

        particleManager.addEffect(new CustomBreakingParticle(world, pos.x, pos.y, pos.z, 0, 0, 0, icon).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
    }

    @OnlyIn (Dist.CLIENT)
    public static void addBlockDestroyEffects(World world, Cuboid6 bounds, List<TextureAtlasSprite> icons, ParticleManager particleManager) {
        Vector3 diff = bounds.max.copy().subtract(bounds.min);
        Vector3 center = bounds.min.copy().add(bounds.max).multiply(0.5);
        Vector3 density = diff.copy().multiply(4).ceil();

        for (int i = 0; i < density.x; ++i) {
            for (int j = 0; j < density.y; ++j) {
                for (int k = 0; k < density.z; ++k) {
                    double x = bounds.min.x + (i + 0.5) * diff.x / density.x;
                    double y = bounds.min.y + (j + 0.5) * diff.y / density.y;
                    double z = bounds.min.z + (k + 0.5) * diff.z / density.z;
                    particleManager.addEffect(new CustomBreakingParticle(world, x, y, z, x - center.x, y - center.y, z - center.z, icons.get(world.rand.nextInt(icons.size()))));
                }
            }
        }
    }
}
