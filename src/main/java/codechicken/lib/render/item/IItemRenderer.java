package codechicken.lib.render.item;

import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.PerspectiveMapWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface IItemRenderer extends IBakedModel {

    /**
     * Called to render your item with complete control. Bypasses all vanilla rendering of your model.
     *
     * @param stack         The {@link ItemStack} being rendered.
     * @param transformType The {@link TransformType} of where we are rendering.
     * @param mStack        The {@link MatrixStack} to get / add transformations to.
     * @param getter        The {@link IRenderTypeBuffer} to retrieve buffers from.
     * @param packedLight   The {@link LightTexture} packed coords.
     * @param packedOverlay The {@link OverlayTexture} packed coords.
     */
    void renderItem(ItemStack stack, TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay);

    /**
     * Gets a Map of {@link TransformType} to {@link TransformationMatrix} transformations.
     * See {@link TransformUtils}.
     *
     * @return The transforms.
     */
    ImmutableMap<TransformType, TransformationMatrix> getTransforms();

    /**
     * Called to handle this model's perspective. Either use {@link #getTransforms()}
     * Or add to the {@link MatrixStack} for the given {@link TransformType}.
     *
     * @param transformType Where we are handling perspective for.
     * @param mat           The {@link MatrixStack}.
     * @return The same model.
     */
    @Override
    default IBakedModel handlePerspective(TransformType transformType, MatrixStack mat) {
        return PerspectiveMapWrapper.handlePerspective(this, getTransforms(), transformType, mat);
    }

    //Useless methods for IItemRenderer.
    //@formatter:off
    @Override default boolean doesHandlePerspectives() { return true; }
    @Override default List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) { return Collections.emptyList(); }
    @Override default boolean isBuiltInRenderer() { return true; }
    @Override default TextureAtlasSprite getParticleTexture() { return TextureUtils.getMissingSprite(); }
    @Override default ItemOverrideList getOverrides() { return ItemOverrideList.EMPTY; }
    //@formatter:on
}
