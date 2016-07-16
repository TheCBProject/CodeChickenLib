package codechicken.lib.render.baked;

import codechicken.lib.render.Vertex5;
import codechicken.lib.render.uv.UV;
import codechicken.lib.vec.Vector3;

import java.util.LinkedList;

/**
 * Created by covers1624 on 7/16/2016.
 */
public class CCBakedModelBuilder {

    private LinkedList<CCBakedQuad> quads = new LinkedList<CCBakedQuad>();

    public void addQuad(Vector3 pos, UV tex, Vector3 normal, int colour, int lightMap) {
        addQuad(new Vertex5(pos, tex), normal, colour, lightMap);
    }

    public void addQuad(Vertex5 vertex, Vector3 normal, int colour, int lightMap) {
        CCBakedQuad quad = new CCBakedQuad(vertex, normal, colour, lightMap);
        quads.add(quad);
    }

    public CCBakedModel build() {
        return new CCBakedModel(quads.toArray(new CCBakedQuad[quads.size()]));

    }

}
