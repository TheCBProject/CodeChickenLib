package codechicken.lib.configv3;

import codechicken.lib.math.MathHelper;

import java.util.function.Predicate;

/**
 * Represents a restriction that can be applied to
 * a {@link ConfigValue} of a given type.
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

    static Restriction intRange(int min, int max) {
        return new Restriction() {
            @Override
            public boolean test(ConfigValue configValue) {
                return MathHelper.between(min, configValue.getInt(), max);
            }

            @Override
            public String describe() {
                return "[ " + min + " ~ " + max + " ]";
            }
        };
    }

}
