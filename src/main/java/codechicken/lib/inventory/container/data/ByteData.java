package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

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
    public void toBytes(MCDataOutput buf) {
        buf.writeByte(value);
    }

    @Override
    public void fromBytes(MCDataInput buf) {
        value = buf.readByte();
    }

    @Override
    public Tag toTag(HolderLookup.Provider holders) {
        return ByteTag.valueOf(value);
    }

    @Override
    public void fromTag(HolderLookup.Provider holders, Tag tag) {
        value = ((NumericTag) tag).getAsByte();
    }
}
