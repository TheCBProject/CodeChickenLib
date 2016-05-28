package codechicken.lib.data;

import codechicken.lib.vec.BlockCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

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

    int readVarShort();

    int readVarInt();

    long readVarLong();

    byte[] readArray(int length);

    String readString();

    BlockCoord readCoord();

    NBTTagCompound readNBTTagCompound();

    ItemStack readItemStack();

    FluidStack readFluidStack();
}
