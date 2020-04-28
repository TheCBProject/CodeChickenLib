package codechicken.lib.render.pipeline.attribute;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.VertexAttribute;
import codechicken.lib.util.VectorUtils;

/**
 * Sets the side state in CCRS based on the provided model. If the model does not have side data it requires normals.
 */
public class SideAttribute extends VertexAttribute<int[]> {

    public static final AttributeKey<int[]> attributeKey = new AttributeKey<int[]>("side", int[]::new);

    private int[] sideRef;

    public SideAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        sideRef = ccrs.model.getAttributes(attributeKey);
        if (ccrs.model.hasAttribute(attributeKey)) {
            return sideRef != null;
        }

        ccrs.pipeline.addDependency(ccrs.normalAttrib);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        if (sideRef != null) {
            ccrs.side = sideRef[ccrs.vertexIndex];
        } else {
            ccrs.side = VectorUtils.findSide(ccrs.normal);
        }
    }
}
