package codechicken.lib.packet;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.math.MathHelper;
import codechicken.lib.util.ServerUtils;
import codechicken.lib.vec.Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.management.OpList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkDirection;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

//Tasty Cheese!
public final class PacketCustom implements MCDataInput, MCDataOutput {

    private final PacketBuffer buf;
    private ResourceLocation channel;
    private int type;

    public PacketCustom(ByteBuf payload) {
        buf = payload instanceof PacketBuffer ? (PacketBuffer) payload : new PacketBuffer(payload);
        type = readUByte();
    }

    public PacketCustom(ResourceLocation channel, int type) {
        if (!MathHelper.between(0, type, 255)) {
            throw new RuntimeException("Invalid packet type, Must be between 0 and 255. Got: " + type);
        }
        buf = new PacketBuffer(Unpooled.buffer());
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

    public IPacket<?> toPacket(NetworkDirection direction) {
        return toPacket(direction, 0);
    }

    public IPacket<?> toPacket(NetworkDirection direction, int index) {
        if (incoming()) {
            throw new IllegalStateException("Tried to write an incoming packet");
        }
        return direction.buildPacket(Pair.of(buf, index), channel).getThis();
    }

    public PacketBuffer getPacketBuffer() {
        return buf;
    }

    //region To and from NBT / TilePacket.
    public CompoundNBT writeToNBT(CompoundNBT tagCompound) {
        tagCompound.putByteArray("CCL:data", buf.array());
        return tagCompound;
    }

    public CompoundNBT toNBTTag() {
        return writeToNBT(new CompoundNBT());
    }

    public static PacketCustom fromNBTTag(CompoundNBT tagCompound) {
        return new PacketCustom(Unpooled.copiedBuffer(tagCompound.getByteArray("CCL:data")));
    }

    public SUpdateTileEntityPacket toTilePacket(BlockPos pos) {
        return new SUpdateTileEntityPacket(pos, -6000, toNBTTag());
    }

    @OnlyIn (Dist.CLIENT)
    public static PacketCustom fromTilePacket(SUpdateTileEntityPacket tilePacket) {
        return fromNBTTag(tilePacket.getNbtCompound());
    }
    //endregion

    //region Server -> Client.
    public void sendToPlayer(ServerPlayerEntity player) {
        sendToPlayer(toPacket(NetworkDirection.PLAY_TO_CLIENT), player);
    }

    public static void sendToPlayer(IPacket<?> packet, ServerPlayerEntity player) {
        if (player == null) {
            sendToClients(packet);
        } else {
            player.connection.sendPacket(packet);
        }
    }

    public void sendToClients() {
        sendToClients(toPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToClients(IPacket<?> packet) {
        ServerUtils.getServer().getPlayerList().sendPacketToAllPlayers(packet);
    }

    public void sendPacketToAllAround(BlockPos pos, double range, DimensionType dim) {
        sendPacketToAllAround(pos.getX(), pos.getY(), pos.getZ(), range, dim);
    }

    public void sendPacketToAllAround(double x, double y, double z, double range, DimensionType dim) {
        sendToAllAround(toPacket(NetworkDirection.PLAY_TO_CLIENT), x, y, z, range, dim);
    }

    public static void sendToAllAround(IPacket<?> packet, double x, double y, double z, double range, DimensionType dim) {
        ServerUtils.getServer().getPlayerList().sendToAllNearExcept(null, x, y, z, range, dim, packet);
    }

    public void sendToDimension(DimensionType dim) {
        sendToDimension(toPacket(NetworkDirection.PLAY_TO_CLIENT), dim);
    }

    public static void sendToDimension(IPacket<?> packet, DimensionType dim) {
        ServerUtils.getServer().getPlayerList().sendPacketToAllPlayersInDimension(packet, dim);
    }

    public void sendToChunk(TileEntity tile) {
        sendToChunk(tile.getWorld(), tile.getPos().getX() >> 4, tile.getPos().getZ() >> 4);
    }

    public void sendToChunk(World world, int chunkX, int chunkZ) {
        sendToChunk(toPacket(NetworkDirection.PLAY_TO_CLIENT), world, chunkX, chunkZ);
    }

    public void sendToChunk(World world, ChunkPos pos) {
        sendToChunk(toPacket(NetworkDirection.PLAY_TO_CLIENT), world, pos);
    }

    public static void sendToChunk(IPacket<?> packet, World world, int chunkX, int chunkZ) {
        sendToChunk(packet, world, new ChunkPos(chunkX, chunkZ));
    }

    public static void sendToChunk(IPacket<?> packet, World world, ChunkPos pos) {
        ServerWorld serverWorld = (ServerWorld) world;
        serverWorld.getChunkProvider().chunkManager.getTrackingPlayers(pos, false).forEach(e -> e.connection.sendPacket(packet));
    }

    public void sendToOps() {
        sendToOps(toPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToOps(IPacket<?> packet) {
        OpList opList = ServerUtils.getServer().getPlayerList().getOppedPlayers();
        for (ServerPlayerEntity player : ServerUtils.getServer().getPlayerList().getPlayers()) {
            if (opList.hasEntry(player.getGameProfile())) {
                sendToPlayer(packet, player);
            }
        }
    }

    @OnlyIn (Dist.CLIENT)
    public void sendToServer() {
        sendToServer(toPacket(NetworkDirection.PLAY_TO_SERVER));
    }

    @OnlyIn (Dist.CLIENT)
    public static void sendToServer(IPacket<?> packet) {
        Minecraft.getInstance().getConnection().sendPacket(packet);
    }

    //endregion

    //region MCDataOutput. Write.
    @Override
    public PacketCustom writeBoolean(boolean b) {
        buf.writeBoolean(b);
        return this;
    }

    @Override
    public PacketCustom writeByte(int b) {
        buf.writeByte(b);
        return this;
    }

    @Override
    public PacketCustom writeShort(int s) {
        buf.writeShort(s);
        return this;
    }

    @Override
    public PacketCustom writeInt(int i) {
        buf.writeInt(i);
        return this;
    }

    @Override
    public PacketCustom writeFloat(float f) {
        buf.writeFloat(f);
        return this;
    }

    @Override
    public PacketCustom writeDouble(double d) {
        buf.writeDouble(d);
        return this;
    }

    @Override
    public PacketCustom writeLong(long l) {
        buf.writeLong(l);
        return this;
    }

    public PacketCustom writeChar(char c) {
        buf.writeChar(c);
        return this;
    }

    @Override
    public PacketCustom writeVarInt(int i) {
        MCDataOutput.super.writeVarInt(i);
        return this;
    }

    @Override
    public PacketCustom writeVarShort(int s) {
        MCDataOutput.super.writeVarShort(s);
        return this;
    }

    @Override
    public PacketCustom writeVarLong(long l) {
        MCDataOutput.super.writeVarLong(l);
        return this;
    }

    public PacketCustom writeArray(byte[] barray) {
        buf.writeBytes(barray);
        return this;
    }

    @Override
    public PacketCustom writeString(String s) {
        MCDataOutput.super.writeString(s);
        return this;
    }

    @Override
    public PacketCustom writeUUID(UUID uuid) {
        MCDataOutput.super.writeUUID(uuid);
        return this;
    }

    @Override
    public PacketCustom writeEnum(Enum<?> value) {
        MCDataOutput.super.writeEnum(value);
        return this;
    }

    @Override
    public PacketCustom writeResourceLocation(ResourceLocation location) {
        MCDataOutput.super.writeResourceLocation(location);
        return this;
    }

    @Override
    public PacketCustom writePos(BlockPos pos) {
        MCDataOutput.super.writePos(pos);
        return this;
    }

    @Override
    public PacketCustom writeVector(Vector3 vec) {
        MCDataOutput.super.writeVector(vec);
        return null;
    }

    @Override
    public PacketCustom writeCompoundNBT(CompoundNBT tag) {
        MCDataOutput.super.writeCompoundNBT(tag);
        return this;
    }

    @Override
    public PacketCustom writeItemStack(ItemStack stack) {
        MCDataOutput.super.writeItemStack(stack);
        return this;
    }

    @Override
    public PacketCustom writeFluidStack(FluidStack liquid) {
        MCDataOutput.super.writeFluidStack(liquid);
        return this;
    }

    @Override
    public PacketCustom writeTextComponent(ITextComponent component) {
        MCDataOutput.super.writeTextComponent(component);
        return this;
    }

    //endregion

    //region MCDataInput. Read.
    public short readUByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public char readChar() {
        return buf.readChar();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    public int readUShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    public byte[] readArray(int length) {
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }
    //endregion
}
