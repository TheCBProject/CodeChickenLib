package codechicken.lib.render.baked;

import codechicken.lib.colour.Colour;
import codechicken.lib.render.EnumDrawMode;
import codechicken.lib.render.Vertex5;
import codechicken.lib.render.uv.UV;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

import java.util.LinkedList;

/**
 * Created by covers1624 on 7/16/2016.
 */
public class CCBakedModelBuilder {

    //Default is blocks if you don't set it.
    private VertexFormat format = DefaultVertexFormats.BLOCK;
    //Default is quads.
    private EnumDrawMode drawMode = EnumDrawMode.QUADS;
    private LinkedList<CCBakedQuad> quads = new LinkedList<CCBakedQuad>();

    public CCBakedModelBuilder addQuad(Vector3 pos, UV tex, Vector3 normal, Colour colour, int lightMap) {
        return addQuad(new Vertex5(pos, tex), normal, colour, lightMap);
    }

    public CCBakedModelBuilder addQuad(Vertex5 vertex, Vector3 normal, Colour colour, int lightMap) {
        CCBakedQuad quad = new CCBakedQuad(format, drawMode, vertex, normal, colour, lightMap);
        quads.add(quad);
        return this;
    }

    public CCBakedModelBuilder setFormat(VertexFormat format) {
        if (!quads.isEmpty()) {
            throw new RuntimeException("Unable to set format on already baked quads!");
        }
        this.format = format;
        return this;
    }

    public CCBakedModelBuilder setDrawMode(EnumDrawMode drawMode){
        if (!quads.isEmpty()){
            throw new RuntimeException("Unable to set DrawMode on already baked quads!");
        }
        this.drawMode = drawMode;
        return this;
    }

    public CCBakedModel build() {
        return new CCBakedModel(quads.toArray(new CCBakedQuad[quads.size()]));

    }

}
