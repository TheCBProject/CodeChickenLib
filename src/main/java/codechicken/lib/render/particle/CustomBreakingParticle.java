package codechicken.lib.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class CustomBreakingParticle extends SingleQuadParticle {

    private final float uo;
    private final float vo;

    public CustomBreakingParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        super(world, x, y, z, dx, dy, dz, icon);
        gravity = 1;
        rCol = gCol = bCol = 0.6F;
        quadSize /= 2.0F;
        uo = random.nextFloat() * 3.0F;
        vo = random.nextFloat() * 3.0F;
    }

    public static CustomBreakingParticle newLandingParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite icon) {
        CustomBreakingParticle particle = new CustomBreakingParticle(world, x, y, z, dx, dy, dz, icon);
        particle.xd = dx;
        particle.yd = dy;
        particle.zd = dz;
        return particle;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TERRAIN;
    }

    protected float getU0() {
        return sprite.getU((uo + 1.0F) / 4.0F);
    }

    protected float getU1() {
        return sprite.getU(uo / 4.0F);
    }

    protected float getV0() {
        return sprite.getV(vo / 4.0F);
    }

    protected float getV1() {
        return sprite.getV((vo + 1.0F) / 4.0F);
    }

    public void setScale(float scale) {
        quadSize = scale;
    }

    public float getScale() {
        return quadSize;
    }
}
