package codechicken.lib.model.bakery.generation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface ILayeredBlockBakery extends IBlockBakery {

    /**
     * Used to actually generate quads for your block based on the face and layer being requested.
     * Use {@link Block#canRenderInLayer(IBlockState, BlockRenderLayer)} to cull layers from this.
     * You will ONLY be requested for quads if canRenderInLayer returns true for the specific layer.
     *
     * Face may be null!!
     * Treat a null face as "general" quads, Quads that will NOT be culled by neighboring blocks.
     *
     * Each layer you agree to with canRenderInLayer will be requested for "general" AND face quads.
     *
     * @param face  The face quads are requested for.
     * @param layer The layer quads are requested for.
     * @param state The IExtendedBlockState of your block. {@link IBlockBakery#handleState(IExtendedBlockState, IBlockAccess, BlockPos)} has already been called.
     * @return The quads for the layer, May be an empty list. Never null.
     */
    @SideOnly (Side.CLIENT)
    @Nonnull
    List<BakedQuad> bakeLayerFace(@Nullable EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state);

}
