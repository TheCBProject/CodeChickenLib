package codechicken.lib.inventory.container.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class DoubleData extends AbstractDataStore<Double> {

    public DoubleData() {
        super(0D);
    }

    public DoubleData(double defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = buf.readDouble();
    }

    @Override
    public Tag toTag(HolderLookup.Provider holders) {
        return DoubleTag.valueOf(value);
    }

    @Override
    public void fromTag(HolderLookup.Provider holders, Tag tag) {
        value = ((NumericTag) tag).doubleValue();
    }
}
