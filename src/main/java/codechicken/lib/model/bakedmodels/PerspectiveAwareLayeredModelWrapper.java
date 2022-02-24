package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
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
public class PerspectiveAwareLayeredModelWrapper extends AbstractPerspectiveLayeredModel {

    private final ImmutableMap<RenderType, BakedModel> layerModelMap;

    public PerspectiveAwareLayeredModelWrapper(Map<RenderType, BakedModel> layerModelMap, PerspectiveProperties properties) {
        super(properties);
        this.layerModelMap = ImmutableMap.copyOf(layerModelMap);
    }

    @Override
    public List<BakedQuad> getLayerQuads(BlockState state, Direction side, RenderType layer, Random rand, IModelData data) {
        if (layerModelMap.containsKey(layer)) {
            return layerModelMap.get(layer).getQuads(state, side, rand);
        }
        return Collections.emptyList();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
