package codechicken.lib.packet;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by covers1624 on 6/4/2016.
 */
public interface INBTPacketTile {

    /**
     * Writes data to the packet.
     *
     * @param tagCompound Tag to write to.
     */
    void writePacketData(NBTTagCompound tagCompound);

    /**
     * Reads data from the packet.
     *
     * @param tagCompound Tag to read from.
     */
    void readPacketData(NBTTagCompound tagCompound);

}
