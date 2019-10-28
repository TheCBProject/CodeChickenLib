//package codechicken.lib.render.particle;
//
//import codechicken.lib.internal.network.PacketDispatcher;
//import codechicken.lib.raytracer.CuboidRayTraceResult;
//import codechicken.lib.texture.IWorldBlockTextureProvider;
//import codechicken.lib.util.ResourceUtils;
//import codechicken.lib.vec.Cuboid6;
//import codechicken.lib.vec.Vector3;
//import com.google.common.collect.Lists;
//import net.minecraft.block.Block;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.particle.ParticleManager;
//import net.minecraft.client.renderer.BlockModelShapes;
//import net.minecraft.client.renderer.block.model.IBakedModel;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.util.BlockRenderLayer;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.RayTraceResult;
//import net.minecraft.util.math.RayTraceResult.Type;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import net.minecraft.world.WorldServer;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * Created by covers1624 on 21/11/2016.
// */
//public class CustomParticleHandler {
//
//    @SideOnly (Side.CLIENT)
//    private static Set<TextureAtlasSprite> ignoredParticleSprites;
//
//    @Deprecated//Use the new particle system.
//    @SideOnly (Side.CLIENT)
//    public static void addHitEffects(IBlockState state, World world, RayTraceResult trace, ParticleManager particleManager, IWorldBlockTextureProvider provider) {
//        TextureAtlasSprite sprite = provider.getTexture(trace.sideHit, state, BlockRenderLayer.SOLID, world, trace.getBlockPos());
//        Cuboid6 cuboid = new Cuboid6(state.getBoundingBox(world, trace.getBlockPos())).add(trace.getBlockPos());
//        addBlockHitEffects(world, cuboid, trace.sideHit, sprite, particleManager);
//    }
//
//    @Deprecated//Use the new particle system.
//    @SideOnly (Side.CLIENT)
//    public static void addDestroyEffects(World world, BlockPos pos, ParticleManager particleManager, IWorldBlockTextureProvider provider) {
//        TextureAtlasSprite[] sprites = new TextureAtlasSprite[6];
//        IBlockState state = world.getBlockState(pos);
//        for (EnumFacing face : EnumFacing.VALUES) {
//            sprites[face.ordinal()] = provider.getTexture(face, state, BlockRenderLayer.SOLID, world, pos);
//        }
//        Cuboid6 cuboid = new Cuboid6(state.getBoundingBox(world, pos)).add(pos);
//        addBlockDestroyEffects(world, cuboid, sprites, particleManager);
//    }
//
//    /**
//     * Call from {@link Block#addLandingEffects}
//     * Provided the model bound is an instance of IModelParticleProvider, you will have landing particles just handled for you.
//     *
//     * @param world        The world.
//     * @param pos          The position of the block.
//     * @param entity       The entity.
//     * @param numParticles The number of particles to spawn.
//     * @return Always true for this, basically just return the result of this method inside {@link Block#addLandingEffects}
//     */
//    public static boolean handleLandingEffects(WorldServer world, BlockPos pos, EntityLivingBase entity, int numParticles) {
//        PacketDispatcher.dispatchLandingEffects(world, pos, entity, numParticles);
//        return true;
//    }
//
//    @SideOnly (Side.CLIENT)
//    public static boolean handleRunningEffects(World world, BlockPos pos, IBlockState state, Entity entity) {
//        //Spoof a raytrace from the feet.
//        RayTraceResult traceResult = new RayTraceResult(new Vec3d(entity.posX, pos.getY() + 1, entity.posZ), EnumFacing.UP, pos);
//        BlockModelShapes modelProvider = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
//        try {
//            state = state.getActualState(world, pos);
//        } catch (Throwable ignored) {
//        }
//        IBakedModel model = modelProvider.getModelForState(state);
//        state = state.getBlock().getExtendedState(state, world, pos);
//        if (model instanceof IModelParticleProvider) {
//            ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
//            Set<TextureAtlasSprite> hitSprites = ((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos);
//            List<TextureAtlasSprite> sprites = hitSprites.stream().filter(sprite -> !ignoredParticleSprites.contains(sprite)).collect(Collectors.toList());
//            TextureAtlasSprite rolledSprite = sprites.get(world.rand.nextInt(sprites.size()));
//            double x = entity.posX + (world.rand.nextFloat() - 0.5D) * entity.width;
//            double y = entity.getEntityBoundingBox().minY + 0.1D;
//            double z = entity.posZ + (world.rand.nextFloat() - 0.5D) * entity.width;
//            particleManager.addEffect(new DigIconParticle(world, x, y, z, -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, rolledSprite));
//            return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * Call from {@link Block#addLandingEffects}
//     * Provided the model bound is an instance of IModelParticleProvider, you will have landing particles just handled for you.
//     * Use the default PerspectiveModel implementations in CCL, Hit effects will be polled from your model based on your bounding box hit.
//     *
//     * @param state       The state.
//     * @param world       The world.
//     * @param traceResult The trace result.
//     * @param manager     The ParticleManager.
//     * @return True if particles were added, basically just return the result of this method inside {@link Block#addLandingEffects}
//     */
//    @SideOnly (Side.CLIENT)
//    public static boolean handleHitEffects(IBlockState state, World world, RayTraceResult traceResult, ParticleManager manager) {
//        if (traceResult != null && traceResult.typeOfHit == Type.BLOCK) {
//            BlockPos pos = traceResult.getBlockPos();
//            BlockModelShapes modelProvider = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
//            try {
//                state = state.getActualState(world, pos);
//            } catch (Throwable ignored) {
//            }
//            IBakedModel model = modelProvider.getModelForState(state);
//            state = state.getBlock().getExtendedState(state, world, pos);
//            if (model instanceof IModelParticleProvider) {
//                Cuboid6 bounds;
//                if (traceResult instanceof CuboidRayTraceResult) {
//                    bounds = ((CuboidRayTraceResult) traceResult).cuboid6;
//                } else {
//                    bounds = new Cuboid6(state.getBoundingBox(world, pos));
//                }
//                bounds = bounds.copy().add(pos);
//                Set<TextureAtlasSprite> hitSprites = ((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos);
//                List<TextureAtlasSprite> sprites = hitSprites.stream().filter(sprite -> !ignoredParticleSprites.contains(sprite)).collect(Collectors.toList());
//                addBlockHitEffects(world, bounds, traceResult.sideHit, sprites.get(world.rand.nextInt(sprites.size())), manager);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * {@link Block#addHitEffects}
//     * Provided the model bound is an instance of IModelParticleProvider, you will have landing particles just handled for you.
//     * Use the default PerspectiveModel implementations inside CCL, Destroy effects will just be handled for you.
//     *
//     * @param world   The world.
//     * @param pos     The position of the block.
//     * @param manager The ParticleManager.
//     * @return True if particles were added, basically just return the result of this method inside {@link Block#addHitEffects}
//     */
//    @SideOnly (Side.CLIENT)
//    public static boolean handleDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
//        IBlockState state = world.getBlockState(pos);
//        BlockModelShapes modelProvider = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
//        try {
//            state = state.getActualState(world, pos);
//        } catch (Throwable ignored) {
//        }
//        IBakedModel model = modelProvider.getModelForState(state);
//        state = state.getBlock().getExtendedState(state, world, pos);
//        if (model instanceof IModelParticleProvider) {
//            Cuboid6 bounds = new Cuboid6(state.getBoundingBox(world, pos));
//            Set<TextureAtlasSprite> destroySprites = ((IModelParticleProvider) model).getDestroyEffects(state, world, pos);
//            List<TextureAtlasSprite> sprites = destroySprites.stream().filter(sprite -> !ignoredParticleSprites.contains(sprite)).collect(Collectors.toList());
//            addBlockDestroyEffects(world, bounds.add(pos), sprites, manager);
//            return true;
//        }
//        return false;
//    }
//
//    @SideOnly (Side.CLIENT)
//    public static void addLandingEffects(World world, BlockPos pos, IBlockState state, Vector3 entityPos, int numParticles) {
//        //Spoof a raytrace from the feet.
//        RayTraceResult traceResult = new RayTraceResult(new Vec3d(entityPos.x, pos.getY() + 1, entityPos.z), EnumFacing.UP, pos);
//        ParticleManager manager = Minecraft.getMinecraft().effectRenderer;
//        Random randy = new Random();
//        BlockModelShapes modelProvider = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
//        try {
//            state = state.getActualState(world, pos);
//        } catch (Throwable ignored) {
//        }
//        IBakedModel model = modelProvider.getModelForState(state);
//        state = state.getBlock().getExtendedState(state, world, pos);
//        if (model instanceof IModelParticleProvider) {
//            Set<TextureAtlasSprite> hitSprites = ((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos);
//            List<TextureAtlasSprite> sprites = hitSprites.stream().filter(sprite -> !ignoredParticleSprites.contains(sprite)).collect(Collectors.toList());
//
//            double speed = 0.15000000596046448D;
//            if (numParticles != 0) {
//                for (int i = 0; i < numParticles; i++) {
//                    double mX = randy.nextGaussian() * speed;
//                    double mY = randy.nextGaussian() * speed;
//                    double mZ = randy.nextGaussian() * speed;
//                    manager.addEffect(DigIconParticle.newLandingParticle(world, entityPos.x, entityPos.y, entityPos.z, mX, mY, mZ, sprites.get(randy.nextInt(sprites.size()))));
//                }
//            }
//        }
//    }
//
//    @SideOnly (Side.CLIENT)
//    public static void addBlockHitEffects(World world, Cuboid6 bounds, EnumFacing side, TextureAtlasSprite icon, ParticleManager particleManager) {
//        float border = 0.1F;
//        Vector3 diff = bounds.max.copy().subtract(bounds.min).add(-2 * border);
//        diff.x *= world.rand.nextDouble();
//        diff.y *= world.rand.nextDouble();
//        diff.z *= world.rand.nextDouble();
//        Vector3 pos = diff.add(bounds.min).add(border);
//
//        if (side == EnumFacing.DOWN) {
//            diff.y = bounds.min.y - border;
//        }
//        if (side == EnumFacing.UP) {
//            diff.y = bounds.max.y + border;
//        }
//        if (side == EnumFacing.NORTH) {
//            diff.z = bounds.min.z - border;
//        }
//        if (side == EnumFacing.SOUTH) {
//            diff.z = bounds.max.z + border;
//        }
//        if (side == EnumFacing.WEST) {
//            diff.x = bounds.min.x - border;
//        }
//        if (side == EnumFacing.EAST) {
//            diff.x = bounds.max.x + border;
//        }
//
//        particleManager.addEffect(new DigIconParticle(world, pos.x, pos.y, pos.z, 0, 0, 0, icon).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
//    }
//
//    @SideOnly (Side.CLIENT)
//    @Deprecated//Remove in 1.13
//    public static void addBlockDestroyEffects(World world, Cuboid6 bounds, TextureAtlasSprite[] icons, ParticleManager particleManager) {
//        addBlockDestroyEffects(world, bounds, Lists.newArrayList(icons), particleManager);
//    }
//
//    @SideOnly (Side.CLIENT)
//    public static void addBlockDestroyEffects(World world, Cuboid6 bounds, List<TextureAtlasSprite> icons, ParticleManager particleManager) {
//        Vector3 diff = bounds.max.copy().subtract(bounds.min);
//        Vector3 center = bounds.min.copy().add(bounds.max).multiply(0.5);
//        Vector3 density = diff.copy().multiply(4).ceil();
//
//        for (int i = 0; i < density.x; ++i) {
//            for (int j = 0; j < density.y; ++j) {
//                for (int k = 0; k < density.z; ++k) {
//                    double x = bounds.min.x + (i + 0.5) * diff.x / density.x;
//                    double y = bounds.min.y + (j + 0.5) * diff.y / density.y;
//                    double z = bounds.min.z + (k + 0.5) * diff.z / density.z;
//                    particleManager.addEffect(new DigIconParticle(world, x, y, z, x - center.x, y - center.y, z - center.z, icons.get(world.rand.nextInt(icons.size()))));
//                }
//            }
//        }
//    }
//
//    /**
//     * Use to tell the particle system to ignore a specific sprite.
//     *
//     * @param sprite The sprite to ignore.
//     */
//    @SideOnly (Side.CLIENT)
//    public static void addIgnoredSprite(TextureAtlasSprite sprite) {
//        ignoredParticleSprites.add(sprite);
//    }
//
//    @SideOnly (Side.CLIENT)
//    public static void init() {
//        ignoredParticleSprites = new HashSet<>();
//        ResourceUtils.registerReloadListener(resourceManager -> ignoredParticleSprites.clear());
//    }
//
//}
