package codechicken.lib.config;

import codechicken.lib.config.parser.ConfigFile;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.util.ThrowingBiConsumer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Created by covers1624 on 18/07/2017.
 */
public class ConfigTagImpl implements ConfigTag {

    private final ConfigFile configFile;
    public String name;
    public String version;
    public Map<String, ConfigTagImpl> children;
    public ConfigTagImpl parent;
    public boolean dirty;

    public List<String> comment;
    public TagType type;

    public TagType listType;

    public Object value;
    public Object defaultValue;

    public boolean syncToClient;
    public ThrowingBiConsumer<ConfigTag, SyncType, SyncException> syncCallback;

    public ConfigTagImpl(String name, ConfigTagImpl parent) {
        this(null, name, parent);
    }

    public ConfigTagImpl(ConfigFile configFile, String name, ConfigTagImpl parent) {
        this.configFile = configFile;
        this.name = name;
        this.parent = parent;
        children = new LinkedHashMap<>();
        comment = new LinkedList<>();
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Nullable
    @Override
    public ConfigTag getParent() {
        return parent;
    }

    @Override
    public boolean isCategory() {
        return value == null || !children.isEmpty();
    }

    @Override
    public boolean isValue() {
        return children.isEmpty() && value != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getQualifiedName() {
        List<String> list = new ArrayList<>();
        ConfigTag parent = this;
        while ((parent = parent.getParent()) != null) {
            list.add(parent.getName());
        }
        StringBuilder s = new StringBuilder();
        for (int i = list.size() - 1; i >= 0; i--) {
            s.append(list.get(i)).append(".");
        }
        s.append(getName());
        return s.toString();
    }

    @Override
    public boolean isDirty() {
        return dirty || (hasParent() && parent.isDirty());
    }

    @Override
    public ConfigTagImpl markDirty() {
        dirty = true;//Mark us a dirty.
        if (hasParent()) {//If we are a child, we need to mark the parent dirty.
            parent.markDirty();
        }
        return this;
    }

    @Override
    public void clear() {
        if (isCategory()) {
            children.clear();
            markDirty();
        }
    }

    protected void onSave() {
        dirty = false;
        children.values().forEach(ConfigTagImpl::onSave);
    }

    @Override
    public void save() {
        if (hasParent()) {
            getParent().save();
            return;
        }
        if (isDirty()) {
            configFile.save(this);
            onSave();
        }
    }

    @Override
    public ConfigTag load() {
        if (hasParent()) {
            throw new IllegalStateException("Must call load on parent.");
        }
        configFile.load();
        return this;
    }

    @Override
    public boolean hasTag(String name) {
        return value == null && children.containsKey(name);
    }

    @Override
    public ConfigTagImpl getTag(String name) {
        addTagCheck();
        return children.computeIfAbsent(name, s -> new ConfigTagImpl(s, this));
    }

    @Override
    @Nullable
    public ConfigTagImpl getTagIfPresent(String name) {
        addTagCheck();
        return children.get(name);
    }

    @Override
    public ConfigTagImpl deleteTag(String name) {
        if (isCategory()) {
            children.remove(name);
        }
        return this;
    }

    @Override
    public List<String> getChildNames() {
        return new LinkedList<>(children.keySet());
    }

    @Override
    public void walkTags(Consumer<ConfigTag> consumer) {
        if (!isCategory()) {
            throw new UnsupportedOperationException("Unable to walk a value.");
        }
        for (ConfigTagImpl tag : children.values()) {
            consumer.accept(tag);
            if (tag.isCategory()) {
                tag.walkTags(consumer);
            }
        }
    }

    @Override
    public ConfigTagImpl resetToDefault() {
        if (isCategory()) {
            children.values().forEach(ConfigTagImpl::resetToDefault);
        } else {
            value = defaultValue;
        }
        return this;
    }

    @Override
    public String getTagVersion() {
        return version;
    }

    @Override
    public ConfigTagImpl setTagVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public TagType getTagType() {
        return type;
    }

    @Override
    public TagType getListType() {
        return listType;
    }

    @Override
    public Object getRawValue() {
        return value;
    }

    @Override
    public ConfigTagImpl setComment(List<String> lines) {
        comment = new LinkedList<>(lines);
        markDirty();
        return this;
    }

    //region Getters.
    @Override
    public boolean getBoolean() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.BOOLEAN) {
            throw new UnsupportedOperationException("ConfigTag is not of a Boolean type, Actual: " + type);
        } else if (!(value instanceof Boolean)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (Boolean) value;
    }

    @Override
    public String getString() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.STRING) {
            throw new UnsupportedOperationException("ConfigTag is not of a String type, Actual: " + type);
        } else if (!(value instanceof String)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (String) value;
    }

    @Override
    public int getInt() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.INT) {
            throw new UnsupportedOperationException("ConfigTag is not of a Integer type, Actual: " + type);
        } else if (!(value instanceof Integer)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (Integer) value;
    }

    @Override
    public int getHex() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.HEX) {
            throw new UnsupportedOperationException("ConfigTag is not of a Hex type, Actual: " + type);
        } else if (!(value instanceof Integer)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (Integer) value;
    }

    @Override
    public double getDouble() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.DOUBLE) {
            throw new UnsupportedOperationException("ConfigTag is not of a Double type, Actual: " + type);
        } else if (!(value instanceof Double)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (Double) value;
    }

    @Override
    public ConfigTagImpl setDefaultBoolean(boolean value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setBoolean(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultString(String value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setString(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultInt(int value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setInt(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultHex(int value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setHex(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultDouble(double value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setDouble(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setBoolean(boolean value) {
        setValueCheck();
        type = TagType.BOOLEAN;
        this.value = value;
        markDirty();
        return this;
    }

    @Override
    public ConfigTagImpl setString(String value) {
        setValueCheck();
        type = TagType.STRING;
        this.value = value;
        markDirty();
        return this;
    }

    @Override
    public ConfigTagImpl setInt(int value) {
        setValueCheck();
        type = TagType.INT;
        this.value = value;
        markDirty();
        return this;
    }

    @Override
    public ConfigTagImpl setHex(int value) {
        setValueCheck();
        this.value = value;
        type = TagType.HEX;
        markDirty();
        return this;
    }

    @Override
    public ConfigTagImpl setDouble(double value) {
        setValueCheck();
        type = TagType.DOUBLE;
        this.value = value;
        markDirty();
        return this;
    }
    //endregion

    //region Lists
    @Override
    @SuppressWarnings ("unchecked")
    public List<Boolean> getBooleanList() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.BOOLEAN) {
            throw new UnsupportedOperationException("List is not of a Boolean type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<String> getStringList() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.STRING) {
            throw new UnsupportedOperationException("List is not of a String type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<Integer> getIntList() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.INT) {
            throw new UnsupportedOperationException("List is not of a Integer type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<Integer> getHexList() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.HEX) {
            throw new UnsupportedOperationException("List is not of a Hex type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<Double> getDoubleList() {
        if (value == null) {
            throw new IllegalStateException("Tag in a weird state, value is null, did you set a default?");
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.DOUBLE) {
            throw new UnsupportedOperationException("List is not of a Double type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    public ConfigTagImpl setDefaultBooleanList(List<Boolean> value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setBooleanList(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultStringList(List<String> value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setStringList(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultIntList(List<Integer> value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setIntList(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultHexList(List<Integer> value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setHexList(value);
        }

        return this;
    }

    @Override
    public ConfigTagImpl setDefaultDoubleList(List<Double> value) {
        setDefaultCheck();

        defaultValue = value;
        if (this.value == null) {
            setDoubleList(value);
        }

        return this;
    }

    protected void setList(List<?> value) {
        setValueCheck();
        type = TagType.LIST;
        this.value = value;
        markDirty();
    }

    @Override
    public ConfigTagImpl setBooleanList(List<Boolean> value) {
        setList(value);
        listType = TagType.BOOLEAN;
        return this;
    }

    @Override
    public ConfigTagImpl setStringList(List<String> value) {
        setList(value);
        listType = TagType.STRING;
        return this;
    }

    @Override
    public ConfigTagImpl setIntList(List<Integer> value) {
        setList(value);
        listType = TagType.INT;
        return this;
    }

    @Override
    public ConfigTagImpl setHexList(List<Integer> value) {
        setList(value);
        listType = TagType.HEX;
        return this;
    }

    @Override
    public ConfigTagImpl setDoubleList(List<Double> value) {
        setList(value);
        listType = TagType.DOUBLE;
        return this;
    }
    //endregion

    @Override
    public ConfigTag copy() {
        return copy(null);
    }

    @Override
    public ConfigTag copy(ConfigTag parent) {
        ConfigTagImpl copy = new ConfigTagImpl(name, (ConfigTagImpl) parent);
        copy.version = version;
        copy.comment = new LinkedList<>(comment);
        if (isCategory()) {
            for (Entry<String, ConfigTagImpl> entry : children.entrySet()) {
                copy.children.put(entry.getKey(), (ConfigTagImpl) entry.getValue().copy(copy));
            }
        } else {
            copy.type = type;
            copy.listType = listType;

            copy.value = type.copy(value);
            if (defaultValue != null) {
                copy.defaultValue = type.copy(defaultValue);
            }
        }
        copy.syncToClient = syncToClient;
        copy.syncCallback = syncCallback;
        return copy;
    }

    @Override
    public ConfigTag copyFrom(ConfigTag other) {
        if (isCategory()) {
            for (Entry<String, ConfigTagImpl> entry : children.entrySet()) {
                ConfigTag otherTag = other.getTagIfPresent(entry.getKey());
                if (otherTag == null) {
                    throw new IllegalArgumentException("copyFrom called with a tag that does not have the same children. Missing: " + entry.getKey());
                }
                entry.getValue().copyFrom(otherTag);
            }
        } else {
            value = type.copy(((ConfigTagImpl) other).value);
        }
        return this;
    }

    @Override
    public ConfigTagImpl setSyncToClient() {
        children.values().forEach(ConfigTag::setSyncToClient);
        syncToClient = true;
        return this;
    }

    @Override
    public ConfigTagImpl setSyncCallback(ThrowingBiConsumer<ConfigTag, SyncType, SyncException> consumer) {
        syncCallback = consumer;
        return this;
    }

    @Override
    public boolean requiresSync() {
        for (ConfigTagImpl tag : children.values()) {
            if (tag.requiresSync()) {
                return true;
            }
        }
        return syncToClient;
    }

    @Override
    public void runSync(SyncType type) throws SyncException {
        if (syncCallback != null) {
            syncCallback.accept(this, type);
        }
        for (ConfigTagImpl tag : children.values()) {
            tag.runSync(type);
        }
    }

    @Override
    public void read(MCDataInput in) {
        if (isCategory()) {
            int numChildren = in.readVarInt();
            for (int i = 0; i < numChildren; i++) {
                String name = in.readString();
                ConfigTagImpl found = children.get(name);
                if (found == null) {
                    throw new IllegalArgumentException("read called with data that does not align to this tag, Missing: " + name);
                }
                found.read(in);
            }
        } else {
            value = type.read(in, listType);
        }
    }

    @Override
    public void write(MCDataOutput out) {
        if (isCategory()) {
            out.writeVarInt(children.size());
            for (Entry<String, ConfigTagImpl> entry : children.entrySet()) {
                out.writeString(entry.getKey());
                entry.getValue().write(out);
            }
        } else {
            type.write(out, listType, value);
        }
    }

    protected void addTagCheck() {
        if (value != null) {
            throw new UnsupportedOperationException("Unable to get a child tag for a tag that has a value.");
        }
    }

    protected void setValueCheck() {
        if (!children.isEmpty()) {
            throw new UnsupportedOperationException("Unable to set the value for a tag that has children.");
        }
    }

    protected void setDefaultCheck() {
        if (defaultValue != null) {
            throw new IllegalStateException("Unable to set the default value of a tag that already has a default value.");
        }
    }
}
