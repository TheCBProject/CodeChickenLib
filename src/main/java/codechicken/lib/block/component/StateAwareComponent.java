package codechicken.lib.block.component;

import codechicken.lib.block.ModularBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Created by covers1624 on 22/7/22.
 */
@ApiStatus.Experimental
public abstract class StateAwareComponent extends ModularBlock.Component {

    @Nullable
    public BlockState getStateForPlacement(BlockState state, BlockPlaceContext ctx) {
        return state;
    }

    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        return state;
    }
}
