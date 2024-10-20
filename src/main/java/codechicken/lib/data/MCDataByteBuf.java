package codechicken.lib.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link MCDataInput} and {@link MCDataOutput} implementation,
 * delegating to the provided {@link ByteBuf}.
 * <p>
 * Created by covers1624 on 4/16/20.
 */
public class MCDataByteBuf implements MCDataInput, MCDataOutput {

    protected final ByteBuf buf;
    protected final @Nullable RegistryAccess registryAccess;

    public MCDataByteBuf() {
        this(Unpooled.buffer(), null);
    }

    public MCDataByteBuf(@Nullable RegistryAccess registryAccess) {
        this(Unpooled.buffer(), registryAccess);
    }

    public MCDataByteBuf(ByteBuf buf) {
        this(buf, null);
    }

    public MCDataByteBuf(ByteBuf buf, @Nullable RegistryAccess registryAccess) {
        this.buf = buf;
        if (registryAccess == null && buf instanceof RegistryFriendlyByteBuf rBuf) {
            registryAccess = rBuf.registryAccess();
        }
        this.registryAccess = registryAccess;
    }

    /**
     * Gets the underlying buffer as a {@link FriendlyByteBuf}.
     *
     * @return The {@link FriendlyByteBuf}.
     */
    public FriendlyByteBuf toFriendlyByteBuf() {
        return buf instanceof FriendlyByteBuf ? (FriendlyByteBuf) buf : new FriendlyByteBuf(buf);
    }

    public RegistryFriendlyByteBuf toRegistryFriendlyByteBuf() {
        if (buf instanceof RegistryFriendlyByteBuf rBuf) return rBuf;
        if (registryAccess == null) {
            throw new RuntimeException("RegistryAccess required for this operation.");
        }
        return new RegistryFriendlyByteBuf(buf, registryAccess, ConnectionType.NEOFORGE);
    }

    public Tag toTag() {
        return new ByteArrayTag(buf.array());
    }

    public static MCDataByteBuf fromTag(Tag tag, RegistryAccess registries) {
        if (!(tag instanceof ByteArrayTag)) {
            throw new IllegalArgumentException("Expected ByteArrayNBT, got: " + tag.getClass().getSimpleName());
        }
        return new MCDataByteBuf(Unpooled.copiedBuffer(((ByteArrayTag) tag).getAsByteArray()), registries);
    }

    public CompoundTag writeToNBT(CompoundTag tag, String key) {
        tag.put(key, toTag());
        return tag;
    }

    public static MCDataByteBuf readFromNBT(CompoundTag tag, String key, RegistryAccess registries) {
        return fromTag(tag.get(key), registries);
    }

    @Override
    public <T> MCDataOutput writeWithCodec(StreamEncoder<? super FriendlyByteBuf, T> codec, T thing) {
        codec.encode(toFriendlyByteBuf(), thing);
        return this;
    }

    @Override
    public <T> MCDataOutput writeWithRegistryCodec(StreamEncoder<? super RegistryFriendlyByteBuf, T> codec, T thing) {
        codec.encode(toRegistryFriendlyByteBuf(), thing);
        return this;
    }

    @Override
    public <T> T readWithCodec(StreamDecoder<? super FriendlyByteBuf, T> codec) {
        return codec.decode(toFriendlyByteBuf());
    }

    @Override
    public <T> T readWithRegistryCodec(StreamDecoder<? super RegistryFriendlyByteBuf, T> codec) {
        return codec.decode(toRegistryFriendlyByteBuf());
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
