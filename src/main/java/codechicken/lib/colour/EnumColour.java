package codechicken.lib.colour;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Triple;

/**
 * Created by covers1624 on 16/09/2016.
 */
public enum EnumColour implements IStringSerializable {

    //Formatted like this due to the cancerous nature of the constructors..
    //@formatter:off
    WHITE     ("white",      "dyeWhite",     "blockWoolWhite",     "item.fireworksCharge.white",     0xFFFFFF),
    ORANGE    ("orange",     "dyeOrange",    "blockWoolOrange",    "item.fireworksCharge.orange",    0xC06300),
    MAGENTA   ("magenta",    "dyeMagenta",   "blockWoolMagenta",   "item.fireworksCharge.magenta",   0xB51AB5),
    LIGHT_BLUE("light_blue", "dyeLightBlue", "blockWoolLightBlue", "item.fireworksCharge.lightBlue", 0x6F84F1),
    YELLOW    ("yellow",     "dyeYellow",    "blockWoolYellow",    "item.fireworksCharge.yellow",    0xBFBF00),
    LIME      ("lime",       "dyeLime",      "blockWoolLime",      "item.fireworksCharge.lime",      0x6BF100),
    PINK      ("pink",       "dyePink",      "blockWoolPink",      "item.fireworksCharge.pink",      0xF14675),
    GRAY      ("gray",       "dyeGray",      "blockWoolGray",      "item.fireworksCharge.gray",      0x535353),
    LIGHT_GRAY("light_gray", "dyeLightGray", "blockWoolLightGray", "item.fireworksCharge.silver",    0x939393),
    CYAN      ("cyan",       "dyeCyan",      "blockWoolCyan",      "item.fireworksCharge.cyan",      0x008787),
    PURPLE    ("purple",     "dyePurple",    "blockWoolPurple",    "item.fireworksCharge.purple",    0x5E00C0),
    BLUE      ("blue",       "dyeBlue",      "blockWoolBlue",      "item.fireworksCharge.blue",      0x1313C0),
    BROWN     ("brown",      "dyeBrown",     "blockWoolBrown",     "item.fireworksCharge.brown",     0x4F2700),
    GREEN     ("green",      "dyeGreen",     "blockWoolGreen",     "item.fireworksCharge.green",     0x088700),
    RED       ("red",        "dyeRed",       "blockWoolRed",       "item.fireworksCharge.red",       0xA20F06),
    BLACK     ("black",      "dyeBlack",     "blockWoolBlack",     "item.fireworksCharge.black",     0x1F1F1F);
    //@formatter:on

    private final String name;
    private final String dyeOreName;
    private final String woolOreName;
    private final String unlocalizedName;
    private final int rgb;

    private static final ImmutableList<Triple<EnumColour, EnumColour, EnumColour>> mixMap;

    EnumColour(String name, String dyeOreName, String woolOreName, String unlocalizedName, int rgb) {
        this.name = name;
        this.dyeOreName = dyeOreName;
        this.woolOreName = woolOreName;
        this.unlocalizedName = unlocalizedName;
        this.rgb = rgb;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDyeOreName() {
        return dyeOreName;
    }

    public String getWoolOreName() {
        return woolOreName;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public String getLocalizedName() {
        return I18n.translateToLocal(getUnlocalizedName());
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

    public static EnumColour fromDyeOre(String id) {
        for (EnumColour c : values()) {
            if (c.getDyeOreName().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public static EnumColour fromWoolOre(String id) {
        for (EnumColour c : values()) {
            if (c.getWoolOreName().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public static EnumColour fromDyeStack(ItemStack stack) {
        for (int id : OreDictionary.getOreIDs(stack)) {
            EnumColour c = fromDyeOre(OreDictionary.getOreName(id));
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public static EnumColour fromWoolStack(ItemStack stack) {
        for (int id : OreDictionary.getOreIDs(stack)) {
            EnumColour c = fromWoolOre(OreDictionary.getOreName(id));
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
        builder.add(getTriple(YELLOW, RED, ORANGE));
        builder.add(getTriple(PINK, PURPLE, MAGENTA));
        builder.add(getTriple(WHITE, BLUE, LIGHT_BLUE));
        //YELLOW
        builder.add(getTriple(WHITE, GREEN, LIME));
        builder.add(getTriple(WHITE, RED, PINK));
        builder.add(getTriple(WHITE, BLACK, GRAY));
        builder.add(getTriple(WHITE, GRAY, LIGHT_GRAY));
        builder.add(getTriple(BLUE, GREEN, CYAN));
        builder.add(getTriple(BLUE, RED, PURPLE));
        //Blue
        builder.add(getTriple(ORANGE, RED, BROWN));
        builder.add(getTriple(YELLOW, BLUE, GREEN));
        //RED
        //BLACK

        mixMap = builder.build();
    }

    private static Triple<EnumColour, EnumColour, EnumColour> getTriple(EnumColour a, EnumColour b, EnumColour result) {
        return Triple.of(a, b, result);
    }
}
