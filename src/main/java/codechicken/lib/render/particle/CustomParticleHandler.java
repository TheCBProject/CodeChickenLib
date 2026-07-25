package codechicken.lib.render.particle;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Created by covers1624 on 21/11/2016.
 */
public class CustomParticleHandler {

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

    public static void addLandingEffects(Level level, Vector3 entity, TextureAtlasSprite sprite, int particleCount) {
        if (particleCount == 0) return;

        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        for (int i = 0; i < particleCount; i++) {
            double mX = level.random.nextGaussian() * 0.15F;
            double mY = level.random.nextGaussian() * 0.15F;
            double mZ = level.random.nextGaussian() * 0.15F;
            manager.add(CustomBreakingParticle.newLandingParticle((ClientLevel) level, entity.x, entity.y, entity.z, mX, mY, mZ, sprite));
        }
    }

    public static void addRunningEffects(Level level, Entity entity, TextureAtlasSprite sprite) {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        double x = entity.getX() + (level.random.nextFloat() - 0.5D) * entity.getBbWidth();
        double y = entity.getBoundingBox().minY + 0.1D;
        double z = entity.getZ() + (level.random.nextFloat() - 0.5D) * entity.getBbWidth();
        manager.add(new CustomBreakingParticle(
                (ClientLevel) level,
                x, y, z,
                -entity.getDeltaMovement().x * 4.0D, 1.5D, -entity.getDeltaMovement().z * 4.0D,
                sprite
        ));
    }
}
