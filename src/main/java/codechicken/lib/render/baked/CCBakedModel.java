package codechicken.lib.render.baked;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.EnumDrawMode;
import codechicken.lib.render.uv.UVTransformation;
import codechicken.lib.vec.Transformation;
import net.minecraft.client.renderer.vertex.VertexFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by covers1624 on 6/26/2016.
 */
public class CCBakedModel {
    private final VertexFormat format;
    private final EnumDrawMode drawMode;
    private final CCBakedQuad[] quads;

    public CCBakedModel(CCBakedQuad... quads) {
        this.quads = quads;
        this.format = this.quads[0].getFormat();
        this.drawMode = this.quads[0].getDrawMode();
    }

    public void render(IBakedVertexOperation... transformations) {
        render(0, quads.length, transformations);
    }

    public void render(int start, int end, IBakedVertexOperation... transformations) {
        boolean shouldStop = false;
        if (!CCRenderState.isDrawing()) {
            shouldStop = true;
            CCRenderState.startDrawing(drawMode.getDrawMode(), format);//Start drawing if we aren't already.
        }

        CCRenderState.setVertexRange(start, end);
        for (CCRenderState.vertexIndex = CCRenderState.firstVertexIndex; CCRenderState.vertexIndex < CCRenderState.lastVertexIndex; CCRenderState.vertexIndex++) {
            CCBakedQuad quad = quads[CCRenderState.vertexIndex];
            CCRenderState.vert.set(quad.getVertex());
            CCRenderState.normal.set(quad.getNormal());
            CCRenderState.normalActive = true;//TODO, why.. Should just assume there is normals.
            CCRenderState.colour = quad.getColour();
            CCRenderState.brightness = quad.getLightMap();
            for (IBakedVertexOperation transform : transformations) {
                transform.operateBaked();
            }
            CCRenderState.writeVert();
        }

        if (shouldStop) {
            CCRenderState.draw();
        }
    }

    public CCBakedModel apply(Transformation t) {
        for (CCBakedQuad quad : quads) {
            quad.getVertex().apply(t);
            if (quad.getNormal() != null) {
                t.applyN(quad.getNormal());
            }
        }
        return this;
    }

    public CCBakedModel apply(UVTransformation t) {
        for (CCBakedQuad quad : quads) {
            quad.getVertex().apply(t);
        }
        return this;
    }

    public static CCBakedModel combine(CCBakedModel... models) {
        ArrayList<CCBakedQuad[]> quadArrays = new ArrayList<CCBakedQuad[]>();
        for (CCBakedModel model : models) {
            quadArrays.add(model.quads);
        }
        CCBakedQuad[] combinedQuads = combineQuads(quadArrays.toArray(new CCBakedQuad[][] {}));
        if (!validateQuads(combinedQuads)) {
            throw new RuntimeException("Unable to combine quads across VertexFormats!");
        }
        return new CCBakedModel(combinedQuads);
    }

    private static CCBakedQuad[] combineQuads(CCBakedQuad[]... quadArrays) {
        LinkedList<CCBakedQuad> quads = new LinkedList<CCBakedQuad>();
        for (CCBakedQuad[] quadArray : quadArrays) {
            Collections.addAll(quads, quadArray);
        }
        return quads.toArray(new CCBakedQuad[quads.size()]);
    }

    public static boolean validateQuads(CCBakedQuad... quads) {
        return validateQuads(quads[0].getFormat(), quads[0].getDrawMode(), quads);
    }

    public static boolean validateQuads(VertexFormat format, EnumDrawMode drawMode, CCBakedQuad... quads) {
        for (CCBakedQuad quad : quads) {
            if (!format.equals(quad.getFormat()) || !drawMode.equals(quad.getDrawMode())) {
                return false;
            }
        }
        return true;
    }

}
