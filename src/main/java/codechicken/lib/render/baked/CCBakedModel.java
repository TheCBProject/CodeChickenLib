package codechicken.lib.render.baked;

import codechicken.lib.render.CCRenderState;

/**
 * Created by covers1624 on 6/26/2016.
 */
public class CCBakedModel {

    private final CCBakedQuad[] quads;

    public CCBakedModel(CCBakedQuad[] quads) {
        this.quads = quads;
    }

    public void render(IBakedVertexOperation... transformations) {
        render(0, quads.length, transformations);
    }

    public void render(int start, int end, IBakedVertexOperation... transformations) {
        CCRenderState.setVertexRange(start, end);
        for (CCRenderState.vertexIndex = CCRenderState.firstVertexIndex; CCRenderState.vertexIndex < CCRenderState.lastVertexIndex; CCRenderState.vertexIndex++) {
            CCBakedQuad quad = quads[CCRenderState.vertexIndex];
            CCRenderState.vert.set(quad.getVertex());
            CCRenderState.normal.set(quad.getNormal());
            CCRenderState.normalActive = true;
            CCRenderState.colour = quad.getColour();
            CCRenderState.brightness = quad.getLightMap();
            for (IBakedVertexOperation transform : transformations) {
                transform.operateBaked();
            }
            CCRenderState.writeVert();
        }
    }

}
