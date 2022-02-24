package codechicken.lib.data;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.mojang.math.Vector3f;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static codechicken.lib.util.SneakyUtils.unsafeCast;

/**
 * Provides the ability to read various datas from some sort of data stream.
 * See {@link MCDataInputStream} to wrap an {@link InputStream} to this.
 * See {@link MCByteStream} to wrap an {@link ByteBuf} to this.
 * <p>
 * Created by covers1624 on 4/15/20.
 */
public interface MCDataInput {

    //region Primitives

    /**
     * Reads a byte from the stream.
     *
     * @return The byte.
     */
    byte readByte();

    /**
     * Reads an Unsigned byte from the stream.
     *
     * @return The Unsigned byte.
     */
    short readUByte();

    /**
     * Reads a char from the stream.
     *
     * @return The char.
     */
    char readChar();

    /**
     * Reads a Short from the stream.
     *
     * @return The short.
     */
    short readShort();

    /**
     * Reads an Unsigned short from the stream.
     *
     * @return The Unsigned short.
     */
    int readUShort();

    /**
     * Reads an int from the stream.
     *
     * @return The int.
     */
    int readInt();

    /**
     * Reads a long from the stream.
     *
     * @return The long.
     */
    long readLong();

    /**
     * Reads a float from the stream.
     *
     * @return The float.
     */
    float readFloat();

    /**
     * Reads a double from the stream.
     *
     * @return The double.
     */
    double readDouble();

    /**
     * Reads a boolean from the stream.
     *
     * @return The boolean.
     */
    boolean readBoolean();
    //endregion

    //region Arrays.

    /**
     * Reads a block of bytes written with,
     * {@link MCDataOutput#writeBytes(byte[])},
     * {@link MCDataOutput#writeBytes(byte[], int, int)}, or
     * {@link MCDataOutput#writeByteBuffer(ByteBuffer)}
     *
     * @return The bytes.
     */
    default byte[] readBytes() {
        int len = readVarInt();
        byte[] arr = new byte[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readByte();
        }
        return arr;
    }

    /**
     * Reads a block of chars written with,
     * {@link MCDataOutput#writeChars(char[])},
     * {@link MCDataOutput#writeChars(char[], int, int)}, or
     * {@link MCDataOutput#writeCharBuffer(CharBuffer)}
     *
     * @return The chars.
     */
    default char[] readChars() {
        int len = readVarInt();
        char[] arr = new char[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readChar();
        }
        return arr;
    }

    /**
     * Reads a block of shorts written with,
     * {@link MCDataOutput#writeShorts(short[])},
     * {@link MCDataOutput#writeShorts(short[], int, int)}, or
     * {@link MCDataOutput#writeShortBuffer(ShortBuffer)}.
     *
     * @return The shorts.
     */
    default short[] readShorts() {
        int len = readVarInt();
        short[] arr = new short[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readShort();
        }
        return arr;
    }

    /**
     * Reads a block of ints written with,
     * {@link MCDataOutput#writeInts(int[])},
     * {@link MCDataOutput#writeInts(int[], int, int)}, or
     * {@link MCDataOutput#writeIntBuffer(IntBuffer)}.
     *
     * @return The ints.
     */
    default int[] readInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readInt();
        }
        return arr;
    }

    /**
     * Reads a block of longs written with,
     * {@link MCDataOutput#writeLongs(long[])},
     * {@link MCDataOutput#writeLongs(long[], int, int)}, or
     * {@link MCDataOutput#writeLongBuffer(LongBuffer)}.
     *
     * @return The longs.
     */
    default long[] readLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readLong();
        }
        return arr;
    }

    /**
     * Reads a block of floats written with,
     * {@link MCDataOutput#writeFloats(float[])},
     * {@link MCDataOutput#writeFloats(float[], int, int)}, or
     * {@link MCDataOutput#writeFloatBuffer(FloatBuffer)}.
     *
     * @return The floats.
     */
    default float[] readFloats() {
        int len = readVarInt();
        float[] arr = new float[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readFloat();
        }
        return arr;
    }

    /**
     * Reads a block of doubles written with,
     * {@link MCDataOutput#writeDoubles(double[])},
     * {@link MCDataOutput#writeDoubles(double[], int, int)}, or
     * {@link MCDataOutput#writeDoubleBuffer(DoubleBuffer)}.
     *
     * @return The doubles.
     */
    default double[] readDoubles() {
        int len = readVarInt();
        double[] arr = new double[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readDouble();
        }
        return arr;
    }

    /**
     * Reads a block of booleans written with,
     * {@link MCDataOutput#writeBooleans(boolean[])}, or
     * {@link MCDataOutput#writeBooleans(boolean[], int, int)}
     *
     * @return The booleans.
     */
    default boolean[] readBooleans() {
        int len = readVarInt();
        boolean[] arr = new boolean[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readBoolean();
        }
        return arr;
    }
    //endregion

    //region Var-Primitives

    /**
     * Reads a Variable length int from the stream.
     *
     * @return The int.
     */
    default int readVarInt() {
        int i = 0;
        int j = 0;
        byte b0;

        do {
            b0 = readByte();
            i |= (b0 & 0x7f) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        }
        while ((b0 & 0x80) == 0x80);

        return i;
    }

    /**
     * Reads a Variable length long from the stream.
     *
     * @return The long.
     */
    default long readVarLong() {
        long i = 0L;
        int j = 0;
        byte b0;

        do {
            b0 = readByte();
            i |= (long) (b0 & 0x7f) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

        }
        while ((b0 & 0x80) == 0x80);

        return i;
    }

    /**
     * Reads a Variable length signed int.
     *
     * @return The int.
     * @see MCDataOutput#writeSignedVarInt
     */
    default int readSignedVarInt() {
        int i = readVarInt();
        return (i & 1) == 0 ? i >>> 1 : -(i >>> 1) - 1;
    }

    /**
     * Reads a Variable length signed long.
     *
     * @return The long.
     * @see MCDataOutput#writeSignedVarLong
     */
    default long readSignedVarLong() {
        long i = readVarLong();
        return (i & 1) == 0 ? i >>> 1 : -(i >>> 1) - 1;
    }

    //endregion

    //region Var-Arrays.

    /**
     * Reads an array of Variable length ints from the stream.
     *
     * @return The ints.
     */
    default int[] readVarInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readVarInt();
        }
        return arr;
    }

    /**
     * Reads an array of Variable length longs from the stream.
     *
     * @return The longs.
     */
    default long[] readVarLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readVarLong();
        }
        return arr;
    }

    /**
     * Reads an array of Variable length Signed ints from the stream.
     *
     * @return The ints.
     */
    default int[] readSignedVarInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readSignedVarInt();
        }
        return arr;
    }

    /**
     * Reads an array of Variable length Signed longs from the stream.
     *
     * @return The longs.
     */
    default long[] readSignedVarLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readSignedVarLong();
        }
        return arr;
    }
    //endregion

    //region Java Objects.

    /**
     * Reads a UTF-8 encoded {@link String} from the stream.
     *
     * @return The {@link String}.
     */
    default String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a {@link UUID} from the stream.
     *
     * @return The {@link UUID}.
     */
    default UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    /**
     * Reads an {@link Enum} value from the stream.
     *
     * @param clazz The Class of the enum.
     * @return The {@link Enum} value.
     */
    default <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[readVarInt()];
    }

    /**
     * Reads a block of bytes written with,
     * {@link MCDataOutput#writeBytes(byte[])},
     * {@link MCDataOutput#writeBytes(byte[], int, int)},
     * {@link MCDataOutput#writeByteBuffer(ByteBuffer)}, or
     * {@link MCDataOutput#writeByteBuf(ByteBuf)}
     *
     * @return The bytes.
     */
    default ByteBuffer readByteBuffer() {
        return ByteBuffer.wrap(readBytes());
    }

    /**
     * Reads a block of chars written with,
     * {@link MCDataOutput#writeChars(char[])},
     * {@link MCDataOutput#writeChars(char[], int, int)}, or
     * {@link MCDataOutput#writeCharBuffer(CharBuffer)}
     *
     * @return The chars.
     */
    default CharBuffer readCharBuffer() {
        return CharBuffer.wrap(readChars());
    }

    /**
     * Reads a block of shorts written with,
     * {@link MCDataOutput#writeShorts(short[])},
     * {@link MCDataOutput#writeShorts(short[], int, int)}, or
     * {@link MCDataOutput#writeShortBuffer(ShortBuffer)}.
     *
     * @return The shorts.
     */
    default ShortBuffer readShortBuffer() {
        return ShortBuffer.wrap(readShorts());
    }

    /**
     * Reads a block of ints written with,
     * {@link MCDataOutput#writeInts(int[])},
     * {@link MCDataOutput#writeInts(int[], int, int)}, or
     * {@link MCDataOutput#writeIntBuffer(IntBuffer)}.
     *
     * @return The ints.
     */
    default IntBuffer readIntBuffer() {
        return IntBuffer.wrap(readInts());
    }

    /**
     * Reads a block of longs written with,
     * {@link MCDataOutput#writeLongs(long[])},
     * {@link MCDataOutput#writeLongs(long[], int, int)}, or
     * {@link MCDataOutput#writeLongBuffer(LongBuffer)}.
     *
     * @return The longs.
     */
    default LongBuffer readLongBuffer() {
        return LongBuffer.wrap(readLongs());
    }

    /**
     * Reads a block of floats written with,
     * {@link MCDataOutput#writeFloats(float[])},
     * {@link MCDataOutput#writeFloats(float[], int, int)}, or
     * {@link MCDataOutput#writeFloatBuffer(FloatBuffer)}.
     *
     * @return The floats.
     */
    default FloatBuffer readFloatBuffer() {
        return FloatBuffer.wrap(readFloats());
    }

    /**
     * Reads a block of doubles written with,
     * {@link MCDataOutput#writeDoubles(double[])},
     * {@link MCDataOutput#writeDoubles(double[], int, int)}, or
     * {@link MCDataOutput#writeDoubleBuffer(DoubleBuffer)}.
     *
     * @return The doubles.
     */
    default DoubleBuffer readDoubleBuffer() {
        return DoubleBuffer.wrap(readDoubles());
    }
    //endregion

    //region CCL Objects.

    /**
     * Reads a {@link Vector3} from the stream.
     *
     * @return The {@link Vector3}.
     */
    default Vector3 readVector() {
        return new Vector3(readDouble(), readDouble(), readDouble());
    }

    /**
     * Reads a {@link Cuboid6} from the stream.
     *
     * @return The {@link Cuboid6}.
     */
    default Cuboid6 readCuboid() {
        return new Cuboid6(readVector(), readVector());
    }
    //endregion

    //region MinecraftObjects

    /**
     * Reads a {@link ResourceLocation} from the stream.
     *
     * @return The {@link ResourceLocation}.
     */
    default ResourceLocation readResourceLocation() {
        return new ResourceLocation(readString());
    }

    /**
     * Reads a {@link Direction} from the stream.
     *
     * @return The {@link Direction}.
     */
    default Direction readDirection() {
        return readEnum(Direction.class);
    }

    /**
     * Reads a {@link BlockPos} from the stream.
     *
     * @return The {@link BlockPos}.
     */
    default BlockPos readPos() {
        return new BlockPos(readSignedVarInt(), readSignedVarInt(), readSignedVarInt());
    }

    /**
     * Reads a {@link Vec3i} from the stream.
     *
     * @return The {@link Vec3i}.
     */
    default Vec3i readVec3i() {
        return readPos();
    }

    /**
     * Reads a {@link Vec3} from the stream.
     *
     * @return The {@link Vec3}.
     */
    default Vec3 readVec3d() {
        return new Vec3(readDouble(), readDouble(), readDouble());
    }

    /**
     * Reads a {@link Vector3f} from the stream.
     *
     * @return The {@link Vector3f}.
     */
    default Vector3f readVec3f() {
        return new Vector3f(readFloat(), readFloat(), readFloat());
    }

    /**
     * Reads a {@link CompoundTag} from the stream.
     *
     * @return The {@link CompoundTag}.
     */
    @Nullable
    default CompoundTag readCompoundNBT() {
        if (!readBoolean()) {
            return null;
        } else {
            try {
                return NbtIo.read(toDataInput(), new NbtAccounter(2097152L));
            } catch (IOException e) {
                throw new EncoderException("Failed to read CompoundNBT from stream.", e);
            }
        }
    }

    /**
     * Reads an {@link ItemStack} from the stream.
     * It should also be noted that this implementation reads the {@link ItemStack#getCount()}
     * as a varInt opposed to a byte, as that is favourable in some cases.
     *
     * @return The {@link ItemStack}.
     */
    default ItemStack readItemStack() {
        if (!readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item item = readRegistryIdUnsafe(ForgeRegistries.ITEMS);
            int count = readVarInt();
            ItemStack stack = new ItemStack(item, count);
            stack.readShareTag(readCompoundNBT());
            return stack;
        }
    }

    /**
     * Reads a {@link FluidStack} from the stream.
     *
     * @return The {@link FluidStack}.
     */
    default FluidStack readFluidStack() {
        if (!readBoolean()) {
            return FluidStack.EMPTY;
        } else {
            Fluid fluid = readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
            int amount = readVarInt();
            CompoundTag tag = readCompoundNBT();
            if (fluid == Fluids.EMPTY) {
                return FluidStack.EMPTY;
            }
            return new FluidStack(fluid, amount, tag);
        }
    }

    /**
     * Reads an {@link Component} from the stream.
     *
     * @return The {@link Component}.
     */
    default MutableComponent readTextComponent() {
        return Component.Serializer.fromJson(readString());
    }

    /**
     * Reads an {@link IForgeRegistryEntry} from the stream in an 'unsafe' manner,
     * See {@link MCDataOutput#writeRegistryIdUnsafe(IForgeRegistry, IForgeRegistryEntry)}
     * for more information on its apparent 'unsafe'ness.
     *
     * @param registry The {@link IForgeRegistry} to load the entry from.
     * @return The {@link IForgeRegistryEntry}.
     */
    default <T extends IForgeRegistryEntry<T>> T readRegistryIdUnsafe(IForgeRegistry<T> registry) {
        ForgeRegistry<T> _registry = unsafeCast(registry);
        return _registry.getValue(readVarInt());
    }

    /**
     * Reads an {@link IForgeRegistryEntry} from the stream.
     * See {@link MCDataOutput#writeRegistryId(IForgeRegistryEntry)}
     * for more information.
     *
     * @return The {@link IForgeRegistryEntry}.
     */
    default <T extends IForgeRegistryEntry<T>> T readRegistryId() {
        ResourceLocation rName = readResourceLocation();
        ForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(rName);
        return readRegistryIdUnsafe(registry);
    }
    //endregion

    //region Netty

    /**
     * Reads a block of bytes written with,
     * {@link MCDataOutput#writeBytes(byte[])},
     * {@link MCDataOutput#writeBytes(byte[], int, int)},
     * {@link MCDataOutput#writeByteBuffer(ByteBuffer)}, or
     * {@link MCDataOutput#writeByteBuf(ByteBuf)}
     *
     * @return The bytes.
     */
    default ByteBuf readByteBuf() {
        return Unpooled.wrappedBuffer(readBytes());
    }
    //endregion

    /**
     * Wraps this stream into a {@link DataInput} stream.
     * any data read from the returned {@link DataInput}
     * is read from this stream.
     *
     * @return The {@link DataInput}.
     */
    default DataInput toDataInput() {
        return new DataInputStream(toInputStream());
    }

    /**
     * Wraps this stream into an {@link InputStream}.
     * any data read from the returned {@link InputStream}
     * is read from this stream.
     *
     * @return The {@link InputStream}.
     */
    default InputStream toInputStream() {
        return new InputStreamWrapper(this);
    }

    /**
     * Simple wrapper to an {@link InputStream}.
     */
    final class InputStreamWrapper extends InputStream {

        private final MCDataInput in;

        public InputStreamWrapper(MCDataInput in) {
            this.in = in;
        }

        @Override
        public int read() {
            return in.readByte() & 0xFF;
        }
    }
}
