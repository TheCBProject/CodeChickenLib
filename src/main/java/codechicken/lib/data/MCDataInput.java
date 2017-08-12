package codechicken.lib.data;

import codechicken.lib.vec.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import java.util.UUID;

public interface MCDataInput {

    long readLong();

    int readInt();

    short readShort();

    int readUShort();

    byte readByte();

    short readUByte();

    double readDouble();

    float readFloat();

    boolean readBoolean();

    char readChar();

    default int readVarShort() {
        return MCDataUtils.readVarShort(this);
    }

    default int readVarInt() {
        return MCDataUtils.readVarInt(this);
    }

    default long readVarLong() {
        return MCDataUtils.readVarLong(this);
    }

    byte[] readArray(int length);

    default String readString() {
        return MCDataUtils.readString(this);
    }

    default UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    default <T extends Enum<T>> T readEnum(Class<T> enumClass) {
        return enumClass.getEnumConstants()[readVarInt()];
    }

    default EnumFacing readEnumFacing() {
        return EnumFacing.VALUES[readByte()];
    }

    default ResourceLocation readResourceLocation() {
        return new ResourceLocation(readString());
    }

    default BlockPos readPos() {
        return new BlockPos(readInt(), readInt(), readInt());
    }

    default Vector3 readVector() {
        return new Vector3(readDouble(), readDouble(), readDouble());
    }

    default NBTTagCompound readNBTTagCompound() {
        return MCDataUtils.readNBTTagCompound(this);
    }

    default ItemStack readItemStack() {
        return MCDataUtils.readItemStack(this);
    }

    default FluidStack readFluidStack() {
        return MCDataUtils.readFluidStack(this);
    }
}
