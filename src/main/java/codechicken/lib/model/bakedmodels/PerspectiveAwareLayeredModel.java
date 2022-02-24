package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by covers1624 on 19/11/2016.
 */
public class PerspectiveAwareLayeredModel extends AbstractPerspectiveLayeredModel {

    private final ImmutableMap<RenderType, Map<Direction, List<BakedQuad>>> layerFaceQuadMap;
    private final ImmutableMap<RenderType, List<BakedQuad>> layerGeneralQuads;

    public PerspectiveAwareLayeredModel(Map<RenderType, Map<Direction, List<BakedQuad>>> layerFaceQuadMap, PerspectiveProperties properties) {
        this(layerFaceQuadMap, ImmutableMap.of(), properties, RenderType.solid());
    }

    public PerspectiveAwareLayeredModel(Map<RenderType, Map<Direction, List<BakedQuad>>> layerFaceQuadMap, Map<RenderType, List<BakedQuad>> layerGeneralQuads, PerspectiveProperties properties, RenderType generallayer) {
        super(properties, generallayer);
        this.layerFaceQuadMap = ImmutableMap.copyOf(layerFaceQuadMap);
        this.layerGeneralQuads = ImmutableMap.copyOf(layerGeneralQuads);
    }

    @Override
    public List<BakedQuad> getLayerQuads(BlockState state, Direction side, RenderType layer, Random rand, IModelData data) {
        if (side == null) {
            if (layerGeneralQuads.containsKey(layer)) {
                return layerGeneralQuads.get(layer);
            }
        } else if (layerFaceQuadMap.containsKey(layer)) {
            Map<Direction, List<BakedQuad>> faceQuadMap = layerFaceQuadMap.get(layer);
            if (faceQuadMap.containsKey(side)) {
                return faceQuadMap.get(side);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
