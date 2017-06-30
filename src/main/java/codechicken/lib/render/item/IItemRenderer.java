package codechicken.lib.render.item;

import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;

/**
 * GL ItemRendering!
 * See {@link CCRenderItem} for how this is fired.
 */
public interface IItemRenderer extends IBakedModel {

    /**
     * Used to render an item with GL access!
     * Custom transforms can be applied by implementing handlePerspective and calling {@link PerspectiveMapWrapper#handlePerspective}.
     * All your use cases for transforms should be handled by implementing getTransforms, handlePerspective is defaulted to use that.
     *
     * @param stack         Stack to render.
     * @param transformType The TransformType we are rendering with. Use this for TransformType dependant rendering!
     */
    void renderItem(ItemStack stack, TransformType transformType);

    @Override
    default Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return PerspectiveMapWrapper.handlePerspective(this, getTransforms(), cameraTransformType);
    }

    /**
     * The default transforms to use. For custom more complicated things, override handlePerspective and ignore this.
     *
     * @return The IModelState for transforms.
     */
    IModelState getTransforms();

    @Override
    default List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
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
        return ItemOverrideList.NONE;
    }
}
