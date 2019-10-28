package codechicken.lib.render.item;

import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * GL ItemRendering!
 * See {@link CCRenderItem} for how this is fired.
 */
public interface IItemRenderer extends IBakedModel {

    /**
     * Used to render an item with GL access!
     * Custom transforms can be applied by implementing handlePerspective and calling {@link PerspectiveMapWrapper#handlePerspective}.
     * All your use cases for transforms should be handled by implementing getTransforms, handlePerspective is defaulted to use that.
     * Overriding handlePerspective you WILL need to call CCRenderItem.notifyTransform, this by default is done for you otherwise.
     *
     * @param stack         Stack to render.
     * @param transformType The TransformType we are rendering with. Use this for TransformType dependant rendering!
     */
    void renderItem(ItemStack stack, TransformType transformType);

    /**
     * Override this to do more custom transforms that cannot be done through getTransforms.
     * I HIGHLY suggest using getTransforms and just creating a static CCModelState and passing that trough.
     * You should be able to do all perspective aware transforms you need to through that.
     *
     * @param cameraTransformType The transform type.
     * @return The IBakedModel to actually render and the Matrix4 to apply as transforms.
     */
    @Override
    default Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        CCRenderItem.notifyTransform(cameraTransformType);
        return PerspectiveMapWrapper.handlePerspective(this, getTransforms(), cameraTransformType);
    }

    /**
     * The default transforms to use. For custom more complicated things, override handlePerspective and ignore this.
     *
     * @return The IModelState for transforms.
     */
    IModelState getTransforms();

    @Override
    default List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return Collections.emptyList();
    }

    @Override
    default boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    default TextureAtlasSprite getParticleTexture() {
        return TextureUtils.getMissingSprite();
    }

    @Override
    default ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
