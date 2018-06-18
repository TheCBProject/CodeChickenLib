package codechicken.lib.configuration;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

/**
 * Internal interface used by ConfigSyncManager
 * Created by covers1624 on 15/06/18.
 */
public interface ISerializableConfigTag<E extends ISerializableConfigTag> {

    void read(MCDataInput in);

    void write(MCDataOutput out);
}
