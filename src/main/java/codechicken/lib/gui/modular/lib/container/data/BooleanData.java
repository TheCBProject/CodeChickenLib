package codechicken.lib.gui.modular.lib.container.data;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
@Deprecated //Not sure if this will stay in CCL
public class BooleanData extends AbstractDataStore<Boolean> {

    public BooleanData() {
        super(false);
    }

    public BooleanData(boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
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
