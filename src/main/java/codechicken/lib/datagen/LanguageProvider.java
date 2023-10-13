package codechicken.lib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 12/7/21.
 */
public abstract class LanguageProvider implements DataProvider {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, String> client = new TreeMap<>();
    private final Map<String, String> server = new TreeMap<>();
    private final DataGenerator gen;
    private final String modid;
    private final String locale;
    private final Side distFilter;

    protected LanguageProvider(DataGenerator gen, String modid, String locale, Side distFilter) {
        this.gen = gen;
        this.modid = modid;
        this.locale = locale;
        this.distFilter = distFilter;
    }

    public static Side getDist(GatherDataEvent event) {
        if (event.includeServer() && event.includeClient()) return Side.BOTH;
        if (event.includeServer()) return Side.SERVER;
        if (event.includeClient()) return Side.CLIENT;
        return Side.NONE;
    }

    protected abstract void addTranslations();

    @Override
    public @NotNull CompletableFuture<?> run(final @NotNull CachedOutput cache) {
        return CompletableFuture.supplyAsync(() -> {
            addTranslations();
            try {
                if (distFilter.includeClient() && !client.isEmpty()) {
                    save(cache, client, gen.getPackOutput().getOutputFolder().resolve("assets/" + modid + "/lang/" + locale + ".json"));
                }
                if (distFilter.includeServer() && !server.isEmpty()) {
                    save(cache, server, gen.getPackOutput().getOutputFolder().resolve("data/" + modid + "/lang/" + locale + ".json"));
                }
            } catch (IOException exception) {
                throw new CompletionException(exception);
            }
            return CompletableFuture.completedFuture(this);
        }, Executors.newCachedThreadPool());
    }

    @Override
    public @NotNull String getName() {
        return modid + " Languages: " + locale;
    }

    public String getLocale() {
        return locale;
    }

    private void save(CachedOutput cache, Object object, Path target) throws IOException {
        DataProvider.saveStable(cache, GSON.toJsonTree(object), target);
    }

    //@formatter:off
    public void add(Block key, String name) { add(key.getDescriptionId(), name); }

    public void add(Item key, String name) { add(key.getDescriptionId(), name); }

    public void add(ItemStack key, String name) { add(key.getDescriptionId(), name); }

    public void add(Enchantment key, String name) { add(key.getDescriptionId(), name); }

    public void add(MobEffect key, String name) { add(key.getDescriptionId(), name); }

    public void add(EntityType<?> key, String name) { add(key.getDescriptionId(), name); }

    public void addBlock(Supplier<? extends Block> key, String name) { add(key.get().getDescriptionId(), name); }

    public void addItem(Supplier<? extends Item> key, String name) { add(key.get().getDescriptionId(), name); }

    public void addItemStack(Supplier<ItemStack> key, String name) { add(key.get().getDescriptionId(), name); }

    public void addEnchantment(Supplier<Enchantment> key, String name) { add(key.get().getDescriptionId(), name); }

    public void addEffect(Supplier<MobEffect> key, String name) { add(key.get().getDescriptionId(), name); }
    //@formatter:on

    public void addEntityType(Supplier<EntityType<?>> key, String name) { add(key.get().getDescriptionId(), name); }

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

    public enum Side {
        CLIENT,
        SERVER,
        BOTH,
        NONE;

        public boolean includeClient() {
            return this == CLIENT || this == BOTH;
        }

        public boolean includeServer() {
            return this == SERVER || this == BOTH;
        }
    }
}
