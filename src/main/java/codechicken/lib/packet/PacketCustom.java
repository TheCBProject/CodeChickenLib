package codechicken.lib.packet;

import codechicken.lib.data.MCDataByteBuf;
import codechicken.lib.math.MathHelper;
import codechicken.lib.util.ServerUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.*;
import java.util.UUID;

//TODO, Evaluate explicit packet compression again.
//Tasty Cheese!
public final class PacketCustom extends MCDataByteBuf {

    private final ResourceLocation channel;
    private final int type;

    public PacketCustom(ByteBuf payload) {
        super(payload);
        this.channel = null;
        this.type = readUByte();
    }

    public PacketCustom(ResourceLocation channel, int type) {
        super(Unpooled.buffer());
        if (!MathHelper.between(0, type, 255)) {
            throw new RuntimeException("Invalid packet type, Must be between 0 and 255. Got: " + type);
        }
        this.channel = channel;
        this.type = type;
        writeByte(type);
    }

    public boolean incoming() {
        return channel == null;
    }

    public int getType() {
        return type;
    }

    public ResourceLocation getChannel() {
        return channel;
    }

    public Packet<?> toPacket(NetworkDirection direction) {
        return toPacket(direction, 0);
    }

    public Packet<?> toPacket(NetworkDirection direction, int index) {
        if (incoming()) {
            throw new IllegalStateException("Tried to write an incoming packet");
        }
        return direction.buildPacket(Pair.of(toPacketBuffer(), index), channel).getThis();
    }

    //endregion
    //region Server -> Client.
    public void sendToPlayer(ServerPlayer player) {
        sendToPlayer(toPacket(NetworkDirection.PLAY_TO_CLIENT), player);
    }

    public static void sendToPlayer(Packet<?> packet, ServerPlayer player) {
        if (player == null) {
            sendToClients(packet);
        } else {
            player.connection.send(packet);
        }
    }

    public void sendToClients() {
        sendToClients(toPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToClients(Packet<?> packet) {
        ServerUtils.getServer().getPlayerList().broadcastAll(packet);
    }

    public void sendPacketToAllAround(BlockPos pos, double range, ResourceKey<Level> dim) {
        sendPacketToAllAround(pos.getX(), pos.getY(), pos.getZ(), range, dim);
    }

    public void sendPacketToAllAround(double x, double y, double z, double range, ResourceKey<Level> dim) {
        sendToAllAround(toPacket(NetworkDirection.PLAY_TO_CLIENT), x, y, z, range, dim);
    }

    public static void sendToAllAround(Packet<?> packet, double x, double y, double z, double range, ResourceKey<Level> dim) {
        ServerUtils.getServer().getPlayerList().broadcast(null, x, y, z, range, dim, packet);
    }

    public void sendToDimension(ResourceKey<Level> dim) {
        sendToDimension(toPacket(NetworkDirection.PLAY_TO_CLIENT), dim);
    }

    public static void sendToDimension(Packet<?> packet, ResourceKey<Level> dim) {
        ServerUtils.getServer().getPlayerList().broadcastAll(packet, dim);
    }

    public void sendToChunk(BlockEntity tile) {
        sendToChunk(tile.getLevel(), tile.getBlockPos());
    }

    public void sendToChunk(Level world, BlockPos blockPos) {
        sendToChunk(toPacket(NetworkDirection.PLAY_TO_CLIENT), world, blockPos);
    }

    public void sendToChunk(Level world, int chunkX, int chunkZ) {
        sendToChunk(toPacket(NetworkDirection.PLAY_TO_CLIENT), world, chunkX, chunkZ);
    }

    public void sendToChunk(Level world, ChunkPos pos) {
        sendToChunk(toPacket(NetworkDirection.PLAY_TO_CLIENT), world, pos);
    }

    public static void sendToChunk(Packet<?> packet, Level world, BlockPos blockPos) {
        sendToChunk(packet, world, blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    public static void sendToChunk(Packet<?> packet, Level world, int chunkX, int chunkZ) {
        sendToChunk(packet, world, new ChunkPos(chunkX, chunkZ));
    }

    public static void sendToChunk(Packet<?> packet, Level world, ChunkPos pos) {
        ServerLevel serverLevel = (ServerLevel) world;
        serverLevel.getChunkSource().chunkMap.getPlayers(pos, false).forEach(e -> e.connection.send(packet));
    }

    public void sendToOps() {
        sendToOps(toPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToOps(Packet<?> packet) {
        ServerOpList opList = ServerUtils.getServer().getPlayerList().getOps();
        for (ServerPlayer player : ServerUtils.getServer().getPlayerList().getPlayers()) {
            if (opList.get(player.getGameProfile()) != null) {
                sendToPlayer(packet, player);
            }
        }
    }

    @OnlyIn (Dist.CLIENT)
    public void sendToServer() {
        sendToServer(toPacket(NetworkDirection.PLAY_TO_SERVER));
    }

    @OnlyIn (Dist.CLIENT)
    public static void sendToServer(Packet<?> packet) {
        Minecraft.getInstance().getConnection().send(packet);
    }

    //endregion
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
    @Override public PacketCustom writeFluidStack(FluidStack stack) { super.writeFluidStack(stack); return this; }
    @Override public PacketCustom writeItemStack(ItemStack stack) { super.writeItemStack(stack); return this; }
    @Override public PacketCustom writeItemStack(ItemStack stack, boolean limitedTag) { super.writeItemStack(stack, limitedTag); return this; }
    @Override public PacketCustom writeTextComponent(Component component) { super.writeTextComponent(component); return this; }
    @Override public <T extends IForgeRegistryEntry<T>> PacketCustom writeRegistryIdUnsafe(IForgeRegistry<T> registry, T entry) { super.writeRegistryIdUnsafe(registry, entry); return this; }
    @Override public <T extends IForgeRegistryEntry<T>> PacketCustom writeRegistryIdUnsafe(IForgeRegistry<T> registry, ResourceLocation entry) { super.writeRegistryIdUnsafe(registry, entry); return this; }
    @Override public <T extends IForgeRegistryEntry<T>> PacketCustom writeRegistryId(T entry) { super.writeRegistryId(entry); return this; }
    @Override public PacketCustom writeByteBuf(ByteBuf buf) { super.writeByteBuf(buf); return this; }
    @Override public PacketCustom append(ByteBuf buf) { super.append(buf); return this; }
    //@formatter:off
    //endregion
}
