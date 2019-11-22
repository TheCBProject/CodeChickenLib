package codechicken.lib.colour;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

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

    private static final ImmutableList<Triple<EnumColour, EnumColour, EnumColour>> mixMap;

    EnumColour(String name, String dyeTagName, String woolTagName, String unlocalizedName, int rgb) {
        this.name = name;
        this.dyeTagName = new ResourceLocation(dyeTagName);
        this.woolTagName = new ResourceLocation(woolTagName);
        this.unlocalizedName = unlocalizedName;
        this.rgb = rgb;
    }

    @Override
    public String getName() {
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
        return I18n.format(getUnlocalizedName());
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
        synchronized (mixMap) {
            for (Triple<EnumColour, EnumColour, EnumColour> triple : mixMap) {
                if ((triple.getLeft().equals(a) && triple.getMiddle().equals(b)) || (triple.getLeft().equals(b) && triple.getMiddle().equals(a))) {
                    return triple.getRight();
                }
            }
        }
        return null;
    }

    public static EnumColour fromWoolMeta(int id) {
        return values()[id];
    }

    public static EnumColour fromDyeMeta(int id) {
        return values()[15 - id];
    }

    public static EnumColour fromDyeTag(ResourceLocation tag) {
        for (EnumColour c : values()) {
            if (c.getDyeTagName().equals(tag)) {
                return c;
            }
        }
        return null;
    }

    public static EnumColour fromWoolTag(ResourceLocation tag) {
        for (EnumColour c : values()) {
            if (c.getWoolTagName().equals(tag)) {
                return c;
            }
        }
        return null;
    }

    //TODO, Set.contains + Stream nonsense
    public static EnumColour fromDyeStack(ItemStack stack) {
        for (ResourceLocation tag : stack.getItem().getTags()) {
            EnumColour c = fromDyeTag(tag);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public static EnumColour fromWoolStack(ItemStack stack) {
        for (ResourceLocation tag : stack.getItem().getTags()) {
            EnumColour c = fromWoolTag(tag);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public static EnumColour fromName(String name) {
        for (EnumColour colour : values()) {
            if (colour.getName().equalsIgnoreCase(name)) {
                return colour;
            }
        }
        return null;
    }

    static {
        ImmutableList.Builder<Triple<EnumColour, EnumColour, EnumColour>> builder = ImmutableList.builder();
        //WHITE
        builder.add(Triple.of(YELLOW, RED, ORANGE));
        builder.add(Triple.of(PINK, PURPLE, MAGENTA));
        builder.add(Triple.of(WHITE, BLUE, LIGHT_BLUE));
        //YELLOW
        builder.add(Triple.of(WHITE, GREEN, LIME));
        builder.add(Triple.of(WHITE, RED, PINK));
        builder.add(Triple.of(WHITE, BLACK, GRAY));
        builder.add(Triple.of(WHITE, GRAY, LIGHT_GRAY));
        builder.add(Triple.of(BLUE, GREEN, CYAN));
        builder.add(Triple.of(BLUE, RED, PURPLE));
        //Blue
        builder.add(Triple.of(ORANGE, RED, BROWN));
        builder.add(Triple.of(YELLOW, BLUE, GREEN));
        //RED
        //BLACK

        mixMap = builder.build();
    }
}
