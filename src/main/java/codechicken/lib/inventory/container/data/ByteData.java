package codechicken.lib.inventory.container.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class ByteData extends AbstractDataStore<Byte> {

    public ByteData() {
        super((byte) 0);
    }

    public ByteData(byte defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeByte(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = buf.readByte();
    }

    @Override
    public Tag toTag(HolderLookup.Provider holders) {
        return ByteTag.valueOf(value);
    }

    @Override
    public void fromTag(HolderLookup.Provider holders, Tag tag) {
        value = ((NumericTag) tag).byteValue();
    }
}
