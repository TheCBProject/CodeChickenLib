package codechicken.lib.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by covers1624 on 16/9/24.
 */
public abstract class NoValidationBLockLootSubProvider extends BlockLootSubProvider {

    private final Map<ResourceLocation, LootTable.Builder> map;

    protected NoValidationBLockLootSubProvider() {
        this(Set.of());
    }

    protected NoValidationBLockLootSubProvider(Set<Item> explosionResistant) {
        this(explosionResistant, FeatureFlagSet.of());
    }

    protected NoValidationBLockLootSubProvider(Set<Item> explosionResistant, FeatureFlagSet flags) {
        this(explosionResistant, flags, new HashMap<>());
    }

    protected NoValidationBLockLootSubProvider(Set<Item> explosionResistant, FeatureFlagSet flags, Map<ResourceLocation, LootTable.Builder> map) {
        super(explosionResistant, flags, map);
        this.map = map;
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> cons) {
        generate();
        map.forEach(cons);
    }
}
