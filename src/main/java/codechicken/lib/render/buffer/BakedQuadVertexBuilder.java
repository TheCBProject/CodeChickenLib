package codechicken.lib.render.buffer;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.Quad;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link VertexConsumer} implementation to build {@link BakedQuad}s.
 * <p>
 * Created by covers1624 on 4/23/20.
 */
public class BakedQuadVertexBuilder implements VertexConsumer, ISpriteAwareVertexConsumer {

    private final List<Quad> quadList = new ArrayList<>();
    private final VertexFormat.Mode mode;
    private final int vSize;

    private CachedFormat format;
    private Colour defaultColour;
    private Quad current;
    private int vertex;

    public BakedQuadVertexBuilder() {
        this(VertexFormat.Mode.QUADS);
    }

    public BakedQuadVertexBuilder(VertexFormat format) {
        this(format, VertexFormat.Mode.QUADS);
    }

    public BakedQuadVertexBuilder(CachedFormat format) {
        this(format, VertexFormat.Mode.QUADS);
    }

    public BakedQuadVertexBuilder(VertexFormat.Mode mode) {
        this(CachedFormat.BLOCK, mode);
    }

    public BakedQuadVertexBuilder(VertexFormat format, VertexFormat.Mode mode) {
        this(CachedFormat.lookup(format), mode);
    }

    public BakedQuadVertexBuilder(CachedFormat format, VertexFormat.Mode mode) {
        if (mode != VertexFormat.Mode.QUADS && mode != VertexFormat.Mode.TRIANGLES) {
            throw new IllegalArgumentException("Only QUADS or TRIANGLES supported. Got: " + mode);
        }
        this.mode = mode;
        this.format = format;
        vSize = mode.primitiveLength;
    }

    // Provided for interop with other mods that may provide different quad formats.
    public void setFormat(VertexFormat format) {
        setFormat(CachedFormat.lookup(format));
    }

    // Provided for interop with other mods that may provide different quad formats.
    public void setFormat(CachedFormat format) {
        this.format = format;
    }

    public void reset() {
        quadList.clear();
        current = null;
        vertex = 0;
    }

    @Override
    public void sprite(TextureAtlasSprite sprite) {
        checkNewQuad();
        current.setTexture(sprite);
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        if (!format.hasPosition) return this;

        checkNewQuad();
        current.vertices[vertex].vec[0] = (float) x;
        current.vertices[vertex].vec[1] = (float) y;
        current.vertices[vertex].vec[2] = (float) z;
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        if (!format.hasColor) return this;

        checkNewQuad();
        current.vertices[vertex].color[0] = red / 255F;
        current.vertices[vertex].color[1] = green / 255F;
        current.vertices[vertex].color[2] = blue / 255F;
        current.vertices[vertex].color[3] = alpha / 255F;
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        if (!format.hasUV) return this;

        checkNewQuad();
        current.vertices[vertex].uv[0] = u;
        current.vertices[vertex].uv[1] = v;
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        if (!format.hasOverlay) return this;

        checkNewQuad();
        current.vertices[vertex].overlay[0] = u / (float) 0xF0;
        current.vertices[vertex].overlay[1] = v / (float) 0xF0;
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        if (!format.hasLightMap) return this;

        checkNewQuad();
        current.vertices[vertex].lightmap[0] = u / (float) 0xF0;
        current.vertices[vertex].lightmap[1] = v / (float) 0xF0;
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        if (!format.hasNormal) return this;

        checkNewQuad();
        current.vertices[vertex].normal[0] = x;
        current.vertices[vertex].normal[1] = y;
        current.vertices[vertex].normal[2] = z;
        return this;
    }

    @Override
    public void endVertex() {
        vertex++;
        if (vertex == vSize) {
            if (mode == VertexFormat.Mode.TRIANGLES) {
                //Quadulate.
                for (int e = 0; e < current.format.elementCount; e++) {
                    System.arraycopy(current.vertices[2].raw[e], 0, current.vertices[3].raw[e], 0, 4);
                }
            }
            if (current.sprite == null) {
                throw new IllegalStateException("Sprite not set.");
            }
            if (defaultColour != null) {
                float[] colour = defaultColour.getRGBA();
                for (Quad.Vertex v : current.vertices) {
                    System.arraycopy(colour, 0, v.color, 0, 4);
                }
            }
            quadList.add(current);
            current = null;
            vertex = 0;
        }
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        defaultColour = new ColourRGBA(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor() {
        defaultColour = null;
    }

    public List<BakedQuad> bake() {
        if (current != null) {
            throw new IllegalStateException("Not finished building.");
        }
        return quadList.stream().map(Quad::bake).collect(Collectors.toList());
    }

    private void checkNewQuad() {
        if (current == null) {
            current = new Quad(format);
        }
    }
}
