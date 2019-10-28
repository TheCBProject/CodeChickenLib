package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (state == null && side == null) {
            return quads;
        }
        return Collections.emptyList();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }
}
