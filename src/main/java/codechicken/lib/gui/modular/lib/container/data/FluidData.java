package codechicken.lib.gui.modular.lib.container.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by brandon3055 on 09/09/2023
 */
@Deprecated //Not sure if this will stay in CCL
public class FluidData extends AbstractDataStore<FluidStack> {

    public FluidData() {
        super(FluidStack.EMPTY);
    }

    public FluidData(FluidStack defaultValue) {
        super(defaultValue);
    }

    @Override
    public void setValue(FluidStack value) {
        this.value = value.copy();
        markDirty();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        value.writeToPacket(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        value = FluidStack.readFromPacket(buf);
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
