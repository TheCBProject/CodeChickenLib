package codechicken.lib.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Created by covers1624 on 22/7/22.
 */
@ApiStatus.Experimental
public class DirectionComponent extends PropertyComponent<Direction> {

    public static PlacementFunc NONE = (state, ctx) -> null;
    public static PlacementFunc PLAYER_HORIZONTAL = (state, ctx) -> ctx.getHorizontalDirection();
    public static PlacementFunc PLAYER_LOOKING_NEAREST = (state, ctx) -> ctx.getNearestLookingDirection().getOpposite();
    public static PlacementFunc FACE_NORMAL = (state, ctx) -> ctx.getClickedFace();

    private final boolean horizontal;
    private PlacementFunc placementFunc;

    public DirectionComponent(boolean horizontal) {
        super(horizontal ? HORIZONTAL_FACING : FACING, Direction.NORTH);
        this.horizontal = horizontal;
        placementFunc = horizontal ? PLAYER_HORIZONTAL : PLAYER_LOOKING_NEAREST;
    }

    public DirectionComponent withPlacement(PlacementFunc func) {
        placementFunc = func;
        return this;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockState state, BlockPlaceContext ctx) {
        if (placementFunc == NONE) return state;

        Direction dir = placementFunc.apply(state, ctx);
        if (dir == null) return state;

        return state.setValue(property, dir);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        return state.setValue(property, rotation.rotate(state.getValue(property)));
    }

    public interface PlacementFunc {

        @Nullable
        Direction apply(BlockState state, BlockPlaceContext ctx);
    }
}
