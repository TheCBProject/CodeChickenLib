package codechicken.lib.config;

import codechicken.lib.math.MathHelper;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.Optional;

/**
 * Represents a restriction that can be applied to a {@link ConfigValueList}.
 * <p>
 * Created by covers1624 on 19/5/22.
 */
public interface ListRestriction {

    /**
     * Test the provided {@link ConfigValueList} against this {@link ListRestriction}.
     *
     * @param values The {@link ConfigValueList} to test.
     * @return The result. Empty if the test passes,
     * otherwise contains the first index and value that does not pass.
     */
    Optional<Failure> test(ConfigValueList values);

    /**
     * Describe this {@link ListRestriction} for display.
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
    static ListRestriction intRange(int min, int max) {
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
    static ListRestriction doubleRange(double min, double max) {
        return new DoubleRange(min, max);
    }

    record IntRange(int min, int max) implements ListRestriction {

        public IntRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public Optional<Failure> test(ConfigValueList configValue) {
            IntIterator iter = configValue.getInts().intIterator();
            int i = 0;
            while (iter.hasNext()) {
                int val = iter.nextInt();
                if (!MathHelper.between(min, val, max)) {
                    return Optional.of(new Failure(i, val));
                }
                i++;
            }
            return Optional.empty();
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }

    record DoubleRange(double min, double max) implements ListRestriction {

        public DoubleRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public Optional<Failure> test(ConfigValueList configValue) {
            DoubleIterator iter = configValue.getDoubles().doubleIterator();
            int i = 0;
            while (iter.hasNext()) {
                double val = iter.nextDouble();
                if (!MathHelper.between(min, val, max)) {
                    return Optional.of(new Failure(i, val));
                }
                i++;
            }
            return Optional.empty();
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }

    record Failure(int index, Object value) {
    }
}
