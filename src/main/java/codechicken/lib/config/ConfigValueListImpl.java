package codechicken.lib.config;

import codechicken.lib.config.ListRestriction.Failure;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
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

import java.util.*;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * TODO do we want to explode if someone tires to set a value when it is set from the network?
 * Created by covers1624 on 19/4/22.
 */
public class ConfigValueListImpl extends AbstractConfigTag<ConfigValueList> implements ConfigValueList {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValueListImpl.class);

    @Nullable
    private List<?> defaultValue = null;
    @Nullable
    private List<?> value = null;
    @Nullable
    private List<?> networkValue = null;
    private ValueType type = ValueType.UNKNOWN;
    @Nullable
    private ListRestriction restriction;

    public ConfigValueListImpl(String name, @Nullable ConfigCategoryImpl parent) {
        super(name, parent);
    }

    static ConfigValueList proxy(List<?> value, ValueType type) {
        ConfigValueListImpl list = new ConfigValueListImpl("proxy", null);
        list.setKnownType(type);
        list.setValue(value);
        return list;
    }

    @Override
    public ValueType getType() {
        return type;
    }

    @Override
    public BooleanList getBooleans() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.BOOLEAN) throw new IllegalStateException("Tag has incompatible type: " + type);

        if (networkValue != null) {
            return BooleanLists.unmodifiable(unsafeCast(networkValue));
        }

        value = tryConvert(value, type);
        if (value == null) return getDefaultBooleans();

        return BooleanLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public List<String> getStrings() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.STRING) throw new IllegalStateException("Tag has incompatible type: " + type);

        if (networkValue != null) {
            return Collections.unmodifiableList(unsafeCast(networkValue));
        }

        value = tryConvert(value, type);
        if (value == null) return getDefaultStrings();

        return Collections.unmodifiableList(unsafeCast(value));
    }

    @Override
    public IntList getInts() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.INT) throw new IllegalStateException("Tag has incompatible type: " + type);

        if (networkValue != null) {
            return IntLists.unmodifiable(unsafeCast(networkValue));
        }

        value = tryConvert(value, type);
        if (value == null) return getDefaultInts();

        return IntLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public LongList getLongs() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.LONG) throw new IllegalStateException("Tag has incompatible type: " + type);

        if (networkValue != null) {
            return LongLists.unmodifiable(unsafeCast(networkValue));
        }

        value = tryConvert(value, type);
        if (value == null) return getDefaultLongs();

        return LongLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public IntList getHexs() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.HEX) throw new IllegalStateException("Tag has incompatible type: " + type);

        if (networkValue != null) {
            return IntLists.unmodifiable(unsafeCast(networkValue));
        }

        value = tryConvert(value, type);
        if (value == null) return getDefaultHexs();

        return IntLists.unmodifiable(unsafeCast(value));
    }

    @Override
    public DoubleList getDoubles() {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tag does not have a type assigned yet.");
        if (type != ValueType.DOUBLE) throw new IllegalStateException("Tag has incompatible type: " + type);

        if (networkValue != null) {
            return DoubleLists.unmodifiable(unsafeCast(networkValue));
        }

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
    public ConfigValueList setRestriction(ListRestriction restriction) {
        this.restriction = restriction;
        if (defaultValue != null) {
            Optional<Failure> failureOpt = restriction.test(proxy(defaultValue, type));
            if (failureOpt.isPresent()) {
                Failure failure = failureOpt.get();
                throw new IllegalStateException("Default list value at index " + failure.index() + " with value " + failure.value() + " was not accepted by Restriction.");
            }
        }
        return this;
    }

    @Nullable
    @Override
    public ListRestriction getRestriction() {
        return restriction;
    }

    @Override
    public void reset() {
        if (defaultValue != null) {
            value = null;
            dirty = true;
        }
    }

    @Override
    public AbstractConfigTag<ConfigValueList> copy(@Nullable ConfigCategoryImpl parent) {
        ConfigValueListImpl clone = new ConfigValueListImpl(getName(), parent);
        clone.comment = List.copyOf(comment);
        clone.defaultValue = defaultValue != null ? List.copyOf(defaultValue) : null;
        clone.value = value != null ? List.copyOf(value) : null;
        clone.type = type;

        return clone;
    }

    @Override
    public void write(MCDataOutput out) {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tried to write UNKNOWN tag to network");
        out.writeEnum(type);
        switch (type) {
            case STRING -> {
                List<String> values = getStrings();
                out.writeVarInt(values.size());
                for (String s : values) {
                    out.writeString(s);
                }
            }
            case BOOLEAN -> out.writeBooleans(getBooleans().toBooleanArray());
            case INT -> out.writeInts(getInts().toIntArray());
            case LONG -> out.writeLongs(getLongs().toLongArray());
            case HEX -> out.writeInts(getHexs().toIntArray());
            case DOUBLE -> out.writeDoubles(getDoubles().toDoubleArray());
        }
    }

    @Override
    public void read(MCDataInput in) {
        ValueType netType = in.readEnum(ValueType.class);
        if (networkSynthetic) {
            type = netType;
        }
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tried to read into an UNKNOWN tag from the network");
        if (netType != type) throw new IllegalStateException("Tried to read a " + netType + " tag from the network into a " + type + " tag");

        switch (type) {
            case STRING -> networkValue = readStringList(in);
            case BOOLEAN -> networkValue = new BooleanArrayList(in.readBooleans());
            case INT, HEX -> networkValue = new IntArrayList(in.readInts());
            case LONG -> networkValue = new LongArrayList(in.readLongs());
            case DOUBLE -> networkValue = new DoubleArrayList(in.readDoubles());
        }
    }

    @Override
    public void resetFromNetwork() {
        networkValue = null;
    }

    private List<String> readStringList(MCDataInput in) {
        int len = in.readVarInt();
        List<String> values = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            values.add(in.readString());
        }
        return values;
    }

    public ConfigValueList setValue(List<?> value) {
        this.value = value;
        if (type != ValueType.UNKNOWN) {
            this.value = tryConvert(this.value, type);
        }
        dirty = true;
        return this;
    }

    public List<?> getRawValue() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }

        value = tryConvert(value, type);
        if (value == null) {
            if (defaultValue == null) throw new IllegalStateException("Default value not set.");
            return defaultValue;
        }

        return value;
    }

    public ConfigValueList setDefaultValue(List<?> value) {
        if (restriction != null) {
            Optional<Failure> failureOpt = restriction.test(proxy(value, type));
            if (failureOpt.isPresent()) {
                Failure failure = failureOpt.get();
                throw new IllegalStateException("Default list value at index " + failure.index() + " with value " + failure.value() + " was not accepted by Restriction.");
            }
        }
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
            list = convert(list, type);
        } catch (IllegalStateException ex) {
            LOGGER.error("Failed to convert config list tag {} to {}. Resetting to default.", getDesc(), type, ex);
            dirty = true;
            return null;
        }
        if (restriction != null) {
            Optional<Failure> failureOpt = restriction.test(proxy(list, type));
            if (failureOpt.isPresent()) {
                Failure failure = failureOpt.get();
                LOGGER.error(
                        "List violates restriction. Resetting to default. Tag {}, Index {} Value {}, Restriction {}, All values, {}",
                        getDesc(),
                        failure.index(),
                        failure.value(),
                        restriction.describe(),
                        list
                );
                dirty = true;
                return null;
            }
        }
        return list;
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
