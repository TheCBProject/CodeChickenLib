package codechicken.lib.render.pipeline;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.attribute.AttributeKey;
import codechicken.lib.vec.Vertex5;
import org.jetbrains.annotations.Nullable;

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
     * Gets an attribute from this {@link IVertexSource}.
     *
     * @param attr The vertex attribute to get
     * @return The {@code T} for the given {@link AttributeKey}
     * or {@code null} of the Attribute does not exist.
     */
    @Nullable <T> T getAttribute(AttributeKey<T> attr);

    /**
     * Returns {@code true} if the specified attribute is provided by this {@link IVertexSource}.
     * <p>
     * The {@link IVertexSource} will either return data from {@link #getAttribute(AttributeKey)}
     * or set the state in {@link #prepareVertex(CCRenderState)}.
     *
     * @return {@code true} if the attribute exists.
     */
    boolean hasAttribute(AttributeKey<?> attr);

    /**
     * Called before the pipeline processes a vertex.
     *
     * @param ccrs The instance.
     */
    void prepareVertex(CCRenderState ccrs);
}
