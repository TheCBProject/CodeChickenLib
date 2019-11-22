package codechicken.lib.render.particle;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

public class CustomBreakingParticle extends SpriteTexturedParticle {

    private final float field_217571_C;
    private final float field_217572_F;

    public CustomBreakingParticle(World world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        super(world, x, y, z, dx, dy, dz);
        setSprite(icon);
        particleGravity = 1;
        particleRed = particleGreen = particleBlue = 0.6F;
        particleScale /= 2.0F;
        this.field_217571_C = this.rand.nextFloat() * 3.0F;
        this.field_217572_F = this.rand.nextFloat() * 3.0F;
    }

    public static CustomBreakingParticle newLandingParticle(World world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        CustomBreakingParticle particle = new CustomBreakingParticle(world, x, y, z, dx, dy, dz, icon);
        particle.motionX = dx;
        particle.motionY = dy;
        particle.motionZ = dz;
        return particle;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.TERRAIN_SHEET;
    }

    protected float getMinU() {
        return this.sprite.getInterpolatedU((double) ((this.field_217571_C + 1.0F) / 4.0F * 16.0F));
    }

    protected float getMaxU() {
        return this.sprite.getInterpolatedU((double) (this.field_217571_C / 4.0F * 16.0F));
    }

    protected float getMinV() {
        return this.sprite.getInterpolatedV((double) (this.field_217572_F / 4.0F * 16.0F));
    }

    protected float getMaxV() {
        return this.sprite.getInterpolatedV((double) ((this.field_217572_F + 1.0F) / 4.0F * 16.0F));
    }

    public void setScale(float scale) {
        particleScale = scale;
    }

    public float getScale() {
        return particleScale;
    }
}
