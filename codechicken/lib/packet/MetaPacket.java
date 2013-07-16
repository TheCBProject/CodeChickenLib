package codechicken.lib.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

public class MetaPacket extends Packet
{
    public ArrayList<Packet> packets = new ArrayList<Packet>();
    
    public MetaPacket(Packet... packets)
    {
        for(Packet p : packets)
            this.packets.add(p);
    }
    
    public MetaPacket(Collection<? extends Packet> packets)
    {
        this.packets.addAll(packets);
    }
    
    @Override
    public void readPacketData(DataInput datain)
    {
        throw new IllegalStateException("Meta packets can't be read");
    }

    @Override
    public void writePacketData(DataOutput dataout) throws IOException
    {
        for(Packet p : packets)
            p.writePacketData(dataout);
    }

    @Override
    public void processPacket(NetHandler nethandler)
    {
        for(Packet p : packets)//Memory connection
            p.processPacket(nethandler);
    }

    @Override
    public int getPacketSize()
    {
        int size = 0;
        for(Packet p : packets)
            size+=p.getPacketSize();
        return size;
    }
}
