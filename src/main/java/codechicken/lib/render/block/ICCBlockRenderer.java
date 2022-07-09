package codechicken.lib.render.block;

import codechicken.lib.render.CCRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

/**
 * Capable of rendering Blocks and Fluids directly to the chunk buffer.
 * <p>
 * Created by covers1624 on 8/09/2016.
 *
 * @see BlockRenderingRegistry
 */
public interface ICCBlockRenderer {

    //region Block

    /**
     * Called to evaluate weather this ICCBlockRenderer will be called for the specified BlockState.
     *
     * @param world      The world.
     * @param pos        The pos.
     * @param blockState The BlockState.
     * @param renderType The {@link RenderType}, {@code null} for breaking.
     * @return If you wish to render the BlockState.
     */
    boolean canHandleBlock(BlockAndTintGetter world, BlockPos pos, BlockState blockState, @Nullable RenderType renderType);

    /**
     * Called to render your block in world.
     * You MUST use the provided {@link VertexConsumer}.
     * THE BUFFER IS ALREADY DRAWING!
     * YOU MAY BE FIRED ON THE CHUNK BATCHING THREAD!
     *
     * @param state      Your state.
     * @param pos        Position.
     * @param world      World.
     * @param mStack     The {@link PoseStack}.
     * @param builder    The {@link VertexConsumer} to add quads to.
     * @param random     Position seeded Random for this block position.
     * @param data       Any ModelData.
     * @param renderType The {@link RenderType}, {@code null} for breaking.
     */
    void renderBlock(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack mStack, VertexConsumer builder, RandomSource random, ModelData data, @Nullable RenderType renderType);

    /**
     * Called when vanilla is rendering breaking texture over your block.
     * Usually just render out your normal quads.
     *
     * @param state   Your state.
     * @param pos     Position.
     * @param world   World.
     * @param mStack  The {@link PoseStack}.
     * @param builder The {@link VertexConsumer} to add quads to.
     * @param data    Any ModelData.
     */
    default void renderBreaking(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack mStack, VertexConsumer builder, ModelData data) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.overlay = OverlayTexture.NO_OVERLAY;
        ccrs.brightness = LevelRenderer.getLightColor(world, state, pos);
        mStack.pushPose();
        renderBlock(state, pos, world, mStack, builder, RandomSource.create(), data, null);
        mStack.popPose();
    }
    //endregion

    //region Rendering from an entity, Minecarts, enderman, etc.

    /**
     * Called to evaluate weather this ICCBlockRenderer will be called to handle
     * {@link #renderEntity} for the given BlockState.
     *
     * @param state The state.
     * @return If you wish to render this block.
     */
    default boolean canHandleEntity(BlockState state) {
        return false;
    }

    /**
     * Called for misc entities holding or have blocks as part of their model.
     * IronGolems, Enderman, Mooshroom, Minecarts, TNT.
     *
     * @param state         The BlockState to render.
     * @param nStack        The {@link PoseStack}.
     * @param builder       The {@link MultiBufferSource}
     * @param packedLight   The {@link LightTexture} packed coords.
     * @param packedOverlay The {@link OverlayTexture} packed coords.
     * @param data          Any ModelData.
     * @param renderType    The {@link RenderType} may be {@code null}.
     */
    default void renderEntity(BlockState state, PoseStack nStack, MultiBufferSource builder, int packedLight, int packedOverlay, ModelData data, @Nullable RenderType renderType) { }
    //endregion

    //region Fluids

    /**
     * Called to evaluate weather this ICCBlockRenderer will be called for the specified IFluidState.
     *
     * @param world      The world.
     * @param pos        The pos.
     * @param blockState The {@link BlockState}
     * @param fluidState The {@link FluidState}.
     * @return If you wish to render the {@link FluidState}.
     */
    default boolean canHandleFluid(BlockAndTintGetter world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        return false;
    }

    /**
     * Called to render your fluid in world.
     * You MUST use the provided {@link VertexConsumer}.
     * THE BUFFER IS ALREADY DRAWING!
     * YOU MAY BE FIRED ON THE CHUNK BATCHING THREAD!
     *
     * @param pos        Position.
     * @param world      World.
     * @param builder    The {@link VertexConsumer}.
     * @param blockState The {@link BlockState}
     * @param fluidState The {@link FluidState}
     */
    default void renderFluid(BlockPos pos, BlockAndTintGetter world, VertexConsumer builder, BlockState blockState, FluidState fluidState) { }
    //endregion
}
