package codechicken.lib.render.item;

import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper;
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
public interface IItemRenderer extends IBakedModel, IStackPerspectiveAwareModel {

    /**
     * Used to render an item with GL access!
     * Custom transforms can be applied by overriding handlePerspective and calling {@link MapWrapper#handlePerspective}.
     * By default, ItemBlocks use {@link TransformUtils#DEFAULT_BLOCK} and standard items use {@link TransformUtils#DEFAULT_ITEM}
     * Note the use of IStackPerspectiveAwareModel, In {@link CCRenderItem} we ALWAYS have an IStackPerspectiveAwareModel handle transforms,
     * before IPerspectiveAwareModel, So you WILL need to override the handlePerspective method here.
     *
     * @param stack         Stack to render.
     * @param transformType The TransformType we are rendering with. Use this for TransformType dependant rendering!
     */
    void renderItem(ItemStack stack, TransformType transformType);

    @Override
    default Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemStack stack, TransformType cameraTransformType) {
        IModelState state = stack.getItem() instanceof ItemBlock ? TransformUtils.DEFAULT_BLOCK : TransformUtils.DEFAULT_ITEM;
        return MapWrapper.handlePerspective(this, state, cameraTransformType);
    }

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
    default ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    default ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
