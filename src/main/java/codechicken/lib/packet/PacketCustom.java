package codechicken.lib.packet;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.EncoderException;
import io.netty.util.AttributeKey;
import io.netty.util.ByteProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkHandshakeEstablished;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class PacketCustom extends ByteBuf implements MCDataInput, MCDataOutput {

    public static AttributeKey<CustomInboundHandler> cclHandler = AttributeKey.valueOf("ccl:handler");

    //region In/OutBound Handling
    @ChannelHandler.Sharable
    public static class CustomInboundHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {

        public EnumMap<Side, CustomHandler> handlers = Maps.newEnumMap(Side.class);

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

            super.handlerAdded(ctx);
            ctx.channel().attr(cclHandler).set(this);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) throws Exception {

            handlers.get(ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get()).handle(ctx.channel().attr(NetworkRegistry.NET_HANDLER).get(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get(), new PacketCustom(msg.payload()));
        }
    }

    public interface CustomHandler {

        void handle(INetHandler handler, String channel, PacketCustom packet);
    }

    public static class ClientInboundHandler implements CustomHandler {

        private IClientPacketHandler handler;

        public ClientInboundHandler(ICustomPacketHandler handler) {

            this.handler = (IClientPacketHandler) handler;
        }

        @Override
        public void handle(final INetHandler netHandler, final String channel, final PacketCustom packet) {

            if (netHandler instanceof INetHandlerPlayClient) {
                Minecraft mc = Minecraft.getMinecraft();
                if (!mc.isCallingFromMinecraftThread()) {
                    mc.addScheduledTask(() -> handle(netHandler, channel, packet));
                } else {
                    handler.handlePacket(packet, mc, (INetHandlerPlayClient) netHandler);
                }
            } else {
                System.err.println("Invalid INetHandler for PacketCustom on channel: " + channel);
            }
        }
    }

    public static class ServerInboundHandler implements CustomHandler {

        private IServerPacketHandler handler;

        public ServerInboundHandler(ICustomPacketHandler handler) {

            this.handler = (IServerPacketHandler) handler;
        }

        @Override
        public void handle(final INetHandler netHandler, final String channel, final PacketCustom packet) {

            if (netHandler instanceof NetHandlerPlayServer) {
                MinecraftServer mc = FMLCommonHandler.instance().getMinecraftServerInstance();
                if (!mc.isCallingFromMinecraftThread()) {
                    mc.addScheduledTask(() -> handle(netHandler, channel, packet));
                } else {
                    handler.handlePacket(packet, ((NetHandlerPlayServer) netHandler).player, (INetHandlerPlayServer) netHandler);
                }
            } else {
                System.err.println("Invalid INetHandler for PacketCustom on channel: " + channel);
            }
        }
    }

    public static class HandshakeInboundHandler extends ChannelInboundHandlerAdapter {

        public IHandshakeHandler handler;

        public HandshakeInboundHandler(IHandshakeHandler handler) {

            this.handler = handler;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

            if (evt instanceof NetworkHandshakeEstablished) {
                INetHandler netHandler = ((NetworkDispatcher) ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).get()).getNetHandler();
                if (netHandler instanceof NetHandlerPlayServer) {
                    handler.handshakeReceived((NetHandlerPlayServer) netHandler);
                }
            } else {
                ctx.fireUserEventTriggered(evt);
            }
        }
    }
    //endregion

    public static FMLEmbeddedChannel getOrCreateChannel(String channelName, Side side) {

        if (!NetworkRegistry.INSTANCE.hasChannel(channelName, side)) {
            NetworkRegistry.INSTANCE.newChannel(channelName, new CustomInboundHandler());
        }
        return NetworkRegistry.INSTANCE.getChannel(channelName, side);
    }

    public static String channelName(Object channelKey) {

        if (channelKey instanceof String) {
            return (String) channelKey;
        }
        if (channelKey instanceof ModContainer) {
            String s = ((ModContainer) channelKey).getModId();
            if (s.length() > 20) {
                throw new IllegalArgumentException("Mod ID (" + s + ") too long for use as channel (20 chars). Use a string identifier");
            }
            return s;
        }

        ModContainer mc = FMLCommonHandler.instance().findContainerFor(channelKey);
        if (mc != null) {
            return mc.getModId();
        }

        throw new IllegalArgumentException("Invalid channel: " + channelKey);
    }

    public static void assignHandler(Object channelKey, ICustomPacketHandler handler) {

        String channelName = channelName(channelKey);
        Side side = handler instanceof IServerPacketHandler ? Side.SERVER : Side.CLIENT;
        FMLEmbeddedChannel channel = getOrCreateChannel(channelName, side);
        channel.attr(cclHandler).get().handlers.put(side, side == Side.SERVER ? new ServerInboundHandler(handler) : new ClientInboundHandler(handler));
    }

    public static void assignHandshakeHandler(Object channelKey, IHandshakeHandler handler) {

        FMLEmbeddedChannel channel = getOrCreateChannel(channelName(channelKey), Side.SERVER);
        channel.pipeline().addLast(new HandshakeInboundHandler(handler));
    }

    private final ByteBuf buf;
    private String channel;
    private int type;

    public PacketCustom(ByteBuf payload) {
        byte[] bytes = new byte[payload.readableBytes()];
        payload.readBytes(bytes);
        buf = Unpooled.wrappedBuffer(bytes);

        type = readUnsignedByte();
        if (type > 0x80) {
            decompress();
        }
        type &= 0x7F;
    }

    public PacketCustom(Object channelKey, int type) {

        buf = Unpooled.buffer();
        if (type <= 0 || type >= 0x80) {
            throw new IllegalArgumentException("Packet type: " + type + " is not within required 0 < t < 0x80");
        }

        this.channel = channelName(channelKey);
        this.type = type;
        writeByte(type);
    }

    /**
     * Decompresses the remaining ByteBuf (after type has been read) using Snappy
     */
    private void decompress() {

        Inflater inflater = new Inflater();
        try {
            int len = readVarInt();
            byte[] out = new byte[len];
            inflater.setInput(array(), readerIndex(), readableBytes());
            inflater.inflate(out);
            clear();
            writeArray(out);

        } catch (Exception e) {
            throw new EncoderException(e);
        } finally {
            inflater.end();
        }
    }

    /**
     * Compresses the payload ByteBuf after the type byte
     */
    private void do_compress() {

        Deflater deflater = new Deflater();
        try {
            readerIndex(1);
            int len = readableBytes();
            deflater.setInput(array(), readerIndex(), len);
            deflater.finish();
            byte[] out = new byte[len];
            int clen = deflater.deflate(out);
            if (clen >= len - 5 || !deflater.finished())//not worth compressing, gets larger
            {
                return;
            }
            clear();
            writeByte(type | 0x80);
            writeVarInt(len);
            writeArray(out);
        } catch (Exception e) {
            throw new EncoderException(e);
        } finally {
            readerIndex(0);
            deflater.end();
        }
    }

    public boolean incoming() {

        return channel == null;
    }

    public int getType() {

        return type & 0x7F;
    }

    public PacketCustom compress() {

        if (incoming()) {
            throw new IllegalStateException("Tried to compress an incoming packet");
        }
        if ((type & 0x80) != 0) {
            throw new IllegalStateException("Packet already compressed");
        }
        type |= 0x80;
        return this;
    }

    public FMLProxyPacket toPacket() {

        if (incoming()) {
            throw new IllegalStateException("Tried to write an incoming packet");
        }

        if (readableBytes() > 32000 || (type & 0x80) != 0) {
            do_compress();
        }

        return new FMLProxyPacket(new PacketBuffer(copy()), channel);
    }

    public PacketBuffer toPacketBuffer() {
        return new PacketBuffer(this);
    }

    //region Send methods, To and from NBT.
    public NBTTagCompound toNBTTag(NBTTagCompound tagCompound) {

        tagCompound.setByteArray("CCL:data", array());
        return tagCompound;
    }

    public NBTTagCompound toNBTTag() {

        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setByteArray("CCL:data", array());
        return tagCompound;
    }

    public static PacketCustom fromNBTTag(NBTTagCompound tagCompound) {

        return new PacketCustom(Unpooled.copiedBuffer(tagCompound.getByteArray("CCL:data")));
    }

    public SPacketUpdateTileEntity toTilePacket(BlockPos pos) {

        return new SPacketUpdateTileEntity(pos, 0, toNBTTag());
    }

    @SideOnly (Side.CLIENT)
    public static PacketCustom fromTilePacket(SPacketUpdateTileEntity tilePacket) {

        return fromNBTTag(tilePacket.getNbtCompound());
    }

    public void sendToPlayer(EntityPlayer player) {

        sendToPlayer(toPacket(), player);
    }

    public static void sendToPlayer(Packet packet, EntityPlayer player) {

        if (player == null) {
            sendToClients(packet);
        } else {
            ((EntityPlayerMP) player).connection.sendPacket(packet);
        }
    }

    public void sendToClients() {

        sendToClients(toPacket());
    }

    public static void sendToClients(Packet packet) {

        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(packet);
    }

    public void sendPacketToAllAround(BlockPos pos, double range, int dim) {

        sendPacketToAllAround(pos.getX(), pos.getY(), pos.getZ(), range, dim);
    }

    public void sendPacketToAllAround(double x, double y, double z, double range, int dim) {

        sendToAllAround(toPacket(), x, y, z, range, dim);
    }

    public static void sendToAllAround(Packet packet, double x, double y, double z, double range, int dim) {

        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendToAllNearExcept(null, x, y, z, range, dim, packet);
    }

    public void sendToDimension(int dim) {

        sendToDimension(toPacket(), dim);
    }

    public static void sendToDimension(Packet packet, int dim) {

        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayersInDimension(packet, dim);
    }

    public void sendToChunk(TileEntity tile) {

        sendToChunk(tile.getWorld(), tile.getPos().getX() >> 4, tile.getPos().getZ() >> 4);
    }

    public void sendToChunk(World world, int chunkX, int chunkZ) {

        sendToChunk(toPacket(), world, chunkX, chunkZ);
    }

    public static void sendToChunk(Packet packet, World world, int chunkX, int chunkZ) {

        PlayerChunkMapEntry playerInstance = ((WorldServer) world).getPlayerChunkMap().getEntry(chunkX, chunkZ);
        if (playerInstance != null) {
            playerInstance.sendPacket(packet);
        }
    }

    public void sendToOps() {

        sendToOps(toPacket());
    }

    public static void sendToOps(Packet packet) {

        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(player.getGameProfile())) {
                sendToPlayer(packet, player);
            }
        }
    }

    @SideOnly (Side.CLIENT)
    public void sendToServer() {

        sendToServer(toPacket());
    }

    @SideOnly (Side.CLIENT)
    public static void sendToServer(Packet packet) {

        Minecraft.getMinecraft().getConnection().sendPacket(packet);
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
    public PacketCustom writeNBTTagCompound(NBTTagCompound tag) {
        MCDataOutput.super.writeNBTTagCompound(tag);
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
        readBytes(bytes);
        return bytes;
    }

    //endregion

    //region ByteBuf wrapper overrides.
    //@formatter:off \o/ wrappers.
    @Override public boolean hasMemoryAddress() {return buf.hasMemoryAddress();}
    @Override public long memoryAddress() {return buf.memoryAddress();}
    @Override public int capacity() {return buf.capacity();}
    @Override public PacketCustom capacity(int newCapacity) {buf.capacity(newCapacity); return this;}
    @Override public int maxCapacity() {return buf.maxCapacity();}
    @Override public ByteBufAllocator alloc() {return buf.alloc();}
    @Override public ByteOrder order() {return buf.order();}
    @Override public ByteBuf order(ByteOrder endianness) {return buf.order(endianness);}
    @Override public ByteBuf unwrap() {return buf;}
    @Override public boolean isDirect() {return buf.isDirect();}
    @Override public boolean isReadOnly() {return toPacketBuffer().isReadOnly();}
    @Override public ByteBuf asReadOnly() {return buf.asReadOnly();}
    @Override public int readerIndex() {return buf.readerIndex();}
    @Override public ByteBuf readerIndex(int readerIndex) {buf.readerIndex(readerIndex);return this;}
    @Override public int writerIndex() {return buf.writerIndex();}
    @Override public PacketCustom writerIndex(int writerIndex) {buf.writerIndex(writerIndex);return this;}
    @Override public PacketCustom setIndex(int readerIndex, int writerIndex) {buf.setIndex(readerIndex, writerIndex);return this;}
    @Override public int readableBytes() {return buf.readableBytes();}
    @Override public int writableBytes() {return buf.writableBytes();}
    @Override public int maxWritableBytes() {return buf.maxWritableBytes();}
    @Override public boolean isReadable() {return buf.isReadable();}
    @Override public boolean isWritable() {return buf.isWritable();}
    @Override public PacketCustom clear() {buf.clear();return this;}
    @Override public PacketCustom markReaderIndex() {buf.markReaderIndex();return this;}
    @Override public PacketCustom resetReaderIndex() {buf.resetReaderIndex();return this;}
    @Override public PacketCustom markWriterIndex() {buf.markWriterIndex();return this;}
    @Override public PacketCustom resetWriterIndex() {buf.resetWriterIndex();return this;}
    @Override public PacketCustom discardReadBytes() {buf.discardReadBytes();return this;}
    @Override public PacketCustom discardSomeReadBytes() {buf.discardSomeReadBytes();return this;}
    @Override public PacketCustom ensureWritable(int minWritableBytes) {buf.ensureWritable(minWritableBytes);return this;}
    @Override public int ensureWritable(int minWritableBytes, boolean force) {return buf.ensureWritable(minWritableBytes, force);}
    @Override public boolean getBoolean(int index) {return buf.getBoolean(index);}
    @Override public byte getByte(int index) {return buf.getByte(index);}
    @Override public short getUnsignedByte(int index) {return buf.getUnsignedByte(index);}
    @Override public short getShort(int index) {return buf.getShort(index);}
    @Override public short getShortLE(int index) {return buf.getShortLE(index);}
    @Override public int getUnsignedShort(int index) {return buf.getUnsignedShort(index);}
    @Override public int getUnsignedShortLE(int index) {return buf.getUnsignedShortLE(index);}
    @Override public int getMedium(int index) {return buf.getMedium(index);}
    @Override public int getMediumLE(int index) {return buf.getMediumLE(index);}
    @Override public int getUnsignedMedium(int index) {return buf.getUnsignedMedium(index);}
    @Override public int getUnsignedMediumLE(int index) {return buf.getUnsignedShortLE(index);}
    @Override public int getInt(int index) {return buf.getInt(index);}
    @Override public int getIntLE(int index) {return buf.getIntLE(index);}
    @Override public long getUnsignedInt(int index) {return buf.getUnsignedInt(index);}
    @Override public long getUnsignedIntLE(int index) {return buf.getUnsignedIntLE(index);}
    @Override public long getLong(int index) {return buf.getLong(index);}
    @Override public long getLongLE(int index) {return buf.getLongLE(index);}
    @Override public char getChar(int index) {return buf.getChar(index);}
    @Override public float getFloat(int index) {return buf.getFloat(index);}
    @Override public double getDouble(int index) {return buf.getDouble(index);}
    @Override public PacketCustom getBytes(int index, ByteBuf dst) {buf.getBytes(index, dst);return this;}
    @Override public PacketCustom getBytes(int index, ByteBuf dst, int length) {buf.getBytes(index, dst, length);return this;}
    @Override public PacketCustom getBytes(int index, ByteBuf dst, int dstIndex, int length) {buf.getBytes(index, dst, dstIndex, length);return this;}
    @Override public PacketCustom getBytes(int index, byte[] dst) {buf.getBytes(index, dst);return this;}
    @Override public PacketCustom getBytes(int index, byte[] dst, int dstIndex, int length) {buf.getBytes(index, dst, dstIndex, length);return this;}
    @Override public PacketCustom getBytes(int index, ByteBuffer dst) {buf.getBytes(index, dst);return this;}
    @Override public PacketCustom getBytes(int index, OutputStream out, int length) throws IOException {buf.getBytes(index, out, length);return this;}
    @Override public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {return buf.getBytes(index, out, length);}
    @Override public int getBytes(int index, FileChannel out, long position, int length) throws IOException {return buf.getBytes(index, out, position, length);}
    @Override public CharSequence getCharSequence(int index, int length, Charset charset) {return buf.getCharSequence(index, length, charset);}
    @Override public PacketCustom setBoolean(int index, boolean value) {buf.setBoolean(index, value);return this;}
    @Override public PacketCustom setByte(int index, int value) {buf.setByte(index, value);return this;}
    @Override public PacketCustom setShort(int index, int value) {buf.setShort(index, value);return this;}
    @Override public ByteBuf setShortLE(int index, int value) {return buf.setShortLE(index, value);}
    @Override public PacketCustom setMedium(int index, int value) {buf.setMedium(index, value);return this;}
    @Override public ByteBuf setMediumLE(int index, int value) {return buf.setMediumLE(index, value);}
    @Override public PacketCustom setInt(int index, int value) {buf.setInt(index, value);return this;}
    @Override public ByteBuf setIntLE(int index, int value) {return buf.setIntLE(index, value);}
    @Override public PacketCustom setLong(int index, long value) {buf.setLong(index, value);return this;}
    @Override public ByteBuf setLongLE(int index, long value) {return buf.setLong(index, value);}
    @Override public PacketCustom setChar(int index, int value) {buf.setChar(index, value);return this;}
    @Override public PacketCustom setFloat(int index, float value) {buf.setFloat(index, value);return this;}
    @Override public PacketCustom setDouble(int index, double value) {buf.setDouble(index, value);return this;}
    @Override public PacketCustom setBytes(int index, ByteBuf src) {buf.setBytes(index, src);return this;}
    @Override public PacketCustom setBytes(int index, ByteBuf src, int length) {buf.setBytes(index, src, length);return this;}
    @Override public PacketCustom setBytes(int index, ByteBuf src, int srcIndex, int length) {buf.setBytes(index, src, srcIndex, length);return this;}
    @Override public PacketCustom setBytes(int index, byte[] src) {buf.setBytes(index, src);return this;}
    @Override public PacketCustom setBytes(int index, byte[] src, int srcIndex, int length) {buf.setBytes(index, src, srcIndex, length);return this;}
    @Override public PacketCustom setBytes(int index, ByteBuffer src) {buf.setBytes(index, src);return this;}
    @Override public int setBytes(int index, InputStream in, int length) throws IOException {return buf.setBytes(index, in, length);}
    @Override public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {return buf.setBytes(index, in, length);}
    @Override public int setBytes(int index, FileChannel in, long position, int length) throws IOException {return buf.setBytes(index, in, position, length);}
    @Override public PacketCustom setZero(int index, int length) {buf.setZero(index, length);return this;}
    @Override public int setCharSequence(int index, CharSequence sequence, Charset charset) {return buf.setCharSequence(index, sequence, charset);}
    @Override public short readUnsignedByte() {return buf.readUnsignedByte();}
    @Override public int readUnsignedShort() {return buf.readUnsignedShort();}
    @Override public int readUnsignedShortLE() {return buf.readUnsignedShortLE();}
    @Override public int readMedium() {return buf.readMedium();}
    @Override public int readMediumLE() {return buf.readMediumLE();}
    @Override public int readUnsignedMedium() {return buf.readUnsignedMedium();}
    @Override public int readUnsignedMediumLE() {return buf.readUnsignedMediumLE();}
    @Override public long readUnsignedInt() {return buf.readUnsignedInt();}
    @Override public long readUnsignedIntLE() {return buf.readUnsignedIntLE();}
    @Override public ByteBuf readBytes(int length) {return buf.readBytes(length);}
    @Override public ByteBuf readSlice(int length) {return buf.readSlice(length);}
    @Override public ByteBuf readRetainedSlice(int length) {return buf.readRetainedSlice(length);}
    @Override public PacketCustom readBytes(ByteBuf dst) {buf.readBytes(dst);return this;}
    @Override public PacketCustom readBytes(ByteBuf dst, int length) {buf.readBytes(dst, length);return this;}
    @Override public PacketCustom readBytes(ByteBuf dst, int dstIndex, int length) {buf.readBytes(dst, dstIndex, length);return this;}
    @Override public PacketCustom readBytes(byte[] dst) {buf.readBytes(dst);return this;}
    @Override public PacketCustom readBytes(byte[] dst, int dstIndex, int length) {buf.readBytes(dst, dstIndex, length);return this;}
    @Override public PacketCustom readBytes(ByteBuffer dst) {buf.readBytes(dst);return this;}
    @Override public PacketCustom readBytes(OutputStream out, int length) throws IOException {buf.readBytes(out, length);return this;}
    @Override public int readBytes(GatheringByteChannel out, int length) throws IOException {return buf.readBytes(out, length);}
    @Override public CharSequence readCharSequence(int length, Charset charset) {return buf.readCharSequence(length, charset);}
    @Override public int readBytes(FileChannel out, long position, int length) throws IOException {return buf.readBytes(out, position, length);}
    @Override public PacketCustom skipBytes(int length) {buf.skipBytes(length);return this;}
    @Override public PacketCustom writeMedium(int value) {buf.writeMedium(value);return this;}
    @Override public ByteBuf writeMediumLE(int value) {return buf.writeMediumLE(value);}
    @Override public PacketCustom writeChar(int value) {buf.writeChar(value);return this;}
    @Override public PacketCustom writeBytes(ByteBuf src) {buf.writeBytes(src);return this;}
    @Override public PacketCustom writeBytes(ByteBuf src, int length) {buf.writeBytes(src, length);return this;}
    @Override public PacketCustom writeBytes(ByteBuf src, int srcIndex, int length) {buf.writeBytes(src, srcIndex, length);return this;}
    @Override public PacketCustom writeBytes(byte[] src) {buf.writeBytes(src);return this;}
    @Override public PacketCustom writeBytes(byte[] src, int srcIndex, int length) {buf.writeBytes(src, srcIndex, length);return this;}
    @Override public PacketCustom writeBytes(ByteBuffer src) {buf.writeBytes(src);return this;}
    @Override public ByteBuf writeShortLE(int value) {return buf.writeShortLE(value);}
    @Override public ByteBuf writeLongLE(long value) {return buf.writeLongLE(value);}
    @Override public ByteBuf writeIntLE(int value) {return buf.writeIntLE(value);}
    @Override public short readShortLE() {return buf.readShortLE();}
    @Override public int readIntLE() {return buf.readIntLE();}
    @Override public long readLongLE() {return buf.readLong();}
    @Override public int writeBytes(InputStream in, int length) throws IOException {return buf.writeBytes(in, length);}
    @Override public int writeBytes(ScatteringByteChannel in, int length) throws IOException {return buf.writeBytes(in, length);}
    @Override public int writeBytes(FileChannel in, long position, int length) throws IOException {return buf.writeBytes(in, position, length);}
    @Override public PacketCustom writeZero(int length) {buf.writeZero(length);return this;}
    @Override public int writeCharSequence(CharSequence sequence, Charset charset) {return buf.writeCharSequence(sequence, charset);}
    @Override public int indexOf(int fromIndex, int toIndex, byte value) {return buf.indexOf(fromIndex, toIndex, value);}
    @Override public int bytesBefore(byte value) {return buf.bytesBefore(value);}
    @Override public int bytesBefore(int length, byte value) {return buf.bytesBefore(length, value);}
    @Override public int bytesBefore(int index, int length, byte value) {return buf.bytesBefore(index, length, value);}
    @Override public int forEachByte(ByteProcessor processor) {return buf.forEachByte(processor);}
    @Override public int forEachByte(int index, int length, ByteProcessor processor) {return buf.forEachByte(index, length, processor);}
    @Override public int forEachByteDesc(ByteProcessor processor) {return buf.forEachByteDesc(processor);}
    @Override public int forEachByteDesc(int index, int length, ByteProcessor processor) {return buf.forEachByteDesc(index, length, processor);}
    @Override public ByteBuf copy() {return buf.copy();}
    @Override public ByteBuf copy(int index, int length) {return buf.copy(index, length);}
    @Override public ByteBuf slice() {return buf.slice();}
    @Override public ByteBuf retainedSlice() {return buf.retainedSlice();}
    @Override public ByteBuf slice(int index, int length) {return buf.slice(index, length);}
    @Override public ByteBuf retainedSlice(int index, int length) {return buf.retainedSlice(index, length);}
    @Override public ByteBuf duplicate() {return buf.duplicate();}
    @Override public ByteBuf retainedDuplicate() {return buf.retainedDuplicate();}
    @Override public int nioBufferCount() {return buf.nioBufferCount();}
    @Override public ByteBuffer nioBuffer() {return buf.nioBuffer();}
    @Override public ByteBuffer nioBuffer(int index, int length) {return buf.nioBuffer(index, length);}
    @Override public ByteBuffer[] nioBuffers() {return buf.nioBuffers();}
    @Override public ByteBuffer[] nioBuffers(int index, int length) {return buf.nioBuffers(index, length);}
    @Override public ByteBuffer internalNioBuffer(int index, int length) {return buf.internalNioBuffer(index, length);}
    @Override public boolean hasArray() {return buf.hasArray();}
    @Override public byte[] array() {return buf.array();}
    @Override public int arrayOffset() {return buf.arrayOffset();}
    @Override public String toString(Charset charset) {return buf.toString(charset);}
    @Override public String toString(int index, int length, Charset charset) {return buf.toString(index, length, charset);}
    @Override public int hashCode() {return buf.hashCode();}
    @Override public boolean equals(Object obj) {return buf.equals(obj);}
    @Override public int compareTo(ByteBuf buffer) {return buf.compareTo(buffer);}
    @Override public String toString() {return String.format("%s{ %s }", this.getClass().getName(), buf.toString());}
    @Override public PacketCustom retain(int increment) {buf.retain(increment);return this;}
    @Override public PacketCustom retain() {buf.retain();return this;}
    @Override public ByteBuf touch() {return buf.touch();}
    @Override public ByteBuf touch(Object hint) {return buf.touch();}
    @Override public boolean isReadable(int size) {return buf.isReadable(size);}
    @Override public boolean isWritable(int size) {return buf.isWritable(size);}
    @Override public int refCnt() {return buf.refCnt();}
    @Override public boolean release() {return buf.release();}
    @Override public boolean release(int decrement) {return buf.release(decrement);}
    //@formatter:on
    //endregion
}
