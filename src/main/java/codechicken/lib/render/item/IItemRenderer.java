package codechicken.lib.render.item;

import codechicken.lib.model.PerspectiveModel;
import codechicken.lib.texture.TextureUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public interface IItemRenderer extends PerspectiveModel {

    /**
     * Called to render your item with complete control. Bypasses all vanilla rendering of your model.
     *
     * @param stack         The {@link ItemStack} being rendered.
     * @param ctx           The {@link ItemDisplayContext} of where we are rendering.
     * @param mStack        The {@link PoseStack} to get / add transformations to.
     * @param source        The {@link MultiBufferSource} to retrieve buffers from.
     * @param packedLight   The {@link LightTexture} packed coords.
     * @param packedOverlay The {@link OverlayTexture} packed coords.
     */
    void renderItem(ItemStack stack, ItemDisplayContext ctx, PoseStack mStack, MultiBufferSource source, int packedLight, int packedOverlay);

    //Useless methods for IItemRenderer.
    //@formatter:off
    @Override default List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) { return Collections.emptyList(); }
    @Override default boolean isCustomRenderer() { return true; }
    @Override default TextureAtlasSprite getParticleIcon() { return TextureUtils.getMissingSprite(); }
    @Override default ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
    //@formatter:on
}
