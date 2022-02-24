package codechicken.lib.render.buffer;

import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Created by covers1624 on 4/24/20.
 */
public class TransformingVertexConsumer implements ISpriteAwareVertexConsumer {

    private final VertexConsumer delegate;
    private final Transformation transform;
    private final Vector3 storage = new Vector3();

    public TransformingVertexConsumer(VertexConsumer delegate, PoseStack stack) {
        this(delegate, new Matrix4(stack));
    }

    public TransformingVertexConsumer(VertexConsumer delegate, Transformation transform) {
        this.delegate = delegate;
        this.transform = transform;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        storage.set(x, y, z);
        transform.apply(storage);
        delegate.vertex(storage.x, storage.y, storage.z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        delegate.color(red, green, blue, alpha);
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
        storage.set(x, y, z);
        transform.applyN(storage);
        delegate.normal((float) storage.x, (float) storage.y, (float) storage.z);
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
    public void sprite(TextureAtlasSprite sprite) {
        if (delegate instanceof ISpriteAwareVertexConsumer) {
            ((ISpriteAwareVertexConsumer) delegate).sprite(sprite);
        }
    }
}
