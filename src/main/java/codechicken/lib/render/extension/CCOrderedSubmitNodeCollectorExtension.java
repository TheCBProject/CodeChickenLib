package codechicken.lib.render.extension;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Matrix4;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Created by covers1624 on 11/2/25.
 */
public interface CCOrderedSubmitNodeCollectorExtension {

    private OrderedSubmitNodeCollector self() {
        return (OrderedSubmitNodeCollector) this;
    }

    /**
     * The same as {@link OrderedSubmitNodeCollector#submitCustomGeometry} however using a {@link Matrix4}.
     * <p>
     * The matrix is copied for your invocation and provided to your callback.
     *
     * @param mat        The matrix.
     * @param renderType The render type.
     * @param renderer   Your renderer callback.
     */
    default void cc$submitCustomGeometry(Matrix4 mat, RenderType renderType, Mat4CustomGeometryRenderer renderer) {
        var matCopy = mat.copy();
        self().submitCustomGeometry(Internal.EMPTY, renderType, (p, consumer) -> renderer.render(matCopy, renderType, consumer));
    }

    /**
     * Similar to {@link SubmitNodeCollector.CustomGeometryRenderer} except
     * carries a {@link Matrix4} instead of a {@link PoseStack.Pose}.
     */
    interface Mat4CustomGeometryRenderer {

        void render(Matrix4 mat, RenderType renderType, VertexConsumer cons);
    }

    /**
     * The same as {@link OrderedSubmitNodeCollector#submitCustomGeometry} except, additionally
     * provides you the {@link RenderType} you requested as a callback parameter.
     *
     * @param pStack     The pose stack.
     * @param renderType The render type.
     * @param renderer   Your renderer callback.
     */
    default void cc$submitCustomGeometry(PoseStack pStack, RenderType renderType, CustomGeometryRendererExtended renderer) {
        self().submitCustomGeometry(pStack, renderType, (pose, consumer) -> renderer.render(pose, renderType, consumer));
    }

    /**
     * Similar to {@link SubmitNodeCollector.CustomGeometryRenderer} except
     * additionally carries the {@link RenderType} for convenience.
     */
    interface CustomGeometryRendererExtended {

        void render(PoseStack.Pose pose, RenderType renderType, VertexConsumer cons);
    }

    /**
     * The same as {@link OrderedSubmitNodeCollector#submitCustomGeometry} however, provides
     * you a {@link CCRenderState} instance already reset and bound.
     *
     * @param mat        The matrix to capture.
     * @param renderType The render type.
     * @param renderer   Your render callback.
     */
    default void cc$submitCCRS(Matrix4 mat, RenderType renderType, CustomGeometryRendererCCRS renderer) {
        var matCopy = mat.copy();
        self().submitCustomGeometry(Internal.EMPTY, renderType, (p, consumer) -> {
            var ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(consumer, renderType.format());
            renderer.render(matCopy, ccrs);
        });
    }

    /**
     * The same as {@link OrderedSubmitNodeCollector#submitCustomGeometry} however, provides
     * you a {@link CCRenderState} instance already reset and bound.
     *
     * @param pose       The pose to capture.
     * @param renderType The render type.
     * @param renderer   Your render callback.
     */
    default void cc$submitCCRS(PoseStack pose, RenderType renderType, CustomGeometryRendererCCRS renderer) {
        var matCopy = new Matrix4(pose);
        self().submitCustomGeometry(Internal.EMPTY, renderType, (p, consumer) -> {
            var ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(consumer, renderType.format());
            renderer.render(matCopy, ccrs);
        });
    }

    interface CustomGeometryRendererCCRS {

        void render(Matrix4 mat, CCRenderState ccrs);
    }

    class Internal {

        private static final PoseStack EMPTY = new PoseStack();
    }
}
