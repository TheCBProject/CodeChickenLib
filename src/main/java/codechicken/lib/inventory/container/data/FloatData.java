package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

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
    public void toBytes(MCDataOutput buf) {
        buf.writeFloat(value);
    }

    @Override
    public void fromBytes(MCDataInput buf) {
        value = buf.readFloat();
    }

    @Override
    public Tag toTag() {
        return FloatTag.valueOf(value);
    }

    @Override
    public void fromTag(Tag tag) {
        value = ((NumericTag) tag).getAsFloat();
    }
}
