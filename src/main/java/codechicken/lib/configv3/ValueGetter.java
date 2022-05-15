package codechicken.lib.configv3;

/**
 * Created by covers1624 on 15/5/22.
 */
public interface ValueGetter {

    boolean getBoolean();

    String getString();

    int getInt();

    long getLong();

    int getHex();

    double getDouble();

    static ValueGetter of(ConfigValue value) {
        // @formatter:off
        return new ValueGetter() {
            @Override public boolean getBoolean() { return value.getBoolean(); }
            @Override public String getString() { return value.getString(); }
            @Override public int getInt() { return value.getInt(); }
            @Override public long getLong() { return value.getLong(); }
            @Override public int getHex() { return value.getHex(); }
            @Override public double getDouble() { return value.getDouble(); }
        };
        // @formatter:on
    }

    static ValueGetter of(Object obj, ValueType type) {
        return new ValueGetter() {
            private Object checkValue(ValueType expected) {
                if (expected != type) throw new IllegalStateException("Expected value of type " + expected + " Got " + type);
                return obj;
            }

            // @formatter:off
            @Override public boolean getBoolean() { return (boolean) checkValue(ValueType.BOOLEAN); }
            @Override public String getString() { return (String) checkValue(ValueType.STRING); }
            @Override public int getInt() { return (int) checkValue(ValueType.INT); }
            @Override public long getLong() { return (long) checkValue(ValueType.LONG); }
            @Override public int getHex() { return (int) checkValue(ValueType.HEX); }
            @Override public double getDouble() { return (double) checkValue(ValueType.DOUBLE); }
            // @formatter:on
        };
    }
}
