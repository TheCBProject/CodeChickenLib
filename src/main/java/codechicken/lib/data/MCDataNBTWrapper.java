package codechicken.lib.data;

import codechicken.lib.vec.BlockCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by covers1624 on 5/28/2016.
 */
public class MCDataNBTWrapper implements MCDataHandler {

    private NBTTagCompound tagCompound = new NBTTagCompound();

    private int readIndex = 0;
    private int writeIndex = 0;

    //Write.
    public MCDataNBTWrapper() {

    }

    public MCDataNBTWrapper(NBTTagCompound tagCompound) {
        this.tagCompound = tagCompound;
    }

    private void nextReadIndex() {
        readIndex++;
    }

    private String getReadIndex() {
        return String.valueOf(readIndex);
    }

    private void nextWriteIndex() {
        writeIndex++;
    }

    private String getWriteIndex() {
        return String.valueOf(writeIndex);
    }

    public NBTTagCompound build() {
        return tagCompound;
    }

    @Override
    public long readLong() {
        long l = tagCompound.getLong(getReadIndex());
        nextReadIndex();
        return l;
    }

    @Override
    public int readInt() {
        int i = tagCompound.getInteger(getReadIndex());
        nextReadIndex();
        return i;
    }

    @Override
    public short readShort() {
        short s = tagCompound.getShort(getReadIndex());
        nextReadIndex();
        return s;
    }

    @Override
    public int readUShort() {
        short s = tagCompound.getShort(getReadIndex());
        nextReadIndex();
        return s;
    }

    @Override
    public byte readByte() {
        byte b = tagCompound.getByte(getReadIndex());
        nextReadIndex();
        return b;
    }

    @Override
    public short readUByte() {
        byte b = tagCompound.getByte(getReadIndex());
        nextReadIndex();
        return b;
    }

    @Override
    public double readDouble() {
        double d = tagCompound.getDouble(getReadIndex());
        nextReadIndex();
        return d;
    }

    @Override
    public float readFloat() {
        float f = tagCompound.getFloat(getReadIndex());
        nextReadIndex();
        return f;
    }

    @Override
    public boolean readBoolean() {
        boolean b = tagCompound.getBoolean(getReadIndex());
        nextReadIndex();
        return b;
    }

    @Override
    public char readChar() {
        char c = tagCompound.getString(getReadIndex()).charAt(0);
        nextReadIndex();
        return c;
    }

    @Override
    public int readVarShort() {
        return MCDataIO.readVarShort(this);
    }

    @Override
    public int readVarInt() {
        return MCDataIO.readVarInt(this);
    }

    @Override
    public long readVarLong() {
        long i = 0L;
        int j = 0;

        while (true) {
            byte b0 = readByte();
            i |= (long) (b0 & 127) << j++ * 7;

            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    @Override
    public byte[] readArray(int length) {
        byte[] b = tagCompound.getByteArray(getReadIndex());
        nextReadIndex();
        return b;
    }

    @Override
    public String readString() {
        String s = tagCompound.getString(getReadIndex());
        nextReadIndex();
        return s;
    }

    @Override
    public BlockCoord readCoord() {
        return new BlockCoord(readPos());
    }

    @Override
    public BlockPos readPos() {
        return new BlockPos(readInt(), readInt(), readInt());
    }

    @Override
    public NBTTagCompound readNBTTagCompound() {
        NBTTagCompound tag = tagCompound.getCompoundTag(getReadIndex());
        nextReadIndex();
        return tag;
    }

    @Override
    public ItemStack readItemStack() {
        return MCDataIO.readItemStack(this);
    }

    @Override
    public FluidStack readFluidStack() {
        return MCDataIO.readFluidStack(this);
    }

    @Override
    public MCDataNBTWrapper writeLong(long l) {
        tagCompound.setLong(getWriteIndex(), l);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeInt(int i) {
        tagCompound.setInteger(getWriteIndex(), i);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeShort(int s) {
        tagCompound.setShort(getWriteIndex(), (short) s);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeByte(int b) {
        tagCompound.setByte(getWriteIndex(), (byte) b);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeDouble(double d) {
        tagCompound.setDouble(getWriteIndex(), d);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeFloat(float f) {
        tagCompound.setFloat(getWriteIndex(), f);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeBoolean(boolean b) {
        tagCompound.setBoolean(getWriteIndex(), b);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeChar(char c) {
        tagCompound.setString(getWriteIndex(), String.valueOf(c));
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeVarInt(int i) {
        MCDataIO.writeVarInt(this, i);
        return this;
    }

    @Override
    public MCDataNBTWrapper writeVarShort(int s) {
        MCDataIO.writeVarShort(this, s);
        return this;
    }

    @Override
    public MCDataNBTWrapper writeArray(byte[] array) {
        tagCompound.setByteArray(getWriteIndex(), array);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeString(String s) {
        tagCompound.setString(getWriteIndex(), s);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeCoord(int x, int y, int z) {
        writeInt(x);
        writeInt(y);
        writeInt(z);
        return this;
    }

    @Override
    public MCDataNBTWrapper writePos(BlockPos pos) {
        writeInt(pos.getX());
        writeInt(pos.getY());
        writeInt(pos.getZ());
        return this;
    }

    @Override
    public MCDataNBTWrapper writeCoord(BlockCoord coord) {
        writePos(coord.pos());
        return this;
    }

    @Override
    public MCDataNBTWrapper writeNBTTagCompound(NBTTagCompound tag) {
        tagCompound.setTag(getWriteIndex(), tag);
        nextWriteIndex();
        return this;
    }

    @Override
    public MCDataNBTWrapper writeItemStack(ItemStack stack) {
        MCDataIO.writeItemStack(this, stack);
        return this;
    }

    @Override
    public MCDataNBTWrapper writeFluidStack(FluidStack liquid) {
        MCDataIO.writeFluidStack(this, liquid);
        return this;
    }
}
