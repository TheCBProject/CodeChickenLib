package codechicken.lib.datagen;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by covers1624 on 7/10/20.
 */
public abstract class LootTableProvider implements DataProvider {

    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final PackOutput.PathProvider output;
    private final Map<ResourceLocation, LootTable> tables = new HashMap<>();

    protected LootTableProvider(PackOutput output) {
        this.output = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
    }

    @Override
    public CompletableFuture<Void> run(CachedOutput cache) {
        tables.clear();

        registerTables();

        ValidationContext validator = new ValidationContext(LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
            @Override
            public <T> T getElement(LootDataId<T> pId) {
                return null;
            }
        });

        tables.forEach((name, table) -> table.validate(validator));

        Multimap<String, String> problems = validator.getProblems();
        if (!problems.isEmpty()) {
            logger.warn("Problems detected for LootTableGenerator: " + getName());
            problems.forEach((name, table) -> {
                logger.warn("Found validation problem in {}: {}", name, table);
            });
            throw new IllegalStateException("Failed to validate loot tables, see logs.");
        }
        List<CompletableFuture<?>> futures = new LinkedList<>();
        tables.forEach((name, table) -> {
            Path output = this.output.json(name);
            futures.add(DataProvider.saveStable(cache, LootDataType.TABLE.parser().toJsonTree(table), output));
        });
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
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

        protected static final LootItemCondition.Builder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)))
        );
        protected static final LootItemCondition.Builder NO_SILK_TOUCH = SILK_TOUCH.invert();

        protected BlockLootProvider(PackOutput output) {
            super(output);
        }

        protected LootPool.Builder singleItem(ItemLike item) {
            return LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(item));
        }

        protected LootPool.Builder singleItemOr(ItemLike failDrop, LootItemCondition.Builder condition, LootPoolEntryContainer.Builder<?> passDrop) {
            return LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(failDrop)
                            .when(condition)
                            .otherwise(passDrop)
                    );
        }

        protected LootPool.Builder singleItemOrSilk(ItemLike silk, LootItem.Builder<?> drop) {
            return singleItemOr(silk, SILK_TOUCH, drop);
        }

        protected LootPool.Builder singleItemOrSilk(ItemLike silk, ItemLike drop) {
            return singleItemOrSilk(silk, LootItem.lootTableItem(drop));
        }

        protected LootPool.Builder valueRange(ItemLike item, int min, int max) {
            return LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                    );
        }

        protected LootPool.Builder valueRangeOrSilk(ItemLike silk, ItemLike drop, int min, int max) {
            return singleItemOr(silk, SILK_TOUCH, LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))));
        }

        protected LootPool.Builder valueRangeOrSilkWithFortune(ItemLike silk, ItemLike drop, int min, int max) {
            return singleItemOr(silk, SILK_TOUCH, LootItem.lootTableItem(drop)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
            );
        }

        protected void register(Block block, LootPool.Builder... pools) {
            LootTable.Builder builder = LootTable.lootTable();
            for (LootPool.Builder pool : pools) {
                builder.withPool(pool);
            }
            register(block, builder);
        }

        public void register(Block block, LootTable.Builder builder) {
            register(ForgeRegistries.BLOCKS.getKey(block), builder);
        }

        protected void register(ResourceLocation name, LootTable.Builder builder) {
            registerTable(new ResourceLocation(name.getNamespace(), "blocks/" + name.getPath()), builder.setParamSet(LootContextParamSets.BLOCK).build());
        }
    }

}
