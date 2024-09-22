package codechicken.lib.packet;

import codechicken.lib.data.MCDataByteBuf;
import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.nio.*;
import java.util.UUID;

//Tasty Cheese!
public final class PacketCustom extends MCDataByteBuf {

    private final ResourceLocation channel;
    private final boolean inbound;
    private final int type;

    PacketCustom(Pkt pkt) {
        super(pkt.data);
        channel = pkt.id;
        inbound = true;
        type = readUByte();
    }

    public PacketCustom(ResourceLocation channel, int type) {
        super(Unpooled.buffer());
        if (!MathHelper.between(0, type, 255)) {
            throw new RuntimeException("Invalid packet type, Must be between 0 and 255. Got: " + type);
        }
        this.channel = channel;
        inbound = false;
        this.type = type;
        writeByte(type);
    }

    public boolean isInbound() {
        return inbound;
    }

    public int getType() {
        return type;
    }

    public ResourceLocation getChannel() {
        return channel;
    }

    public CustomPacketPayload toCustomPayload() {
        if (isInbound()) throw new RuntimeException("Unable to send an inbound packet.");

        return new Pkt(channel, toFriendlyByteBuf());
    }

    // region Server -> Client
    public Packet<?> toClientPacket() {
        return PacketSender.toClientPacket(toCustomPayload());
    }

    public void sendToPlayer(@Nullable ServerPlayer player) {
        PacketSender.sendToPlayer(toCustomPayload(), player);
    }

    public void sendToAllPlayers() {
        PacketSender.sendToAllPlayers(toCustomPayload());
    }

    public void sendToAllAround(BlockPos pos, double range, ResourceKey<Level> dim) {
        PacketSender.sendToAllAround(toCustomPayload(), pos, range, dim);
    }

    public void sendToAllAround(double x, double y, double z, double range, ResourceKey<Level> dim) {
        PacketSender.sendToAllAround(toCustomPayload(), x, y, z, range, dim);
    }

    public void sendToDimension(ResourceKey<Level> dim) {
        PacketSender.sendToDimension(toCustomPayload(), dim);
    }

    public void sendToChunk(BlockEntity tile) {
        PacketSender.sendToChunk(toCustomPayload(), tile);
    }

    public void sendToChunk(ServerLevel level, BlockPos pos) {
        PacketSender.sendToChunk(toCustomPayload(), level, pos);
    }

    public void sendToChunk(ServerLevel level, int chunkX, int chunkZ) {
        PacketSender.sendToChunk(toCustomPayload(), level, chunkX, chunkZ);
    }

    public void sendToChunk(ServerLevel level, ChunkPos pos) {
        PacketSender.sendToChunk(toCustomPayload(), level, pos);
    }

    public void sendToOps() {
        PacketSender.sendToOps(toCustomPayload());
    }
    // endregion

    // region Client -> Server
    public Packet<?> toServerPacket() {
        return PacketSender.toServerPacket(toCustomPayload());
    }

    public void sendToServer() {
        PacketSender.sendToServer(toCustomPayload());
    }
    // endregion

    //region Machine Generated overrides.
    //@formatter:off
    @Override public PacketCustom writeByte(int p0) { super.writeByte(p0); return this; }
    @Override public PacketCustom writeChar(int c) { super.writeChar(c); return this; }
    @Override public PacketCustom writeShort(int p0) { super.writeShort(p0); return this; }
    @Override public PacketCustom writeInt(int p0) { super.writeInt(p0); return this; }
    @Override public PacketCustom writeLong(long l) { super.writeLong(l); return this; }
    @Override public PacketCustom writeFloat(float f) { super.writeFloat(f); return this; }
    @Override public PacketCustom writeDouble(double p0) { super.writeDouble(p0); return this; }
    @Override public PacketCustom writeBoolean(boolean b) { super.writeBoolean(b); return this; }
    @Override public PacketCustom writeBytes(byte[] b) { super.writeBytes(b); return this; }
    @Override public PacketCustom writeBytes(byte[] b, int off, int len) { super.writeBytes(b, off, len); return this; }
    @Override public PacketCustom writeChars(char[] c) { super.writeChars(c); return this; }
    @Override public PacketCustom writeChars(char[] c, int off, int len) { super.writeChars(c, off, len); return this; }
    @Override public PacketCustom writeShorts(short[] s) { super.writeShorts(s); return this; }
    @Override public PacketCustom writeShorts(short[] s, int off, int len) { super.writeShorts(s, off, len); return this; }
    @Override public PacketCustom writeInts(int[] i) { super.writeInts(i); return this; }
    @Override public PacketCustom writeInts(int[] i, int off, int len) { super.writeInts(i, off, len); return this; }
    @Override public PacketCustom writeLongs(long[] l) { super.writeLongs(l); return this; }
    @Override public PacketCustom writeLongs(long[] l, int off, int len) { super.writeLongs(l, off, len); return this; }
    @Override public PacketCustom writeFloats(float[] f) { super.writeFloats(f); return this; }
    @Override public PacketCustom writeFloats(float[] f, int off, int len) { super.writeFloats(f, off, len); return this; }
    @Override public PacketCustom writeDoubles(double[] d) { super.writeDoubles(d); return this; }
    @Override public PacketCustom writeDoubles(double[] d, int off, int len) { super.writeDoubles(d, off, len); return this; }
    @Override public PacketCustom writeBooleans(boolean[] b) { super.writeBooleans(b); return this; }
    @Override public PacketCustom writeBooleans(boolean[] b, int off, int len) { super.writeBooleans(b, off, len); return this; }
    @Override public PacketCustom append(byte[] bytes) { super.append(bytes); return this; }
    @Override public PacketCustom writeVarInt(int i) { super.writeVarInt(i); return this; }
    @Override public PacketCustom writeVarLong(long l) { super.writeVarLong(l); return this; }
    @Override public PacketCustom writeVarInts(int[] i) { super.writeVarInts(i); return this; }
    @Override public PacketCustom writeVarInts(int[] i, int off, int len) { super.writeVarInts(i, off, len); return this; }
    @Override public PacketCustom writeVarLongs(long[] l) { super.writeVarLongs(l); return this; }
    @Override public PacketCustom writeVarLongs(long[] l, int off, int len) { super.writeVarLongs(l, off, len); return this; }
    @Override public PacketCustom writeString(String s) { super.writeString(s); return this; }
    @Override public PacketCustom writeString(String s, int maxLen) { super.writeString(s, maxLen); return this; }
    @Override public PacketCustom writeUUID(UUID uuid) { super.writeUUID(uuid); return this; }
    @Override public PacketCustom writeEnum(Enum<?> value) { super.writeEnum(value); return this; }
    @Override public PacketCustom writeByteBuffer(ByteBuffer buffer) { super.writeByteBuffer(buffer); return this; }
    @Override public PacketCustom writeCharBuffer(CharBuffer buffer) { super.writeCharBuffer(buffer); return this; }
    @Override public PacketCustom writeShortBuffer(ShortBuffer buffer) { super.writeShortBuffer(buffer); return this; }
    @Override public PacketCustom writeIntBuffer(IntBuffer buffer) { super.writeIntBuffer(buffer); return this; }
    @Override public PacketCustom writeLongBuffer(LongBuffer buffer) { super.writeLongBuffer(buffer); return this; }
    @Override public PacketCustom writeFloatBuffer(FloatBuffer buffer) { super.writeFloatBuffer(buffer); return this; }
    @Override public PacketCustom writeDoubleBuffer(DoubleBuffer buffer) { super.writeDoubleBuffer(buffer); return this; }
    @Override public PacketCustom writeVector(Vector3 vec) { super.writeVector(vec); return this; }
    @Override public PacketCustom writeCuboid(Cuboid6 cuboid) { super.writeCuboid(cuboid); return this; }
    @Override public PacketCustom writeResourceLocation(ResourceLocation loc) { super.writeResourceLocation(loc); return this; }
    @Override public PacketCustom writeDirection(Direction dir) { super.writeDirection(dir); return this; }
    @Override public PacketCustom writePos(BlockPos pos) { super.writePos(pos); return this; }
    @Override public PacketCustom writeVec3i(Vec3i vec) { super.writeVec3i(vec); return this; }
    @Override public PacketCustom writeVec3d(Vec3 vec) { super.writeVec3d(vec); return this; }
    @Override public PacketCustom writeCompoundNBT(CompoundTag tag) { super.writeCompoundNBT(tag); return this; }
    @Override public PacketCustom writeNullableCompoundNBT(@Nullable CompoundTag tag) { super.writeNullableCompoundNBT(tag); return this; }
    @Override public PacketCustom writeFluidStack(FluidStack stack) { super.writeFluidStack(stack); return this; }
    @Override public PacketCustom writeItemStack(ItemStack stack) { super.writeItemStack(stack); return this; }
    @Override public PacketCustom writeTextComponent(Component component) { super.writeTextComponent(component); return this; }
    @Override public <T> PacketCustom writeRegistryIdDirect(Registry<T> registry, T entry) { super.writeRegistryIdDirect(registry, entry); return this; }
    @Override public <T> PacketCustom writeRegistryIdDirect(Registry<T> registry, ResourceLocation entry) { super.writeRegistryIdDirect(registry, entry); return this; }
    @Override public <T> PacketCustom writeRegistryId(Registry<T> registry, T entry) { super.writeRegistryId(registry, entry); return this; }
    @Override public <T> PacketCustom writeRegistryId(Registry<T> registry, ResourceLocation entry) { super.writeRegistryId(registry, entry); return this; }
    @Override public PacketCustom writeByteBuf(ByteBuf buf) { super.writeByteBuf(buf); return this; }
    @Override public PacketCustom append(ByteBuf buf) { super.append(buf); return this; }
    //@formatter:on
    //endregion

    record Pkt(ResourceLocation id, ByteBuf data) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
            data.markReaderIndex();
            buf.writeBytes(data);
            data.resetReaderIndex();
        }
    }
}
