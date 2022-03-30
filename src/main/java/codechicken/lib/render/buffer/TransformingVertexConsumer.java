package codechicken.lib.render.buffer;

import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Created by covers1624 on 4/24/20.
 */
public class TransformingVertexConsumer extends DelegatingVertexConsumer {

    private final Transformation transform;
    private final Vector3 storage = new Vector3();

    public TransformingVertexConsumer(VertexConsumer delegate, PoseStack stack) {
        this(delegate, new Matrix4(stack));
    }

    public TransformingVertexConsumer(VertexConsumer delegate, Transformation transform) {
        super(delegate);
        this.transform = transform;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        storage.set(x, y, z);
        transform.apply(storage);
        return super.vertex(storage.x, storage.y, storage.z);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        storage.set(x, y, z);
        transform.applyN(storage);
        return delegate.normal((float) storage.x, (float) storage.y, (float) storage.z);
    }
}
