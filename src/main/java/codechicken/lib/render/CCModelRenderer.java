package codechicken.lib.render;

import codechicken.lib.render.uv.UVTransformation;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

/**
 * Created by covers1624 on 4/13/2016.
 * <p/>
 * Used as a temporary hack to go around all the render things.
 */
public class CCModelRenderer {

    public static void renderModel(CCModel model, UVTransformation transform){

    }

    public static void renderModel(CCModel model, Matrix4 transformMatrix) {
        transformMatrix.glApply();
        renderModel(model);
    }

    /**
     * Auto calculates the best Default vertex format to use then renders the model.
     *
     * @param model CCModel to render.
     */
    public static void renderModel(CCModel model) {
        Vector3[] normals = model.normals();
        boolean hasNormals = normals != null;

        model.assignVertexFormat();

        VertexBuffer buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(model.vertexMode, model.vertexFormat);

        for (int i = 0; i < model.verts.length; i++) {
            Vertex5 vertex = model.verts[i];
            buffer.pos(vertex.vec.x, vertex.vec.y, vertex.vec.z);
            buffer.tex(vertex.uv.u, vertex.uv.v);
            if (hasNormals) {
                Vector3 normal = normals[i];
                buffer.normal((float) normal.x, (float) normal.y, (float) normal.z);
            }
            buffer.endVertex();
        }
        Tessellator.getInstance().draw();
    }

}
