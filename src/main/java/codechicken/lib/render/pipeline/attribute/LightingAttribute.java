package codechicken.lib.render.pipeline.attribute;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.VertexAttribute;
import org.jetbrains.annotations.Nullable;

/**
 * Created by covers1624 on 10/10/2016.
 */
public class LightingAttribute extends VertexAttribute<int[]> {

    public static final AttributeKey<int[]> attributeKey = AttributeKey.create("lighting", int[]::new);

    private int @Nullable [] colourRef;

    public LightingAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        assert ccrs.cFmt != null;
        assert ccrs.model != null;

        if (!ccrs.computeLighting || !ccrs.cFmt.hasColor || !ccrs.model.hasAttribute(attributeKey)) {
            return false;
        }

        colourRef = ccrs.model.getAttribute(attributeKey);
        if (colourRef != null) {
            ccrs.pipeline.addDependency(ccrs.colourAttrib);
            return true;
        }
        return false;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        assert colourRef != null;
        ccrs.colour = ColourRGBA.multiply(ccrs.colour, colourRef[ccrs.vertexIndex]);
    }
}
