package codechicken.lib.gui.modular.lib.container.data;

import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

/**
 * The base class of a simple general purpose serializable data system.
 *
 * Created by brandon3055 on 08/09/2023
 */
@Deprecated //Not sure if this will stay in CCL
public abstract class AbstractDataStore<T> {

    protected T value;

    public AbstractDataStore(T defaultValue) {
        this.value = defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        markDirty();
    }

    public void markDirty(){}

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void fromBytes(FriendlyByteBuf buf);

    public abstract Tag toTag();

    public abstract void fromTag(Tag tag);

    public boolean isSameValue(T newValue) {
        return Objects.equals(value, newValue);
    }
}

