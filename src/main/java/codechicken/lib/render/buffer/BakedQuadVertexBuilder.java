package codechicken.lib.render.buffer;

import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.Quad;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexConsumer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Similar to {@link BakedQuadBuilder} except receives its data from a vanilla {@link IVertexConsumer}.
 * <p>
 * Created by covers1624 on 4/23/20.
 */
public class BakedQuadVertexBuilder implements IVertexBuilder, ISpriteAwareVertexBuilder {

    private final List<Quad> quadList = new ArrayList<>();
    private final int glMode;
    private final int vSize;

    private CachedFormat format = CachedFormat.lookup(DefaultVertexFormats.BLOCK);
    private Quad current;
    private int vertex;

    public BakedQuadVertexBuilder() {
        this(GL11.GL_QUADS);
    }

    public BakedQuadVertexBuilder(int glMode) {
        if (glMode != GL11.GL_QUADS && glMode != GL11.GL_TRIANGLES) {
            throw new IllegalArgumentException("Only GL_QUADS and GL_TRIANGLES supported. Got: " + glMode);
        }

        this.glMode = glMode;
        vSize = glMode == GL11.GL_QUADS ? 4 : 3;
    }

    public void reset() {
        quadList.clear();
        current = null;
        vertex = 0;
    }

    public void setFormat(@Nonnull VertexFormat format) {
        this.format = CachedFormat.lookup(format);
    }

    @Override
    public void sprite(TextureAtlasSprite sprite) {
        checkNewQuad();
        current.setTexture(sprite);
    }

    @Nonnull
    @Override
    public IVertexBuilder vertex(double x, double y, double z) {
        if (!format.hasPosition) {
            return this;
        }

        checkNewQuad();
        current.vertices[vertex].vec[0] = (float) x;
        current.vertices[vertex].vec[1] = (float) y;
        current.vertices[vertex].vec[2] = (float) z;
        return this;
    }

	@Nonnull
    @Override
    public IVertexBuilder color(int red, int green, int blue, int alpha) {
        if (!format.hasColor) {
            return this;
        }

        checkNewQuad();
        current.vertices[vertex].color[0] = red / 255F;
        current.vertices[vertex].color[1] = green / 255F;
        current.vertices[vertex].color[2] = blue / 255F;
        current.vertices[vertex].color[3] = alpha / 255F;
        return this;
    }

	@Nonnull
    @Override
    public IVertexBuilder uv(float u, float v) {
        if (!format.hasUV) {
            return this;
        }

        checkNewQuad();
        current.vertices[vertex].uv[0] = u;
        current.vertices[vertex].uv[1] = v;
        return this;
    }

	@Nonnull
    @Override
    public IVertexBuilder overlayCoords(int u, int v) {
        if (!format.hasUV) {
            return this;
        }

        checkNewQuad();
        current.vertices[vertex].overlay[0] = u / (float) 0xF0;
        current.vertices[vertex].overlay[1] = v / (float) 0xF0;
        return this;
    }

	@Nonnull
    @Override
    public IVertexBuilder uv2(int u, int v) {
        if (!format.hasUV) {
            return this;
        }

        checkNewQuad();
        current.vertices[vertex].lightmap[0] = u / (float) 0xF0;
        current.vertices[vertex].lightmap[1] = v / (float) 0xF0;
        return this;
    }

	@Nonnull
    @Override
    public IVertexBuilder normal(float x, float y, float z) {
        if (!format.hasNormal) {
            return this;
        }

        checkNewQuad();
        current.vertices[vertex].normal[0] = x;
        current.vertices[vertex].normal[1] = y;
        current.vertices[vertex].normal[2] = z;
        return this;
    }

    @Override
    public void endVertex() {
    	if(current == null) {
    		throw new IllegalStateException("Not building.");
		}
    	
        vertex++;
        if (vertex == vSize) {
            if (glMode == GL11.GL_TRIANGLES) {
                //Quadulate.
                for (int e = 0; e < current.format.elementCount; e++) {
                    System.arraycopy(current.vertices[2].raw[e], 0, current.vertices[3].raw[e], 0, 4);
                }
            }

            if (current.sprite == null) {
                throw new IllegalStateException("Sprite not set.");
            }

            quadList.add(current);
            current = null;
            vertex = 0;
        }
    }

    public List<BakedQuad> bake() {
        if (current != null) {
            throw new IllegalStateException("Not finished building.");
        }

        final ArrayList<BakedQuad> quads = new ArrayList<>();

        for (final Quad unbakedQuad : quadList) {
            quads.add(unbakedQuad.bake());
        }

        return quads;
    }

    private void checkNewQuad() {
        if (current == null) {
            current = new Quad(format);
        }
    }

}
