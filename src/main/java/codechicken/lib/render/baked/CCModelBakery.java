package codechicken.lib.render.baked;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.Vertex5;

/**
 * Created by covers1624 on 7/16/2016.
 */
public class CCModelBakery {

    /**
     * Uses the CCL RenderPipeline to bake a model.
     *
     * @param model Model to bake.
     * @return Baked Model.
     */
    @Deprecated//TODO make this not use CCRenderState to bake.
    public static CCBakedModel bakeModel(CCModel model) {
        CCRenderState.reset();
        System.out.println("Baking Model!");
        CCBakedModelBuilder builder = new CCBakedModelBuilder();

        CCRenderState.setPipeline(model, 0, model.verts.length);
        Vertex5[] verts = model.getVertices();
        for (CCRenderState.vertexIndex = CCRenderState.firstVertexIndex; CCRenderState.vertexIndex < CCRenderState.lastVertexIndex; CCRenderState.vertexIndex++) {
            model.prepareVertex();
            CCRenderState.vert.set(verts[CCRenderState.vertexIndex]);
            CCRenderState.runPipeline();
            builder.addQuad(CCRenderState.vert.copy(), CCRenderState.normal.copy(), CCRenderState.colour, CCRenderState.brightness);
        }
        return builder.build();
    }

}
