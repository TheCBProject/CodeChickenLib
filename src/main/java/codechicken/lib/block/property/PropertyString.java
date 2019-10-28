package codechicken.lib.block.property;

import net.minecraft.state.Property;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 2/6/2016.
 */
public class PropertyString extends Property<String> {

    private final Set<String> valuesSet;

    public PropertyString(String name, Collection<String> values) {
        super(name, String.class);
        valuesSet = values.stream().map(String::intern).collect(Collectors.toSet());
    }

    public PropertyString(String name, String... values) {
        super(name, String.class);
        valuesSet = Arrays.stream(values).map(String::intern).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Collection<String> getAllowedValues() {
        return Collections.unmodifiableSet(valuesSet);
    }

    @Nonnull
    @Override
    public Optional<String> parseValue(@Nonnull String value) {
        if (valuesSet.contains(value.intern())) {
            return Optional.of(value.intern());
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String getName(@Nonnull String value) {
        return value.intern();
    }
}

