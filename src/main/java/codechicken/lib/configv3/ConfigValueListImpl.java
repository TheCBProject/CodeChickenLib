package codechicken.lib.configv3;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanLists;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import net.covers1624.quack.collection.StreamableIterable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 19/4/22.
 */
public class ConfigValueListImpl extends AbstractConfigTag<ConfigValueList> implements ConfigValueList {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValueListImpl.class);

    @Nullable
    private List<?> defaultValue = null;
    @Nullable
    private List<?> value = null;
    private ValueType type = ValueType.UNKNOWN;

    public ConfigValueListImpl(String name, @Nullable ConfigCategory parent) {
        super(name, parent);
    }

    @Override
    public ValueType getType() {
        return type;
    }

    @Override
    public BooleanList getBooleans() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.BOOLEAN) throw new IllegalStateException("Tag has incompatible type: " + type);

        value = tryConvert(value, type);
        if (value == null) return getDefaultBooleans();

        return BooleanLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public List<String> getStrings() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.STRING) throw new IllegalStateException("Tag has incompatible type: " + type);

        value = tryConvert(value, type);
        if (value == null) return getDefaultStrings();

        return Collections.unmodifiableList(unsafeCast(value));
    }

    @Override
    public IntList getInts() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.INT) throw new IllegalStateException("Tag has incompatible type: " + type);

        value = tryConvert(value, type);
        if (value == null) return getDefaultInts();

        return IntLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public LongList getLongs() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.LONG) throw new IllegalStateException("Tag has incompatible type: " + type);

        value = tryConvert(value, type);
        if (value == null) return getDefaultLongs();

        return LongLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public IntList getHexs() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.HEX) throw new IllegalStateException("Tag has incompatible type: " + type);

        value = tryConvert(value, type);
        if (value == null) return getDefaultHexs();

        return IntLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public DoubleList getDoubles() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.DOUBLE) throw new IllegalStateException("Tag has incompatible type: " + type);

        value = tryConvert(value, type);
        if (value == null) return getDefaultDoubles();

        return DoubleLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public ConfigValueList setBooleans(List<Boolean> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.BOOLEAN;
        if (type != ValueType.BOOLEAN) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setValue(values);
    }

    @Override
    public ConfigValueList setStrings(List<String> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.STRING;
        if (type != ValueType.STRING) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setValue(values);
    }

    @Override
    public ConfigValueList setInts(List<Integer> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.INT;
        if (type != ValueType.INT) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setValue(values);
    }

    @Override
    public ConfigValueList setLongs(List<Long> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.LONG;
        if (type != ValueType.LONG) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setValue(values);
    }

    @Override
    public ConfigValueList setHexs(List<Integer> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.HEX;
        if (type != ValueType.HEX) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setValue(values);
    }

    @Override
    public ConfigValueList setDoubles(List<Double> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.DOUBLE;
        if (type != ValueType.DOUBLE) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setValue(values);
    }

    @Override
    public BooleanList getDefaultBooleans() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.BOOLEAN) throw new IllegalStateException("Tag has incompatible type: " + type);
        if (defaultValue == null) throw new IllegalStateException("No default value is set.");

        return BooleanLists.unmodifiable(unsafeCast(defaultValue));
    }

    @Override
    public List<String> getDefaultStrings() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.STRING) throw new IllegalStateException("Tag has incompatible type: " + type);
        if (defaultValue == null) throw new IllegalStateException("No default value is set.");

        return Collections.unmodifiableList(unsafeCast(defaultValue));
    }

    @Override
    public IntList getDefaultInts() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.INT) throw new IllegalStateException("Tag has incompatible type: " + type);
        if (defaultValue == null) throw new IllegalStateException("No default value is set.");

        return IntLists.unmodifiable(unsafeCast(defaultValue));
    }

    @Override
    public LongList getDefaultLongs() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.LONG) throw new IllegalStateException("Tag has incompatible type: " + type);
        if (defaultValue == null) throw new IllegalStateException("No default value is set.");

        return LongLists.unmodifiable(unsafeCast(defaultValue));
    }

    @Override
    public IntList getDefaultHexs() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.HEX) throw new IllegalStateException("Tag has incompatible type: " + type);
        if (defaultValue == null) throw new IllegalStateException("No default value is set.");

        return IntLists.unmodifiable(unsafeCast(defaultValue));
    }

    @Override
    public DoubleList getDefaultDoubles() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.DOUBLE) throw new IllegalStateException("Tag has incompatible type: " + type);
        if (defaultValue == null) throw new IllegalStateException("No default value is set.");

        return DoubleLists.unmodifiable(unsafeCast(defaultValue));
    }

    @Override
    public ConfigValueList setDefaultBooleans(List<Boolean> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.BOOLEAN;
        if (type != ValueType.BOOLEAN) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setDefaultValue(new BooleanArrayList(values));
    }

    @Override
    public ConfigValueList setDefaultStrings(List<String> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.STRING;
        if (type != ValueType.STRING) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setDefaultValue(new StringList(values));
    }

    @Override
    public ConfigValueList setDefaultInts(List<Integer> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.INT;
        if (type != ValueType.INT) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setDefaultValue(new IntArrayList(values));
    }

    @Override
    public ConfigValueList setDefaultLongs(List<Long> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.LONG;
        if (type != ValueType.LONG) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setDefaultValue(new LongArrayList(values));
    }

    @Override
    public ConfigValueList setDefaultHexs(List<Integer> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.HEX;
        if (type != ValueType.HEX) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setDefaultValue(new IntArrayList(values));
    }

    @Override
    public ConfigValueList setDefaultDoubles(List<Double> values) {
        if (type == ValueType.UNKNOWN) type = ValueType.DOUBLE;
        if (type != ValueType.DOUBLE) throw new IllegalStateException("Tag has incompatible type: " + type);

        return setDefaultValue(new DoubleArrayList(values));
    }

    @Override
    public void reset() {
        if (defaultValue != null) {
            value = null;
            // TODO set pending change counter for onChange callbacks?
        }
    }

    public ConfigValueList setValue(List<?> value) {
        this.value = value;
        if (type != ValueType.UNKNOWN) {
            this.value = tryConvert(this.value, type);
        }
        dirty = true;
        return this;
    }

    public ConfigValueList setDefaultValue(List<?> value) {
        defaultValue = value;
        dirty = true;
        return this;
    }

    public void setKnownType(ValueType type) {
        this.type = type;
    }

    @Nullable
    List<?> tryConvert(@Nullable List<?> list, ValueType type) {
        if (list == null) return null;

        try {
            return convert(list, type);
        } catch (IllegalStateException ex) {
            LOGGER.warn("Failed to convert config list tag {} to {}. Resetting to default.", getDesc(), type, ex);
            dirty = true;
            return null;
        }
    }

    static List<?> convert(List<?> list, ValueType type) {
        return switch (type) {
            case UNKNOWN -> throw new AssertionError("Impossible to reach this branch.");
            case BOOLEAN -> convertToBooleans(list);
            case STRING -> convertToStrings(list);
            case INT -> convertToInts(list);
            case LONG -> convertToLongs(list);
            case HEX -> convertToHexs(list);
            case DOUBLE -> convertToDoubles(list);
        };
    }

    private static BooleanList convertToBooleans(List<?> list) {
        if (list instanceof BooleanList bList) return bList;

        BooleanArrayList bList = new BooleanArrayList(list.size());
        for (Object o : list) {
            bList.add(ConfigValueImpl.convertToBoolean(o).booleanValue());
        }
        return bList;
    }

    private static List<String> convertToStrings(List<?> list) {
        if (list instanceof StringList sList) return sList;

        return new StringList(StreamableIterable.of(list).map(Object::toString).toList());
    }

    private static IntList convertToInts(List<?> list) {
        if (list instanceof IntList iList) return iList;

        IntList iList = new IntArrayList(list.size());
        for (Object o : list) {
            iList.add(ConfigValueImpl.convertToInteger(o).intValue());
        }
        return iList;
    }

    private static LongList convertToLongs(List<?> list) {
        if (list instanceof LongList lList) return lList;

        LongList lList = new LongArrayList(list.size());
        for (Object o : list) {
            lList.add(ConfigValueImpl.convertToLong(o).longValue());
        }
        return lList;
    }

    private static IntList convertToHexs(List<?> list) {
        if (list instanceof IntList iList) return iList;

        IntList iList = new IntArrayList(list.size()) { };
        for (Object o : list) {
            iList.add(ConfigValueImpl.convertToHex(o).intValue());
        }
        return iList;
    }

    private static DoubleList convertToDoubles(List<?> list) {
        if (list instanceof DoubleList dList) return dList;

        DoubleList iList = new DoubleArrayList(list.size()) { };
        for (Object o : list) {
            iList.add(ConfigValueImpl.convertToDouble(o).doubleValue());
        }
        return iList;
    }

    static class StringList extends ArrayList<String> {

        public StringList(Collection<? extends String> c) {
            super(c);
        }
    }
}
