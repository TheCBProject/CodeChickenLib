package codechicken.lib.render.buffer;

import codechicken.lib.colour.Colour;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

/**
 * Created by covers1624 on 20/09/2016.
 */
public interface IVertexBuffer {

    IVertexBuffer startDrawingQuads(VertexFormat format);

    IVertexBuffer startDrawing(int glMode, VertexFormat format);

    void draw();

    IVertexBuffer tex(double u, double v);

    IVertexBuffer lightMap(int sky, int block);

    IVertexBuffer colour(float red, float green, float blue);

    IVertexBuffer colour(float red, float green, float blue, float alpha);

    IVertexBuffer colour(int red, int green, int blue);

    IVertexBuffer colour(int red, int green, int blue, int alpha);

    IVertexBuffer colour(Colour colour);

    IVertexBuffer normal(double x, double y, double z);

    IVertexBuffer pos(double x, double y, double z);

    void endVertex();

    void reset();

    boolean isDrawing();

    int getVertexMode();

    VertexFormat getVertexFormat();

    VertexBuffer getBuffer();

}
