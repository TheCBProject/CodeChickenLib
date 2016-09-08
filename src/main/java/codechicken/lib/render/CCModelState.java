package codechicken.lib.render;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * Created by covers1624 on 5/16/2016.
 * Same as a SimpleModelState except has a getter for the Transform map.
 */
public class CCModelState implements IModelState {

    private final ImmutableMap<TransformType, TRSRTransformation> map;
    private final Optional<TRSRTransformation> defaultTransform;

    public CCModelState(ImmutableMap<TransformType, TRSRTransformation> map) {
        this(map, Optional.<TRSRTransformation>absent());
    }

    public CCModelState(ImmutableMap<TransformType, TRSRTransformation> map, Optional<TRSRTransformation> defaultTransform) {
        this.map = map;
        this.defaultTransform = defaultTransform;
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
        if (!part.isPresent() || !(part.get() instanceof TransformType) || !map.containsKey(part.get())) {
            return defaultTransform;
        }
        return Optional.fromNullable(map.get(part.get()));
    }

    public ImmutableMap<TransformType, TRSRTransformation> getTransforms() {
        return MapWrapper.getTransforms(this);
    }
}
