package codechicken.lib.gui.modular.lib.container.data;

import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
@Deprecated //Not sure if this will stay in CCL
public class LongData extends AbstractDataStore<Long> {

    public LongData() {
        super(0L);
    }

    public LongData(long defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarLong(value);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
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
