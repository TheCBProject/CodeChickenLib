package codechicken.lib.render.particle;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;

public class CustomBreakingParticle extends SpriteTexturedParticle {

    private final float uo;
    private final float vo;

    public CustomBreakingParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        super(world, x, y, z, dx, dy, dz);
        setSprite(icon);
        gravity = 1;
        rCol = gCol = bCol = 0.6F;
        quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    public static CustomBreakingParticle newLandingParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        CustomBreakingParticle particle = new CustomBreakingParticle(world, x, y, z, dx, dy, dz, icon);
        particle.xd = dx;
        particle.yd = dy;
        particle.zd = dz;
        return particle;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.TERRAIN_SHEET;
    }

    protected float getU0() {
        return this.sprite.getU((double) ((this.uo + 1.0F) / 4.0F * 16.0F));
    }

    protected float getU1() {
        return this.sprite.getU((double) (this.uo / 4.0F * 16.0F));
    }

    protected float getV0() {
        return this.sprite.getV((double) (this.vo / 4.0F * 16.0F));
    }

    protected float getV1() {
        return this.sprite.getV((double) ((this.vo + 1.0F) / 4.0F * 16.0F));
    }

    public void setScale(float scale) {
        quadSize = scale;
    }

    public float getScale() {
        return quadSize;
    }
}
