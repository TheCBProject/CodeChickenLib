package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 25/11/2016.
 */
public class PerspectiveAwareBakedModel extends AbstractBakedPropertiesModel {

    private final ImmutableMap<EnumFacing, List<BakedQuad>> faceQuads;
    private final ImmutableList<BakedQuad> generalQuads;

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, IModelState state, ModelProperties properties) {
        this(faceQuads, ImmutableList.of(), state, properties);
    }

    public PerspectiveAwareBakedModel(List<BakedQuad> generalQuads, IModelState state, ModelProperties properties) {
        this(ImmutableMap.of(), generalQuads, state, properties);
    }

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, List<BakedQuad> generalQuads, IModelState state, ModelProperties properties) {
        this(faceQuads, generalQuads, new PerspectiveProperties(state, properties));
    }

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, PerspectiveProperties properties) {
        this(faceQuads, ImmutableList.of(), properties);
    }

    public PerspectiveAwareBakedModel(List<BakedQuad> generalQuads, PerspectiveProperties properties) {
        this(ImmutableMap.of(), generalQuads, properties);
    }

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, List<BakedQuad> generalQuads, PerspectiveProperties properties) {
        super(properties);
        this.faceQuads = ImmutableMap.copyOf(faceQuads);
        this.generalQuads = ImmutableList.copyOf(generalQuads);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side == null) {
            return generalQuads;
        } else {
            if (faceQuads.containsKey(side)) {
                return faceQuads.get(side);
            }
        }
        return ImmutableList.of();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
