package codechicken.lib.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

/**
 * An outbound packet for a specific {@link StreamNetworkChannel} packet.
 * <p>
 * You will generally only ever see one of the subclasses, {@link ToClient} or {@link ToServer}.
 * <p>
 * Created by covers1624 on 10/30/25.
 */
//Tasty Cheese!
public sealed class StreamPacket extends RegistryFriendlyByteBuf implements CustomPacketPayload permits StreamPacket.ToClient, StreamPacket.ToServer {

    private final Type<StreamPacket> type;

    StreamPacket(Type<StreamPacket> type, ByteBuf source, RegistryAccess registryAccess, ConnectionType connectionType) {
        super(source, registryAccess, connectionType);
        this.type = type;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }

    static StreamCodec<RegistryFriendlyByteBuf, StreamPacket> createCodec(Type<StreamPacket> type) {
        return StreamCodec.of(
                (buf, p) -> buf.writeBytes(p, 0, p.writerIndex()),
                (buf) -> {
                    var packet = new StreamPacket(type, Unpooled.buffer(buf.readableBytes()), buf.registryAccess(), ConnectionType.NEOFORGE);
                    buf.readBytes(packet);
                    return packet;
                }
        );
    }

    /**
     * A client-bound packet, write your data, then use one of the various send methods,
     * or {@link #toVanilla()} to send it out a vanilla path.
     */
    public static final class ToClient extends StreamPacket {

        ToClient(Type<StreamPacket> type, ByteBuf source, RegistryAccess registryAccess, ConnectionType connectionType) {
            super(type, source, registryAccess, connectionType);
        }

        /**
         * Wrap this packet into a vanilla {@link Packet}.
         *
         * @return The vanilla packet.
         */
        public Packet<?> toVanilla() {
            return PacketSender.toClientPacket(this);
        }

        /**
         * Send the given packet to the specified player. If the specified player is {@code null},
         * sends the packet to all connected clients.
         *
         * @param player The receiver. {@code null} for all players.
         */
        public void sendToPlayer(@Nullable ServerPlayer player) {
            PacketSender.sendToPlayer(toVanilla(), player);
        }

        /**
         * Sends the given packet to all connected clients.
         */
        public void sendToAllPlayers() {
            PacketSender.sendToAllPlayers(toVanilla());
        }

        /**
         * Sends the given packet to all players in the specified dimension, within the specified radius, around
         * the specified origin.
         * <p>
         * For blocks, it is advised to instead use one of the {@link #sendToChunk} methods.
         *
         * @param pos   The origin point to send around.
         * @param range The range in which to send.
         * @param dim   The dimension to send to.
         */
        public void sendToAllAround(BlockPos pos, double range, ResourceKey<Level> dim) {
            PacketSender.sendToAllAround(toVanilla(), pos, range, dim);
        }

        /**
         * Sends the given packet to all players in the specified dimension, within the specified radius, around
         * the specified origin.
         * <p>
         * For blocks, it is advised to instead use one of the {@link #sendToChunk} methods.
         *
         * @param x     The X origin point to send around.
         * @param y     The Y origin point to send around.
         * @param z     The Z origin point to send around.
         * @param range The range in which to send.
         * @param dim   The dimension to send to.
         */
        public void sendToAllAround(double x, double y, double z, double range, ResourceKey<Level> dim) {
            PacketSender.sendToAllAround(toVanilla(), x, y, z, range, dim);
        }

        /**
         * Send the given packet to all players within the given dimension.
         *
         * @param dim The dimension to send to.
         */
        public void sendToDimension(ResourceKey<Level> dim) {
            PacketSender.sendToDimension(toVanilla(), dim);
        }

        /**
         * Send the given packet to all players watching the chunk, that the given {@link BlockEntity}
         * is within.
         *
         * @param tile The {@link BlockEntity} of the watched chunk to send to.
         */
        public void sendToChunk(BlockEntity tile) {
            PacketSender.sendToChunk(toVanilla(), tile);
        }

        /**
         * Send the given packet to all players watching the chunk specified by the
         * given {@link BlockPos} in the specified dimension.
         *
         * @param level The level containing the chunk to send to.
         * @param pos   The {@link BlockPos} specifying the chunk to send to.
         */
        public void sendToChunk(ServerLevel level, BlockPos pos) {
            PacketSender.sendToChunk(toVanilla(), level, pos);
        }

        /**
         * Send the given packet to all players watching the specified chunk in the specified dimension.
         *
         * @param level  The level containing the chunk to send to.
         * @param chunkX The chunk X coordinate to send to.
         * @param chunkZ The chunk Z coordinate to send to.
         */
        public void sendToChunk(ServerLevel level, int chunkX, int chunkZ) {
            PacketSender.sendToChunk(toVanilla(), level, chunkX, chunkZ);
        }

        /**
         * Send the given packet to all players watching the specified chunk in the specified dimension.
         *
         * @param level The level containing the chunk to send to.
         * @param pos   The {@link ChunkPos} to send to.
         */
        public void sendToChunk(ServerLevel level, ChunkPos pos) {
            PacketSender.sendToChunk(toVanilla(), level, pos);
        }

        /**
         * Send the given packet to all server operators.
         */
        public void sendToOps() {
            PacketSender.sendToOps(toVanilla());
        }
    }

    /**
     * A server-bound packet, write your data, then use one of the various send methods,
     * or {@link #toVanilla()} to send it out a vanilla path.
     */
    public static final class ToServer extends StreamPacket {

        ToServer(Type<StreamPacket> type, ByteBuf source, RegistryAccess registryAccess, ConnectionType connectionType) {
            super(type, source, registryAccess, connectionType);
        }

        /**
         * Wrap this packet into a vanilla {@link Packet}.
         *
         * @return The vanilla packet.
         */
        public Packet<?> toVanilla() {
            return PacketSender.toServerPacket(this);
        }

        /**
         * Send this packet to the server.
         */
        public void sendToServer() {
            PacketSender.sendToServer(toVanilla());
        }
    }
}
