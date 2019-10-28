package codechicken.lib.render.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

/**
 * Created by covers1624 on 8/09/2016.
 */
public interface ICCBlockRenderer {

    /**
     * Called to evaluate weather this ICCBlockRenderer will be called for the specified BlockState.
     *
     * @param world      The world.
     * @param pos        The pos.
     * @param blockState The BlockState.
     * @return If you wish to render the BlockState.
     */
    boolean canHandle(IEnviromentBlockReader world, BlockPos pos, BlockState blockState);

    /**
     * Called to evaluate weather this ICCBlockRenderer will be called to handle
     * {@link #renderBrightness(BlockState, float)} for the given BlockState.
     *
     * @param state The state.
     * @return If you wish to render this block.
     */
    default boolean canHandleBrightness(BlockState state) {
        return false;
    }

    /**
     * Called to evaluate weather this ICCBlockRenderer will be called for the specified IFluidState.
     *
     * @param world      The world.
     * @param pos        The pos.
     * @param fluidState The IFluidState.
     * @return If you wish to render the IFluidState.
     */
    default boolean canHandle(IEnviromentBlockReader world, BlockPos pos, IFluidState fluidState) {
        return false;
    }

    /**
     * Exactly the same as {@link #renderBlock} Except you MUST use the provided sprite.
     *
     * @param world  World.
     * @param pos    Position.
     * @param state  Your state.
     * @param sprite The overriden sprite.
     * @param buffer The buffer.
     */
    @OnlyIn (Dist.CLIENT)
    default void handleRenderBlockDamage(IEnviromentBlockReader world, BlockPos pos, BlockState state, TextureAtlasSprite sprite, BufferBuilder buffer) {
    }

    /**
     * Called to render your block in world.
     * You MUST use the provided BufferBuilder.
     * THE BUFFER IS ALREADY DRAWING!
     * YOU MAY BE FIRED ON THE CHUNK BATCHING THREAD!
     *
     * @param world  World.
     * @param pos    Position.
     * @param state  Your state.
     * @param buffer The buffer.
     * @return If quads were added.
     */
    @OnlyIn (Dist.CLIENT)
    boolean renderBlock(IEnviromentBlockReader world, BlockPos pos, BlockState state, BufferBuilder buffer, Random random, IModelData modelData);

    /**
     * Called to render your fluid in world.
     * You MUST use the provided BufferBuilder.
     * THE BUFFER IS ALREADY DRAWING!
     * YOU MAY BE FIRED ON THE CHUNK BATCHING THREAD!
     *
     * @param world      The world.
     * @param pos        The pos.
     * @param fluidState Your state.
     * @param buffer     The Buffer.
     * @return If quads were added.
     */
    @OnlyIn (Dist.CLIENT)
    default boolean renderFluid(IEnviromentBlockReader world, BlockPos pos, IFluidState fluidState, BufferBuilder buffer) {
        return false;
    }

    /**
     * Called for misc entities holding or have blocks as part of their model.
     * IronGolems, Enderman, Mooshroom, Minecarts, TNT.
     *
     * @param state      State.
     * @param brightness Brightness.
     */
    @OnlyIn (Dist.CLIENT)
    default void renderBrightness(BlockState state, float brightness) {
    }
}
