package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by covers1624 on 16/12/2016.
 */
public class PerspectiveAwareMultiModel extends AbstractBakedPropertiesModel {

    private final BakedModel baseModel;
    private final List<BakedModel> subModels;

    public PerspectiveAwareMultiModel(BakedModel baseModel, List<BakedModel> subModels, PerspectiveProperties properties) {
        super(properties);
        this.baseModel = baseModel;
        this.subModels = subModels;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
        List<BakedQuad> quads = new LinkedList<>();
        if (baseModel != null) {
            quads.addAll(baseModel.getQuads(state, side, rand, data));
        }
        for (BakedModel subModel : subModels) {
            quads.addAll(subModel.getQuads(state, side, rand, data));
        }
        return quads;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
