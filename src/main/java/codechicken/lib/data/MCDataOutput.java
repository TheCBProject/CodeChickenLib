package codechicken.lib.data;

import codechicken.lib.vec.BlockCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public interface MCDataOutput {
    MCDataOutput writeLong(long l);

    MCDataOutput writeInt(int i);

    MCDataOutput writeShort(int s);

    MCDataOutput writeByte(int b);

    MCDataOutput writeDouble(double d);

    MCDataOutput writeFloat(float f);

    MCDataOutput writeBoolean(boolean b);

    MCDataOutput writeChar(char c);

    MCDataOutput writeVarInt(int i);

    MCDataOutput writeVarShort(int s);

    MCDataOutput writeArray(byte[] array);

    MCDataOutput writeString(String s);

    @Deprecated
    MCDataOutput writeCoord(int x, int y, int z);

    MCDataOutput writePos(BlockPos pos);

    MCDataOutput writeCoord(BlockCoord coord);

    MCDataOutput writeNBTTagCompound(NBTTagCompound tag);

    /**
     * Supports large stacks by writing stackSize as a varInt
     */
    MCDataOutput writeItemStack(ItemStack stack);

    MCDataOutput writeFluidStack(FluidStack liquid);
}
