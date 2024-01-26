package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class BooleanData extends AbstractDataStore<Boolean> {

    public BooleanData() {
        super(false);
    }

    public BooleanData(boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(MCDataOutput buf) {
        buf.writeBoolean(value);
    }

    @Override
    public void fromBytes(MCDataInput buf) {
        value = buf.readBoolean();
    }

    @Override
    public Tag toTag() {
        return ByteTag.valueOf(value);
    }

    @Override
    public void fromTag(Tag tag) {
        value = ((NumericTag) tag).getAsByte() != 0;
    }
}
