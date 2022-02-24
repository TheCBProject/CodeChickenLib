package codechicken.lib.render.item;

import codechicken.lib.texture.TextureUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.PerspectiveMapWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface IItemRenderer extends BakedModel {

    /**
     * Called to render your item with complete control. Bypasses all vanilla rendering of your model.
     *
     * @param stack         The {@link ItemStack} being rendered.
     * @param transformType The {@link TransformType} of where we are rendering.
     * @param mStack        The {@link PoseStack} to get / add transformations to.
     * @param source        The {@link MultiBufferSource} to retrieve buffers from.
     * @param packedLight   The {@link LightTexture} packed coords.
     * @param packedOverlay The {@link OverlayTexture} packed coords.
     */
    void renderItem(ItemStack stack, TransformType transformType, PoseStack mStack, MultiBufferSource source, int packedLight, int packedOverlay);

    /**
     * Gets the {@link ModelState} for this model used to describe
     * the transformations of this Model in various states.
     *
     * @return The transforms.
     */
    ModelState getModelTransform();

    /**
     * Called to handle this model's perspective. Either use {@link #getModelTransform()}
     * Or add to the {@link PoseStack} for the given {@link TransformType}.
     *
     * @param transformType Where we are handling perspective for.
     * @param mat           The {@link PoseStack}.
     * @return The same model.
     */
    @Override
    default BakedModel handlePerspective(TransformType transformType, PoseStack mat) {
        return PerspectiveMapWrapper.handlePerspective(this, getModelTransform(), transformType, mat);
    }

    //Useless methods for IItemRenderer.
    //@formatter:off
    @Override default boolean doesHandlePerspectives() { return true; }
    @Override default List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) { return Collections.emptyList(); }
    @Override default boolean isCustomRenderer() { return true; }
    @Override default TextureAtlasSprite getParticleIcon() { return TextureUtils.getMissingSprite(); }
    @Override default ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
    //@formatter:on
}
