package codechicken.lib.render.pipeline.attribute;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.VertexAttribute;

/**
 * Created by covers1624 on 10/10/2016.
 */
public class LightingAttribute extends VertexAttribute<int[]> {

    public static final AttributeKey<int[]> attributeKey = new AttributeKey<>("lighting", int[]::new);

    private int[] colourRef;

    public LightingAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        if (!ccrs.computeLighting || !ccrs.fmt.hasColor() || !ccrs.model.hasAttribute(attributeKey)) {
            return false;
        }

        colourRef = ccrs.model.getAttributes(attributeKey);
        if (colourRef != null) {
            ccrs.pipeline.addDependency(ccrs.colourAttrib);
            return true;
        }
        return false;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        ccrs.colour = ColourRGBA.multiply(ccrs.colour, colourRef[ccrs.vertexIndex]);
    }
}
