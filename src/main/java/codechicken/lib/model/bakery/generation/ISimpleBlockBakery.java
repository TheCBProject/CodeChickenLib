package codechicken.lib.model.bakery.generation;

import net.minecraft.client.renderer.block.model.BakedQuad;
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
public interface ISimpleBlockBakery extends IBlockBakery {

    /**
     * Used to actually generate quads fro your block. Using this interface it is assumed that you only wish to render on one specific layer.
     * If you want to render on multiple layers use {@link ILayeredBlockBakery}
     *
     * Face may be null!!
     * Treat a null face as "general" quads, Quads that will NOT be culled by neighboring blocks.
     *
     * You will be requested for "general" AND face quads.
     *
     * @param face  The face quads are requested for.
     * @param state The IExtendedBlockState of your block. {@link IBlockBakery#handleState(IExtendedBlockState, IBlockAccess, BlockPos)} has already been called.
     * @return The quads for the face, May be an empty list. Never null.
     */
    @SideOnly (Side.CLIENT)
    @Nonnull
    List<BakedQuad> bakeQuads(@Nullable EnumFacing face, IExtendedBlockState state);
}
