package codechicken.lib.render;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class DigIconParticle extends Particle {
    public DigIconParticle(World world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        super(world, x, y, z, dx, dy, dz);
        particleTexture = icon;
        particleGravity = 1;
        particleRed = particleGreen = particleBlue = 0.6F;
        particleScale /= 2.0F;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    public void setScale(float scale) {
        particleScale = scale;
    }

    public float getScale() {
        return particleScale;
    }

    public void setMaxAge(int age) {
        particleMaxAge = age;
    }

    public int getMaxAge() {
        return particleMaxAge;
    }

    /**
     * copy pasted from EntityDiggingFX
     * //TODO Doc.
     */
    @Override
    public void renderParticle(VertexBuffer vertexBuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f6 = (particleTextureIndexX + particleTextureJitterX / 4.0F) / 16.0F;
        float f7 = f6 + 0.015609375F;
        float f8 = (particleTextureIndexY + particleTextureJitterY / 4.0F) / 16.0F;
        float f9 = f8 + 0.015609375F;
        float f10 = 0.1F * particleScale;

        if (particleTexture != null) {
            f6 = particleTexture.getInterpolatedU(particleTextureJitterX / 4.0F * 16.0F);
            f7 = particleTexture.getInterpolatedU((particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
            f8 = particleTexture.getInterpolatedV(particleTextureJitterY / 4.0F * 16.0F);
            f9 = particleTexture.getInterpolatedV((particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
        }

        float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
        float f14 = 1.0F;

        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;

        vertexBuffer.pos(f11 - rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 - rotationYZ * f10 - rotationXZ * f10).tex(f6, f9).color(f14 * particleRed, f14 * particleGreen, f14 * particleBlue, 255F).lightmap(j, k).endVertex();
        vertexBuffer.pos(f11 - rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 - rotationYZ * f10 + rotationXZ * f10).tex(f6, f8).color(f14 * particleRed, f14 * particleGreen, f14 * particleBlue, 255F).lightmap(j, k).endVertex();
        vertexBuffer.pos(f11 + rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 + rotationYZ * f10 + rotationXZ * f10).tex(f7, f8).color(f14 * particleRed, f14 * particleGreen, f14 * particleBlue, 255F).lightmap(j, k).endVertex();
        vertexBuffer.pos(f11 + rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 + rotationYZ * f10 - rotationXZ * f10).tex(f7, f9).color(f14 * particleRed, f14 * particleGreen, f14 * particleBlue, 255F).lightmap(j, k).endVertex();
    }

    public static void addBlockHitEffects(World world, Cuboid6 bounds, int side, TextureAtlasSprite icon, ParticleManager effectRenderer) {
        float border = 0.1F;
        Vector3 diff = bounds.max.copy().subtract(bounds.min).add(-2 * border);
        diff.x *= world.rand.nextDouble();
        diff.y *= world.rand.nextDouble();
        diff.z *= world.rand.nextDouble();
        Vector3 pos = diff.add(bounds.min).add(border);

        if (side == 0) {
            diff.y = bounds.min.y - border;
        }
        if (side == 1) {
            diff.y = bounds.max.y + border;
        }
        if (side == 2) {
            diff.z = bounds.min.z - border;
        }
        if (side == 3) {
            diff.z = bounds.max.z + border;
        }
        if (side == 4) {
            diff.x = bounds.min.x - border;
        }
        if (side == 5) {
            diff.x = bounds.max.x + border;
        }

        effectRenderer.addEffect(new DigIconParticle(world, pos.x, pos.y, pos.z, 0, 0, 0, icon).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
    }

    public static void addBlockDestroyEffects(World world, Cuboid6 bounds, TextureAtlasSprite[] icons, ParticleManager effectRenderer) {
        Vector3 diff = bounds.max.copy().subtract(bounds.min);
        Vector3 center = bounds.min.copy().add(bounds.max).multiply(0.5);
        Vector3 density = diff.copy().multiply(4);
        density.x = Math.ceil(density.x);
        density.y = Math.ceil(density.y);
        density.z = Math.ceil(density.z);

        for (int i = 0; i < density.x; ++i) {
            for (int j = 0; j < density.y; ++j) {
                for (int k = 0; k < density.z; ++k) {
                    double x = bounds.min.x + (i + 0.5) * diff.x / density.x;
                    double y = bounds.min.y + (j + 0.5) * diff.y / density.y;
                    double z = bounds.min.z + (k + 0.5) * diff.z / density.z;
                    effectRenderer.addEffect(new DigIconParticle(world, x, y, z, x - center.x, y - center.y, z - center.z, icons[world.rand.nextInt(icons.length)]));
                }
            }
        }
    }
}
