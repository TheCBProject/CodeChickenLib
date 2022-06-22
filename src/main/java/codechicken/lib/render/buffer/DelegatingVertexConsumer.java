package codechicken.lib.render.buffer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * A simple {@link VertexConsumer} implementation which forwards to a delegate.
 * <p>
 * Created by covers1624 on 29/3/22.
 */
public abstract class DelegatingVertexConsumer implements ISpriteAwareVertexConsumer {

    protected final VertexConsumer delegate;

    public DelegatingVertexConsumer(VertexConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void sprite(TextureAtlasSprite sprite) {
        if (delegate instanceof ISpriteAwareVertexConsumer spriteCons) {
            spriteCons.sprite(sprite);
        }
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        delegate.color(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        delegate.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        delegate.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        delegate.uv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {
        delegate.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        delegate.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor() {
        delegate.unsetDefaultColor();
    }

    @Override
    public VertexFormat getVertexFormat() {
        return delegate.getVertexFormat();
    }
}
