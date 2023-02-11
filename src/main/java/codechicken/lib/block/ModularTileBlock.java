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
    private final LazyValue<BlockEntityType<T>> type;

    private final TickList clientTicks = new TickList();
    private final TickList serverTicks = new TickList();

    public ModularTileBlock(Properties props, Supplier<BlockEntityType<T>> typeSupplier) {
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
    public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != this.type.get()) return null;

        if (level.isClientSide) {
            return (BlockEntityTicker<T>) clientTicks.compileTicker();
        }

        return (BlockEntityTicker<T>)serverTicks.compileTicker();
    }

    public static abstract class TileComponent<D extends DataComponent> extends Component {

        String name;
        int id;

        protected abstract D createData(ModularBlockEntity ent);
    }

    private static class TickList {

        private final LinkedList<BlockEntityTicker<?>> tickers = new LinkedList<>();
        @Nullable
        private BlockEntityTicker<?> compiled;

        private void addTickerFirst(BlockEntityTicker<?> pre) {
            assert compiled == null : "Unable to hot-add new tickers.";

            tickers.addFirst(pre);
        }

        private void addTicker(BlockEntityTicker<?> ticker) {
            assert compiled == null : "Unable to hot-add new tickers.";

            tickers.add(ticker);
        }

        @Nullable
        private BlockEntityTicker<?> compileTicker() {
            if (compiled != null) return compiled;
            if (tickers.isEmpty()) return null;

            if (tickers.size() == 1) {
                compiled = tickers.getFirst();
                tickers.clear();
            } else {
                @SuppressWarnings ("unchecked")
                BlockEntityTicker<BlockEntity>[] tickers = this.tickers.toArray(new BlockEntityTicker[0]);
                this.tickers.clear();
                compiled = (level, pos, state, tile) -> {
                    for (BlockEntityTicker<BlockEntity> ticker : tickers) {
                        ticker.tick(level, pos, state, tile);
                    }
                };
            }

            return compiled;
        }
    }
}
