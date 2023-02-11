package codechicken.lib.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * An abstract {@link Block} implementation providing lazy
 * addition of {@link BlockState} {@link Property Properties}
 * from any point during the {@link Block} constructor.
 * <p>
 * Created by covers1624 on 18/7/22.
 */
@ApiStatus.Experimental
public abstract class LazyStateBlock extends Block {

    private final Map<Property<?>, Comparable<?>> propMap = new LinkedHashMap<>();
    private boolean computedState = false;

    public LazyStateBlock(Properties props) {
        super(props);
    }

    /**
     * Adds a {@link BlockState} {@link Property} to the {@link Block}.
     * <p>
     * May be called any time during the {@link Block Block's} constructor.
     * <p>
     * May be called multiple times to replace the default value.
     *
     * @param prop     The property to add.
     * @param default_ The default value for this property.
     */
    protected final <T extends Comparable<T>, V extends T> void addProperty(Property<T> prop, V default_) {
        if (computedState) throw new IllegalStateException("State has already been computed.");

        propMap.put(prop, default_);
    }

    /**
     * Called when the default state is resolved as defined by registered properties.
     * <p>
     * May be used to alter the default state further.
     *
     * @param state The state.
     * @return The modified state.
     */
    protected BlockState processDefault(BlockState state) {
        return state;
    }

    @Override
    public StateDefinition<Block, BlockState> getStateDefinition() {
        computeState();
        return super.getStateDefinition();
    }

    @Override
    public BlockState defaultBlockState() {
        computeState();
        return super.defaultBlockState();
    }

    private void computeState() {
        if (computedState) return;

        BlockState defaultState;
        // Don't compute a new state container if we don't have any properties.
        if (!propMap.isEmpty()) {
            StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
            propMap.keySet().forEach(builder::add);
            stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
            defaultState = stateDefinition.any();

            for (Map.Entry<Property<?>, Comparable<?>> entry : propMap.entrySet()) {
                defaultState = defaultState.setValue(entry.getKey(), unsafeCast(entry.getValue()));
            }
        } else {
            defaultState = stateDefinition.any();
        }

        registerDefaultState(processDefault(defaultState));
        computedState = true;
        propMap.clear();
    }

    @Override
    protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Explicitly disallowed.
    }
}
