package codechicken.lib.render.baked;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.EnumDrawMode;
import codechicken.lib.render.Vertex5;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.vertex.VertexFormat;

/**
 * Created by covers1624 on 6/26/2016.
 * A more simple version of a BakedQuad for CCBakedModel.
 */
public class CCBakedQuad {

    private final VertexFormat format;
    private final EnumDrawMode drawMode;
    private final Vertex5 vertex;
    private final Vector3 normal;
    private final Colour colour;
    private final int lightMap;

    public CCBakedQuad(VertexFormat format, Vertex5 vertex, Vector3 normal, Colour colour, int lightMap){
        this(format, EnumDrawMode.QUADS, vertex, normal, colour, lightMap);
    }

    public CCBakedQuad(VertexFormat format, EnumDrawMode drawMode, Vertex5 vertex, Vector3 normal, Colour colour, int lightMap) {
        this.format = format;
        this.drawMode = drawMode;
        this.vertex = vertex.copy();
        this.normal = normal.copy();
        this.colour = new ColourRGBA(colour.rgba());
        this.lightMap = lightMap;
    }

    public VertexFormat getFormat() {
        return format;
    }

    public EnumDrawMode getDrawMode() {
        return drawMode;
    }

    public Vertex5 getVertex() {
        return vertex.copy();
    }

    public Vector3 getPos() {
        return vertex.vec.copy();
    }

    public Vector3 getNormal() {
        return normal.copy();
    }

    public Colour getColour() {
        return colour;
    }

    public int getLightMap() {
        return lightMap;
    }
}
