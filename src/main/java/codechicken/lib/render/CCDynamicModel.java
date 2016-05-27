package codechicken.lib.render;

import codechicken.lib.lighting.LC;
import codechicken.lib.render.CCRenderState.IVertexSource;
import codechicken.lib.render.CCRenderState.VertexAttribute;
import codechicken.lib.render.uv.UV;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.vertex.VertexFormat;

/**
 * Provides WorldRenderer style access to CCRenderState pipeline.
 * A zero length model that implements IVertexSource.hasAttribute.
 * Use CCDynamicModel.endVertex instead of CCRenderState.render
 */
//Broken.
public class CCDynamicModel implements IVertexSource {
    public final VertexAttribute[] attributes;

    public CCDynamicModel(VertexAttribute... attributes) {
        this.attributes = attributes;
    }

    @Override
    public Vertex5[] getVertices() {
        return new Vertex5[0];
    }

    @Override
    public <T> T getAttributes(VertexAttribute<T> attr) {
        return null;
    }

    @Override
    public boolean hasAttribute(VertexAttribute<?> attr) {
        for (VertexAttribute a : attributes) {
            if (a == attr) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void prepareVertex() {
    }

    public CCDynamicModel pos(Vector3 pos) {
        CCRenderState.vert.vec.set(pos);
        return this;
    }

    public CCDynamicModel pos(double x, double y, double z) {
        CCRenderState.vert.vec.set(x, y, z);
        return this;
    }

    public CCDynamicModel tex(UV uv) {
        CCRenderState.vert.uv.set(uv);
        return this;
    }

    public CCDynamicModel tex(double u, double v) {
        CCRenderState.vert.uv.set(u, v);
        return this;
    }

    public CCDynamicModel vert(Vertex5 vert) {
        CCRenderState.vert.set(vert);
        return this;
    }

    public CCDynamicModel normal(Vector3 normal) {
        CCRenderState.normal.set(normal);
        return this;
    }

    public CCDynamicModel normal(double x, double y, double z) {
        CCRenderState.normal.set(x, y, z);
        return this;
    }

    public CCDynamicModel colour(int colour) {
        CCRenderState.colour = colour;
        return this;
    }

    public CCDynamicModel brightness(int brightness) {
        CCRenderState.brightness = brightness;
        return this;
    }

    public CCDynamicModel side(int side) {
        CCRenderState.side = side;
        return this;
    }

    public CCDynamicModel lightCoord(LC lc) {
        CCRenderState.lc = lc;
        return this;
    }

    public void endVertex() {
        CCRenderState.runPipeline();
        CCRenderState.writeVert();
    }
}
