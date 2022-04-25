package codechicken.lib.configv3;

import net.covers1624.quack.collection.StreamableIterable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 17/4/22.
 */
public class ConfigCategoryImpl extends AbstractConfigTag<ConfigCategory> implements ConfigCategory {

    private final Map<String, AbstractConfigTag<?>> tagMap = new LinkedHashMap<>();

    public ConfigCategoryImpl(String name, @Nullable ConfigCategory parent) {
        super(name, parent);
    }

    @Override
    public boolean has(String name) {
        return tagMap.containsKey(name);
    }

    @Nullable
    @Override
    public ConfigTag findTag(String name) {
        return tagMap.get(name);
    }

    @Override
    public ConfigCategoryImpl getCategory(String name) {
        AbstractConfigTag<?> tag = tagMap.get(name);
        if (tag != null && !(tag instanceof ConfigCategory)) {
            throw new IllegalStateException("ConfigTag already exists with key " + name + ", however, is not a category.");
        }
        if (tag == null) {
            tag = new ConfigCategoryImpl(name, this);
            tagMap.put(name, tag);
        }
        return unsafeCast(tag);
    }

    @Nullable
    @Override
    public ConfigCategoryImpl findCategory(String name) {
        ConfigTag existing = tagMap.get(name);
        if (!(existing instanceof ConfigCategory)) return null;

        return unsafeCast(existing);
    }

    @Override
    public ConfigValueImpl getValue(String name) {
        AbstractConfigTag<?> tag = tagMap.get(name);
        if (tag != null && !(tag instanceof ConfigValue)) {
            throw new IllegalStateException("ConfigTag already exists with key " + name + ", however, is not a value.");
        }
        if (tag == null) {
            tag = new ConfigValueImpl(name, this);
            tagMap.put(name, tag);
        }
        return unsafeCast(tag);
    }

    @Nullable
    @Override
    public ConfigValueImpl findValue(String name) {
        ConfigTag existing = tagMap.get(name);
        if (!(existing instanceof ConfigValue)) return null;

        return unsafeCast(existing);
    }

    @Override
    public ConfigValueListImpl getValueList(String name) {
        AbstractConfigTag<?> tag = tagMap.get(name);
        if (tag != null && !(tag instanceof ConfigValueList)) {
            throw new IllegalStateException("ConfigTag already exists with key " + name + ", however, is not a List.");
        }
        if (tag == null) {
            tag = new ConfigValueListImpl(name, this);
            tagMap.put(name, tag);
        }
        return unsafeCast(tag);
    }

    @Nullable
    @Override
    public ConfigValueListImpl findValueList(String name) {
        ConfigTag existing = tagMap.get(name);
        if (!(existing instanceof ConfigValueList)) return null;

        return unsafeCast(existing);
    }

    @Override
    public Collection<ConfigTag> getChildren() {
        return Collections.unmodifiableCollection(tagMap.values());
    }

    @Override
    public ConfigCategory delete(String name) {
        tagMap.remove(name);
        return this;
    }

    @Override
    public void clear() {
        tagMap.clear();
    }

    @Override
    public void reset() {
        for (ConfigTag value : tagMap.values()) {
            value.reset();
        }
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || StreamableIterable.of(getChildren()).anyMatch(ConfigTag::isDirty);
    }

    @Override
    public void clearDirty() {
        super.clearDirty();
        for (AbstractConfigTag<?> child : tagMap.values()) {
            child.clearDirty();
        }
    }
}
