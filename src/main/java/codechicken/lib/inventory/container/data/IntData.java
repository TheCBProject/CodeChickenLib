package codechicken.lib.inventory.container.data;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

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
    public Tag toTag(HolderLookup.Provider holders) {
        return IntTag.valueOf(value);
    }

    @Override
    public void fromTag(HolderLookup.Provider holders, Tag tag) {
        value = ((NumericTag) tag).getAsInt();
    }
}
