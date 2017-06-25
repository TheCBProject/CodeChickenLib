package codechicken.lib.inventory.container;

import codechicken.lib.packet.PacketCustom;

public interface IContainerSyncVar {

    boolean changed();

    void reset();

    void writeChange(PacketCustom packet);

    void readChange(PacketCustom packet);
}
