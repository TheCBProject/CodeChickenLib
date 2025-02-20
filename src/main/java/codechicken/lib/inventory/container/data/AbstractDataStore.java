package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

import java.util.Objects;

/**
 * The base class of a simple general purpose serializable data system.
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public abstract class AbstractDataStore<T> {

    protected T value;

    public AbstractDataStore(T defaultValue) {
        this.value = defaultValue;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        markDirty();
    }

    public void markDirty() { }

    public abstract void toBytes(MCDataOutput buf);

    public abstract void fromBytes(MCDataInput buf);

    public abstract Tag toTag(HolderLookup.Provider holders);

    public abstract void fromTag(HolderLookup.Provider holders, Tag tag);

    public boolean isSameValue(T newValue) {
        return Objects.equals(value, newValue);
    }
}

