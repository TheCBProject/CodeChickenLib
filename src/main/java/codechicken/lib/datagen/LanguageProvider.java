package codechicken.lib.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 12/7/21.
 */
public abstract class LanguageProvider implements IDataProvider {

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

    protected abstract void addTranslations();

    @Override
    public void run(DirectoryCache p_200398_1_) throws IOException {
        addTranslations();
        if (distFilter.includeClient() && !client.isEmpty()) {
            save(p_200398_1_, client, gen.getOutputFolder().resolve("assets/" + modid + "/lang/" + locale + ".json"));
        }
        if (distFilter.includeServer() && !server.isEmpty()) {
            save(p_200398_1_, server, gen.getOutputFolder().resolve("data/" + modid + "/lang/" + locale + ".json"));
        }
    }

    @Override
    public String getName() {
        return modid + " Languages: " + locale;
    }

    @SuppressWarnings ({ "deprecation", "UnstableApiUsage" })
    private void save(DirectoryCache cache, Object object, Path target) throws IOException {
        String data = GSON.toJson(object);
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON
        String hash = IDataProvider.SHA1.hashUnencodedChars(data).toString();
        if (!Objects.equals(cache.getHash(target), hash) || !Files.exists(target)) {
            Files.createDirectories(target.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
                bufferedwriter.write(data);
            }
        }

        cache.putNew(target, hash);
    }

    //@formatter:off
    public void add(Block key, String name) { add(key.getDescriptionId(), name); }
    public void add(Item key, String name) { add(key.getDescriptionId(), name); }
    public void add(ItemStack key, String name) { add(key.getDescriptionId(), name); }
    public void add(Enchantment key, String name) { add(key.getDescriptionId(), name); }
    public void add(Effect key, String name) { add(key.getDescriptionId(), name); }
    public void add(EntityType<?> key, String name) { add(key.getDescriptionId(), name); }
    public void addBlock(Supplier<Block> key, String name) { add(key.get().getDescriptionId(), name); }
    public void addItem(Supplier<Item> key, String name) { add(key.get().getDescriptionId(), name); }
    public void addItemStack(Supplier<ItemStack> key, String name) { add(key.get().getDescriptionId(), name); }
    public void addEnchantment(Supplier<Enchantment> key, String name) { add(key.get().getDescriptionId(), name); }
    public void addEffect(Supplier<Effect> key, String name) { add(key.get().getDescriptionId(), name); }
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

    public static Side getDist(GatherDataEvent event) {
        if (event.includeServer() && event.includeClient()) return Side.BOTH;
        if (event.includeServer()) return Side.SERVER;
        if (event.includeClient()) return Side.CLIENT;
        return Side.NONE;
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
