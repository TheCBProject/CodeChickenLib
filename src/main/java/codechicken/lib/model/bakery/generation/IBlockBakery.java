package codechicken.lib.model.bakery.generation;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface IBlockBakery extends IItemBakery {

    ChunkRenderTypeSet SOLID = ChunkRenderTypeSet.of(RenderType.solid());
    ChunkRenderTypeSet CUTOUT_MIPPED = ChunkRenderTypeSet.of(RenderType.cutoutMipped());
    ChunkRenderTypeSet CUTOUT = ChunkRenderTypeSet.of(RenderType.cutout());
    ChunkRenderTypeSet TRANSLUCENT = ChunkRenderTypeSet.of(RenderType.translucent());

    /**
     * Gets the layers to generate quads for.
     * <p>
     * Please cache these, they are expensive to construct.
     *
     * @return The {@link ChunkRenderTypeSet}.
     */
    ChunkRenderTypeSet getBlockRenderLayers();

    /**
     * Called to generate the quads for a given face, layer, state and model data.
     *
     * @param face  The face quads are requested for, {@code null} for un-culled.
     * @param layer The layer quads are requested for.
     * @param state The {@link BlockState} of your block
     * @param data  Any {@link ModelData} your block may have returned.
     * @return The quads for the layer or empty.
     */
    @OnlyIn (Dist.CLIENT)
    List<BakedQuad> bakeFace(@Nullable Direction face, RenderType layer, BlockState state, ModelData data);
}
