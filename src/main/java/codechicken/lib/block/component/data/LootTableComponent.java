package codechicken.lib.block.component.data;

import codechicken.lib.block.ModularBlock;
import codechicken.lib.datagen.LootTableProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An {@link ModularBlock.Component} for loot table data generation.
 * <p>
 * Created by covers1624 on 4/9/22.
 */
@ApiStatus.Experimental
public class LootTableComponent extends DataGenComponent {

    private LootTable.Builder table = new LootTable.Builder();
    private final Consumer<LootTableComponent> configure;

    public LootTableComponent(Consumer<LootTableComponent> configure) {
        this.configure = configure;
    }

    @Override
    protected void addToProvider(DataProvider provider) {
        if (provider instanceof LootTableProvider.BlockLootProvider loot) {
            configure.accept(this);
            loot.register(getBlock(), table);
        }
    }

    public void setTable(LootTable.Builder table) {
        this.table = table;
    }

    public void addPool(LootPool.Builder builder) {
        table.withPool(builder);
    }

    public void singleItemPool(ItemLike item) {
        addPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item))
        );
    }
}
