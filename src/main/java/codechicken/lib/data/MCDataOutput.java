package codechicken.lib.data;

import codechicken.lib.vec.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.UUID;

public interface MCDataOutput {

    MCDataOutput writeLong(long l);

    MCDataOutput writeInt(int i);

    MCDataOutput writeShort(int s);

    MCDataOutput writeByte(int b);

    MCDataOutput writeDouble(double d);

    MCDataOutput writeFloat(float f);

    MCDataOutput writeBoolean(boolean b);

    MCDataOutput writeChar(char c);

    default MCDataOutput writeVarInt(int i) {
        MCDataUtils.writeVarInt(this, i);
        return this;
    }

    default MCDataOutput writeVarShort(int s) {
        MCDataUtils.writeVarShort(this, s);
        return this;
    }

    default MCDataOutput writeVarLong(long l) {
        MCDataUtils.writeVarLong(this, l);
        return this;
    }

    MCDataOutput writeArray(byte[] array);

    default MCDataOutput writeString(String s) {
        MCDataUtils.writeString(this, s);
        return this;
    }

    default MCDataOutput writeString(String s, int maxLen) {
        MCDataUtils.writeString(this, s, maxLen);
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

    default MCDataOutput writeResourceLocation(ResourceLocation location) {
        writeString(location.toString());
        return this;
    }

    default MCDataOutput writePos(BlockPos pos) {
        writeInt(pos.getX());
        writeInt(pos.getY());
        writeInt(pos.getZ());
        return this;
    }

    default MCDataOutput writeVector(Vector3 vec) {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
        return this;
    }

    default MCDataOutput writeCompoundNBT(CompoundNBT tag) {
        MCDataUtils.writeCompoundNBT(this, tag);
        return this;
    }

    /**
     * Supports large stacks by writing stackSize as a varInt
     */
    default MCDataOutput writeItemStack(ItemStack stack) {
        MCDataUtils.writeItemStack(this, stack);
        return this;
    }

    default MCDataOutput writeFluidStack(FluidStack liquid) {
        MCDataUtils.writeFluidStack(this, liquid);
        return this;
    }

    default MCDataOutput writeTextComponent(ITextComponent component) {
        MCDataUtils.writeTextComponent(this, component);
        return this;
    }
}
