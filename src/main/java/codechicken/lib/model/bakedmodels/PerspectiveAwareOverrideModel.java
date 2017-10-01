package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 1/02/2017.
 */
public class PerspectiveAwareOverrideModel extends AbstractBakedPropertiesModel {

    private final ItemOverrideList overrideList;
    private final List<BakedQuad> quads;

    public PerspectiveAwareOverrideModel(ItemOverrideList overrideList, PerspectiveProperties properties) {
        this(overrideList, properties, new ArrayList<>());
    }

    public PerspectiveAwareOverrideModel(ItemOverrideList overrideList, PerspectiveProperties properties, List<BakedQuad> quads) {
        super(properties);
        this.overrideList = overrideList;
        this.quads = quads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null && side == null) {
            return quads;
        }
        return ImmutableList.of();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }
}
