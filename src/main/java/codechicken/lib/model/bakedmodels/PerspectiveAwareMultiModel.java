package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

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
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        if (baseModel != null) {
            quads.addAll(baseModel.getQuads(state, side, rand));
        }
        for (IBakedModel subModel : subModels) {
            quads.addAll(subModel.getQuads(state, side, rand));
        }
        return quads;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
