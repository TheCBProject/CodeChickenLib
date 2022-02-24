package codechicken.lib.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class CustomBreakingParticle extends TextureSheetParticle {

    private final float uo;
    private final float vo;

    public CustomBreakingParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        super(world, x, y, z, dx, dy, dz);
        setSprite(icon);
        gravity = 1;
        rCol = gCol = bCol = 0.6F;
        quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    public static CustomBreakingParticle newLandingParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        CustomBreakingParticle particle = new CustomBreakingParticle(world, x, y, z, dx, dy, dz, icon);
        particle.xd = dx;
        particle.yd = dy;
        particle.zd = dz;
        return particle;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
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
