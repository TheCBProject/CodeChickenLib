package codechicken.lib.data;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
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
 * Provides the ability to write various datas to some sort of data stream.
 * See {@link MCDataOutputStream} to wrap an {@link OutputStream} to this.
 * See {@link MCByteStream} to wrap an {@link ByteBuf} to this.
 * <p>
 * Created by covers1624 on 4/15/20.
 */
public interface MCDataOutput {

    //region Primitives.

    /**
     * Writes a byte to the stream.
     *
     * @param b The byte.
     * @return The same stream.
     */
    MCDataOutput writeByte(int b);

    /**
     * Writes a char to the stream.
     *
     * @param c The char.
     * @return The same stream.
     */
    MCDataOutput writeChar(int c);

    /**
     * Writes a short to the stream.
     *
     * @param s The short.
     * @return The same stream.
     */
    MCDataOutput writeShort(int s);

    /**
     * Writes a int to the stream.
     *
     * @param i The int.
     * @return The same stream.
     */
    MCDataOutput writeInt(int i);

    /**
     * Writes a long to the stream.
     *
     * @param l The long.
     * @return The same stream.
     */
    MCDataOutput writeLong(long l);

    /**
     * Writes a float to the stream.
     *
     * @param f The float.
     * @return The same stream.
     */
    MCDataOutput writeFloat(float f);

    /**
     * Writes a double to the stream.
     *
     * @param d The double.
     * @return The same stream.
     */
    MCDataOutput writeDouble(double d);

    /**
     * Writes a boolean to the stream.
     *
     * @param b The boolean.
     * @return The same stream.
     */
    MCDataOutput writeBoolean(boolean b);
    //endregion

    //region Arrays.

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param b The array.
     * @return The same stream.
     */
    default MCDataOutput writeBytes(byte[] b) {
        return writeBytes(b, 0, b.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param b   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeBytes(byte[] b, int off, int len) {
        Objects.requireNonNull(b);
        checkLen(b.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeByte(b[off + i]);
        }
        return this;
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param c The array.
     * @return The same stream.
     */
    default MCDataOutput writeChars(char[] c) {
        return writeChars(c, 0, c.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param c   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeChars(char[] c, int off, int len) {
        Objects.requireNonNull(c);
        checkLen(c.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeChar(c[off + i]);
        }
        return this;
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param s The array.
     * @return The same stream.
     */
    default MCDataOutput writeShorts(short[] s) {
        return writeShorts(s, 0, s.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param s   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeShorts(short[] s, int off, int len) {
        Objects.requireNonNull(s);
        checkLen(s.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeShort(s[off + i]);
        }
        return this;
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param i The array.
     * @return The same stream.
     */
    default MCDataOutput writeInts(int[] i) {
        return writeInts(i, 0, i.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param i   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeInt(i[off + i2]);
        }
        return this;
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param l The array.
     * @return The same stream.
     */
    default MCDataOutput writeLongs(long[] l) {
        return writeLongs(l, 0, l.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param l   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeLong(l[off + i2]);
        }
        return this;
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param f The array.
     * @return The same stream.
     */
    default MCDataOutput writeFloats(float[] f) {
        return writeFloats(f, 0, f.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param f   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeFloats(float[] f, int off, int len) {
        Objects.requireNonNull(f);
        checkLen(f.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeFloat(f[off + i2]);
        }
        return this;
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param d The array.
     * @return The same stream.
     */
    default MCDataOutput writeDoubles(double[] d) {
        return writeDoubles(d, 0, d.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param d   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeDoubles(double[] d, int off, int len) {
        Objects.requireNonNull(d);
        checkLen(d.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeDouble(d[off + i2]);
        }
        return this;
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param b The array.
     * @return The same stream.
     */
    default MCDataOutput writeBooleans(boolean[] b) {
        return writeBooleans(b, 0, b.length);
    }

    /**
     * Writes an array to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param b   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeBooleans(boolean[] b, int off, int len) {
        Objects.requireNonNull(b);
        checkLen(b.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeBoolean(b[off + i2]);
        }
        return this;
    }

    /**
     * Appends the content of the array to the end of this buffer.
     *
     * @param bytes The array.
     * @return The same stream.
     */
    default MCDataOutput append(byte[] bytes) {
        for (byte b : bytes) {
            writeByte(b);
        }
        return this;
    }
    //endregion

    //region Var-Primitives.

    /**
     * Writes a Variable length int.
     * Doesn't handle Signed ints well, they end up as 5 bytes,
     * instead of 4, Use {@link #writeSignedVarInt} if you requires numbers <= -1
     *
     * @param i The int.
     * @return The same stream.
     */
    default MCDataOutput writeVarInt(int i) {
        while ((i & 0xffffff80) != 0) {
            writeByte(i & 0x7f | 0x80);
            i >>>= 7;
        }

        writeByte(i);
        return this;
    }

    /**
     * Writes a Variable length long.
     * Doesn't handle Signed longs well, they end up as 10 bytes,
     * instead of 8, Use {@link #writeSignedVarLong} if you requires numbers <= -1
     *
     * @param l The long.
     * @return The same stream.
     */
    default MCDataOutput writeVarLong(long l) {
        while ((l & 0xffffffffffffff80L) != 0L) {
            writeByte((int) (l & 0x7fL) | 0x80);
            l >>>= 7;
        }
        writeByte((int) l);
        return this;
    }

    /**
     * Writes a Signed Variable length int.
     * Favourable for numbers <= -1
     *
     * @param i The int.
     * @return The same stream.
     */
    default MCDataOutput writeSignedVarInt(int i) {
        return writeVarInt(i >= 0 ? 2 * i : -2 * (i + 1) + 1);
    }

    /**
     * Writes a Signed Variable length long.
     * Favourable for numbers <= -1
     *
     * @param i The long.
     * @return The same stream.
     */
    default MCDataOutput writeSignedVarLong(long i) {
        return writeVarLong(i >= 0 ? 2 * i : -2 * (i + 1) + 1);
    }
    //endregion

    //region Var-Arrays.

    /**
     * Writes an array of Variable length ints to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param i The array.
     * @return The same stream.
     */
    default MCDataOutput writeVarInts(int[] i) {
        return writeVarInts(i, 0, i.length);
    }

    /**
     * Writes an array of Variable length ints to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param i   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeVarInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeVarInt(i[off + i2]);
        }
        return this;
    }

    /**
     * Writes an array of Variable length longs to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param l The array.
     * @return The same stream.
     */
    default MCDataOutput writeVarLongs(long[] l) {
        return writeVarLongs(l, 0, l.length);
    }

    /**
     * Writes an array of Variable length longs to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param l   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeVarLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeVarLong(l[off + i2]);
        }
        return this;
    }

    /**
     * Writes an array of Variable length Signed  ints to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param i The array.
     * @return The same stream.
     */
    default MCDataOutput writeSignedVarInts(int[] i) {
        return writeSignedVarInts(i, 0, i.length);
    }

    /**
     * Writes an array of Variable length Signed  ints to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param i   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeSignedVarInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeSignedVarInt(i[off + i2]);
        }
        return this;
    }

    /**
     * Writes an array of Variable length Signed  longs to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param l The array.
     * @return The same stream.
     */
    default MCDataOutput writeSignedVarLongs(long[] l) {
        return writeSignedVarLongs(l, 0, l.length);
    }

    /**
     * Writes an array of Variable length Signed longs to the stream.
     * First writes the arrays length as a varInt, followed
     * by the array data.
     *
     * @param l   The array.
     * @param off An offset into the array to start reading from.
     * @param len How many elements to read.
     * @return The same stream.
     */
    default MCDataOutput writeSignedVarLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeSignedVarLong(l[off + i2]);
        }
        return this;
    }
    //endregion

    //region Java Objects.

    /**
     * Writes a UTF-8 Encoded {@link String} to the stream.
     * Forces a length of 32767 encoded bytes.
     *
     * @param s The {@link String}.
     * @return The same stream.
     */
    default MCDataOutput writeString(String s) {
        return writeString(s, 32767);
    }

    /**
     * Writes a UTF-8 Encoded {@link String} to the stream.
     *
     * @param s      The {@link String}.
     * @param maxLen The maximum number of bytes to write,
     *               extra bytes will cause an EncoderException.
     * @return The same stream.
     */
    default MCDataOutput writeString(String s, int maxLen) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxLen) {
            throw new EncoderException("String too big. Encoded: " + bytes.length + " Max: " + maxLen);
        }
        writeBytes(bytes);
        return this;
    }

    /**
     * Writes a {@link UUID} to the stream.
     *
     * @param uuid The {@link UUID}.
     * @return The same stream.
     */
    default MCDataOutput writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    /**
     * Writes an {@link Enum} value to the stream.
     *
     * @param value The {@link Enum} value to write.
     * @return The same stream.
     */
    default MCDataOutput writeEnum(Enum<?> value) {
        writeVarInt(value.ordinal());
        return this;
    }

    /**
     * Writes a {@link ByteBuffer} to the stream.
     *
     * @param buffer The {@link ByteBuffer}.
     * @return The same stream.
     */
    default MCDataOutput writeByteBuffer(ByteBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeByte(buffer.get());
        }
        return this;
    }

    /**
     * Writes a {@link CharBuffer} to the stream.
     *
     * @param buffer The {@link CharBuffer}.
     * @return The same stream.
     */
    default MCDataOutput writeCharBuffer(CharBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeChar(buffer.get());
        }
        return this;
    }

    /**
     * Writes a {@link ShortBuffer} to the stream.
     *
     * @param buffer The {@link ShortBuffer}.
     * @return The same stream.
     */
    default MCDataOutput writeShortBuffer(ShortBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeShort(buffer.get());
        }
        return this;
    }

    /**
     * Writes a {@link IntBuffer} to the stream.
     *
     * @param buffer The {@link IntBuffer}.
     * @return The same stream.
     */
    default MCDataOutput writeIntBuffer(IntBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeInt(buffer.get());
        }
        return this;
    }

    /**
     * Writes a {@link LongBuffer} to the stream.
     *
     * @param buffer The {@link LongBuffer}.
     * @return The same stream.
     */
    default MCDataOutput writeLongBuffer(LongBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeLong(buffer.get());
        }
        return this;
    }

    /**
     * Writes a {@link FloatBuffer} to the stream.
     *
     * @param buffer The {@link FloatBuffer}.
     * @return The same stream.
     */
    default MCDataOutput writeFloatBuffer(FloatBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeFloat(buffer.get());
        }
        return this;
    }

    /**
     * Writes a {@link DoubleBuffer} to the stream.
     *
     * @param buffer The {@link DoubleBuffer}.
     * @return The same stream.
     */
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

    /**
     * Writes a {@link Vector3} to the stream.
     *
     * @param vec The {@link Vector3}.
     * @return The same stream.
     */
    default MCDataOutput writeVector(Vector3 vec) {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
        return this;
    }

    /**
     * Writes a {@link Cuboid6} to the stream.
     *
     * @param cuboid The {@link Cuboid6}
     * @return The same stream.
     */
    default MCDataOutput writeCuboid(Cuboid6 cuboid) {
        writeVector(cuboid.min);
        writeVector(cuboid.max);
        return this;
    }
    //endregion

    //region Minecraft Objects.

    /**
     * Writes a {@link ResourceLocation} to the stream.
     *
     * @param loc The {@link ResourceLocation}.
     * @return The same stream.
     */
    default MCDataOutput writeResourceLocation(ResourceLocation loc) {
        return writeString(loc.toString());
    }

    /**
     * Writes a {@link Direction} to the stream.
     *
     * @param dir The {@link Direction}.
     * @return The same stream.
     */
    default MCDataOutput writeDirection(Direction dir) {
        return writeEnum(dir);
    }

    /**
     * Writes a {@link BlockPos} to the stream.
     *
     * @param pos The {@link BlockPos}.
     * @return The same stream.
     */
    default MCDataOutput writePos(BlockPos pos) {
        return writeVec3i(pos);
    }

    /**
     * Writes a {@link Vector3i} to the stream.
     *
     * @param vec The {@link Vector3i}.
     * @return The same stream.
     */
    default MCDataOutput writeVec3i(Vector3i vec) {
        writeSignedVarInt(vec.getX());
        writeSignedVarInt(vec.getY());
        writeSignedVarInt(vec.getZ());
        return this;
    }

    /**
     * Writes a {@link Vector3f} to the stream.
     *
     * @param vec The {@link Vector3f}.
     * @return The same stream.
     */
    default MCDataOutput writeVec3f(Vector3f vec) {
        writeFloat(vec.x());
        writeFloat(vec.y());
        writeFloat(vec.z());
        return this;
    }

    /**
     * Writes a {@link Vector3d} to the stream.
     *
     * @param vec The {@link Vector3d}.
     * @return The same stream.
     */
    default MCDataOutput writeVec3d(Vector3d vec) {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
        return this;
    }

    /**
     * Writes a {@link CompoundNBT} to the stream.
     *
     * @param tag The {@link CompoundNBT}.
     * @return The same stream.
     */
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

    /**
     * Writes an {@link ItemStack} to the stream.
     * Overload for {@link #writeItemStack(ItemStack, boolean)} passing true.
     *
     * @param stack The {@link ItemStack}.
     * @return The same stream.
     */
    default MCDataOutput writeItemStack(ItemStack stack) {
        return writeItemStack(stack, true);
    }

    /**
     * Writes an {@link ItemStack} to the stream.
     * The <code>limitedTag</code> parameter, can be used to force the use of
     * the stack {@link ItemStack#getShareTag()} instead of its {@link ItemStack#getTag()}.
     * Under normal circumstances in Server -> Client sync, not all NBT tags are required,
     * to be sent to the client. For Example, the inventory of a pouch / bag(Containers sync it).
     * However, in Client -> Server sync, the entire tag may be required, modders can choose,
     * if they want a stacks full tag or not. The default is to use {@link ItemStack#getShareTag()}.
     * <p>
     * It should also be noted that this implementation writes the {@link ItemStack#getCount()}
     * as a varInt opposed to a byte, as that is favourable in some cases.
     *
     * @param stack      The {@link ItemStack}.
     * @param limitedTag Weather to use the stacks {@link ItemStack#getShareTag()} instead.
     * @return The same stream.
     */
    default MCDataOutput writeItemStack(ItemStack stack, boolean limitedTag) {
        if (stack.isEmpty()) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            Item item = stack.getItem();
            writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
            writeVarInt(stack.getCount());
            CompoundNBT nbt = null;
            if (item.canBeDepleted() || item.shouldOverrideMultiplayerNbt()) {
                nbt = limitedTag ? stack.getShareTag() : stack.getTag();
            }
            writeCompoundNBT(nbt);
        }
        return this;
    }

    /**
     * Writes a {@link FluidStack} to the stream.
     *
     * @param stack The {@link FluidStack}.
     * @return The same stream.
     */
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

    /**
     * Writes a {@link ITextComponent} to the stream.
     *
     * @param component The {@link ITextComponent}.
     * @return The same stream.
     */
    default MCDataOutput writeTextComponent(ITextComponent component) {
        return writeString(ITextComponent.Serializer.toJson(component), 262144);//32kb
    }

    /**
     * Writes an {@link IForgeRegistryEntry} to the stream, in an 'unsafe' manner.
     * Does little checking that the data is valid and just assumes its all good to go,
     * but is in no means 'unsafe', simply put, use this to avoid a tiny bit of overhead,
     * when you 100% know your {@link IForgeRegistryEntry} is valid and of the correct type.
     *
     * @param registry The registry that owns <code>entry</code>.
     * @param entry    The {@link IForgeRegistryEntry} to write to the stream.
     * @return The same stream.
     */
    default <T extends IForgeRegistryEntry<T>> MCDataOutput writeRegistryIdUnsafe(IForgeRegistry<T> registry, T entry) {
        ForgeRegistry<T> r = unsafeCast(Objects.requireNonNull(registry));
        writeVarInt(r.getID(entry));
        return this;
    }

    /**
     * Writes an {@link IForgeRegistryEntry} to the stream, in an 'unsafe' manner.
     * Does little checking that the data is valid and just assumes its all good to go,
     * but is in no means 'unsafe', simply put, use this to avoid a tiny bit of overhead,
     * when you 100% know your {@link IForgeRegistryEntry} is valid and of the correct type.
     *
     * @param registry The registry that owns <code>entry</code>.
     * @param entry    The {@link IForgeRegistryEntry} to write to the stream.
     * @return The same stream.
     */
    default <T extends IForgeRegistryEntry<T>> MCDataOutput writeRegistryIdUnsafe(IForgeRegistry<T> registry, ResourceLocation entry) {
        ForgeRegistry<T> r = unsafeCast(Objects.requireNonNull(registry));
        writeVarInt(r.getID(entry));
        return this;
    }

    /**
     * Write an arbitrary {@link IForgeRegistryEntry} to the stream.
     * Does many sanity checks on the provided entry. Use this if you
     * don't (for some reason), know the type / registry of your {@link IForgeRegistryEntry}.
     *
     * @param entry The {@link IForgeRegistryEntry} to write.
     * @return The same stream.
     */
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

    //region Netty.

    /**
     * Writes a {@link ByteBuf} to the stream, including its length.
     * First writes the arrays length as a varInt, followed
     * by the ByteBuf data.
     *
     * @param buf The {@link ByteBuf}.
     * @return The same stream.
     */
    default MCDataOutput writeByteBuf(ByteBuf buf) {
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return writeBytes(arr);
    }

    /**
     * Appends a {@link ByteBuf} to the end of this stream.
     *
     * @param buf The {@link ByteBuf} to append.
     * @return The same stream.
     */
    default MCDataOutput append(ByteBuf buf) {
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return append(arr);
    }
    //endregion

    //Region To wrapper.

    /**
     * Wraps this stream into a {@link DataOutput} stream,
     * any data written into the returned {@link DataOutput}
     * is appended to this stream.
     *
     * @return The {@link DataOutput} stream.
     */
    default DataOutput toDataOutput() {
        return new DataOutputStream(toOutputStream());
    }

    /**
     * Wraps this stream into an {@link OutputStream},
     * any data written into the returned {@link OutputStream}
     * is appended to this stream.
     *
     * @return The {@link OutputStream} stream.
     */
    default OutputStream toOutputStream() {
        return new OutputStreamWrapper(this);
    }
    //endregion

    /**
     * Simple wrapper to an {@link OutputStream}.
     */
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
