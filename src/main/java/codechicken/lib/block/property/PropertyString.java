package codechicken.lib.block.property;

import codechicken.lib.math.MathHelper;
import codechicken.lib.util.ArrayUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.minecraft.block.properties.PropertyHelper;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by covers1624 on 2/6/2016.
 */
public class PropertyString extends PropertyHelper<String> {

    private final Set<String> valuesSet;
    private final String[] metaLookup;

    public PropertyString(String name, Collection<String> values) {
        super(name, String.class);
        metaLookup = values.stream().map(String::intern).toArray(String[]::new);
        valuesSet = new HashSet<>();
        Collections.addAll(valuesSet, metaLookup);
    }

    public PropertyString(String name, String... values) {
        super(name, String.class);
        metaLookup = Arrays.stream(values).map(String::intern).toArray(String[]::new);
        valuesSet = new HashSet<>();
        Collections.addAll(valuesSet, metaLookup);
    }

    public List<String> values() {
        return Lists.newArrayList(metaLookup);
    }

    @Nonnull
    @Override
    public Collection<String> getAllowedValues() {
        return Collections.unmodifiableSet(valuesSet);
    }

    @SuppressWarnings ("Guava")
    @Nonnull
    @Override
    public Optional<String> parseValue(@Nonnull String value) {
        if (valuesSet.contains(value.intern())) {
            return Optional.of(value.intern());
        }
        return Optional.absent();
    }

    @Nonnull
    @Override
    public String getName(@Nonnull String value) {
        return value.intern();
    }

    public int toMeta(String value) {
        return ArrayUtils.indexOf(metaLookup, value.intern());
    }

    public String fromMeta(int meta) {
        if (!MathHelper.between(0, meta, metaLookup.length)) {
            throw new IllegalArgumentException(String.format("Meta data out of bounds. Meta: %s, Lookup: %s.", meta, Joiner.on(",").join(metaLookup)));
        }
        return metaLookup[meta];
    }
}

