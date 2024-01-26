package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class FluidData extends AbstractDataStore<FluidStack> {

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
    public void toBytes(MCDataOutput buf) {
        buf.writeFluidStack(value);
    }

    @Override
    public void fromBytes(MCDataInput buf) {
        value = buf.readFluidStack();
    }

    @Override
    public Tag toTag() {
        return value.writeToNBT(new CompoundTag());
    }

    @Override
    public void fromTag(Tag tag) {
        value = FluidStack.loadFluidStackFromNBT((CompoundTag) tag);
    }

    @Override
    public boolean isSameValue(FluidStack newValue) {
        return value.equals(newValue);
    }
}
