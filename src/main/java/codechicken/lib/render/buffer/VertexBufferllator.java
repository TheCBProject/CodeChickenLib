package codechicken.lib.render.buffer;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.UV;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import java.nio.ByteBuffer;

/**
 * Created by covers1624 on 4/03/2017.
 */
public class VertexBufferllator extends VertexBuffer {

    private final VertexBuffer wrapped;

    //Storage.
    private final UV uv = new UV();
    private final int[] lmap = new int[2];
    private final Colour colour = new ColourRGBA(0xFFFFFFFF);
    private final Vector3 pos = new Vector3();
    private final Vector3 normal = new Vector3();

    public VertexBufferllator(VertexBuffer buffer) {
        super(1);
        wrapped = buffer;
    }

    @Override
    public void endVertex() {
        VertexFormat format = getVertexFormat();
        for (int e = 0; e < format.getElementCount(); e++) {
            VertexFormatElement fmte = format.getElement(e);
            switch (fmte.getUsage()) {
                case POSITION:
                    wrapped.pos(pos.x, pos.y, pos.z);
                    break;
                case UV:
                    if (fmte.getIndex() == 0) {
                        wrapped.tex(uv.u, uv.v);
                    } else {
                        wrapped.lightmap(lmap[0], lmap[1]);
                    }
                    break;
                case COLOR:
                    if (wrapped.isColorDisabled()) {
                        //-_- Fucking mojang..
                        wrapped.nextVertexFormatIndex();
                    } else {
                        wrapped.color(colour.r & 0xFF, colour.g & 0xFF, colour.b & 0xFF, colour.a & 0xFF);
                    }
                    break;
                case NORMAL:
                    wrapped.normal((float) normal.x, (float) normal.y, (float) normal.z);
                    break;
                case PADDING:
                    break;
                default:
                    throw new UnsupportedOperationException("Generic vertex format element");
            }
        }
        wrapped.endVertex();
    }

    @Override
    public VertexBufferllator pos(double x, double y, double z) {
        pos.set(x, y, z);
        return this;
    }

    @Override
    public VertexBufferllator tex(double u, double v) {
        uv.set(u, v);
        return this;
    }

    @Override
    public VertexBuffer normal(float x, float y, float z) {
        normal.set(x, y, z);
        return this;
    }

    @Override
    public VertexBufferllator lightmap(int p_187314_1_, int p_187314_2_) {
        lmap[0] = p_187314_1_;
        lmap[1] = p_187314_2_;
        return this;
    }

    @Override
    public VertexBufferllator color(float red, float green, float blue, float alpha) {
        colour.set(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexBufferllator color(int red, int green, int blue, int alpha) {
        colour.set(red, green, blue, alpha);
        return this;
    }

    //region Wrapped Calls.

    @Override
    public void sortVertexData(float p_181674_1_, float p_181674_2_, float p_181674_3_) {
        wrapped.sortVertexData(p_181674_1_, p_181674_2_, p_181674_3_);
    }

    @Override
    public State getVertexState() {
        return wrapped.getVertexState();
    }

    @Override
    public void setVertexState(State state) {
        wrapped.setVertexState(state);
    }

    @Override
    public void begin(int glMode, VertexFormat format) {
        wrapped.begin(glMode, format);
    }

    @Override
    public void putBrightness4(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_) {
        wrapped.putBrightness4(p_178962_1_, p_178962_2_, p_178962_3_, p_178962_4_);
    }

    @Override
    public void putPosition(double x, double y, double z) {
        wrapped.putPosition(x, y, z);
    }

    @Override
    public int getColorIndex(int vertexIndex) {
        return wrapped.getColorIndex(vertexIndex);
    }

    @Override
    public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
        wrapped.putColorMultiplier(red, green, blue, vertexIndex);
    }

    @Override
    public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
        wrapped.putColorRGB_F(red, green, blue, vertexIndex);
    }

    @Override
    public void putColorRGBA(int index, int red, int green, int blue, int alpha) {
        wrapped.putColorRGBA(index, red, green, blue, alpha);
    }

    @Override
    public void noColor() {
        wrapped.noColor();
    }

    @Override
    public void addVertexData(int[] vertexData) {
        wrapped.addVertexData(vertexData);
    }

    @Override
    public void putNormal(float x, float y, float z) {
        wrapped.putNormal(x, y, z);
    }

    @Override
    public void nextVertexFormatIndex() {
        wrapped.nextVertexFormatIndex();
    }

    @Override
    public void setTranslation(double x, double y, double z) {
        wrapped.setTranslation(x, y, z);
    }

    @Override
    public void finishDrawing() {
        wrapped.finishDrawing();
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return wrapped.getByteBuffer();
    }

    @Override
    public VertexFormat getVertexFormat() {
        return wrapped.getVertexFormat();
    }

    @Override
    public int getVertexCount() {
        return wrapped.getVertexCount();
    }

    @Override
    public int getDrawMode() {
        return wrapped.getDrawMode();
    }

    @Override
    public void putColor4(int argb) {
        wrapped.putColor4(argb);
    }

    @Override
    public void putColorRGB_F4(float red, float green, float blue) {
        wrapped.putColorRGB_F4(red, green, blue);
    }

    @Override
    public boolean isColorDisabled() {
        return wrapped.isColorDisabled();
    }

    //endregion
}
