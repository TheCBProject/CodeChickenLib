package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 19/11/2016.
 */
@Deprecated // These may go away unless there is sufficient need for generic implementations like these.
public class PerspectiveAwareLayeredModel extends AbstractBakedPropertiesModel {

    private final ImmutableMap<RenderType, List<BakedQuad>> unculledQuads;
    private final ImmutableMap<RenderType, Map<Direction, List<BakedQuad>>> faceQuads;
    private final RenderType generallayer;
    private final ChunkRenderTypeSet chunkLayers;

    public PerspectiveAwareLayeredModel(Map<RenderType, Map<Direction, List<BakedQuad>>> faceQuads, PerspectiveProperties properties) {
        this(faceQuads, ImmutableMap.of(), properties, RenderType.solid());
    }

    public PerspectiveAwareLayeredModel(Map<RenderType, Map<Direction, List<BakedQuad>>> faceQuads, Map<RenderType, List<BakedQuad>> unculledQuads, PerspectiveProperties properties, RenderType generallayer) {
        super(properties);
        this.faceQuads = ImmutableMap.copyOf(faceQuads);
        this.unculledQuads = ImmutableMap.copyOf(unculledQuads);
        this.generallayer = generallayer;
        chunkLayers = ChunkRenderTypeSet.union(ChunkRenderTypeSet.of(faceQuads.keySet()), ChunkRenderTypeSet.of(unculledQuads.keySet()));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return getQuads(state, side, rand, ModelData.EMPTY, null);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType layer) {
        if (layer == null) {
            layer = generallayer;
        }
        if (side == null) {
            return unculledQuads.getOrDefault(layer, List.of());
        }
        Map<Direction, List<BakedQuad>> faceQuadMap = faceQuads.get(layer);
        if (faceQuadMap != null) {
            return faceQuadMap.getOrDefault(side, List.of());
        }
        return List.of();
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return chunkLayers;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
