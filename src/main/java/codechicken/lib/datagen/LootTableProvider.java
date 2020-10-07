package codechicken.lib.datagen;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.*;
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
    public void act(DirectoryCache cache) {
        tables.clear();
        Path out = dataGenerator.getOutputFolder();

        registerTables();

        ValidationTracker validator = new ValidationTracker(LootParameterSets.GENERIC, e -> null, tables::get);

        tables.forEach((name, table) -> {
            LootTableManager.func_227508_a_(validator, name, table);
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
                IDataProvider.save(GSON, cache, LootTableManager.toJson(table), output);
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

        protected BlockLootProvider(DataGenerator dataGenerator) {
            super(dataGenerator);
        }

        protected LootPool.Builder singleItem(IItemProvider item) {
            return LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(item));
        }

        protected void register(Block block, LootPool.Builder... pools) {
            LootTable.Builder builder = LootTable.builder();
            for (LootPool.Builder pool : pools) {
                builder.addLootPool(pool);
            }
            register(block, builder);
        }

        protected void register(Block block, LootTable.Builder builder) {
            register(block.getRegistryName(), builder);
        }

        protected void register(ResourceLocation name, LootTable.Builder builder) {
            registerTable(new ResourceLocation(name.getNamespace(), "blocks/" + name.getPath()), builder.setParameterSet(LootParameterSets.BLOCK).build());
        }
    }

}
