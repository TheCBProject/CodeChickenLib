package codechicken.lib.colour;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import io.netty.buffer.ByteBuf;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Created by covers1624 on 16/09/2016.
 */
public enum EnumColour implements StringRepresentable {

    //@formatter:off
    WHITE     ("white",      "c:dyes/white",      "c:wools/white",      "item.minecraft.firework_star.white",       0xFFFFFF),
    ORANGE    ("orange",     "c:dyes/orange",     "c:wools/orange",     "item.minecraft.firework_star.orange",      0xC06300),
    MAGENTA   ("magenta",    "c:dyes/magenta",    "c:wools/magenta",    "item.minecraft.firework_star.magenta",     0xB51AB5),
    LIGHT_BLUE("light_blue", "c:dyes/light_blue", "c:wools/light_blue", "item.minecraft.firework_star.light_blue",  0x6F84F1),
    YELLOW    ("yellow",     "c:dyes/yellow",     "c:wools/yellow",     "item.minecraft.firework_star.yellow",      0xBFBF00),
    LIME      ("lime",       "c:dyes/lime",       "c:wools/lime",       "item.minecraft.firework_star.lime",        0x6BF100),
    PINK      ("pink",       "c:dyes/pink",       "c:wools/pink",       "item.minecraft.firework_star.pink",        0xF14675),
    GRAY      ("gray",       "c:dyes/gray",       "c:wools/gray",       "item.minecraft.firework_star.gray",        0x535353),
    LIGHT_GRAY("light_gray", "c:dyes/light_gray", "c:wools/light_gray", "item.minecraft.firework_star.light_gray",  0x939393),
    CYAN      ("cyan",       "c:dyes/cyan",       "c:wools/cyan",       "item.minecraft.firework_star.cyan",        0x008787),
    PURPLE    ("purple",     "c:dyes/purple",     "c:wools/purple",     "item.minecraft.firework_star.purple",      0x5E00C0),
    BLUE      ("blue",       "c:dyes/blue",       "c:wools/blue",       "item.minecraft.firework_star.blue",        0x1313C0),
    BROWN     ("brown",      "c:dyes/brown",      "c:wools/brown",      "item.minecraft.firework_star.brown",       0x4F2700),
    GREEN     ("green",      "c:dyes/green",      "c:wools/green",      "item.minecraft.firework_star.green",       0x088700),
    RED       ("red",        "c:dyes/red",        "c:wools/red",        "item.minecraft.firework_star.red",         0xA20F06),
    BLACK     ("black",      "c:dyes/black",      "c:wools/black",      "item.minecraft.firework_star.black",       0x1F1F1F);
    //@formatter:on

    public static final StringRepresentable.EnumCodec<EnumColour> CODEC = StringRepresentable.fromEnum(EnumColour::values);
    public static final IntFunction<EnumColour> BY_ID = ByIdMap.continuous(EnumColour::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, EnumColour> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, EnumColour::ordinal);

    private final String name;
    private final ResourceLocation dyeTagName;
    private final ResourceLocation woolTagName;
    private final String unlocalizedName;
    private final int rgb;

    private static final ImmutableTable<EnumColour, EnumColour, EnumColour> mixMap;

    private static final Map<String, EnumColour> nameLookup = FastStream.of(values())
            .toMap(e -> e.name, Function.identity());

    private static final Map<ResourceLocation, EnumColour> dyeTagLookup = FastStream.of(values())
            .toMap(e -> e.dyeTagName, Function.identity());

    private static final Map<ResourceLocation, EnumColour> woolTagLookup = FastStream.of(values())
            .toMap(e -> e.woolTagName, Function.identity());

    static {
        Table<EnumColour, EnumColour, EnumColour> tmp = HashBasedTable.create();
        //WHITE
        tmp.put(YELLOW, RED, ORANGE);
        tmp.put(PINK, PURPLE, MAGENTA);
        tmp.put(WHITE, BLUE, LIGHT_BLUE);
        //YELLOW
        tmp.put(WHITE, GREEN, LIME);
        tmp.put(WHITE, RED, PINK);
        tmp.put(WHITE, BLACK, GRAY);
        tmp.put(WHITE, GRAY, LIGHT_GRAY);
        tmp.put(BLUE, GREEN, CYAN);
        tmp.put(BLUE, RED, PURPLE);
        //Blue
        tmp.put(ORANGE, RED, BROWN);
        tmp.put(YELLOW, BLUE, GREEN);
        //RED
        //BLACK

        //Build reverse lookups.
        ImmutableTable.Builder<EnumColour, EnumColour, EnumColour> builder = ImmutableTable.builder();
        tmp.cellSet().forEach(e -> {
            builder.put(e.getRowKey(), e.getColumnKey(), e.getValue());
            builder.put(e.getColumnKey(), e.getRowKey(), e.getValue());
        });

        mixMap = builder.build();
    }

    EnumColour(String name, String dyeTagName, String woolTagName, String unlocalizedName, int rgb) {
        this.name = name;
        this.dyeTagName = ResourceLocation.parse(dyeTagName);
        this.woolTagName = ResourceLocation.parse(woolTagName);
        this.unlocalizedName = unlocalizedName;
        this.rgb = rgb;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public ResourceLocation getDyeTagName() {
        return dyeTagName;
    }

    public ResourceLocation getWoolTagName() {
        return woolTagName;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public String getLocalizedName() {
        return I18n.get(getUnlocalizedName());
    }

    public int getWoolMeta() {
        return ordinal();
    }

    public int getDyeMeta() {
        return 15 - ordinal();
    }

    public int rgba() {
        return rgba(0xFF);
    }

    public int rgba(int alpha) {
        return rgb << 8 | (alpha & 0xFF);
    }

    public int argb() {
        return argb(0xFF);
    }

    public int argb(int alpha) {
        return (alpha & 0xFF) << 24 | rgb;
    }

    public int rgb() {
        return rgb;
    }

    public float rF() {
        return (rgb >> 16 & 255) / 255.0f;
    }

    public float gF() {
        return (rgb >> 8 & 255) / 255.0f;
    }

    public float bF() {
        return (rgb & 255) / 255.0f;
    }

    public ColourRGBA getColour() {
        return getColour(0xFF);
    }

    public ColourRGBA getColour(int alpha) {
        return new ColourRGBA(rgba(alpha));
    }

    public @Nullable EnumColour mix(EnumColour b) {
        return mix(this, b);
    }

    public static @Nullable EnumColour mix(EnumColour a, EnumColour b) {
        if (a == b) {
            return a;
        }
        return mixMap.get(a, b);
    }

    public static EnumColour fromWoolMeta(int id) {
        return values()[id];
    }

    public static EnumColour fromDyeMeta(int id) {
        return values()[15 - id];
    }

    public static @Nullable EnumColour fromDyeTag(ResourceLocation tag) {
        return dyeTagLookup.get(tag);
    }

    public static @Nullable EnumColour fromWoolTag(ResourceLocation tag) {
        return woolTagLookup.get(tag);
    }

    public static @Nullable EnumColour fromDyeStack(ItemStack stack) {
        return FastStream.of(stack.getTags())
                .map(TagKey::location)
                .map(dyeTagLookup::get)
                .filter(Objects::nonNull)
                .firstOrDefault();
    }

    public static @Nullable EnumColour fromWoolStack(ItemStack stack) {
        return FastStream.of(stack.getTags())
                .map(TagKey::location)
                .map(woolTagLookup::get)
                .filter(Objects::nonNull)
                .firstOrDefault();
    }

    public static @Nullable EnumColour fromName(String name) {
        return nameLookup.get(name);
    }
}
