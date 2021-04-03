package codechicken.lib.datagen;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 7/10/20.
 */
public abstract class LootTableProvider implements IDataProvider {

    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator dataGenerator;
    private final Map<ResourceLocation, LootTable> tables = new HashMap<>();

    protected LootTableProvider(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @Override
    public void run(DirectoryCache cache) {
        tables.clear();
        Path out = dataGenerator.getOutputFolder();

        registerTables();

        ValidationTracker validator = new ValidationTracker(LootParameterSets.ALL_PARAMS, e -> null, tables::get);

        tables.forEach((name, table) -> {
            LootTableManager.validate(validator, name, table);
        });

        Multimap<String, String> problems = validator.getProblems();
        if (!problems.isEmpty()) {
            logger.warn("Problems detected for LootTableGenerator: " + getName());
            problems.forEach((name, table) -> {
                logger.warn("Found validation problem in {}: {}", name, table);
            });
            throw new IllegalStateException("Failed to validate loot tables, see logs.");
        }
        tables.forEach((name, table) -> {
            Path output = getPath(out, name);
            try {
                IDataProvider.save(GSON, cache, LootTableManager.serialize(table), output);
            } catch (IOException e) {
                logger.error("Couldn't save loot table {}", output, e);
            }
        });
    }

    protected abstract void registerTables();

    protected void registerTable(ResourceLocation name, LootTable table) {
        if (tables.put(name, table) != null) {
            throw new IllegalArgumentException("Duplicate loot table registered: " + name);
        }
    }

    private static Path getPath(Path pathIn, ResourceLocation id) {
        return pathIn.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
    }

    public static abstract class BlockLootProvider extends LootTableProvider {

        protected static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1)))
        );
        protected static final ILootCondition.IBuilder NO_SILK_TOUCH = SILK_TOUCH.invert();

        protected BlockLootProvider(DataGenerator dataGenerator) {
            super(dataGenerator);
        }

        protected LootPool.Builder singleItem(IItemProvider item) {
            return LootPool.lootPool()
                    .setRolls(ConstantRange.exactly(1))
                    .add(ItemLootEntry.lootTableItem(item));
        }

        protected LootPool.Builder singleItemOr(IItemProvider failDrop, ILootCondition.IBuilder condition, LootEntry.Builder<?> passDrop) {
            return LootPool.lootPool()
                    .setRolls(ConstantRange.exactly(1))
                    .add(ItemLootEntry.lootTableItem(failDrop)
                            .when(condition)
                            .otherwise(passDrop)
                    );
        }

        protected LootPool.Builder singleItemOrSilk(IItemProvider silk, LootEntry.Builder<?> drop) {
            return singleItemOr(silk, SILK_TOUCH, drop);
        }

        protected LootPool.Builder singleItemOrSilk(IItemProvider silk, IItemProvider drop) {
            return singleItemOrSilk(silk, ItemLootEntry.lootTableItem(drop));
        }

        protected LootPool.Builder valueRange(IItemProvider item, int min, int max) {
            return LootPool.lootPool()
                    .setRolls(ConstantRange.exactly(1))
                    .add(ItemLootEntry.lootTableItem(item)
                            .apply(SetCount.setCount(new RandomValueRange(min, max)))
                    );
        }

        protected LootPool.Builder valueRangeOrSilk(IItemProvider silk, IItemProvider drop, int min, int max) {
            return singleItemOr(silk, SILK_TOUCH, ItemLootEntry.lootTableItem(drop).apply(SetCount.setCount(new RandomValueRange(min, max))));
        }

        protected LootPool.Builder valueRangeOrSilkWithFortune(IItemProvider silk, IItemProvider drop, int min, int max) {
            return singleItemOr(silk, SILK_TOUCH, ItemLootEntry.lootTableItem(drop)
                    .apply(SetCount.setCount(new RandomValueRange(min, max)))
                    .apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
            );
        }

        protected void register(Block block, LootPool.Builder... pools) {
            LootTable.Builder builder = LootTable.lootTable();
            for (LootPool.Builder pool : pools) {
                builder.withPool(pool);
            }
            register(block, builder);
        }

        protected void register(Block block, LootTable.Builder builder) {
            register(block.getRegistryName(), builder);
        }

        protected void register(ResourceLocation name, LootTable.Builder builder) {
            registerTable(new ResourceLocation(name.getNamespace(), "blocks/" + name.getPath()), builder.setParamSet(LootParameterSets.BLOCK).build());
        }
    }

}
