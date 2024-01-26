package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class IntData extends AbstractDataStore<Integer> {

    public IntData() {
        super(0);
    }

    public IntData(int defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(MCDataOutput buf) {
        buf.writeVarInt(value);
    }

    @Override
    public void fromBytes(MCDataInput buf) {
        value = buf.readVarInt();
    }

    @Override
    public Tag toTag() {
        return IntTag.valueOf(value);
    }

    @Override
    public void fromTag(Tag tag) {
        value = ((NumericTag) tag).getAsInt();
    }
}
