package codechicken.lib.render.buffer;

import codechicken.lib.model.Quad;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link VertexConsumer} implementation to build {@link BakedQuad}s.
 * <p>
 * Created by covers1624 on 4/23/20.
 */
public class BakedQuadVertexBuilder implements VertexConsumer, ISpriteAwareVertexConsumer {

    private final List<BakedQuad> quadList = new ArrayList<>();
    private final VertexFormat.Mode mode;

    private final Quad current = new Quad();
    private int vertex = -1;

    public BakedQuadVertexBuilder() {
        this(VertexFormat.Mode.QUADS);
    }

    public BakedQuadVertexBuilder(VertexFormat.Mode mode) {
        if (mode != VertexFormat.Mode.QUADS && mode != VertexFormat.Mode.TRIANGLES) {
            throw new IllegalArgumentException("Only QUADS or TRIANGLES supported. Got: " + mode);
        }
        this.mode = mode;
    }

    public void reset() {
        quadList.clear();
        vertex = -1;
    }

    @Override
    public void sprite(TextureAtlasSprite sprite) {
        current.sprite = sprite;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        endPrevVertex();
        current.vertices[vertex].vec.set(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int color) {
        current.vertices[vertex].color = color;
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        setColor(ARGB.color(red, green, blue, alpha));
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        current.vertices[vertex].uv.set(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        current.vertices[vertex].normal.set(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        // Baked quads dont support overlay.
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        // Baked quads dont support arbitrary lightmap.
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float lineWidth) {
        // Baked quads dont support line width.
        return this;
    }

    private void endPrevVertex() {
        vertex++;
        if (vertex == mode.primitiveLength) {
            if (mode == VertexFormat.Mode.TRIANGLES) {
                // Quadulate.
                current.vertices[3].copyFrom(current.vertices[2]);
            }
            if (current.sprite == null) {
                throw new IllegalStateException("Sprite not set.");
            }
            current.calculateOrientation(false);
            quadList.add(current.bake());
            vertex = 0;
        }
    }

    public List<BakedQuad> bake() {
        endPrevVertex();
        vertex = -1;
        return new ArrayList<>(quadList);
    }
}
