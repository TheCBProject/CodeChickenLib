package codechicken.lib.model.bakery;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.CCRenderState.*;
import codechicken.lib.render.Vertex5;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

import java.util.List;

import static codechicken.lib.render.CCRenderState.*;

/**
 * Created by covers1624 on 8/20/2016.
 * Uses the CCL RenderPipe to bake a CCModel to an array of BakedQuads with the provided VertexFormat.
 */
public class CCModelBakery {

    /**
     * Bakes a CCModel to a List of BakedQuads.
     * Assumes DefaultVertexFormats.BLOCK.
     * Assumes you want to bake the entire model.
     *
     * @param model Model to bake.
     * @param sprite Sprite the quads are to be baked from.
     * @param ops Any Operations to apply.
     * @return The BakedQuads for the model.
     */
    public static List<BakedQuad> bakeModel(CCModel model, TextureAtlasSprite sprite, IVertexOperation... ops) {
        return bakeModel(model, sprite, 0, model.getVertices().length, ops);
    }

    /**
     * Bakes a CCModel to a List of BakedQuads.
     * Assumes DefaultVertexFormats.BLOCK.
     *
     * @param model Model to bake.
     * @param sprite Sprite the quads are to be baked from.
     * @param start The first vertex index to bake.
     * @param end The Vertex index to bake until.
     * @param ops Any Operations to apply.
     * @return The BakedQuads for the model.
     */
    public static List<BakedQuad> bakeModel(CCModel model, TextureAtlasSprite sprite, int start, int end, IVertexOperation... ops) {
        return bakeModel(model, DefaultVertexFormats.BLOCK, sprite, start, end, ops);
    }

    /**
     * Bakes a CCModel to a List of BakedQuads.
     * Assumes you want to bake the entire model.
     *
     * @param model  Model to bake.
     * @param format VertexFormat to bake to.
     * @param sprite Sprite the quads are to be baked from.
     * @param ops    Any Operations to apply.
     * @return The BakedQuads for the model.
     */
    public static List<BakedQuad> bakeModel(CCModel model, VertexFormat format, TextureAtlasSprite sprite, IVertexOperation... ops) {
        return bakeModel(model, format, sprite, 0, model.getVertices().length, ops);
    }

    /**
     * Bakes a CCModel to a List of BakedQuads.
     *
     * @param model  Model to bake.
     * @param format VertexFormat to bake to.
     * @param sprite Sprite the quads are to be baked from.
     * @param start  The first vertex index to bake.
     * @param end    The Vertex index to bake until.
     * @param ops    Any Operations to apply.
     * @return The BakedQuads for the model.
     */
    public static List<BakedQuad> bakeModel(CCModel model, VertexFormat format, TextureAtlasSprite sprite, int start, int end, IVertexOperation... ops) {
        //The face for quads is assigned down the line using the quads normals.
        CCQuadBakery bakery = new CCQuadBakery(format, sprite);
        if (model.vp == 3) {
            bakery.startBakingTriangles();
        } else {
            bakery.startBakingQuads();
        }
        CCRenderState.fmt = format;
        CCRenderState.setPipeline(model, start, end, ops);
        Vertex5[] verts = model.getVertices();
        for (vertexIndex = firstVertexIndex; vertexIndex < lastVertexIndex; vertexIndex++) {
            model.prepareVertex();
            vert.set(verts[vertexIndex]);
            runPipeline();
            Vertex5 vert = CCRenderState.vert;
            bakery.setColour(CCRenderState.colour);
            bakery.setLightMap(CCRenderState.brightness);
            bakery.setNormal(CCRenderState.normal);
            bakery.addVertexWithUV(vert);
        }
        return bakery.finishBaking();
    }
}
