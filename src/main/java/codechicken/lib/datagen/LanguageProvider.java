package codechicken.lib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 12/7/21.
 */
public abstract class LanguageProvider implements DataProvider {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, String> client = new TreeMap<>();
    private final Map<String, String> server = new TreeMap<>();
    private final PackOutput output;
    private final String modid;
    private final String locale;

    protected LanguageProvider(PackOutput output, String modid, String locale) {
        this.output = output;
        this.modid = modid;
        this.locale = locale;
    }

    protected abstract void addTranslations();

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        addTranslations();
        List<CompletableFuture<?>> futures = new LinkedList<>();
        if (!client.isEmpty()) {
            futures.add(DataProvider.saveStable(output, GSON.toJsonTree(client), this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modid + "/lang/" + locale + ".json")));
        }
        if (!server.isEmpty()) {
            futures.add(DataProvider.saveStable(output, GSON.toJsonTree(server), this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(modid + "/lang/" + locale + ".json")));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return modid + " Languages: " + locale;
    }

    public String getLocale() {
        return locale;
    }

    private void save(CachedOutput cache, Object object, Path target) {
        DataProvider.saveStable(cache, GSON.toJsonTree(object), target);
    }

    //@formatter:off
    public void add(Block key, String name) { add(key.getDescriptionId(), name); }
    public void add(Item key, String name) { add(key.getDescriptionId(), name); }
    public void add(Supplier<? extends ItemLike> key, String name) { add(key.get().asItem(), name); }
    public void add(ItemStack key, String name) { add(key.getItem(), name); }
    public void add(MobEffect key, String name) { add(key.getDescriptionId(), name); }
    public void add(EntityType<?> key, String name) { add(key.getDescriptionId(), name); }
    public void addBlock(Supplier<? extends Block> key, String name) { add(key.get().getDescriptionId(), name); }
    public void addItem(Supplier<? extends Item> key, String name) { add(key.get().getDescriptionId(), name); }
    public void addItemStack(Supplier<ItemStack> key, String name) { add(key.get(), name); }
    public void addEffect(Supplier<MobEffect> key, String name) { add(key.get().getDescriptionId(), name); }
    public void addEntityType(Supplier<EntityType<?>> key, String name) { add(key.get().getDescriptionId(), name); }
    //@formatter:on

    public void add(String key, String name) {
        if (client.put(key, name) != null) {
            throw new IllegalArgumentException("Duplicate translation key :" + key);
        }
    }

    public void addServer(String key, String name) {
        add(key, name);
        if (server.put(key, name) != null) {
            throw new IllegalArgumentException("Duplicate translation key :" + key);
        }
    }
}
