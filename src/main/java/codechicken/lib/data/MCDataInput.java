package codechicken.lib.data;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import io.netty.handler.codec.EncoderException;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.*;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static codechicken.lib.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 4/15/20.
 */
public interface MCDataInput {

    //region Primitives
    byte readByte();

    short readUByte();

    char readChar();

    short readShort();

    int readUShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    boolean readBoolean();
    //endregion

    //region Arrays.
    default byte[] readBytes() {
        int len = readVarInt();
        byte[] arr = new byte[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readByte();
        }
        return arr;
    }

    default char[] readChars() {
        int len = readVarInt();
        char[] arr = new char[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readChar();
        }
        return arr;
    }

    default short[] readShorts() {
        int len = readVarInt();
        short[] arr = new short[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readShort();
        }
        return arr;
    }

    default int[] readInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readInt();
        }
        return arr;
    }

    default long[] readLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readLong();
        }
        return arr;
    }

    default float[] readFloats() {
        int len = readVarInt();
        float[] arr = new float[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readFloat();
        }
        return arr;
    }

    default double[] readDoubles() {
        int len = readVarInt();
        double[] arr = new double[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readDouble();
        }
        return arr;
    }

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
    @Deprecated //TODO Is this worth it?
    default int readVarShort() {
        int low = readUShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = readUByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

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

    default long readVarLong() {
        long i = 0L;
        int j = 0;
        byte b0;

        do {
            b0 = this.readByte();
            i |= (long) (b0 & 0x7f) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

        }
        while ((b0 & 0x80) == 0x80);

        return i;
    }
    //endregion

    //region Var-Arrays.
    @Deprecated
    default short[] readVarShorts() {
        int len = readVarInt();
        short[] arr = new short[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (short) readVarShort();
        }
        return arr;
    }

    default int[] readVarInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readVarInt();
        }
        return arr;
    }

    default long[] readVarLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readVarLong();
        }
        return arr;
    }
    //endregion

    //region Java Objects.
    default String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    default UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    default <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[readVarInt()];
    }

    default ByteBuffer readByteBuffer() {
        return ByteBuffer.wrap(readBytes());
    }

    default CharBuffer readCharBuffer() {
        return CharBuffer.wrap(readChars());
    }

    default ShortBuffer readShortBuffer() {
        return ShortBuffer.wrap(readShorts());
    }

    default IntBuffer readIntBuffer() {
        return IntBuffer.wrap(readInts());
    }

    default LongBuffer readLongBuffer() {
        return LongBuffer.wrap(readLongs());
    }

    default FloatBuffer readFloatBuffer() {
        return FloatBuffer.wrap(readFloats());
    }

    default DoubleBuffer readDoubleBuffer() {
        return DoubleBuffer.wrap(readDoubles());
    }
    //endregion

    //region CCL Objects.
    default Vector3 readVector() {
        return new Vector3(readDouble(), readDouble(), readDouble());
    }

    default Cuboid6 readCuboid() {
        return new Cuboid6(readVector(), readVector());
    }
    //endregion

    //region MinecraftObjects
    default ResourceLocation readResourceLocation() {
        return new ResourceLocation(readString());
    }

    default BlockPos readPos() {
        return new BlockPos(readVarInt(), readVarInt(), readVarInt());
    }

    default Vec3i readVec3i() {
        return readPos();
    }

    default Vec3d readVec3d() {
        return new Vec3d(readDouble(), readDouble(), readDouble());
    }

    default CompoundNBT readCompoundNBT() {
        if (!readBoolean()) {
            return null;
        } else {
            try {
                return CompressedStreamTools.read(toDataInput(), new NBTSizeTracker(2097152L));
            } catch (IOException e) {
                throw new EncoderException("Failed to read CompoundNBT from stream.", e);
            }
        }
    }

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

    default FluidStack readFluidStack() {
        if (!readBoolean()) {
            return FluidStack.EMPTY;
        } else {
            Fluid fluid = readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
            int amount = readVarInt();
            CompoundNBT tag = readCompoundNBT();
            if (fluid == Fluids.EMPTY) {
                return FluidStack.EMPTY;
            }
            return new FluidStack(fluid, amount, tag);
        }
    }

    default ITextComponent readTextComponent() {
        return ITextComponent.Serializer.fromJson(readString());
    }

    default <T extends IForgeRegistryEntry<T>> T readRegistryIdUnsafe(IForgeRegistry<T> registry) {
        ForgeRegistry<T> _registry = unsafeCast(registry);
        return _registry.getValue(readVarInt());
    }

    default <T extends IForgeRegistryEntry<T>> T readRegistryId() {
        ResourceLocation rName = readResourceLocation();
        ForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(rName);
        return readRegistryIdUnsafe(registry);
    }
    //endregion

    default DataInput toDataInput() {
        return new DataInputStream(toInputStream());
    }

    default InputStream toInputStream() {
        return new InputStreamWrapper(this);
    }

    final class InputStreamWrapper extends InputStream {

        private final MCDataInput in;

        public InputStreamWrapper(MCDataInput in) {
            this.in = in;
        }

        @Override
        public int read() {
            return in.readByte();
        }
    }
}
