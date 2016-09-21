package codechicken.lib.render.buffer;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.Vertex5;
import codechicken.lib.render.uv.UV;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

/**
 * Created by covers1624 on 20/09/2016.
 */
public class CachingBufferWrapper implements IVertexBuffer {

    private Vertex5 vert = new Vertex5();
    private Vector3 normal = null;
    private Colour colour = new ColourRGBA(0xFFFFFFFF);
    private int sky = 0;
    private int block = 0;

    private IVertexBuffer buffer;

    public CachingBufferWrapper(VertexBuffer buffer) {
        this(new VertexBufferWrapper(buffer));
    }

    public CachingBufferWrapper(IVertexBuffer wrappedBuffer) {
        this.buffer = wrappedBuffer;
    }

    @Override
    public IVertexBuffer startDrawingQuads(VertexFormat format) {
        return buffer.startDrawingQuads(format);
    }

    @Override
    public IVertexBuffer startDrawing(int glMode, VertexFormat format) {
        return buffer.startDrawing(glMode, format);
    }

    @Override
    public void draw() {
        buffer.draw();
    }

    @Override
    public IVertexBuffer tex(double u, double v) {
        this.vert.uv.set(u, v);
        return this;
    }

    @Override
    public IVertexBuffer lightMap(int sky, int block) {
        this.sky = sky;
        this.block = block;
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
        this.colour.set(colour);
        return this;
    }

    @Override
    public IVertexBuffer pos(double x, double y, double z) {
        vert.vec.set(x, y, z);
        return this;
    }

    @Override
    public IVertexBuffer normal(double x, double y, double z) {
        this.normal.set(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {
        for (int e = 0; e < getVertexFormat().getElementCount(); e++) {
            VertexFormatElement fmte = getVertexFormat().getElement(e);
            switch (fmte.getUsage()) {
                case POSITION:
                    buffer.pos(vert.vec.x, vert.vec.y, vert.vec.z);
                    break;
                case UV:
                    if (fmte.getIndex() == 0) {
                        buffer.tex(vert.uv.u, vert.uv.v);
                    } else {
                        buffer.lightMap(sky, block);
                    }
                    break;
                case COLOR:
                    buffer.colour(colour);
                    break;
                case NORMAL:
                    buffer.normal((float) normal.x, (float) normal.y, (float) normal.z);
                    break;
                case PADDING:
                    break;
                default:
                    throw new UnsupportedOperationException("Generic vertex format element");
            }
        }
        buffer.endVertex();
    }

    @Override
    public void reset() {
        vert = new Vertex5();
        colour = new ColourRGBA(0xFFFFFFFF);
        normal = new Vector3();
        sky = 0;
        block = 0;
        buffer.reset();
    }

    @Override
    public boolean isDrawing() {
        return buffer.isDrawing();
    }

    @Override
    public int getVertexMode() {
        return buffer.getVertexMode();
    }

    @Override
    public VertexFormat getVertexFormat() {
        return buffer.getVertexFormat();
    }

    @Override
    public VertexBuffer getBuffer() {
        return buffer.getBuffer();
    }
}
