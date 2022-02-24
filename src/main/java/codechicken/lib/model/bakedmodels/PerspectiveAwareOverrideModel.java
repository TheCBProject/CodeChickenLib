package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by covers1624 on 1/02/2017.
 */
public class PerspectiveAwareOverrideModel extends AbstractBakedPropertiesModel {

    private final ItemOverrides overrideList;
    private final List<BakedQuad> quads;

    public PerspectiveAwareOverrideModel(ItemOverrides overrideList, PerspectiveProperties properties) {
        this(overrideList, properties, new ArrayList<>());
    }

    public PerspectiveAwareOverrideModel(ItemOverrides overrideList, PerspectiveProperties properties, List<BakedQuad> quads) {
        super(properties);
        this.overrideList = overrideList;
        this.quads = quads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (state == null && side == null) {
            return quads;
        }
        return Collections.emptyList();
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }
}
