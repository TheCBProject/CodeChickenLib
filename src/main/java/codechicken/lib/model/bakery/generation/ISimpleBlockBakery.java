package codechicken.lib.model.bakery.generation;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface ISimpleBlockBakery extends IBlockBakery {

    /**
     * Used to actually generate quads for your block. Using this interface it is assumed that you only wish to render on one specific layer.
     * If you want to render on multiple layers use {@link ILayeredBlockBakery}
     * <p>
     * Face may be null!!
     * Treat a null face as "general" quads, Quads that will NOT be culled by neighboring blocks.
     * <p>
     * You will be requested for "general" AND face quads.
     *
     * @param face  The face quads are requested for.
     * @param state The IExtendedBlockState of your block. {@link IBlockBakery#handleState(IExtendedBlockState, IBlockAccess, BlockPos)} has already been called.
     * @return The quads for the face, May be an empty list. Never null.
     */
    @Nonnull
    @OnlyIn (Dist.CLIENT)
    List<BakedQuad> bakeQuads(@Nullable Direction face, BlockState state, IModelData data);
}
