package codechicken.lib.block;

import codechicken.lib.block.component.PropertyComponent;
import codechicken.lib.block.component.StateAwareComponent;
import codechicken.lib.block.component.data.DataGenComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * A block whose functionality can be extended via modular 'components'.
 * <p>
 * Created by covers1624 on 17/7/22.
 * @see Component
 * @see DataGenComponent
 * @see StateAwareComponent
 */
@ApiStatus.Experimental
public abstract class ModularBlock extends LazyStateBlock {

    protected final List<Component> componentList = new LinkedList<>();
    protected final List<DataGenComponent> datagenComponents = new LinkedList<>();
    protected final List<StateAwareComponent> stateComponents = new LinkedList<>();

    public ModularBlock(Properties props) {
        super(props);
    }

    public final <T extends Component> T addComponent(T comp) {
        comp.block = this;

        if (comp instanceof PropertyComponent<?> propComp) {
            addProperty(propComp.property, unsafeCast(propComp.defaultValue));
        }

        if (comp instanceof StateAwareComponent stateComp) {
            stateComponents.add(stateComp);
        }

        // DataGenComponents don't get added to the main component list.
        if (comp instanceof DataGenComponent dataComp) {
            // Only added if datagen is currently running.
            if (DatagenModLoader.isRunningDataGen()) {
                datagenComponents.add(dataComp);
            }
            return comp;
        }

        componentList.add(comp);
        return comp;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = defaultBlockState();
        for (StateAwareComponent component : stateComponents) {
            state = component.getStateForPlacement(state, ctx);
            if (state == null) break;
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        for (StateAwareComponent component : stateComponents) {
            state = component.rotate(state, level, pos, rotation);
        }
        return state;
    }

    public List<DataGenComponent> getDatagenComponents() { return Collections.unmodifiableList(datagenComponents); }

    public static abstract class Component {

        @Nullable
        ModularBlock block;

        public ModularBlock getBlock() {
            return Objects.requireNonNull(block, "Not yet added to a block.");
        }
    }

}
