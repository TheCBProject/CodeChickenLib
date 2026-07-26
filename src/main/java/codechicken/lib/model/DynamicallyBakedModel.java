package codechicken.lib.model;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple cache around some dynamically loaded model.
 * <p>
 * Created by covers1624 on 7/26/26.
 */
public abstract class DynamicallyBakedModel<S> extends DynamicModel {

    private final Map<S, List<BlockModelPart>> parts = new ConcurrentHashMap<>();

    public DynamicallyBakedModel(Identifier identifier) {
        super(identifier);
    }

    /**
     * Collect this models parts.
     *
     * @param state  The model state.
     * @param output The parts to add to.
     */
    public final void collect(S state, List<BlockModelPart> output) {
        output.addAll(parts.computeIfAbsent(state, this::bake));
    }

    /**
     * Called to bake your model into parts.
     *
     * @param state The state.
     * @return The baked parts.
     */
    protected abstract List<BlockModelPart> bake(S state);

    @Override
    protected void clear() {
        super.clear();
        parts.clear();
    }
}
