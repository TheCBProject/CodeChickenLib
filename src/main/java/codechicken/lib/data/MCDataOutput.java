package codechicken.lib.data;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.*;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static codechicken.lib.data.DataUtils.checkLen;
import static codechicken.lib.util.SneakyUtils.unsafeCast;
import static java.text.MessageFormat.format;

/**
 * Created by covers1624 on 4/15/20.
 */
public interface MCDataOutput {

    //region Primitives.
    MCDataOutput writeByte(int b);

    MCDataOutput writeChar(int c);

    MCDataOutput writeShort(int s);

    MCDataOutput writeInt(int i);

    MCDataOutput writeLong(long l);

    MCDataOutput writeFloat(float f);

    MCDataOutput writeDouble(double d);

    MCDataOutput writeBoolean(boolean b);
    //endregion

    //region Arrays.
    default MCDataOutput writeBytes(byte[] b) {
        return writeBytes(b, 0, b.length);
    }

    default MCDataOutput writeBytes(byte[] b, int off, int len) {
        Objects.requireNonNull(b);
        checkLen(b.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeByte(b[off + i]);
        }
        return this;
    }

    default MCDataOutput writeChars(char[] c) {
        return writeChars(c, 0, c.length);
    }

    default MCDataOutput writeChars(char[] c, int off, int len) {
        Objects.requireNonNull(c);
        checkLen(c.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeChar(c[off + i]);
        }
        return this;
    }

    default MCDataOutput writeShorts(short[] s) {
        return writeShorts(s, 0, s.length);
    }

    default MCDataOutput writeShorts(short[] s, int off, int len) {
        Objects.requireNonNull(s);
        checkLen(s.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeShort(s[off + i]);
        }
        return this;
    }

    default MCDataOutput writeInts(int[] i) {
        return writeInts(i, 0, i.length);
    }

    default MCDataOutput writeInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeInt(i[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeLongs(long[] l) {
        return writeLongs(l, 0, l.length);
    }

    default MCDataOutput writeLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeLong(l[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeFloats(float[] f) {
        return writeFloats(f, 0, f.length);
    }

    default MCDataOutput writeFloats(float[] f, int off, int len) {
        Objects.requireNonNull(f);
        checkLen(f.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeFloat(f[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeDoubles(double[] d) {
        return writeDoubles(d, 0, d.length);
    }

    default MCDataOutput writeDoubles(double[] d, int off, int len) {
        Objects.requireNonNull(d);
        checkLen(d.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeDouble(d[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeBooleans(boolean[] b) {
        return writeBooleans(b, 0, b.length);
    }

    default MCDataOutput writeBooleans(boolean[] b, int off, int len) {
        Objects.requireNonNull(b);
        checkLen(b.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeBoolean(b[off + i2]);
        }
        return this;
    }

    default MCDataOutput append(byte[] bytes) {
        for (byte b : bytes) {
            writeByte(b);
        }
        return this;
    }
    //endregion

    //region Var-Primitives.
    default MCDataOutput writeVarShort(int s) {
        int low = s & 0x7FFF;
        int high = (s & 0x7F8000) >> 15;
        if (high != 0) {
            low |= 0x8000;
        }
        writeShort(low);
        if (high != 0) {
            writeByte(high);
        }
        return this;
    }

    default MCDataOutput writeVarInt(int i) {
        while ((i & 0xffffff80) != 0) {
            this.writeByte(i & 0x7f | 0x80);
            i >>>= 7;
        }

        this.writeByte(i);
        return this;
    }

    default MCDataOutput writeVarLong(long l) {
        while ((l & 0xffffffffffffff80L) != 0L) {
            writeByte((int) (l & 0x7fL) | 0x80);
            l >>>= 7;
        }
        writeByte((int) l);
        return this;
    }
    //endregion

    //region Var-Arrays.
    default MCDataOutput writeVarShorts(short[] s) {
        return writeVarShorts(s, 0, s.length);
    }

    default MCDataOutput writeVarShorts(short[] s, int off, int len) {
        Objects.requireNonNull(s);
        checkLen(s.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeVarShort(s[off + i]);
        }
        return this;
    }

    default MCDataOutput writeVarInts(int[] i) {
        return writeVarInts(i, 0, i.length);
    }

    default MCDataOutput writeVarInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeVarInt(i[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeVarLongs(long[] l) {
        return writeVarLongs(l, 0, l.length);
    }

    default MCDataOutput writeVarLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeVarLong(l[off + i2]);
        }
        return this;
    }
    //endregion

    //region Java Objects.
    default MCDataOutput writeString(String s) {
        return writeString(s, 32767);
    }

    default MCDataOutput writeString(String s, int maxLen) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxLen) {
            throw new EncoderException("String too big. Encoded: " + bytes.length + " Max: " + maxLen);
        }
        writeBytes(bytes);
        return this;
    }

    default MCDataOutput writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    default MCDataOutput writeEnum(Enum<?> value) {
        writeVarInt(value.ordinal());
        return this;
    }

    default MCDataOutput writeByteBuffer(ByteBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeByte(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeCharBuffer(CharBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeChar(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeShortBuffer(ShortBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeShort(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeIntBuffer(IntBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeInt(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeLongBuffer(LongBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeLong(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeFloatBuffer(FloatBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeFloat(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeDoubleBuffer(DoubleBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeDouble(buffer.get());
        }
        return this;
    }
    //endregion

    //region CCL Objects.
    default MCDataOutput writeVector(Vector3 vec) {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
        return this;
    }

    default MCDataOutput writeCuboid(Cuboid6 cuboid) {
        writeVector(cuboid.min);
        writeVector(cuboid.max);
        return this;
    }
    //endregion

    //region Minecraft Objects.
    default MCDataOutput writeResourceLocation(ResourceLocation loc) {
        return writeString(loc.toString());
    }

    default MCDataOutput writePos(BlockPos pos) {
        return writeVec3i(pos);
    }

    default MCDataOutput writeVec3i(Vec3i vec) {
        writeVarInt(vec.getX());
        writeVarInt(vec.getY());
        writeVarInt(vec.getZ());
        return this;
    }

    default MCDataOutput writeVec3d(Vec3d vec) {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
        return this;
    }

    default MCDataOutput writeCompoundNBT(CompoundNBT tag) {
        if (tag == null) {
            writeBoolean(false);
        } else {
            try {
                writeBoolean(true);
                CompressedStreamTools.write(tag, toDataOutput());
            } catch (IOException e) {
                throw new EncoderException("Failed to write CompoundNBT to stream.", e);
            }
        }
        return this;
    }

    default MCDataOutput writeItemStack(ItemStack stack) {
        return writeItemStack(stack, true);
    }

    default MCDataOutput writeItemStack(ItemStack stack, boolean limitedTag) {
        if (stack.isEmpty()) {
            writeBoolean(false);
        } else {
            Item item = stack.getItem();
            writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
            writeVarInt(stack.getCount());
            CompoundNBT nbt = null;
            if (item.isDamageable() || item.shouldSyncTag()) {
                nbt = limitedTag ? stack.getShareTag() : stack.getTag();
            }
            writeCompoundNBT(nbt);
        }
        return this;
    }

    default MCDataOutput writeFluidStack(FluidStack stack) {
        if (stack.isEmpty()) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, stack.getFluid());
            writeVarInt(stack.getAmount());
            writeCompoundNBT(stack.getTag());
        }
        return this;
    }

    default MCDataOutput writeTextComponent(ITextComponent component) {
        return writeString(ITextComponent.Serializer.toJson(component), 262144);//32kb
    }

    default <T extends IForgeRegistryEntry<T>> MCDataOutput writeRegistryIdUnsafe(IForgeRegistry<T> registry, T entry) {
        ForgeRegistry<T> r = unsafeCast(Objects.requireNonNull(registry));
        writeVarInt(r.getID(entry));
        return this;
    }

    default <T extends IForgeRegistryEntry<T>> MCDataOutput writeRegistryIdUnsafe(IForgeRegistry<T> registry, ResourceLocation entry) {
        ForgeRegistry<T> r = unsafeCast(Objects.requireNonNull(registry));
        writeVarInt(r.getID(entry));
        return this;
    }

    default <T extends IForgeRegistryEntry<T>> MCDataOutput writeRegistryId(T entry) {
        Class<T> rType = Objects.requireNonNull(entry).getRegistryType();
        ForgeRegistry<T> registry = unsafeCast(RegistryManager.ACTIVE.getRegistry(rType));
        if (registry == null) {
            throw new IllegalArgumentException(format("Unable to determine registry type of '{0}'", rType.getName()));
        }
        ResourceLocation rName = registry.getRegistryName();
        if (!registry.containsValue(entry)) {
            Object s = entry.getRegistryName() != null ? entry.getRegistryName() : entry;
            throw new IllegalArgumentException(format("Registry '{0}' does not contain entry '{1}'", rName, s));
        }
        writeResourceLocation(rName);
        writeRegistryIdUnsafe(registry, entry);
        return null;
    }
    //endregion

    default MCDataOutput writeByteBuf(ByteBuf buf) {
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return writeBytes(arr);
    }

    //Region To wrapper.
    default DataOutput toDataOutput() {
        return new DataOutputStream(toOutputStream());
    }

    default OutputStream toOutputStream() {
        return new OutputStreamWrapper(this);
    }
    //endregion

    final class OutputStreamWrapper extends OutputStream {

        private final MCDataOutput out;

        public OutputStreamWrapper(MCDataOutput out) {
            this.out = out;
        }

        @Override
        public void write(int b) {
            out.writeByte(b);
        }
    }

}
