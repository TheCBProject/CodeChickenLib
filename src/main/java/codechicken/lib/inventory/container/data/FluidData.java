package codechicken.lib.inventory.container.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class FluidData extends AbstractDataStore<FluidStack> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FluidData.class);

    public FluidData() {
        super(FluidStack.EMPTY);
    }

    public FluidData(FluidStack defaultValue) {
        super(defaultValue);
    }

    @Override
    public void set(FluidStack value) {
        this.value = value.copy();
        markDirty();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.cc$writeWithRegistryCodec(FluidStack.OPTIONAL_STREAM_CODEC, value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = buf.cc$readWithRegistryCodec(FluidStack.OPTIONAL_STREAM_CODEC);
    }

    @Override
    public Tag toTag(HolderLookup.Provider holders) {
        return FluidStack.OPTIONAL_CODEC
                .encodeStart(holders.createSerializationContext(NbtOps.INSTANCE), value)
                .getOrThrow();
    }

    @Override
    public void fromTag(HolderLookup.Provider holders, Tag tag) {
        value = FluidStack.OPTIONAL_CODEC
                .parse(holders.createSerializationContext(NbtOps.INSTANCE), tag)
                .getOrThrow();
    }

    @Override
    public boolean isSameValue(FluidStack newValue) {
        return value.equals(newValue);
    }
}
