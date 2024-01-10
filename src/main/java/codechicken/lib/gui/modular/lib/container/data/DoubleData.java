package codechicken.lib.gui.modular.lib.container.data;

import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
@Deprecated //Not sure if this will stay in CCL
public class DoubleData extends AbstractDataStore<Double> {

    public DoubleData() {
        super(0D);
    }

    public DoubleData(double defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(value);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        value = buf.readDouble();
    }

    @Override
    public Tag toTag() {
        return DoubleTag.valueOf(value);
    }

    @Override
    public void fromTag(Tag tag) {
        value = ((NumericTag) tag).getAsDouble();
    }
}
