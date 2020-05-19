package codechicken.lib.capability;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by covers1624 on 17/5/20.
 */
public class SimpleCapProvider<T> implements ICapabilityProvider {

    protected final Capability<T> capability;
    protected final T instance;
    protected final LazyOptional<T> instanceOpt;

    public SimpleCapProvider(Capability<T> capability, T instance) {
        this.capability = capability;
        this.instance = instance;
        instanceOpt = LazyOptional.of(() -> this.instance);
    }

    @Nonnull
    @Override
    public <R> LazyOptional<R> getCapability(@Nonnull Capability<R> cap, @Nullable Direction side) {
        if (capability == cap) {
            return instanceOpt.cast();
        }
        return null;
    }
}
