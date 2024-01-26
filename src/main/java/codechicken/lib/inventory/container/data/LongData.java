package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class LongData extends AbstractDataStore<Long> {

    public LongData() {
        super(0L);
    }

    public LongData(long defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(MCDataOutput buf) {
        buf.writeVarLong(value);
    }

    @Override
    public void fromBytes(MCDataInput buf) {
        value = buf.readVarLong();
    }

    @Override
    public Tag toTag() {
        return LongTag.valueOf(value);
    }

    @Override
    public void fromTag(Tag tag) {
        value = ((NumericTag) tag).getAsLong();
    }
}
