package codechicken.lib.render.particle;

import codechicken.lib.block.IBlockTextureProvider;
import codechicken.lib.render.DigIconParticle;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Created by covers1624 on 21/11/2016.
 */
public class CustomParticleHandler {

    public static void addHitEffects(IBlockState state, World world, RayTraceResult trace, ParticleManager particleManager, IBlockTextureProvider provider) {
        TextureAtlasSprite sprite = provider.getTexture(trace.sideHit, state.getBlock().getMetaFromState(state));
        Cuboid6 cuboid = new Cuboid6(state.getBoundingBox(world, trace.getBlockPos()));
        addBlockHitEffects(world, cuboid, trace.sideHit, sprite, particleManager);
    }

    public static void addBlockHitEffects(World world, Cuboid6 bounds, EnumFacing side, TextureAtlasSprite icon, ParticleManager particleManager) {
        float border = 0.1F;
        Vector3 diff = bounds.max.copy().subtract(bounds.min).add(-2 * border);
        diff.x *= world.rand.nextDouble();
        diff.y *= world.rand.nextDouble();
        diff.z *= world.rand.nextDouble();
        Vector3 pos = diff.add(bounds.min).add(border);

        if (side == EnumFacing.DOWN) {
            diff.y = bounds.min.y - border;
        }
        if (side == EnumFacing.UP) {
            diff.y = bounds.max.y + border;
        }
        if (side == EnumFacing.NORTH) {
            diff.z = bounds.min.z - border;
        }
        if (side == EnumFacing.SOUTH) {
            diff.z = bounds.max.z + border;
        }
        if (side == EnumFacing.WEST) {
            diff.x = bounds.min.x - border;
        }
        if (side == EnumFacing.EAST) {
            diff.x = bounds.max.x + border;
        }

        particleManager.addEffect(new DigIconParticle(world, pos.x, pos.y, pos.z, 0, 0, 0, icon).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
    }

}
