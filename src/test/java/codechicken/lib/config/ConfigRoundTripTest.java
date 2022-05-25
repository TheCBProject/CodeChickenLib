package codechicken.lib.config;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by covers1624 on 22/5/22.
 */
public class ConfigRoundTripTest {

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    public static void assertTagsParseSame(ConfigTag a, ConfigTag b) {
        assertEquals(a.getClass(), b.getClass());
        assertEquals(a.getDesc(), b.getDesc());

        if (a instanceof ConfigCategoryImpl) assertTagsParseSame((ConfigCategoryImpl) a, (ConfigCategoryImpl) b);
        if (a instanceof ConfigValueImpl) assertTagsParseSame((ConfigValueImpl) a, (ConfigValueImpl) b);
        if (a instanceof ConfigValueListImpl) assertTagsParseSame((ConfigValueListImpl) a, (ConfigValueListImpl) b);
    }

    public static void assertTagsParseSame(ConfigCategoryImpl a, ConfigCategoryImpl b) {
        List<ConfigTag> aChildren = List.copyOf(a.getChildren());
        List<ConfigTag> bChildren = List.copyOf(b.getChildren());
        assertEquals(aChildren.size(), bChildren.size());

        for (int i = 0; i < aChildren.size(); i++) {
            assertTagsParseSame(aChildren.get(i), bChildren.get(i));
        }
    }

    public static void assertTagsParseSame(ConfigValueImpl a, ConfigValueImpl b) {
        assertNotEquals(ValueType.UNKNOWN, a.getType());
        if (b.getType() == ValueType.UNKNOWN) {
            try {
                ConfigValueImpl.convert(b.getRealRawValue(), a.getType());
                b.setKnownType(a.getType());
            } catch (Throwable ex) {
                fail("Tag B (" + b.getDesc() + ") not representable as: " + a.getType(), ex);
            }
        }
        switch (a.getType()) {
            case BOOLEAN -> assertEquals(a.getBoolean(), b.getBoolean());
            case STRING -> assertEquals(a.getString(), b.getString());
            case INT -> assertEquals(a.getInt(), b.getInt());
            case LONG -> assertEquals(a.getLong(), b.getLong());
            case HEX -> assertEquals(a.getHex(), b.getHex());
            case DOUBLE -> assertEquals(a.getDouble(), b.getDouble());
        }
    }

    public static void assertTagsParseSame(ConfigValueListImpl a, ConfigValueListImpl b) {
        assertNotEquals(ValueType.UNKNOWN, a.getType());
        if (b.getType() == ValueType.UNKNOWN) {
            try {
                ConfigValueListImpl.convert(b.getRealRawValue(), a.getType());
                b.setKnownType(a.getType());
            } catch (Throwable ex) {
                fail("Tag B (" + b.getDesc() + ") not representable as: " + a.getType(), ex);
            }
        }
        switch (a.getType()) {
            case BOOLEAN -> assertEquals(a.getBooleans(), b.getBooleans());
            case STRING -> assertEquals(a.getStrings(), b.getStrings());
            case INT -> assertEquals(a.getInts(), b.getInts());
            case LONG -> assertEquals(a.getLongs(), b.getLongs());
            case HEX -> assertEquals(a.getHexs(), b.getHexs());
            case DOUBLE -> assertEquals(a.getDoubles(), b.getDoubles());
        }
    }

    public static ConfigCategoryImpl generateTestTag(Random rand) {
        ConfigCategoryImpl rootTag = new ConfigCategoryImpl("rootTag", null);
        addRandomEntries(rand, rootTag, 0);
        return rootTag;
    }

    private static void addRandomEntries(Random rand, ConfigCategory cat, int depth) {
        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValue("bool").setDefaultBoolean(rand.nextBoolean()));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValue("string").setDefaultString(generateRandomString(rand)));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValue("int").setDefaultInt(rand.nextInt()));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValue("long").setDefaultLong(rand.nextLong()));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValue("hex").setDefaultHex(rand.nextInt()));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValue("double").setDefaultDouble(rand.nextGaussian()));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValueList("boolList").setDefaultBooleans(generateRandomBooleans(rand)));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValueList("stringList").setDefaultStrings(generateRandomStrings(rand)));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValueList("intList").setDefaultInts(generateRandomInts(rand)));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValueList("longList").setDefaultLongs(generateRandomLongs(rand)));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValueList("hexList").setDefaultHexs(generateRandomInts(rand)));
        }

        if (rand.nextBoolean()) {
            addRandomComment(rand, cat.getValueList("doubleList").setDefaultDoubles(generateRandomDoubles(rand)));
        }

        if (depth != 10) {
            int numCats = rand.nextInt(5);
            for (int i = 0; i < numCats; i++) {
                ConfigCategory subCat = cat.getCategory("cat" + i);
                addRandomComment(rand, subCat);
                addRandomEntries(rand, subCat, depth + 1);
                if (subCat.getChildren().isEmpty()) {
                    cat.delete("cat" + i);
                }
            }
        }
    }

    private static void addRandomComment(Random rand, ConfigTag tag) {
        if (rand.nextBoolean()) {
            tag.setComment(generateRandomStrings(rand));
        }
    }

    private static BooleanList generateRandomBooleans(Random rand) {
        boolean[] booleans = new boolean[rand.nextInt(100)];
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = rand.nextBoolean();
        }
        return new BooleanArrayList(booleans);
    }

    private static String generateRandomString(Random rand) {
        int num = rand.nextInt(100);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append(HEX[rand.nextInt(HEX.length)]);
        }
        return builder.toString();
    }

    private static List<String> generateRandomStrings(Random rand) {
        int num = rand.nextInt(5);
        List<String> strings = new ArrayList<>(num + 1);
        for (int i = 0; i < num; i++) {
            strings.add(generateRandomString(rand));
        }
        return strings;
    }

    private static IntList generateRandomInts(Random rand) {
        int[] ints = new int[rand.nextInt(100)];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = rand.nextInt();
        }
        return new IntArrayList(ints);
    }

    private static LongList generateRandomLongs(Random rand) {
        long[] longs = new long[rand.nextInt(100)];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = rand.nextLong();
        }
        return new LongArrayList(longs);
    }

    private static DoubleList generateRandomDoubles(Random rand) {
        double[] doubles = new double[rand.nextInt(100)];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = rand.nextGaussian();
        }
        return new DoubleArrayList(doubles);
    }
}
