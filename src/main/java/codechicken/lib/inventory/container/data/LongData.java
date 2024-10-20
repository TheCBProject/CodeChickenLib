package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

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
    public Tag toTag(HolderLookup.Provider holders) {
        return LongTag.valueOf(value);
    }

    @Override
    public void fromTag(HolderLookup.Provider holders, Tag tag) {
        value = ((NumericTag) tag).getAsLong();
    }
}
