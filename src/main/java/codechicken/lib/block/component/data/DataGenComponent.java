package codechicken.lib.block.component.data;

import codechicken.lib.block.ModularBlock;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

/**
 * Defines a component that only stores data for data generators.
 * <p>
 * There is no automatic discovery mechanism for these, you must manually register your {@link ModularBlock} instances
 * to each {@link DataProvider}
 * <p>
 * You can either add each block manually via {@link #addToProvider(ModularBlock, DataProvider)}. Or you
 * can register your entire {@link DeferredRegister <Block>} via {@link #addAllToProvider(DeferredRegister, DataProvider)}
 * <p>
 * Created by covers1624 on 22/7/22.
 */
@ApiStatus.Experimental
public abstract class DataGenComponent extends ModularBlock.Component {

    /**
     * Add this component to the provided {@link DataProvider}.
     * <p>
     * Implementors will need to filter for the correct {@link DataProvider} implementation.
     *
     * @param provider The data provider to add to.
     */
    protected abstract void addToProvider(DataProvider provider);

    /**
     * Ask all {@link DataGenComponent}s in the provided {@link ModularBlock} to
     * add data to the provided {@link DataProvider}.
     *
     * @param block    The {@link ModularBlock}.
     * @param provider The {@link DataProvider} to add things to.
     */
    public static void addToProvider(ModularBlock block, DataProvider provider) {
        for (DataGenComponent component : block.getDatagenComponents()) {
            component.addToProvider(provider);
        }
    }

    /**
     * Ask all {@link DataGenComponent}s in all {@link ModularBlock}s in the
     * provided {@link DeferredRegister} to add data to the provided {@link DataProvider}.
     *
     * @param blocks   The {@link DeferredRegister} to add blocks from.
     * @param provider The {@link DataProvider} to add things to.
     */
    public static void addAllToProvider(DeferredRegister<Block> blocks, DataProvider provider) {
        for (DeferredHolder<Block, ?> entry : blocks.getEntries()) {
            if (entry.get() instanceof ModularBlock block) {
                addToProvider(block, provider);
            }
        }
    }
}
