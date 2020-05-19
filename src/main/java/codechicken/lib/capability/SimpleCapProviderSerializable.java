package codechicken.lib.capability;

import net.minecraft.nbt.INBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Created by covers1624 on 16/5/20.
 */
public class SimpleCapProviderSerializable<T> extends SimpleCapProvider<T> implements ICapabilitySerializable<INBT> {

    public SimpleCapProviderSerializable(Capability<T> capability, T instance) {
        super(capability, instance);
    }

    @Override
    public INBT serializeNBT() {
        return capability.writeNBT(instance, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        capability.readNBT(instance, null, nbt);
    }
}
