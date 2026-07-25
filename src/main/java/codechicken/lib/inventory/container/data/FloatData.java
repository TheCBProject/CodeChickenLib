package codechicken.lib.inventory.container.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class FloatData extends AbstractDataStore<Float> {

    public FloatData() {
        super(0F);
    }

    public FloatData(float defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeFloat(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = buf.readFloat();
    }

    @Override
    public Tag toTag(HolderLookup.Provider holders) {
        return FloatTag.valueOf(value);
    }

    @Override
    public void fromTag(HolderLookup.Provider holders, Tag tag) {
        value = ((NumericTag) tag).floatValue();
    }
}
