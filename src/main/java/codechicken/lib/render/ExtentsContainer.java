package codechicken.lib.render;

import codechicken.lib.render.buffer.ExtentsConsumer;
import codechicken.lib.vec.Cuboid6;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 7/26/26.
 */
public final class ExtentsContainer {

    private @Nullable Cuboid6 extents;

    public void visitExtents(Consumer<Vector3fc> extents, Consumer<VertexConsumer> cons) {
        var box = get(cons);
        extents.accept(box.min.vector3f());
        extents.accept(box.max.vector3f());
    }

    public Cuboid6 get(Consumer<VertexConsumer> extentsBuilder) {
        if (extents == null) {
            Cuboid6 box = new Cuboid6();
            extentsBuilder.accept(new ExtentsConsumer(box::enclose));
            extents = box;
        }
        return extents;
    }

    public void reset() {
        extents = null;
    }
}
