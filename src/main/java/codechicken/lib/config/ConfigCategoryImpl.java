package codechicken.lib.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.covers1624.quack.collection.StreamableIterable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 17/4/22.
 */
public class ConfigCategoryImpl extends AbstractConfigTag<ConfigCategory> implements ConfigCategory {

    static final byte NET_NO_TAG = (byte) 255;
    static final byte NET_CAT_TAG = 0;
    static final byte NET_VAL_TAG = 1;
    static final byte NET_VAL_LST = 2;

    private final Map<String, AbstractConfigTag<?>> tagMap = new LinkedHashMap<>();

    public ConfigCategoryImpl(String name, @Nullable ConfigCategoryImpl parent) {
        super(name, parent);
    }

    @Override
    public boolean has(String name) {
        return tagMap.containsKey(name);
    }

    @Nullable
    @Override
    public AbstractConfigTag<?> findTag(String name) {
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
    public ConfigCategory syncTagToClient() {
        for (AbstractConfigTag<?> child : tagMap.values()) {
            child.syncTagToClient();
        }

        return super.syncTagToClient();
    }

    @Override
    public boolean requiresClientSync() {
        for (AbstractConfigTag<?> child : tagMap.values()) {
            if (child.requiresClientSync()) {
                return true;
            }
        }
        return super.requiresClientSync();
    }

    @Override
    public void runSync(ConfigCallback.Reason reason) {
        super.runSync(reason);
        for (AbstractConfigTag<?> child : tagMap.values()) {
            child.runSync(reason);
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

    @Override
    public ConfigCategoryImpl copy(@Nullable ConfigCategoryImpl parent) {
        ConfigCategoryImpl clone = new ConfigCategoryImpl(getName(), parent);
        clone.setComment(List.copyOf(getComment()));
        clone.syncToClient = syncToClient;

        for (Map.Entry<String, AbstractConfigTag<?>> entry : tagMap.entrySet()) {
            clone.tagMap.put(entry.getKey(), entry.getValue().copy(clone));
        }

        return clone;
    }

    @Override
    public void write(MCDataOutput out) {
        out.writeVarInt(tagMap.size());
        for (AbstractConfigTag<?> child : tagMap.values()) {
            if (!child.requiresClientSync()) {
                out.writeByte(NET_NO_TAG);
                continue;
            }

            if (child instanceof ConfigCategory) {
                out.writeByte(NET_CAT_TAG);
            } else if (child instanceof ConfigValue) {
                out.writeByte(NET_VAL_TAG);
            } else if (child instanceof ConfigValueList) {
                out.writeByte(NET_VAL_LST);
            } else {
                throw new IllegalStateException("Unknown tag class. " + child.getClass());
            }
            out.writeString(child.getName());
            child.write(out);
        }
    }

    @Override
    public void read(MCDataInput in) {
        int numTags = in.readVarInt();
        for (int i = 0; i < numTags; i++) {
            byte t = in.readByte();
            if (t == NET_NO_TAG) continue;

            String name = in.readString();
            ConfigTag tag = findTag(name);
            switch (t) {
                default -> throw new IllegalStateException("Unknown tag network type: " + t);
                case NET_CAT_TAG -> {
                    if (tag != null && !(tag instanceof ConfigCategoryImpl)) throw new IllegalStateException("Tried to read category into " + tag.getClass().getSimpleName());
                    if (tag == null) {
                        ConfigCategoryImpl cat = getCategory(name);
                        cat.networkSynthetic = true;
                        tag = cat;
                    }
                }
                case NET_VAL_TAG -> {
                    if (tag != null && !(tag instanceof ConfigValueImpl)) throw new IllegalStateException("Tried to read value into " + tag.getClass().getSimpleName());
                    if (tag == null) {
                        ConfigValueImpl cat = getValue(name);
                        cat.networkSynthetic = true;
                        tag = cat;
                    }
                }
                case NET_VAL_LST -> {
                    if (tag != null && !(tag instanceof ConfigValueListImpl)) throw new IllegalStateException("Tried to read list into " + tag.getClass().getSimpleName());
                    if (tag == null) {
                        ConfigValueListImpl cat = getValueList(name);
                        cat.networkSynthetic = true;
                        tag = cat;
                    }
                }
            }
            tag.read(in);
        }
    }

    @Override
    public void resetFromNetwork() {
        assert !networkSynthetic;
        for (Iterator<AbstractConfigTag<?>> iterator = tagMap.values().iterator(); iterator.hasNext(); ) {
            AbstractConfigTag<?> child = iterator.next();
            if (child.networkSynthetic) {
                iterator.remove();
                continue;
            }
            child.resetFromNetwork();
        }
    }
}
