package codechicken.lib.configv3;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

/**
 * TODO do we want to explode if someone tires to set a value when it is set from the network?
 * <p>
 * Created by covers1624 on 18/4/22.
 */
public class ConfigValueImpl extends AbstractConfigTag<ConfigValue> implements ConfigValue {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValueImpl.class);

    @Nullable
    private Object defaultValue = null;
    @Nullable
    private Object value = null;
    @Nullable
    private Object networkValue = null;
    private ValueType type = ValueType.UNKNOWN;
    @Nullable
    private Restriction restriction;

    public ConfigValueImpl(String name, @Nullable ConfigCategoryImpl parent) {
        super(name, parent);
    }

    static ConfigValue proxy(Object value, ValueType type) {
        ConfigValueImpl val = new ConfigValueImpl("proxy", null);
        val.setKnownType(type);
        val.setValue(value);
        return val;
    }

    @Override
    public ValueType getType() {
        return type;
    }

    @Override
    public boolean getBoolean() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.BOOLEAN) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        if (networkValue != null) {
            return (Boolean) networkValue;
        }

        value = tryConvert(value, type);
        if (value == null) {
            return getDefaultBoolean();
        }

        return (Boolean) value;
    }

    @Override
    public String getString() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.STRING) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        if (networkValue != null) {
            return (String) networkValue;
        }

        value = tryConvert(value, type);
        if (value == null) {
            return getDefaultString();
        }

        return (String) value;
    }

    @Override
    public int getInt() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.INT) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        if (networkValue != null) {
            return (Integer) networkValue;
        }

        value = tryConvert(value, type);
        if (value == null) {
            return getDefaultInt();
        }

        return (Integer) value;
    }

    @Override
    public long getLong() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.LONG) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        if (networkValue != null) {
            return (Long) networkValue;
        }

        value = tryConvert(value, type);
        if (value == null) {
            return getDefaultLong();
        }

        return (Long) value;
    }

    @Override
    public int getHex() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.HEX) throw new IllegalStateException("Tag has incompatible type: " + type);

        if (networkValue != null) {
            return (Integer) networkValue;
        }

        value = tryConvert(value, type);
        if (value == null) {
            return getDefaultHex();
        }

        return (Integer) value;
    }

    @Override
    public double getDouble() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.DOUBLE) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        if (networkValue != null) {
            return (Double) networkValue;
        }

        value = tryConvert(value, type);
        if (value == null) {
            return getDefaultDouble();
        }

        return (Double) value;
    }

    @Override
    public ConfigValue setBoolean(boolean value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.BOOLEAN;
        }
        if (type != ValueType.BOOLEAN) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setValue(value);
    }

    @Override
    public ConfigValue setString(String value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.STRING;
        }
        if (type != ValueType.STRING) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setValue(value);
    }

    @Override
    public ConfigValue setInt(int value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.INT;
        }
        if (type != ValueType.INT) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setValue(value);
    }

    @Override
    public ConfigValue setLong(long value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.LONG;
        }
        if (type != ValueType.LONG) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setValue(value);
    }

    @Override
    public ConfigValue setHex(int value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.HEX;
        }
        if (type != ValueType.HEX) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setValue(value);
    }

    @Override
    public ConfigValue setDouble(double value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.DOUBLE;
        }
        if (type != ValueType.DOUBLE) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setValue(value);
    }

    @Override
    public boolean getDefaultBoolean() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.BOOLEAN) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }
        if (defaultValue == null) {
            throw new IllegalStateException("No default value is set.");
        }

        return (Boolean) defaultValue;
    }

    @Override
    public String getDefaultString() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.STRING) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }
        if (defaultValue == null) {
            throw new IllegalStateException("No default value is set.");
        }
        return (String) defaultValue;
    }

    @Override
    public int getDefaultInt() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.INT) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }
        if (defaultValue == null) {
            throw new IllegalStateException("No default value is set.");
        }

        return (Integer) defaultValue;
    }

    @Override
    public long getDefaultLong() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.LONG) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }
        if (defaultValue == null) {
            throw new IllegalStateException("No default value is set.");
        }

        return (Long) defaultValue;
    }

    @Override
    public int getDefaultHex() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.HEX) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }
        if (defaultValue == null) {
            throw new IllegalStateException("No default value is set.");
        }

        return (Integer) defaultValue;
    }

    @Override
    public double getDefaultDouble() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }
        if (type != ValueType.DOUBLE) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }
        if (defaultValue == null) {
            throw new IllegalStateException("No default value is set.");
        }

        return (Double) defaultValue;
    }

    @Override
    public ConfigValue setDefaultBoolean(boolean value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.BOOLEAN;
        }
        if (type != ValueType.BOOLEAN) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setDefaultValue(value);
    }

    @Override
    public ConfigValue setDefaultString(String value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.STRING;
        }
        if (type != ValueType.STRING) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setDefaultValue(value);
    }

    @Override
    public ConfigValue setDefaultInt(int value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.INT;
        }
        if (type != ValueType.INT) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setDefaultValue(value);
    }

    @Override
    public ConfigValue setDefaultLong(long value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.LONG;
        }
        if (type != ValueType.LONG) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setDefaultValue(value);
    }

    @Override
    public ConfigValue setDefaultHex(int value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.HEX;
        }
        if (type != ValueType.HEX) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setDefaultValue(value);
    }

    @Override
    public ConfigValue setDefaultDouble(double value) {
        if (type == ValueType.UNKNOWN) {
            type = ValueType.DOUBLE;
        }
        if (type != ValueType.DOUBLE) {
            throw new IllegalStateException("Tag has incompatible type: " + type);
        }

        return setDefaultValue(value);
    }

    @Override
    public ConfigValue setRestriction(Restriction restriction) {
        this.restriction = restriction;
        if (defaultValue != null && !restriction.test(proxy(defaultValue, type))) {
            throw new IllegalStateException("Default value is not accepted by Restriction.");
        }
        return this;
    }

    @Nullable
    @Override
    public Restriction getRestriction() {
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
    public AbstractConfigTag<ConfigValue> copy(@Nullable ConfigCategoryImpl parent) {
        ConfigValueImpl clone = new ConfigValueImpl(getName(), parent);
        clone.comment = List.copyOf(comment);
        clone.defaultValue = defaultValue;
        clone.value = value;
        clone.type = type;
        clone.restriction = restriction;

        return clone;
    }

    @Override
    public void write(MCDataOutput out) {
        if (type == ValueType.UNKNOWN) throw new IllegalStateException("Tried to write UNKNOWN tag to network");
        out.writeEnum(type);
        switch (type) {
            case BOOLEAN -> out.writeBoolean(getBoolean());
            case STRING -> out.writeString(getString());
            case INT -> out.writeInt(getInt()); // TODO varint? signedVarint?
            case LONG -> out.writeLong(getLong()); // TODO varlong? signedVarlong?
            case HEX -> out.writeInt(getHex()); // TODO varint? signedVarint?
            case DOUBLE -> out.writeDouble(getDouble());
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
            case BOOLEAN -> networkValue = in.readBoolean();
            case STRING -> networkValue = in.readString();
            case INT, HEX -> networkValue = in.readInt();
            case LONG -> networkValue = in.readLong();
            case DOUBLE -> networkValue = in.readDouble();
        }
    }

    @Override
    public void resetFromNetwork() {
        networkValue = null;
    }

    public ConfigValue setValue(Object value) {
        this.value = value;
        if (type != ValueType.UNKNOWN) {
            this.value = tryConvert(this.value, type);
        }
        dirty = true;
        return this;
    }

    public ConfigValue setDefaultValue(Object value) {
        if (restriction != null && !restriction.test(proxy(value, type))) {
            throw new IllegalStateException("Default value is not accepted by Restriction.");
        }
        defaultValue = value;
        dirty = true;
        return this;
    }

    public Object getRawValue() {
        if (type == ValueType.UNKNOWN) {
            throw new IllegalStateException("Tag does not have a type assigned yet.");
        }

        value = tryConvert(value, type);
        if (value == null) {
            if (defaultValue == null) {
                throw new IllegalStateException("Default value not set.");
            }
            return defaultValue;
        }

        return value;
    }

    public void setKnownType(ValueType type) {
        this.type = type;
    }

    @Nullable
    Object tryConvert(@Nullable Object object, ValueType type) {
        if (object == null) {
            return null;
        }

        try {
            object = convert(object, type);
        } catch (IllegalStateException ex) {
            LOGGER.error("Failed to convert config tag {} to {}. Resetting to default.", getDesc(), type, ex);
            dirty = true;
            return null;
        }
        if (restriction != null && !restriction.test(proxy(object, type))) {
            LOGGER.error("Value violates restriction, Resetting to default. Tag {}, Value {}, Restriction {}", getDesc(), object, restriction.describe());
            dirty = true;
            return null;
        }
        return object;
    }

    static Object convert(Object object, ValueType type) {
        return switch (type) {
            case UNKNOWN -> throw new AssertionError("Impossible to reach this branch.");
            case BOOLEAN -> convertToBoolean(object);
            case STRING -> object.toString();
            case INT -> convertToInteger(object);
            case LONG -> convertToLong(object);
            case HEX -> convertToHex(object);
            case DOUBLE -> convertToDouble(object);
        };
    }

    static Boolean convertToBoolean(Object object) {
        if (object instanceof Boolean bool) return bool;

        String str = object.toString().toLowerCase(Locale.ROOT);
        return switch (str) {
            case "true" -> Boolean.TRUE;
            case "false" -> Boolean.FALSE;
            default -> throw new IllegalStateException("Unable to convert value '" + str + "' to a Boolean.");
        };
    }

    static Integer convertToInteger(Object object) {
        if (object instanceof Integer i) return i;
        if (object instanceof Number number) return number.intValue();

        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to convert value '" + object + "' to a Integer.", ex);
        }
    }

    static Long convertToLong(Object object) {
        if (object instanceof Long l) return l;
        if (object instanceof Number number) return number.longValue();

        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to convert value '" + object + "' to a Long.", ex);
        }
    }

    static Integer convertToHex(Object object) {
        if (object instanceof Integer i) return i;
        if (object instanceof Number number) return number.intValue();

        try {
            return (int) Long.parseLong(object.toString().replace("0x", ""), 16);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to convert value '" + object + "' to Hex.", ex);
        }
    }

    static Double convertToDouble(Object object) {
        if (object instanceof Double d) return d;
        if (object instanceof Number number) return number.doubleValue();

        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to convert value '" + object + "' to a Double.", ex);
        }
    }
}
