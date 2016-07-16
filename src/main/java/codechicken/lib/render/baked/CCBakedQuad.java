package codechicken.lib.render.baked;

import codechicken.lib.render.Vertex5;
import codechicken.lib.vec.Vector3;

/**
 * Created by covers1624 on 6/26/2016.
 * Alternative to BakedQuad. Only temporary.
 */
public class CCBakedQuad {
    private final Vertex5 vertex;
    private final Vector3 normal;
    private final int colour;
    private final int lightMap;

    public CCBakedQuad(Vertex5 vertex, Vector3 normal, int colour, int lightMap) {
        this.vertex = vertex.copy();
        this.normal = normal.copy();
        this.colour = colour;
        this.lightMap = lightMap;
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

    public int getColour() {
        return colour;
    }

    public int getLightMap() {
        return lightMap;
    }
}
