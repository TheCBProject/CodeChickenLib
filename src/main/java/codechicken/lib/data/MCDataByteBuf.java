package codechicken.lib.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;

/**
 * Created by covers1624 on 4/16/20.
 */
public class MCDataByteBuf implements MCDataInput, MCDataOutput {

    protected final ByteBuf buf;

    public MCDataByteBuf(ByteBuf buf) {
        this.buf = buf;
    }

    public PacketBuffer toPacketBuffer() {
        return buf instanceof PacketBuffer ? (PacketBuffer) buf : new PacketBuffer(buf);
    }

    //@formatter:off
    @Override public byte readByte() { return buf.readByte(); }
    @Override public short readUByte() { return buf.readUnsignedByte(); }
    @Override public char readChar() { return buf.readChar(); }
    @Override public short readShort() { return buf.readShort(); }
    @Override public int readUShort() { return buf.readUnsignedShort(); }
    @Override public int readInt() { return buf.readInt(); }
    @Override public long readLong() { return buf.readLong(); }
    @Override public float readFloat() { return buf.readFloat(); }
    @Override public double readDouble() { return buf.readDouble(); }
    @Override public boolean readBoolean() { return buf.readBoolean(); }
    @Override public MCDataOutput writeByte(int b) { buf.writeByte(b); return this; }
    @Override public MCDataOutput writeChar(int c) { buf.writeChar(c); return this; }
    @Override public MCDataOutput writeShort(int s) { buf.writeShort(s); return this; }
    @Override public MCDataOutput writeInt(int i) { buf.writeInt(i); return this; }
    @Override public MCDataOutput writeLong(long l) { buf.writeLong(l); return this; }
    @Override public MCDataOutput writeFloat(float f) { buf.writeFloat(f); return this; }
    @Override public MCDataOutput writeDouble(double d) { buf.writeDouble(d); return this; }
    @Override public MCDataOutput writeBoolean(boolean b) { buf.writeBoolean(b); return this; }
    //@formatter:on
}
