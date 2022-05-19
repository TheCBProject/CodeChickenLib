package codechicken.lib.config;

import codechicken.lib.math.MathHelper;

import java.util.function.Predicate;

/**
 * Represents a restriction that can be applied to a {@link ConfigValue}.
 * <p>
 * Created by covers1624 on 17/4/22.
 */
public interface Restriction extends Predicate<ConfigValue> {

    @Override
    boolean test(ConfigValue configValue);

    /**
     * Describe this {@link Restriction} for display.
     *
     * @return The description.
     */
    String describe();

    /**
     * Represents an Integer range restriction.
     * The value must pass {@code min <= value <= max}
     *
     * @param min The minimum value. (inclusive)
     * @param max The maximum value. (inclusive)
     * @return The Restriction.
     */
    static Restriction intRange(int min, int max) {
        return new IntRange(min, max);
    }

    /**
     * Represents a Double range restriction.
     * The value must pass {@code min <= value <= max}
     *
     * @param min The minimum value. (inclusive)
     * @param max The maximum value. (inclusive)
     * @return The Restriction.
     */
    static Restriction doubleRange(double min, double max) {
        return new DoubleRange(min, max);
    }

    record IntRange(int min, int max) implements Restriction {

        public IntRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public boolean test(ConfigValue configValue) {
            return MathHelper.between(min, configValue.getInt(), max);
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }

    record DoubleRange(double min, double max) implements Restriction {

        public DoubleRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public boolean test(ConfigValue configValue) {
            return MathHelper.between(min, configValue.getDouble(), max);
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }
}
