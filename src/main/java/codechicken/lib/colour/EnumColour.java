package codechicken.lib.colour;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 16/09/2016.
 */
public enum EnumColour implements IStringSerializable {

    //@formatter:off
    WHITE     ("white",      "forge:dyes/white",      "forge:wool/white",      "item.minecraft.firework_star.white",       0xFFFFFF),
    ORANGE    ("orange",     "forge:dyes/orange",     "forge:wool/orange",     "item.minecraft.firework_star.orange",      0xC06300),
    MAGENTA   ("magenta",    "forge:dyes/magenta",    "forge:wool/magenta",    "item.minecraft.firework_star.magenta",     0xB51AB5),
    LIGHT_BLUE("light_blue", "forge:dyes/light_blue", "forge:wool/light_blue", "item.minecraft.firework_star.light_blue",  0x6F84F1),
    YELLOW    ("yellow",     "forge:dyes/yellow",     "forge:wool/yellow",     "item.minecraft.firework_star.yellow",      0xBFBF00),
    LIME      ("lime",       "forge:dyes/lime",       "forge:wool/lime",       "item.minecraft.firework_star.lime",        0x6BF100),
    PINK      ("pink",       "forge:dyes/pink",       "forge:wool/pink",       "item.minecraft.firework_star.pink",        0xF14675),
    GRAY      ("gray",       "forge:dyes/gray",       "forge:wool/gray",       "item.minecraft.firework_star.gray",        0x535353),
    LIGHT_GRAY("light_gray", "forge:dyes/light_gray", "forge:wool/light_gray", "item.minecraft.firework_star.light_gray",  0x939393),
    CYAN      ("cyan",       "forge:dyes/cyan",       "forge:wool/cyan",       "item.minecraft.firework_star.cyan",        0x008787),
    PURPLE    ("purple",     "forge:dyes/purple",     "forge:wool/purple",     "item.minecraft.firework_star.purple",      0x5E00C0),
    BLUE      ("blue",       "forge:dyes/blue",       "forge:wool/blue",       "item.minecraft.firework_star.blue",        0x1313C0),
    BROWN     ("brown",      "forge:dyes/brown",      "forge:wool/brown",      "item.minecraft.firework_star.brown",       0x4F2700),
    GREEN     ("green",      "forge:dyes/green",      "forge:wool/green",      "item.minecraft.firework_star.green",       0x088700),
    RED       ("red",        "forge:dyes/red",        "forge:wool/red",        "item.minecraft.firework_star.red",         0xA20F06),
    BLACK     ("black",      "forge:dyes/black",      "forge:wool/black",      "item.minecraft.firework_star.black",       0x1F1F1F);
    //@formatter:on

    private final String name;
    private final ResourceLocation dyeTagName;
    private final ResourceLocation woolTagName;
    private final String unlocalizedName;
    private final int rgb;

    private static final ImmutableTable<EnumColour, EnumColour, EnumColour> mixMap;

    private static final Map<String, EnumColour> nameLookup = Arrays.stream(values())//
            .collect(Collectors.toMap(e -> e.name, Function.identity()));

    private static final Map<ResourceLocation, EnumColour> dyeTagLookup = Arrays.stream(values())//
            .collect(Collectors.toMap(e -> e.dyeTagName, Function.identity()));

    private static final Map<ResourceLocation, EnumColour> woolTagLookup = Arrays.stream(values())//
            .collect(Collectors.toMap(e -> e.woolTagName, Function.identity()));

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
        this.dyeTagName = new ResourceLocation(dyeTagName);
        this.woolTagName = new ResourceLocation(woolTagName);
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

    public EnumColour mix(EnumColour b) {
        return mix(this, b);
    }

    public static EnumColour mix(EnumColour a, EnumColour b) {
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

    public static EnumColour fromDyeTag(ResourceLocation tag) {
        return dyeTagLookup.get(tag);
    }

    public static EnumColour fromWoolTag(ResourceLocation tag) {
        return woolTagLookup.get(tag);
    }

    public static EnumColour fromDyeStack(ItemStack stack) {
        return stack.getItem().getTags().stream()//
                .map(dyeTagLookup::get)//
                .filter(Objects::nonNull)//
                .findFirst()//
                .orElse(null);
    }

    public static EnumColour fromWoolStack(ItemStack stack) {
        return stack.getItem().getTags().stream()//
                .map(woolTagLookup::get)//
                .filter(Objects::nonNull)//
                .findFirst()//
                .orElse(null);
    }

    public static EnumColour fromName(String name) {
        return nameLookup.get(name);
    }
}
