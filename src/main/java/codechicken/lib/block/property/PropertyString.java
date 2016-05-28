package codechicken.lib.block.property;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.PropertyHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by covers1624 on 2/6/2016.
 */
public class PropertyString extends PropertyHelper<String> {

    private final HashSet<String> valuesSet;

    public PropertyString(String name, Collection<String> values) {
        super(name, String.class);
        valuesSet = new HashSet<String>(values);
    }

    public PropertyString(String name, String... values) {
        super(name, String.class);
        valuesSet = new HashSet<String>();
        Collections.addAll(valuesSet, values);
    }

    @Override
    public Collection<String> getAllowedValues() {
        return ImmutableSet.copyOf(valuesSet);
    }

    @Override
    public Optional<String> parseValue(String value) {
        if (valuesSet.contains(value)) {
            return Optional.of(value);
        }
        return Optional.absent();
    }

    @Override
    public String getName(String value) {
        return value;
    }
}

