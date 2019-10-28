package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by covers1624 on 16/12/2016.
 */
public class PerspectiveAwareMultiModel extends AbstractBakedPropertiesModel {

    private final IBakedModel baseModel;
    private final List<IBakedModel> subModels;

    public PerspectiveAwareMultiModel(IBakedModel baseModel, List<IBakedModel> subModels, PerspectiveProperties properties) {
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
        for (IBakedModel subModel : subModels) {
            quads.addAll(subModel.getQuads(state, side, rand, data));
        }
        return quads;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
