package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 25/11/2016.
 */
@Deprecated // These may go away unless there is sufficient need for generic implementations like these.
public class PerspectiveAwareBakedModel extends AbstractBakedPropertiesModel {

    private final Map<Direction, List<BakedQuad>> faceQuads;
    private final List<BakedQuad> generalQuads;

    public PerspectiveAwareBakedModel(Map<Direction, List<BakedQuad>> faceQuads, PerspectiveModelState transforms, ModelProperties properties) {
        this(faceQuads, ImmutableList.of(), transforms, properties);
    }

    public PerspectiveAwareBakedModel(List<BakedQuad> generalQuads, PerspectiveModelState transforms, ModelProperties properties) {
        this(ImmutableMap.of(), generalQuads, transforms, properties);
    }

    public PerspectiveAwareBakedModel(Map<Direction, List<BakedQuad>> faceQuads, List<BakedQuad> generalQuads, PerspectiveModelState transforms, ModelProperties properties) {
        this(faceQuads, generalQuads, properties.toBuilder().withTransforms(transforms).build());
    }

    public PerspectiveAwareBakedModel(Map<Direction, List<BakedQuad>> faceQuads, PerspectiveProperties properties) {
        this(faceQuads, ImmutableList.of(), properties);
    }

    public PerspectiveAwareBakedModel(List<BakedQuad> generalQuads, PerspectiveProperties properties) {
        this(ImmutableMap.of(), generalQuads, properties);
    }

    public PerspectiveAwareBakedModel(Map<Direction, List<BakedQuad>> faceQuads, List<BakedQuad> generalQuads, PerspectiveProperties properties) {
        super(properties);
        this.faceQuads = ImmutableMap.copyOf(faceQuads);
        this.generalQuads = ImmutableList.copyOf(generalQuads);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (side == null) return generalQuads;

        return faceQuads.getOrDefault(side, List.of());
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
