package codechicken.lib.config;

import codechicken.lib.config.ConfigCallback.Reason;
import codechicken.lib.config.ConfigValueListImpl.StringList;
import codechicken.lib.config.parser.ConfigSerializer;
import codechicken.lib.data.MCDataByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by covers1624 on 18/4/22.
 */
public class ConfigV3Tests {

    @Test
    public void testBasics() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        assertEquals("rootTag", root.getName());                        // Name should be set.
        assertNull(root.getParent());                                   // Should have no parent.
        assertEquals(0, root.getChildren().size());                     // Should have no children.
        assertFalse(root.has("no"));                                    // Query should return false.
        assertNull(root.findTag("doesntExist"));                        // Query should return null.
        assertNull(root.findCategory("alsoDoesntExist"));               // Query should return null.
        assertNull(root.findValue("absolutelyDoesntExist"));            // Query should return null.
        assertNull(root.findValueList("listOfAbsolutelyDoesNotExist")); // Query should return null.
        assertEquals(0, root.getComment().size());                      // No comments.
        assertEquals("rootTag", root.getDesc());
    }

    @Test
    public void testCategoryCreation() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigCategory cat1 = root.getCategory("cat1"); // Should create new ConfigCategory
        assertTrue(root.has("cat1"));                   // Query should return true.
        assertSame(cat1, root.getCategory("cat1"));     // Should be identity equal on re-get.
        assertSame(cat1, root.findCategory("cat1"));    // Same with Find.
        assertNull(root.findValue("cat1"));             // Should return null as it's not a Value.
        assertNull(root.findValueList("cat1"));         // Should return null as it's not a Value List.
        assertEquals(1, root.getChildren().size());     // Should only be 1
        assertEquals("rootTag.cat1", cat1.getDesc());

        IllegalStateException ex;
        ex = assertThrows(IllegalStateException.class, () -> root.getValue("cat1"));
        assertEquals("ConfigTag already exists with key cat1, however, is not a value.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> root.getValueList("cat1"));
        assertEquals("ConfigTag already exists with key cat1, however, is not a List.", ex.getMessage());
    }

    @Test
    public void testValueCreation() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValue val1 = root.getValue("val1");     // Should create new ConfigValue
        assertTrue(root.has("val1"));                 // Query should return true.
        assertSame(val1, root.getValue("val1"));      // Should be identity equal on re-get.
        assertSame(val1, root.findValue("val1"));     // Same with Find.
        assertNull(root.findCategory("val1"));        // Should return null as it's not a Category.
        assertNull(root.findValueList("val1"));       // Should return null as it's not a Value list.
        assertEquals(1, root.getChildren().size());  // Should only be 1
        assertEquals("rootTag.val1", val1.getDesc());

        IllegalStateException ex;
        ex = assertThrows(IllegalStateException.class, () -> root.getCategory("val1"));
        assertEquals("ConfigTag already exists with key val1, however, is not a category.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> root.getValueList("val1"));
        assertEquals("ConfigTag already exists with key val1, however, is not a List.", ex.getMessage());
    }

    @Test
    public void testValueListCreation() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValueList list1 = root.getValueList("list1"); // Should create new ConfigValue
        assertTrue(root.has("list1"));                            // Query should return true.
        assertSame(list1, root.getValueList("list1"));      // Should be identity equal on re-get.
        assertSame(list1, root.findValueList("list1"));           // Same with Find.
        assertNull(root.findCategory("list1"));                   // Should return null as it's not a Category.
        assertNull(root.findValue("list1"));                      // Should return null as it's not a Value.
        assertEquals(1, root.getChildren().size());               // Should only be 1
        assertEquals("rootTag.list1", list1.getDesc());

        IllegalStateException ex;
        ex = assertThrows(IllegalStateException.class, () -> root.getCategory("list1"));
        assertEquals("ConfigTag already exists with key list1, however, is not a category.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> root.getValue("list1"));
        assertEquals("ConfigTag already exists with key list1, however, is not a value.", ex.getMessage());
    }

    @Test
    public void testComments() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValue val1 = root.getValue("val1");

        assertEquals(0, val1.getComment().size());
        List<String> comment = List.of("Hello", "World");
        val1.setComment(comment);
        assertEquals(comment, val1.getComment());
        assertTrue(val1.isDirty());

        val1.setComment("Hello\nWorld");
        assertEquals(comment, val1.getComment());
        val1.setComment("Hello", "World");
        assertEquals(comment, val1.getComment());
    }

    @Test
    public void testDeletion() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValue val1 = root.getValue("val1");
        assertSame(val1, root.findValue("val1"));
        val1.delete();
        assertNull(root.findValue("val1"));

        ConfigValue val2 = root.getValue("val2");
        assertSame(val2, root.findValue("val2"));
        root.clear();
        assertNull(root.findValue("val2"));
    }

    @Test
    public void testNoTypeConfigValue() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValue val1 = root.getValue("val1");
        assertEquals(ValueType.UNKNOWN, val1.getType());
        assertTagTypeNotAssigned(val1::getBoolean);
        assertTagTypeNotAssigned(val1::getString);
        assertTagTypeNotAssigned(val1::getInt);
        assertTagTypeNotAssigned(val1::getLong);
        assertTagTypeNotAssigned(val1::getHex);
        assertTagTypeNotAssigned(val1::getDouble);

        assertTagTypeNotAssigned(val1::getDefaultBoolean);
        assertTagTypeNotAssigned(val1::getDefaultString);
        assertTagTypeNotAssigned(val1::getDefaultInt);
        assertTagTypeNotAssigned(val1::getDefaultLong);
        assertTagTypeNotAssigned(val1::getDefaultHex);
        assertTagTypeNotAssigned(val1::getDefaultDouble);
    }

    @Test
    public void testNoTypeConfigValueList() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValueList list1 = root.getValueList("list1");
        assertEquals(ValueType.UNKNOWN, list1.getType());
        assertTagTypeNotAssigned(list1::getBooleans);
        assertTagTypeNotAssigned(list1::getStrings);
        assertTagTypeNotAssigned(list1::getInts);
        assertTagTypeNotAssigned(list1::getLongs);
        assertTagTypeNotAssigned(list1::getHexs);
        assertTagTypeNotAssigned(list1::getDoubles);

        assertTagTypeNotAssigned(list1::getDefaultBooleans);
        assertTagTypeNotAssigned(list1::getDefaultStrings);
        assertTagTypeNotAssigned(list1::getDefaultInts);
        assertTagTypeNotAssigned(list1::getDefaultLongs);
        assertTagTypeNotAssigned(list1::getDefaultHexs);
        assertTagTypeNotAssigned(list1::getDefaultDoubles);
    }

    @Test
    public void testViaSetDefault() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValue val1 = root.getValue("val1");
        assertEquals(ValueType.UNKNOWN, val1.getType());
        val1.setDefaultBoolean(true);
        assertEquals(ValueType.BOOLEAN, val1.getType());
        assertTrue(val1.getDefaultBoolean());
        assertTrue(val1.getBoolean());
        assertTagHasIncompatibleType(() -> val1.setDefaultString("Hello, World"), ValueType.BOOLEAN);
        assertTagHasIncompatibleType(val1::getDefaultString, ValueType.BOOLEAN);
        assertTagHasIncompatibleType(val1::getString, ValueType.BOOLEAN);

        ConfigValue val2 = root.getValue("val2");
        assertEquals(ValueType.UNKNOWN, val2.getType());
        val2.setDefaultString("Hello, World");
        assertEquals(ValueType.STRING, val2.getType());
        assertEquals("Hello, World", val2.getDefaultString());
        assertEquals("Hello, World", val2.getString());
        assertTagHasIncompatibleType(() -> val2.setDefaultInt(22), ValueType.STRING);
        assertTagHasIncompatibleType(val2::getDefaultInt, ValueType.STRING);
        assertTagHasIncompatibleType(val2::getInt, ValueType.STRING);

        ConfigValue val3 = root.getValue("val3");
        assertEquals(ValueType.UNKNOWN, val3.getType());
        val3.setDefaultInt(22);
        assertEquals(ValueType.INT, val3.getType());
        assertEquals(22, val3.getDefaultInt());
        assertEquals(22, val3.getInt());
        assertTagHasIncompatibleType(() -> val3.setDefaultLong(44L), ValueType.INT);
        assertTagHasIncompatibleType(val3::getDefaultLong, ValueType.INT);
        assertTagHasIncompatibleType(val3::getLong, ValueType.INT);

        ConfigValue val4 = root.getValue("val4");
        assertEquals(ValueType.UNKNOWN, val4.getType());
        val4.setDefaultLong(44L);
        assertEquals(ValueType.LONG, val4.getType());
        assertEquals(44L, val4.getDefaultLong());
        assertEquals(44L, val4.getLong());
        assertTagHasIncompatibleType(() -> val4.setDefaultHex(0xFFFFFFFF), ValueType.LONG);
        assertTagHasIncompatibleType(val4::getDefaultHex, ValueType.LONG);
        assertTagHasIncompatibleType(val4::getHex, ValueType.LONG);

        ConfigValue val5 = root.getValue("val5");
        assertEquals(ValueType.UNKNOWN, val5.getType());
        val5.setDefaultHex(0xFFFFFFFF);
        assertEquals(ValueType.HEX, val5.getType());
        assertEquals(0xFFFFFFFF, val5.getDefaultHex());
        assertEquals(0xFFFFFFFF, val5.getHex());
        assertTagHasIncompatibleType(() -> val5.setDefaultDouble(4.20), ValueType.HEX);
        assertTagHasIncompatibleType(val5::getDefaultDouble, ValueType.HEX);
        assertTagHasIncompatibleType(val5::getDouble, ValueType.HEX);

        ConfigValue val6 = root.getValue("val6");
        assertEquals(ValueType.UNKNOWN, val6.getType());
        val6.setDefaultDouble(4.20);
        assertEquals(ValueType.DOUBLE, val6.getType());
        assertEquals(4.20, val6.getDefaultDouble());
        assertEquals(4.20, val6.getDouble());
        assertTagHasIncompatibleType(() -> val6.setDefaultBoolean(true), ValueType.DOUBLE);
        assertTagHasIncompatibleType(val6::getDefaultBoolean, ValueType.DOUBLE);
        assertTagHasIncompatibleType(val6::getBoolean, ValueType.DOUBLE);
    }

    @Test
    public void testListViaSetDefault() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValueList list1 = root.getValueList("list1");
        assertEquals(ValueType.UNKNOWN, list1.getType());
        BooleanList bList = new BooleanArrayList(List.of(true, false));
        list1.setDefaultBooleans(bList);
        assertEquals(ValueType.BOOLEAN, list1.getType());
        assertEquals(bList, list1.getDefaultBooleans());
        assertEquals(bList, list1.getBooleans());
        assertTagHasIncompatibleType(() -> list1.setDefaultStrings(List.of("Hello", "World")), ValueType.BOOLEAN);
        assertTagHasIncompatibleType(list1::getDefaultStrings, ValueType.BOOLEAN);
        assertTagHasIncompatibleType(list1::getStrings, ValueType.BOOLEAN);

        ConfigValueList list2 = root.getValueList("list2");
        assertEquals(ValueType.UNKNOWN, list2.getType());
        List<String> sList = List.of("Hello", "World");
        list2.setDefaultStrings(sList);
        assertEquals(ValueType.STRING, list2.getType());
        assertEquals(sList, list2.getDefaultStrings());
        assertEquals(sList, list2.getStrings());
        assertTagHasIncompatibleType(() -> list2.setDefaultInts(List.of(22, 222)), ValueType.STRING);
        assertTagHasIncompatibleType(list2::getDefaultInts, ValueType.STRING);
        assertTagHasIncompatibleType(list2::getInts, ValueType.STRING);

        ConfigValueList list3 = root.getValueList("list3");
        assertEquals(ValueType.UNKNOWN, list3.getType());
        IntList iList = new IntArrayList(List.of(22, 222));
        list3.setDefaultInts(iList);
        assertEquals(ValueType.INT, list3.getType());
        assertEquals(iList, list3.getDefaultInts());
        assertEquals(iList, list3.getInts());
        assertTagHasIncompatibleType(() -> list3.setDefaultLongs(List.of(44L, 444L)), ValueType.INT);
        assertTagHasIncompatibleType(list3::getDefaultLongs, ValueType.INT);
        assertTagHasIncompatibleType(list3::getLongs, ValueType.INT);

        ConfigValueList list4 = root.getValueList("list4");
        assertEquals(ValueType.UNKNOWN, list4.getType());
        LongList lList = new LongArrayList(List.of(44L, 444L));
        list4.setDefaultLongs(lList);
        assertEquals(ValueType.LONG, list4.getType());
        assertEquals(lList, list4.getDefaultLongs());
        assertEquals(lList, list4.getLongs());
        assertTagHasIncompatibleType(() -> list4.setDefaultHexs(List.of(0xFFFFFFFF, 0xFF00FF00)), ValueType.LONG);
        assertTagHasIncompatibleType(list4::getDefaultHexs, ValueType.LONG);
        assertTagHasIncompatibleType(list4::getHexs, ValueType.LONG);

        ConfigValueList list5 = root.getValueList("list5");
        assertEquals(ValueType.UNKNOWN, list5.getType());
        IntList hList = new IntArrayList(List.of(0xFFFFFFFF, 0xFF00FF00));
        list5.setDefaultHexs(hList);
        assertEquals(ValueType.HEX, list5.getType());
        assertEquals(hList, list5.getDefaultHexs());
        assertEquals(hList, list5.getHexs());
        assertTagHasIncompatibleType(() -> list5.setDefaultDoubles(List.of(4.20, 6.9)), ValueType.HEX);
        assertTagHasIncompatibleType(list5::getDefaultDoubles, ValueType.HEX);
        assertTagHasIncompatibleType(list5::getDoubles, ValueType.HEX);

        ConfigValueList list6 = root.getValueList("list6");
        assertEquals(ValueType.UNKNOWN, list6.getType());
        DoubleList dList = new DoubleArrayList(List.of(4.20, 6.9));
        list6.setDefaultDoubles(dList);
        assertEquals(ValueType.DOUBLE, list6.getType());
        assertEquals(dList, list6.getDefaultDoubles());
        assertEquals(dList, list6.getDoubles());
        assertTagHasIncompatibleType(() -> list6.setDefaultBooleans(List.of(true, false)), ValueType.DOUBLE);
        assertTagHasIncompatibleType(list6::getDefaultBooleans, ValueType.DOUBLE);
        assertTagHasIncompatibleType(list6::getBooleans, ValueType.DOUBLE);
    }

    @Test
    public void testViaSet() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValue val1 = root.getValue("val1");
        assertEquals(ValueType.UNKNOWN, val1.getType());
        val1.setBoolean(true);
        assertEquals(ValueType.BOOLEAN, val1.getType());
        assertTrue(val1.getBoolean());
        assertHasNoDefault(val1::getDefaultBoolean);
        assertTagHasIncompatibleType(() -> val1.setString("Hello, World"), ValueType.BOOLEAN);

        ConfigValue val2 = root.getValue("val2");
        assertEquals(ValueType.UNKNOWN, val2.getType());
        val2.setString("Hello, World");
        assertEquals(ValueType.STRING, val2.getType());
        assertEquals("Hello, World", val2.getString());
        assertHasNoDefault(val2::getDefaultString);
        assertTagHasIncompatibleType(() -> val2.setInt(22), ValueType.STRING);

        ConfigValue val3 = root.getValue("val3");
        assertEquals(ValueType.UNKNOWN, val3.getType());
        val3.setInt(22);
        assertEquals(ValueType.INT, val3.getType());
        assertEquals(22, val3.getInt());
        assertHasNoDefault(val3::getDefaultInt);
        assertTagHasIncompatibleType(() -> val3.setLong(44L), ValueType.INT);

        ConfigValue val4 = root.getValue("val4");
        assertEquals(ValueType.UNKNOWN, val4.getType());
        val4.setLong(44L);
        assertEquals(ValueType.LONG, val4.getType());
        assertEquals(44L, val4.getLong());
        assertHasNoDefault(val4::getDefaultLong);
        assertTagHasIncompatibleType(() -> val4.setHex(0xFFFFFFFF), ValueType.LONG);

        ConfigValue val5 = root.getValue("val5");
        assertEquals(ValueType.UNKNOWN, val5.getType());
        val5.setHex(0xFFFFFFFF);
        assertEquals(ValueType.HEX, val5.getType());
        assertEquals(0xFFFFFFFF, val5.getHex());
        assertHasNoDefault(val5::getDefaultHex);
        assertTagHasIncompatibleType(() -> val5.setDouble(4.20), ValueType.HEX);

        ConfigValue val6 = root.getValue("val6");
        assertEquals(ValueType.UNKNOWN, val6.getType());
        val6.setDouble(4.20);
        assertEquals(ValueType.DOUBLE, val6.getType());
        assertEquals(4.20, val6.getDouble());
        assertHasNoDefault(val6::getDefaultDouble);
        assertTagHasIncompatibleType(() -> val6.setBoolean(true), ValueType.DOUBLE);
    }

    @Test
    public void testListViaSet() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValueList list1 = root.getValueList("list1");
        assertEquals(ValueType.UNKNOWN, list1.getType());
        BooleanList bList = new BooleanArrayList(List.of(true, false));
        list1.setBooleans(bList);
        assertEquals(ValueType.BOOLEAN, list1.getType());
        assertEquals(bList, list1.getBooleans());
        assertTagHasIncompatibleType(() -> list1.setStrings(List.of("Hello", "World")), ValueType.BOOLEAN);
        assertTagHasIncompatibleType(list1::getStrings, ValueType.BOOLEAN);

        ConfigValueList list2 = root.getValueList("list2");
        assertEquals(ValueType.UNKNOWN, list2.getType());
        List<String> sList = List.of("Hello", "World");
        list2.setStrings(sList);
        assertEquals(ValueType.STRING, list2.getType());
        assertEquals(sList, list2.getStrings());
        assertTagHasIncompatibleType(() -> list2.setInts(List.of(22, 222)), ValueType.STRING);
        assertTagHasIncompatibleType(list2::getInts, ValueType.STRING);

        ConfigValueList list3 = root.getValueList("list3");
        assertEquals(ValueType.UNKNOWN, list3.getType());
        IntList iList = new IntArrayList(List.of(22, 222));
        list3.setInts(iList);
        assertEquals(ValueType.INT, list3.getType());
        assertEquals(iList, list3.getInts());
        assertTagHasIncompatibleType(() -> list3.setLongs(List.of(44L, 444L)), ValueType.INT);
        assertTagHasIncompatibleType(list3::getLongs, ValueType.INT);

        ConfigValueList list4 = root.getValueList("list4");
        assertEquals(ValueType.UNKNOWN, list4.getType());
        LongList lList = new LongArrayList(List.of(44L, 444L));
        list4.setLongs(lList);
        assertEquals(ValueType.LONG, list4.getType());
        assertEquals(lList, list4.getLongs());
        assertTagHasIncompatibleType(() -> list4.setHexs(List.of(0xFFFFFFFF, 0xFF00FF00)), ValueType.LONG);
        assertTagHasIncompatibleType(list4::getHexs, ValueType.LONG);

        ConfigValueList list5 = root.getValueList("list5");
        assertEquals(ValueType.UNKNOWN, list5.getType());
        IntList hList = new IntArrayList(List.of(0xFFFFFFFF, 0xFF00FF00));
        list5.setHexs(hList);
        assertEquals(ValueType.HEX, list5.getType());
        assertEquals(hList, list5.getHexs());
        assertTagHasIncompatibleType(() -> list5.setDoubles(List.of(4.20, 6.9)), ValueType.HEX);
        assertTagHasIncompatibleType(list5::getDoubles, ValueType.HEX);

        ConfigValueList list6 = root.getValueList("list6");
        assertEquals(ValueType.UNKNOWN, list6.getType());
        DoubleList dList = new DoubleArrayList(List.of(4.20, 6.9));
        list6.setDoubles(dList);
        assertEquals(ValueType.DOUBLE, list6.getType());
        assertEquals(dList, list6.getDoubles());
        assertTagHasIncompatibleType(() -> list6.setBooleans(List.of(true, false)), ValueType.DOUBLE);
        assertTagHasIncompatibleType(list6::getBooleans, ValueType.DOUBLE);
    }

    @Test
    public void testReset() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValue val1 = root.getValue("val1")
                .setDefaultString("Value 1")
                .setString("Value 1 Modified");

        ConfigCategory cat1 = root.getCategory("cat1");
        ConfigValue val2 = cat1.getValue("val2")
                .setDefaultString("Value 2")
                .setString("Value 2 Modified");

        ConfigCategory cat2 = cat1.getCategory("cat2");
        ConfigValue val3 = cat2.getValue("val3")
                .setDefaultString("Value 3")
                .setString("Value 3 Modified");
        assertEquals("Value 1 Modified", val1.getString());
        assertEquals("Value 2 Modified", val2.getString());
        assertEquals("Value 3 Modified", val3.getString());

        root.reset();
        assertEquals("Value 1", val1.getString());
        assertEquals("Value 2", val2.getString());
        assertEquals("Value 3", val3.getString());
    }

    @Test
    public void testListReset() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValueList list1 = root.getValueList("list1")
                .setDefaultStrings(List.of("Value 1"))
                .setStrings(List.of("Value 1 Modified"));

        ConfigCategory cat1 = root.getCategory("cat1");
        ConfigValueList list2 = cat1.getValueList("list2")
                .setDefaultStrings(List.of("Value 2"))
                .setStrings(List.of("Value 2 Modified"));

        ConfigCategory cat2 = cat1.getCategory("cat2");
        ConfigValueList list3 = cat2.getValueList("list3")
                .setDefaultStrings(List.of("Value 3"))
                .setStrings(List.of("Value 3 Modified"));
        assertEquals(List.of("Value 1 Modified"), list1.getStrings());
        assertEquals(List.of("Value 2 Modified"), list2.getStrings());
        assertEquals(List.of("Value 3 Modified"), list3.getStrings());

        root.reset();
        assertEquals(List.of("Value 1"), list1.getStrings());
        assertEquals(List.of("Value 2"), list2.getStrings());
        assertEquals(List.of("Value 3"), list3.getStrings());
    }

    @Test
    public void testIsDirty() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigCategory cat1 = root.getCategory("cat1");
        ConfigValue val1 = cat1.getValue("val1");
        assertTrue(root.isDirty());
        assertTrue(cat1.isDirty());
        root.clearDirty();
        assertFalse(root.isDirty());
        assertFalse(cat1.isDirty());

        val1.setString("Value 1");

        assertTrue(root.isDirty());
        root.clearDirty();
        assertFalse(root.isDirty());
    }

    @Test
    public void testSavePropagation() {
        boolean[] bool = { false };
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null) {
            @Override
            public void save() {
                bool[0] = true;
            }
        };

        ConfigCategory cat1 = root.getCategory("cat1");
        ConfigValue val1 = cat1.getValue("val1");
        val1.save();
        assertTrue(bool[0]);
    }

    @Test
    public void testRestrictions() {
        // Test getRestriction
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValue val = root.getValue("val1");
        assertNull(val.getRestriction());
        Restriction r = Restriction.intRange(1, 1);
        val.setRestriction(r);
        assertSame(r, val.getRestriction());
        root.clear();

        // Test IntRange properly works
        r = Restriction.intRange(1, 20);
        assertEquals("[ 1 ~ 20 ]", r.describe());
        assertTrue(r.test(ConfigValueImpl.proxy(10, ValueType.INT)));
        assertFalse(r.test(ConfigValueImpl.proxy(0, ValueType.INT)));
        assertTrue(r.test(ConfigValueImpl.proxy(20, ValueType.INT)));

        // Test DoubleRange properly works.
        r = Restriction.doubleRange(4.20, 6.90);
        assertEquals("[ 4.2 ~ 6.9 ]", r.describe());
        assertTrue(r.test(ConfigValueImpl.proxy(4.20, ValueType.DOUBLE)));
        assertFalse(r.test(ConfigValueImpl.proxy(0.0, ValueType.DOUBLE)));
        assertTrue(r.test(ConfigValueImpl.proxy(6.9, ValueType.DOUBLE)));

        // Test defaults get tested against restriction when its added.
        Throwable ex;
        ex = assertThrows(IllegalStateException.class, () ->
                root.getValue("val1")
                        .setDefaultInt(3000)
                        .setRestriction(Restriction.intRange(3, 5))
        );
        assertEquals("Default value is not accepted by Restriction.", ex.getMessage());

        // Test the same, but in reverse, when the default gets added after.
        ex = assertThrows(IllegalStateException.class, () ->
                root.getValue("val2")
                        .setRestriction(Restriction.intRange(3, 5))
                        .setDefaultInt(3000)
        );
        assertEquals("Default value is not accepted by Restriction.", ex.getMessage());
        root.clear();

        // Test Double/IntRange explode when min > max
        ex = assertThrows(IllegalArgumentException.class, () -> Restriction.intRange(300, 4));
        assertEquals("Min cannot be larger than max.", ex.getMessage());
        ex = assertThrows(IllegalArgumentException.class, () -> Restriction.doubleRange(300, 4));
        assertEquals("Min cannot be larger than max.", ex.getMessage());

        // min == max should not explode. Although, I have no idea why you would do this as it makes it unchangeable...
        assertNotNull(Restriction.intRange(3, 3));
        assertNotNull(Restriction.doubleRange(3, 3));

        // Test setting values with restrictions set.
        val = root.getValue("val1")
                .setDefaultInt(20)
                .setInt(10)
                .setRestriction(Restriction.intRange(1, 20));
        assertEquals(10, val.getInt());
        val.setInt(300);
        assertEquals(20, val.getInt());
    }

    @Test
    public void testListRestrictions() {
        // Test getRestriction
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        ConfigValueList val = root.getValueList("list1");
        assertNull(val.getRestriction());
        ListRestriction r = ListRestriction.intRange(1, 1);
        val.setRestriction(r);
        assertSame(r, val.getRestriction());
        root.clear();

        // Test IntRange properly works
        r = ListRestriction.intRange(5, 20);
        assertEquals("[ 5 ~ 20 ]", r.describe());
        assertTrue(r.test(ConfigValueListImpl.proxy(IntList.of(10), ValueType.INT)).isEmpty());
        assertFailure(2, 3, r.test(ConfigValueListImpl.proxy(IntList.of(5, 9, 3, 10), ValueType.INT)));
        assertTrue(r.test(ConfigValueListImpl.proxy(IntList.of(20), ValueType.INT)).isEmpty());

        // Test DoubleRange properly works.
        r = ListRestriction.doubleRange(4.20, 6.90);
        assertEquals("[ 4.2 ~ 6.9 ]", r.describe());
        assertTrue(r.test(ConfigValueListImpl.proxy(DoubleList.of(4.20), ValueType.DOUBLE)).isEmpty());
        assertFailure(2, 3.3D, r.test(ConfigValueListImpl.proxy(DoubleList.of(4.2, 6.9, 3.3, 5.5), ValueType.DOUBLE)));
        assertTrue(r.test(ConfigValueListImpl.proxy(DoubleList.of(6.9), ValueType.DOUBLE)).isEmpty());

        // Test defaults get tested against restriction when its added.
        Throwable ex;
        ex = assertThrows(IllegalStateException.class, () ->
                root.getValueList("list1")
                        .setDefaultInts(IntList.of(3000))
                        .setRestriction(ListRestriction.intRange(3, 5))
        );
        assertEquals("Default list value at index 0 with value 3000 was not accepted by Restriction.", ex.getMessage());

        // Test the same, but in reverse, when the default gets added after.
        ex = assertThrows(IllegalStateException.class, () ->
                root.getValueList("list2")
                        .setRestriction(ListRestriction.intRange(3, 5))
                        .setDefaultInts(IntList.of(3000))
        );
        assertEquals("Default list value at index 0 with value 3000 was not accepted by Restriction.", ex.getMessage());
        root.clear();

        // Test Double/IntRange explode when min > max
        ex = assertThrows(IllegalArgumentException.class, () -> ListRestriction.intRange(300, 4));
        assertEquals("Min cannot be larger than max.", ex.getMessage());
        ex = assertThrows(IllegalArgumentException.class, () -> ListRestriction.doubleRange(300, 4));
        assertEquals("Min cannot be larger than max.", ex.getMessage());

        // min == max should not explode. Although, I have no idea why you would do this as it makes it unchangeable...
        assertNotNull(ListRestriction.intRange(3, 3));
        assertNotNull(ListRestriction.doubleRange(3, 3));

        // Test setting values with restrictions set.
        val = root.getValueList("list1")
                .setDefaultInts(IntList.of(20, 10))
                .setInts(IntList.of(10, 20))
                .setRestriction(ListRestriction.intRange(1, 20));
        assertEquals(IntList.of(10, 20), val.getInts());
        val.setInts(List.of(10, 20, 300));
        assertEquals(IntList.of(20, 10), val.getInts());
    }

    private void assertFailure(int expectedIndex, Object expectedValue, Optional<ListRestriction.Failure> failure) {
        assertTrue(failure.isPresent());
        assertEquals(expectedIndex, failure.get().index());
        assertEquals(expectedValue, failure.get().value());
    }

    @Test
    public void testValueConversion() {
        assertIdentityConversion(true, ValueType.BOOLEAN);
        assertIdentityConversion(false, ValueType.BOOLEAN);
        assertIdentityConversion("Hello, World", ValueType.STRING);
        assertIdentityConversion(22, ValueType.INT);
        assertIdentityConversion(44L, ValueType.LONG);
        assertIdentityConversion(0xFFFFFFFF, ValueType.HEX);
        assertIdentityConversion(4.20D, ValueType.DOUBLE);

        assertEquals(true, ConfigValueImpl.convert("true", ValueType.BOOLEAN));
        assertEquals(true, ConfigValueImpl.convert("TRUE", ValueType.BOOLEAN));
        assertEquals(false, ConfigValueImpl.convert("false", ValueType.BOOLEAN));
        assertEquals(false, ConfigValueImpl.convert("FALSE", ValueType.BOOLEAN));

        assertEquals("22", ConfigValueImpl.convert(22, ValueType.STRING));
        assertEquals("4.2", ConfigValueImpl.convert(4.20D, ValueType.STRING));

        assertEquals(22, ConfigValueImpl.convert("22", ValueType.INT));
        assertEquals(22, ConfigValueImpl.convert(22F, ValueType.INT));

        assertEquals(44L, ConfigValueImpl.convert("44", ValueType.LONG));
        assertEquals(44L, ConfigValueImpl.convert(44F, ValueType.LONG));

        assertEquals(0xFFFFFFFF, ConfigValueImpl.convert("0xFFFFFFFF", ValueType.HEX));
        assertEquals(0xFFFFFFFF, ConfigValueImpl.convert(0xFFFFFFFFL, ValueType.HEX));

        assertEquals(4.20, ConfigValueImpl.convert("4.20", ValueType.DOUBLE));
        assertEquals(4.0D, ConfigValueImpl.convert(4F, ValueType.DOUBLE));

        Throwable ex;
        ex = assertThrows(AssertionError.class, () -> ConfigValueImpl.convert("potatoes", ValueType.UNKNOWN));
        assertEquals("Impossible to reach this branch.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueImpl.convert("potatoes", ValueType.BOOLEAN));
        assertEquals("Unable to convert value 'potatoes' to a Boolean.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueImpl.convert("potatoes", ValueType.INT));
        assertEquals("Unable to convert value 'potatoes' to a Integer.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueImpl.convert("potatoes", ValueType.LONG));
        assertEquals("Unable to convert value 'potatoes' to a Long.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueImpl.convert("potatoes", ValueType.HEX));
        assertEquals("Unable to convert value 'potatoes' to Hex.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueImpl.convert("potatoes", ValueType.DOUBLE));
        assertEquals("Unable to convert value 'potatoes' to a Double.", ex.getMessage());
    }

    @Test
    public void testValueReset() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValueImpl val1 = root.getValue("val1");
        val1.setDefaultInt(22);
        val1.setValue("potato");
        assertEquals(22, val1.getInt());
    }

    @Test
    public void testListValueConversion() {
        assertIdentityListConversion(new BooleanArrayList(List.of(true, false)), ValueType.BOOLEAN);
        assertIdentityListConversion(new StringList(List.of("Hello", "World")), ValueType.STRING);
        assertIdentityListConversion(new IntArrayList(List.of(22, 222)), ValueType.INT);
        assertIdentityListConversion(new LongArrayList(List.of(44L, 444L)), ValueType.LONG);
        assertIdentityListConversion(new IntArrayList(List.of(0xFFFFFFFF, 0xFF00FF00)), ValueType.HEX);
        assertIdentityListConversion(new DoubleArrayList(List.of(4.20D, 6.9D)), ValueType.DOUBLE);

        assertEquals(
                List.of(true, true, false, false),
                ConfigValueListImpl.convert(List.of("true", "TRUE", "false", "FALSE"), ValueType.BOOLEAN)
        );

        assertEquals(
                List.of("22", "4.2"),
                ConfigValueListImpl.convert(List.of(22, 4.20D), ValueType.STRING)
        );

        assertEquals(
                List.of(22, 22),
                ConfigValueListImpl.convert(List.of("22", 22F), ValueType.INT)
        );

        assertEquals(
                List.of(44L, 44L),
                ConfigValueListImpl.convert(List.of("44", 44F), ValueType.LONG)
        );

        assertEquals(
                List.of(0xFFFFFFFF, 0xFFFFFFFF),
                ConfigValueListImpl.convert(List.of("0xFFFFFFFF", 0xFFFFFFFFL), ValueType.HEX)
        );

        assertEquals(
                List.of(4.20D, 4.0D),
                ConfigValueListImpl.convert(List.of("4.20", 4F), ValueType.DOUBLE)
        );

        Throwable ex;
        ex = assertThrows(AssertionError.class, () -> ConfigValueListImpl.convert(List.of("potatoes"), ValueType.UNKNOWN));
        assertEquals("Impossible to reach this branch.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueListImpl.convert(List.of("potatoes"), ValueType.BOOLEAN));
        assertEquals("Unable to convert value 'potatoes' to a Boolean.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueListImpl.convert(List.of("potatoes"), ValueType.INT));
        assertEquals("Unable to convert value 'potatoes' to a Integer.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueListImpl.convert(List.of("potatoes"), ValueType.LONG));
        assertEquals("Unable to convert value 'potatoes' to a Long.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueListImpl.convert(List.of("potatoes"), ValueType.HEX));
        assertEquals("Unable to convert value 'potatoes' to Hex.", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> ConfigValueListImpl.convert(List.of("potatoes"), ValueType.DOUBLE));
        assertEquals("Unable to convert value 'potatoes' to a Double.", ex.getMessage());
    }

    @Test
    public void testListValueReset() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigValueListImpl list = root.getValueList("list1");
        list.setDefaultInts(List.of(1, 2, 3, 4));
        list.setValue(List.of("potato1", "potato2", "potato3", "potato4"));
        assertEquals(List.of(1, 2, 3, 4), list.getInts());
    }

    @Test
    public void testRequiresClientSync() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        assertFalse(root.requiresClientSync());

        {
            ConfigCategory cat1 = root.getCategory("cat1");
            assertFalse(root.requiresClientSync());
            assertFalse(cat1.requiresClientSync());

            ConfigValue sync1 = cat1.getValue("sync1").setString("ABC").syncTagToClient();
            ConfigValue sync2 = cat1.getValue("sync2").setString("ABC").syncTagToClient();

            assertTrue(root.requiresClientSync());
            assertTrue(cat1.requiresClientSync());
            assertTrue(sync1.requiresClientSync());
            assertTrue(sync2.requiresClientSync());
        }

        {
            ConfigCategory cat2 = root.getCategory("cat2").syncTagToClient();
            ConfigValue sync3 = cat2.getValue("sync3").setString("ABC");
            ConfigValue sync4 = cat2.getValue("sync4").setString("ABC");

            assertTrue(root.requiresClientSync());
            assertTrue(cat2.requiresClientSync());
            assertTrue(sync3.requiresClientSync());
            assertTrue(sync4.requiresClientSync());
        }

        {
            ConfigCategory cat3 = root.getCategory("cat3");
            ConfigValue sync5 = cat3.getValue("sync5").setString("ABC");
            ConfigValue sync6 = cat3.getValue("sync6").setString("ABC");

            assertTrue(root.requiresClientSync());
            assertFalse(cat3.requiresClientSync());
            assertFalse(sync5.requiresClientSync());
            assertFalse(sync6.requiresClientSync());

            cat3.syncTagToClient();

            assertTrue(root.requiresClientSync());
            assertTrue(cat3.requiresClientSync());
            assertTrue(sync5.requiresClientSync());
            assertTrue(sync6.requiresClientSync());
        }

        {
            ConfigCategory cat4 = root.getCategory("cat4");
            ConfigValue nosync1 = cat4.getValue("nosync1").setString("ABC");
            ConfigValue nosync2 = cat4.getValue("nosync2").setString("ABC");

            assertTrue(root.requiresClientSync());
            assertFalse(cat4.requiresClientSync());
            assertFalse(nosync1.requiresClientSync());
            assertFalse(nosync2.requiresClientSync());
        }
    }

    @Test
    public void testSyncCallbacks() {
        ConfigCategory root = new ConfigCategoryImpl("rootTag", null);

        ConfigCategory cat = root.getCategory("cat1");
        ConfigValueList list = cat.getValueList("list1");
        ConfigValue val1 = cat.getValue("val1");

        boolean[] wasCallbackFired = { false, false, false, false };
        root.onSync((tag, reason) -> {
            assertEquals(root, tag);
            assertEquals(reason, Reason.MANUAL);
            wasCallbackFired[0] = true;
        });
        cat.onSync((tag, reason) -> {
            assertEquals(cat, tag);
            assertEquals(reason, Reason.MANUAL);
            wasCallbackFired[1] = true;
        });
        list.onSync((tag, reason) -> {
            assertEquals(list, tag);
            assertEquals(reason, Reason.MANUAL);
            wasCallbackFired[2] = true;
        });
        val1.onSync((tag, reason) -> {
            assertEquals(val1, tag);
            assertEquals(reason, Reason.MANUAL);
            wasCallbackFired[3] = true;
        });
        root.forceSync();

        assertTrue(wasCallbackFired[0]);
        assertTrue(wasCallbackFired[1]);
        assertTrue(wasCallbackFired[2]);
        assertTrue(wasCallbackFired[3]);
    }

    @Test
    public void testSyncCallbacksPartial() {
        ConfigCategory root = new ConfigCategoryImpl("rootTag", null);

        ConfigCategory cat = root.getCategory("cat1");
        ConfigValueList list = cat.getValueList("list1");
        ConfigValue val1 = cat.getValue("val1");

        boolean[] wasCallbackFired = { false, false, false };
        root.onSync((tag, reason) -> fail("Callback should not have been fired."));
        cat.onSync((tag, reason) -> {
            assertEquals(cat, tag);
            assertEquals(reason, Reason.MANUAL);
            wasCallbackFired[0] = true;
        });
        list.onSync((tag, reason) -> {
            assertEquals(list, tag);
            assertEquals(reason, Reason.MANUAL);
            wasCallbackFired[1] = true;
        });
        val1.onSync((tag, reason) -> {
            assertEquals(val1, tag);
            assertEquals(reason, Reason.MANUAL);
            wasCallbackFired[2] = true;
        });
        cat.forceSync();

        assertTrue(wasCallbackFired[0]);
        assertTrue(wasCallbackFired[1]);
        assertTrue(wasCallbackFired[2]);
    }

    @Test
    public void testCopy() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigCategory valueCat = root.getCategory("valueCat");
        valueCat.getValue("val1").setBoolean(true);
        valueCat.getValue("val2").setString("Hello, World");
        valueCat.getValue("val3").setInt(22);
        valueCat.getValue("val4").setLong(44L);
        valueCat.getValue("val5").setHex(0xFFFFFFFF);
        valueCat.getValue("val6").setDouble(4.20);

        ConfigCategory listCat = root.getCategory("listCat");
        listCat.getValueList("list1").setBooleans(new BooleanArrayList(List.of(true, false)));
        listCat.getValueList("list2").setStrings(List.of("Hello", "World"));
        listCat.getValueList("list3").setInts(new IntArrayList(List.of(22, 222)));
        listCat.getValueList("list4").setLongs(new LongArrayList(List.of(44L, 444L)));
        listCat.getValueList("list5").setHexs(new IntArrayList(List.of(0xFFFFFFFF, 0xFF00FF00)));
        listCat.getValueList("list6").setDoubles(new DoubleArrayList(List.of(4.20, 6.9)));

        assertTrue(equals(root, root.copy()));
    }

    @Test
    public void testTagCategoryRead() {
        ByteBuf buffer = Unpooled.buffer();
        MCDataByteBuf buf = new MCDataByteBuf(buffer);

        IllegalStateException ex;
        ConfigCategoryImpl rootTag = new ConfigCategoryImpl("rootTag", null);

        { // Test unknown tag type explodes.

            buf.writeVarInt(1);
            buf.writeByte(ConfigCategoryImpl.NET_NO_TAG - 1);
            buf.writeString("cat1");
            ex = assertThrows(IllegalStateException.class, () -> rootTag.read(buf));
            assertEquals("Unknown tag network type: " + (ConfigCategoryImpl.NET_NO_TAG - 1), ex.getMessage());

        }

        rootTag.clear();
        buffer.readerIndex(0);
        buffer.writerIndex(0);

        { // Test NoTag does nothing on read.
            buf.writeVarInt(1);
            buf.writeByte(ConfigCategoryImpl.NET_NO_TAG);
            rootTag.read(buf);
            assertEquals(0, rootTag.getChildren().size());
        }

        rootTag.clear();
        buffer.readerIndex(0);
        buffer.writerIndex(0);

        { // Test NET_CAT_TAG behaves as expected.
            buf.writeVarInt(1);
            buf.writeByte(ConfigCategoryImpl.NET_CAT_TAG);
            buf.writeString("cat1");
            buf.writeVarInt(0);
            rootTag.read(buf);

            // Should read and create synthetic tag.
            ConfigCategoryImpl cat1 = rootTag.findCategory("cat1");
            assertNotNull(cat1);
            assertTrue(cat1.networkSynthetic);
            assertEquals(0, cat1.getChildren().size());

            rootTag.clear();
            buffer.readerIndex(0);

            // Should explode as we are trying to read into a non-category.
            rootTag.getValue("cat1");
            ex = assertThrows(IllegalStateException.class, () -> rootTag.read(buf));
            assertEquals("Tried to read category into ConfigValueImpl", ex.getMessage());
        }

        rootTag.clear();
        buffer.readerIndex(0);
        buffer.writerIndex(0);

        { // Test NET_VAL_TAG behaves as expected.
            buf.writeVarInt(1);
            buf.writeByte(ConfigCategoryImpl.NET_VAL_TAG);
            buf.writeString("val1");
            buf.writeEnum(ValueType.INT);
            buf.writeInt(22);
            rootTag.read(buf);

            // Should read and create synthetic tag.
            ConfigValueImpl val1 = rootTag.findValue("val1");
            assertNotNull(val1);
            assertTrue(val1.networkSynthetic);
            assertEquals(22, val1.getInt());

            rootTag.clear();
            buffer.readerIndex(0);

            // Should explode as we are trying to read into a non-value.
            rootTag.getCategory("val1");
            ex = assertThrows(IllegalStateException.class, () -> rootTag.read(buf));
            assertEquals("Tried to read value into ConfigCategoryImpl", ex.getMessage());
        }

        rootTag.clear();
        buffer.readerIndex(0);
        buffer.writerIndex(0);

        { // Test NET_VAL_LST behaves as expected.
            buf.writeVarInt(1);
            buf.writeByte(ConfigCategoryImpl.NET_VAL_LST);
            buf.writeString("lst1");
            buf.writeEnum(ValueType.INT);
            buf.writeVarInt(2);
            buf.writeInt(22);
            buf.writeInt(44);
            rootTag.read(buf);

            // Should read and create synthetic tag.
            ConfigValueListImpl lst1 = rootTag.findValueList("lst1");
            assertNotNull(lst1);
            assertTrue(lst1.networkSynthetic);
            assertEquals(List.of(22, 44), lst1.getInts());

            rootTag.clear();
            buffer.readerIndex(0);

            // Should explode as we are trying to read into a non-list.
            rootTag.getCategory("lst1");
            ex = assertThrows(IllegalStateException.class, () -> rootTag.read(buf));
            assertEquals("Tried to read list into ConfigCategoryImpl", ex.getMessage());
        }
    }

    @Test
    public void testTagValueRead() {
        ByteBuf buffer = Unpooled.buffer();
        MCDataByteBuf buf = new MCDataByteBuf(buffer);
        buf.writeEnum(ValueType.INT);
        buf.writeInt(22);

        ConfigValueImpl value = new ConfigValueImpl("test", null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> value.write(buf));
        assertEquals("Tried to write UNKNOWN tag to network", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> value.read(buf));
        assertEquals("Tried to read into an UNKNOWN tag from the network", ex.getMessage());

        buffer.readerIndex(0);

        value.setKnownType(ValueType.BOOLEAN);
        ex = assertThrows(IllegalStateException.class, () -> value.read(buf));
        assertEquals("Tried to read a INT tag from the network into a BOOLEAN tag", ex.getMessage());

        buffer.readerIndex(0);

        value.setKnownType(ValueType.INT);
        value.read(buf);
        assertEquals(22, value.getInt());

        value.resetFromNetwork();
        ex = assertThrows(IllegalStateException.class, value::getInt);
        assertEquals("No default value is set.", ex.getMessage());
    }

    @Test
    public void testTagValueListRead() {
        ByteBuf buffer = Unpooled.buffer();
        MCDataByteBuf buf = new MCDataByteBuf(buffer);
        buf.writeEnum(ValueType.INT);
        buf.writeVarInt(2);
        buf.writeInt(22);
        buf.writeInt(44);

        ConfigValueListImpl value = new ConfigValueListImpl("test", null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> value.write(buf));
        assertEquals("Tried to write UNKNOWN tag to network", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () -> value.read(buf));
        assertEquals("Tried to read into an UNKNOWN tag from the network", ex.getMessage());

        buffer.readerIndex(0);

        value.setKnownType(ValueType.BOOLEAN);
        ex = assertThrows(IllegalStateException.class, () -> value.read(buf));
        assertEquals("Tried to read a INT tag from the network into a BOOLEAN tag", ex.getMessage());

        buffer.readerIndex(0);

        value.setKnownType(ValueType.INT);
        value.read(buf);
        assertEquals(List.of(22, 44), value.getInts());

        value.resetFromNetwork();
        ex = assertThrows(IllegalStateException.class, value::getInts);
        assertEquals("No default value is set.", ex.getMessage());
    }

    @Test
    public void testReadWriteEquality() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);
        root.syncTagToClient();

        ConfigCategory valueCat = root.getCategory("valueCat");
        valueCat.getValue("val1").setBoolean(true);
        valueCat.getValue("val2").setString("Hello, World");
        valueCat.getValue("val3").setInt(22);
        valueCat.getValue("val4").setLong(44L);
        valueCat.getValue("val5").setHex(0xFFFFFFFF);
        valueCat.getValue("val6").setDouble(4.20);

        ConfigCategory listCat = root.getCategory("listCat");
        listCat.getValueList("list1").setBooleans(new BooleanArrayList(List.of(true, false)));
        listCat.getValueList("list2").setStrings(List.of("Hello", "World"));
        listCat.getValueList("list3").setInts(new IntArrayList(List.of(22, 222)));
        listCat.getValueList("list4").setLongs(new LongArrayList(List.of(44L, 444L)));
        listCat.getValueList("list5").setHexs(new IntArrayList(List.of(0xFFFFFFFF, 0xFF00FF00)));
        listCat.getValueList("list6").setDoubles(new DoubleArrayList(List.of(4.20, 6.9)));

        MCDataByteBuf buf = new MCDataByteBuf();
        root.write(buf);

        ConfigCategoryImpl root2 = new ConfigCategoryImpl("rootTag", null);
        root2.read(buf);

        assertTrue(equals(root, root2));
    }

    @Test
    public void testResetFormNetwork() {
        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigCategory valueCat = root.getCategory("valueCat")
                .syncTagToClient();
        valueCat.getValue("val1").setString("World, Hello");
        valueCat.getValue("val2").setInt(22);

        ConfigCategory listCat = root.getCategory("listCat")
                .syncTagToClient();
        listCat.getValueList("list1").setStrings(List.of("World", "Hello"));

        root.getValue("not_synced");

        MCDataByteBuf buf = new MCDataByteBuf();
        root.write(buf);

        ConfigCategoryImpl root2 = new ConfigCategoryImpl("rootTag", null);
        ConfigCategory valueCat2 = root2.getCategory("valueCat");
        ConfigValue val2 = valueCat2.getValue("val1").setString("Hello, World");

        ConfigCategory listCat2 = root2.getCategory("listCat");
        ConfigValueList list2 = listCat2.getValueList("list1").setStrings(List.of("Hello", "World"));
        root2.read(buf);

        assertEquals("World, Hello", val2.getString());
        assertNotNull(valueCat2.findTag("val2"));
        assertEquals(22, valueCat2.getValue("val2").getInt());
        assertEquals(List.of("World", "Hello"), list2.getStrings());
        assertNull(root2.findTag("not_synced"));

        root2.resetFromNetwork();

        assertEquals("Hello, World", val2.getString());
        assertNull(valueCat2.findTag("val2"));
        assertEquals(List.of("Hello", "World"), list2.getStrings());
    }

    // TODO, all Serializers need to test with this.
    @Test
    public void testConfigSaveWithNetworkSet() throws Throwable {
        Path dir = Files.createTempDirectory("config_test");
        dir.toFile().deleteOnExit();

        Path config = dir.resolve("config.cfg");

        ConfigCategoryImpl root = new ConfigCategoryImpl("rootTag", null);

        ConfigCategory valueCat = root.getCategory("valueCat")
                .syncTagToClient();
        valueCat.getValue("val1").setString("World, Hello");
        valueCat.getValue("val2").setInt(22);

        ConfigCategory listCat = root.getCategory("listCat")
                .syncTagToClient();
        listCat.getValueList("list1").setStrings(List.of("World", "Hello"));

        MCDataByteBuf buf = new MCDataByteBuf();
        root.write(buf);

        ConfigCategoryImpl root2 = new ConfigCategoryImpl("rootTag", null);
        ConfigCategory valueCat2 = root2.getCategory("valueCat");
        ConfigValue val2 = valueCat2.getValue("val1").setString("Hello, World");

        ConfigCategory listCat2 = root2.getCategory("listCat");
        ConfigValueList list2 = listCat2.getValueList("list1").setStrings(List.of("Hello", "World"));
        root2.read(buf);

        ConfigSerializer.LEGACY.save(config, root2);
        root2.resetFromNetwork();

        ConfigCategoryImpl root3 = new ConfigCategoryImpl("rootTag", null);

        ConfigSerializer.LEGACY.parse(config, root3);

        assertTrue(equals(root2, root3));
    }

    private void assertIdentityConversion(Object object, ValueType type) {
        assertSame(object, ConfigValueImpl.convert(object, type));
    }

    private void assertIdentityListConversion(List<?> object, ValueType type) {
        assertSame(object, ConfigValueListImpl.convert(object, type));
    }

    private void assertTagTypeNotAssigned(Executable executable) {
        IllegalStateException ex = assertThrows(IllegalStateException.class, executable);
        assertEquals("Tag does not have a type assigned yet.", ex.getMessage());
    }

    private void assertTagHasIncompatibleType(Executable executable, ValueType type) {
        IllegalStateException ex = assertThrows(IllegalStateException.class, executable);
        assertEquals("Tag has incompatible type: " + type, ex.getMessage());
    }

    private void assertHasNoDefault(Executable executable) {
        IllegalStateException ex = assertThrows(IllegalStateException.class, executable);
        assertEquals("No default value is set.", ex.getMessage());
    }

    static boolean equals(ConfigTag a, ConfigTag b) {
        if (a.getClass() != b.getClass()) return false;
        if (!a.getDesc().equals(b.getDesc())) return false;
        if (!a.getComment().equals(b.getComment())) return false;

        if (a instanceof ConfigCategory) return equals((ConfigCategory) a, (ConfigCategory) b);
        if (a instanceof ConfigValue) return equals((ConfigValue) a, (ConfigValue) b);
        if (a instanceof ConfigValueList) return equals((ConfigValueList) a, (ConfigValueList) b);

        throw new IllegalStateException("Nani");
    }

    private static boolean equals(ConfigCategory a, ConfigCategory b) {
        if (a.getChildren().size() != b.getChildren().size()) return false;

        List<ConfigTag> aChildren = List.copyOf(a.getChildren());
        List<ConfigTag> bChildren = List.copyOf(b.getChildren());
        for (int i = 0; i < aChildren.size(); i++) {
            if (!equals(aChildren.get(i), bChildren.get(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean equals(ConfigValue a, ConfigValue b) {
        if (a.getType() != b.getType()) return false;

        // Tags must have types when being checked.
        assertNotEquals(ValueType.UNKNOWN, a.getType());

        return switch (a.getType()) {
            case UNKNOWN -> false; // What?
            case BOOLEAN -> a.getBoolean() == b.getBoolean();
            case STRING -> a.getString().equals(b.getString());
            case INT -> a.getInt() == b.getInt();
            case LONG -> a.getLong() == b.getLong();
            case HEX -> a.getHex() == b.getHex();
            case DOUBLE -> a.getDouble() == b.getDouble();
        };
    }

    private static boolean equals(ConfigValueList a, ConfigValueList b) {
        if (a.getType() != b.getType()) return false;

        // Tags must have types when being checked.
        assertNotEquals(ValueType.UNKNOWN, a.getType());

        return switch (a.getType()) {
            case UNKNOWN -> false; // What?
            case BOOLEAN -> a.getBooleans().equals(b.getBooleans());
            case STRING -> a.getStrings().equals(b.getStrings());
            case INT -> a.getInts().equals(b.getInts());
            case LONG -> a.getLongs().equals(b.getLongs());
            case HEX -> a.getHexs().equals(b.getHexs());
            case DOUBLE -> a.getDoubles().equals(b.getDoubles());
        };
    }
}
