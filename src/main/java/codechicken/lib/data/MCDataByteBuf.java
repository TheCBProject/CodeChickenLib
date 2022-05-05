package codechicken.lib.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * An {@link MCDataInput} and {@link MCDataOutput} implementation,
 * delegating to the provided {@link ByteBuf}.
 * <p>
 * Created by covers1624 on 4/16/20.
 */
public class MCDataByteBuf implements MCDataInput, MCDataOutput {

    protected final ByteBuf buf;

    public MCDataByteBuf() {
        this(Unpooled.buffer());
    }

    public MCDataByteBuf(ByteBuf buf) {
        this.buf = buf;
    }

    /**
     * Gets the underlying buffer as a {@link FriendlyByteBuf}.
     *
     * @return The {@link FriendlyByteBuf}.
     */
    public FriendlyByteBuf toPacketBuffer() {
        return buf instanceof FriendlyByteBuf ? (FriendlyByteBuf) buf : new FriendlyByteBuf(buf);
    }

    public Tag toTag() {
        return new ByteArrayTag(buf.array());
    }

    public static MCDataByteBuf fromTag(Tag tag) {
        if (!(tag instanceof ByteArrayTag)) {
            throw new IllegalArgumentException("Expected ByteArrayNBT, got: " + tag.getClass().getSimpleName());
        }
        return new MCDataByteBuf(Unpooled.copiedBuffer(((ByteArrayTag) tag).getAsByteArray()));
    }

    public CompoundTag writeToNBT(CompoundTag tag, String key) {
        tag.put(key, toTag());
        return tag;
    }

    public static MCDataByteBuf readFromNBT(CompoundTag tag, String key) {
        return fromTag(tag.get(key));
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
