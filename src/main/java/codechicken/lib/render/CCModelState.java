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

    private final ImmutableMap<? extends IModelPart, TRSRTransformation> map;
    private final Optional<TRSRTransformation> def;

    public CCModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map) {
        this(map, Optional.<TRSRTransformation>absent());
    }

    public CCModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map, Optional<TRSRTransformation> def) {
        this.map = map;
        this.def = def;
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
        if (!part.isPresent() || !map.containsKey(part.get())) {
            return def;
        }
        return Optional.fromNullable(map.get(part.get()));
    }

    public ImmutableMap<? extends IModelPart, TRSRTransformation> getMap() {
        return this.map;
    }

    public ImmutableMap<TransformType, TRSRTransformation> getTransforms() {
        return MapWrapper.getTransforms(this);
    }
}
