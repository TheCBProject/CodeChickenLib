package codechicken.lib.block.component.data;

import codechicken.lib.block.ModularBlock;
import codechicken.lib.datagen.NoValidationBLockLootSubProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Set;
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
    protected void addToProvider(DataProvider p) {
        if (p instanceof LootTableProvider provider) {
            if (!(provider.subProviders instanceof ArrayList)) {
                provider.subProviders = new ArrayList<>(provider.subProviders);
            }
            provider.subProviders.add(
                    new LootTableProvider.SubProviderEntry(
                            () -> new NoValidationBLockLootSubProvider() {
                                @Override
                                protected void generate() {
                                    add(getBlock(), table);
                                }
                            },
                            LootContextParamSets.BLOCK
                    )
            );

            configure.accept(this);
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
