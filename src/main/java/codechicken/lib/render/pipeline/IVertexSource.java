package codechicken.lib.render.pipeline;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.attribute.AttributeKey;
import codechicken.lib.vec.Vertex5;

/**
 * Created by covers1624 on 10/10/2016.
 */
public interface IVertexSource {

    /**
     * The vertices for this model.
     *
     * @return The Vertices.
     */
    Vertex5[] getVertices();

    /**
     * Gets an array of vertex attributes
     *
     * @param attr The vertex attribute to get
     * @return An array, or null if not computed
     */
    <T> T getAttributes(AttributeKey<T> attr);

    /**
     * @return True if the specified attribute is provided by this model, either by returning an array from getAttributes or by setting the state in prepareVertex
     */
    boolean hasAttribute(AttributeKey<?> attr);

    /**
     * Called before the pipeline processes a vertex.
     *
     * @param ccrs The instance.
     */
    void prepareVertex(CCRenderState ccrs);
}
