package codechicken.lib.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * A {@link Capability.IStorage} implementation, that does nothing.
 * Useful in cases where you don't need the capability to be saved.
 * <p>
 * Created by covers1624 on 5/5/20.
 */
public class NullStorage<T> implements Capability.IStorage<T> {

    public static final NullStorage<?> INSTANCE = new NullStorage<>();

    //@formatter:off
    @Override public INBT writeNBT(Capability<T> capability, T instance, Direction side) { return null; }
    @Override public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) { }
    //@formatter:on
}
