package codechicken.lib.render.pipeline.attribute;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.VertexAttribute;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;

/**
 * Apples normals to the render operation. If the model is a planar model it uses known normals.
 */
public class NormalAttribute extends VertexAttribute<Vector3[]> {

    public static final AttributeKey<Vector3[]> attributeKey = AttributeKey.create("normal", Vector3[]::new);

    private Vector3[] normalRef;

    public NormalAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        normalRef = ccrs.model.getAttribute(attributeKey);
        if (ccrs.model.hasAttribute(attributeKey)) {
            return normalRef != null;
        }

        if (ccrs.model.hasAttribute(SideAttribute.attributeKey)) {
            ccrs.pipeline.addDependency(ccrs.sideAttrib);
            return true;
        }
        throw new IllegalStateException("Normals requested but neither normal or side attrutes are provided by the model");
    }

    @Override
    public void operate(CCRenderState ccrs) {
        if (normalRef != null) {
            ccrs.normal.set(normalRef[ccrs.vertexIndex]);
        } else {
            ccrs.normal.set(Rotation.axes[ccrs.side]);
        }
    }
}
