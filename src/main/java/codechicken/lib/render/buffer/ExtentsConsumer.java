package codechicken.lib.render.buffer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 11/2/25.
 */
public class ExtentsConsumer implements ISpriteAwareVertexConsumer {

    private final Consumer<Vector3fc> cons;

    public ExtentsConsumer(Consumer<Vector3fc> cons) {
        this.cons = cons;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        cons.accept(new Vector3f(x, y, z));
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public VertexConsumer setColor(int color) {
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float lineWidth) {
        return this;
    }

    @Override
    public void sprite(TextureAtlasSprite sprite) {
    }
}
