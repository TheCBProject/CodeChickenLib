package codechicken.lib.model;

import codechicken.lib.util.TransformUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * A simple {@link BakedModel} implementation, with automatic handling of
 * {@link PerspectiveModelState}s.
 * <p>
 * Created by covers1624 on 9/7/22.
 *
 * @see TransformUtils
 */
public interface PerspectiveModel extends BakedModel {

    /**
     * The {@link PerspectiveModelState} for this model.
     *
     * @return The state or {@code null} for vanilla behaviour.
     */
    @Nullable
    PerspectiveModelState getModelState();

    @Override
    default BakedModel applyTransform(ItemDisplayContext context, PoseStack pStack, boolean leftFlip) {
        PerspectiveModelState modelState = getModelState();
        if (modelState != null) {
            Transformation transform = getModelState().getTransform(context);

            Vector3f trans = transform.getTranslation();
            pStack.translate(trans.x(), trans.y(), trans.z());

            pStack.mulPose(transform.getLeftRotation());

            Vector3f scale = transform.getScale();
            pStack.scale(scale.x(), scale.y(), scale.z());

            pStack.mulPose(transform.getRightRotation());

            if (leftFlip) {
                TransformUtils.applyLeftyFlip(pStack);
            }
            return this;
        }
        return BakedModel.super.applyTransform(context, pStack, leftFlip);
    }
}
