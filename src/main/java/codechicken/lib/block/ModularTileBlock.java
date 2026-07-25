package codechicken.lib.block;

import codechicken.lib.block.ModularBlockEntity.DataComponent;
import net.covers1624.quack.util.LazyValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 18/7/22.
 */
@ApiStatus.Experimental
public abstract class ModularTileBlock<T extends ModularBlockEntity> extends ModularBlock implements EntityBlock {

    final Map<String, TileComponent<?>> namedComponents = new HashMap<>();
    private final Supplier<BlockEntityType<? extends T>> type;

    protected final TickList<T> clientTicks = new TickList<>();
    protected final TickList<T> serverTicks = new TickList<>();

    public ModularTileBlock(Properties props, Supplier<BlockEntityType<? extends T>> typeSupplier) {
        super(props);
        type = new LazyValue<>(typeSupplier);
    }

    public final <C extends TileComponent<?>> C addComponent(String name, C comp) {
        if (namedComponents.containsKey(name)) throw new IllegalArgumentException("DataComponent already exists with name:" + name);

        comp.name = name;
        comp.id = namedComponents.size();
        namedComponents.put(name, comp);
        return addComponent(comp);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return type.get().create(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings ("unchecked")
    public final <BE extends BlockEntity> BlockEntityTicker<BE> getTicker(Level level, BlockState state, BlockEntityType<BE> type) {
        if (type != this.type.get()) return null;

        if (level.isClientSide()) {
            return (BlockEntityTicker<BE>) clientTicks.compileTicker();
        }

        return (BlockEntityTicker<BE>) serverTicks.compileTicker();
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }

    public static abstract class TileComponent<D extends DataComponent> extends Component {

        String name;
        int id;

        protected abstract D createData(ModularBlockEntity ent);
    }

    public static class TickList<T extends ModularBlockEntity> {

        private final LinkedList<BlockEntityTicker<T>> tickers = new LinkedList<>();
        @Nullable
        private BlockEntityTicker<T> compiled;

        public void addTickerFirst(BlockEntityTicker<T> pre) {
            assert compiled == null : "Unable to hot-add new tickers.";

            tickers.addFirst(pre);
        }

        public void addTicker(BlockEntityTicker<T> ticker) {
            assert compiled == null : "Unable to hot-add new tickers.";

            tickers.add(ticker);
        }

        @Nullable
        private BlockEntityTicker<T> compileTicker() {
            if (compiled != null) return compiled;
            if (tickers.isEmpty()) return null;

            if (tickers.size() == 1) {
                compiled = tickers.getFirst();
                tickers.clear();
            } else {
                @SuppressWarnings ("unchecked")
                BlockEntityTicker<T>[] tickers = this.tickers.toArray(BlockEntityTicker[]::new);
                this.tickers.clear();
                compiled = (level, pos, state, tile) -> {
                    for (var ticker : tickers) {
                        ticker.tick(level, pos, state, tile);
                    }
                };
            }

            return compiled;
        }
    }
}
