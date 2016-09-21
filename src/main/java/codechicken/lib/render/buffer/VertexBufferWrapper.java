package codechicken.lib.render.buffer;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

/**
 * Created by covers1624 on 20/09/2016.
 */
public class VertexBufferWrapper implements IVertexBuffer {

    private VertexBuffer buffer;

    public VertexBufferWrapper(VertexBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public IVertexBuffer startDrawingQuads(VertexFormat format) {
        return startDrawing(GL11.GL_QUADS, format);
    }

    @Override
    public IVertexBuffer startDrawing(int glMode, VertexFormat format) {
        buffer.begin(glMode, format);
        return this;
    }

    @Override
    public void draw() {
        throw new IllegalStateException("Unable to finish drawing on raw buffer!");
    }

    @Override
    public IVertexBuffer tex(double u, double v) {
        buffer.tex(u, v);
        return this;
    }

    @Override
    public IVertexBuffer lightMap(int sky, int block) {
        buffer.lightmap(sky, block);
        return this;
    }

    @Override
    public IVertexBuffer colour(float red, float green, float blue) {
        return colour(red, green, blue, 1.0F);
    }

    @Override
    public IVertexBuffer colour(float red, float green, float blue, float alpha) {
        return colour(new ColourRGBA(red, green, blue, alpha));
    }

    @Override
    public IVertexBuffer colour(int red, int green, int blue) {
        return colour(red, green, blue, 0xFF);
    }

    @Override
    public IVertexBuffer colour(int red, int green, int blue, int alpha) {
        return colour(new ColourRGBA(red, green, blue, alpha));
    }

    @Override
    public IVertexBuffer colour(Colour colour) {
        buffer.color(colour.r & 0xFF, colour.g & 0xFF, colour.b & 0xFF, colour.a & 0xFF);
        return this;
    }

    @Override
    public IVertexBuffer normal(double x, double y, double z) {
        buffer.normal((float) x, (float) y, (float) z);
        return null;
    }

    @Override
    public IVertexBuffer pos(double x, double y, double z) {
        buffer.pos(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {
        buffer.endVertex();
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    @Override
    public boolean isDrawing() {
        return buffer.isDrawing;
    }

    @Override
    public int getVertexMode() {
        return buffer.getDrawMode();
    }

    @Override
    public VertexFormat getVertexFormat() {
        return buffer.getVertexFormat();
    }

    @Override
    public VertexBuffer getBuffer() {
        return buffer;
    }
}
