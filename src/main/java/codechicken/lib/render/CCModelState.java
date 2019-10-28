package codechicken.lib.render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Map;
import java.util.Optional;

/**
 * Created by covers1624 on 5/16/2016.
 * Same as a SimpleModelState except copy's the input map, saves BS when creating IModelStates.
 */
public class CCModelState implements IModelState, ISprite {

    private final ImmutableMap<TransformType, TRSRTransformation> map;
    private final Optional<TRSRTransformation> defaultTransform;

    public CCModelState(Map<TransformType, TRSRTransformation> map) {
        this(map, Optional.empty());
    }

    public CCModelState(Map<TransformType, TRSRTransformation> map, Optional<TRSRTransformation> defaultTransform) {
        this.map = ImmutableMap.copyOf(map);
        this.defaultTransform = defaultTransform;
    }

    @Override
    public IModelState getState() {
        return this;
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
        if (!part.isPresent() || !(part.get() instanceof TransformType) || !map.containsKey(part.get())) {
            return defaultTransform;
        }
        return Optional.ofNullable(map.get(part.get()));
    }
}
