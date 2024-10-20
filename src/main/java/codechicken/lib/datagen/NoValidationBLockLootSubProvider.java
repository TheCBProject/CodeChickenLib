package codechicken.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
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

    private final Map<ResourceKey<LootTable>, LootTable.Builder> map;

    protected NoValidationBLockLootSubProvider(HolderLookup.Provider registries) {
        this(Set.of(), registries);
    }

    protected NoValidationBLockLootSubProvider(Set<Item> explosionResistant, HolderLookup.Provider registries) {
        this(explosionResistant, FeatureFlagSet.of(), registries);
    }

    protected NoValidationBLockLootSubProvider(Set<Item> explosionResistant, FeatureFlagSet flags, HolderLookup.Provider registries) {
        this(explosionResistant, flags, new HashMap<>(), registries);
    }

    protected NoValidationBLockLootSubProvider(Set<Item> explosionResistant, FeatureFlagSet flags, Map<ResourceKey<LootTable>, LootTable.Builder> map, HolderLookup.Provider registries) {
        super(explosionResistant, flags, map, registries);
        this.map = map;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> cons) {
        generate();
        map.forEach(cons);
    }
}
