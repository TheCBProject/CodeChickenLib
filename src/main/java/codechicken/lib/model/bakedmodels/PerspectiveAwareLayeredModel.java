package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 19/11/2016.
 */
public class PerspectiveAwareLayeredModel extends AbstractPerspectiveLayeredModel {

    private final ImmutableMap<BlockRenderLayer, Map<EnumFacing, List<BakedQuad>>> layerFaceQuadMap;
    private final ImmutableMap<BlockRenderLayer, List<BakedQuad>> layerGeneralQuads;

    public PerspectiveAwareLayeredModel(Map<BlockRenderLayer, Map<EnumFacing, List<BakedQuad>>> layerFaceQuadMap, PerspectiveProperties properties) {
        this(layerFaceQuadMap, ImmutableMap.of(), properties, BlockRenderLayer.SOLID);
    }

    public PerspectiveAwareLayeredModel(Map<BlockRenderLayer, Map<EnumFacing, List<BakedQuad>>> layerFaceQuadMap, Map<BlockRenderLayer, List<BakedQuad>> layerGeneralQuads, PerspectiveProperties properties, BlockRenderLayer generallayer) {
        super(properties, generallayer);
        this.layerFaceQuadMap = ImmutableMap.copyOf(layerFaceQuadMap);
        this.layerGeneralQuads = ImmutableMap.copyOf(layerGeneralQuads);
    }

    @Override
    public List<BakedQuad> getLayerQuads(@Nullable IBlockState state, @Nullable EnumFacing side, BlockRenderLayer layer, long rand) {
        if (side == null) {
            if (layerGeneralQuads.containsKey(layer)) {
                return layerGeneralQuads.get(layer);
            }
        } else if (layerFaceQuadMap.containsKey(layer)) {
            Map<EnumFacing, List<BakedQuad>> faceQuadMap = layerFaceQuadMap.get(layer);
            if (faceQuadMap.containsKey(side)) {
                return faceQuadMap.get(side);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
