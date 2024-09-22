package codechicken.lib.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer;

/**
 * Created by covers1624 on 17/9/24.
 */
public class PacketSender {

    // region Server -> Client

    /**
     * Create a client bound packet for the given custom payload.
     *
     * @param payload The payload.
     * @return The packet.
     */
    public static Packet<?> toClientPacket(CustomPacketPayload payload) {
        return new ClientboundCustomPayloadPacket(payload);
    }

    /**
     * Overload of {@link #sendToPlayer(Packet, ServerPlayer)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToPlayer(CustomPacketPayload payload, @Nullable ServerPlayer player) {
        sendToPlayer(toClientPacket(payload), player);
    }

    /**
     * Send the given packet to the specified player. If the specified player is {@code null},
     * sends the packet to all connected clients.
     *
     * @param packet The packet to send.
     * @param player The receiver. {@code null} for all players.
     */
    public static void sendToPlayer(Packet<?> packet, @Nullable ServerPlayer player) {
        if (player == null) {
            sendToAllPlayers(packet);
        } else {
            player.connection.send(packet);
        }
    }

    /**
     * Overload of {@link #sendToAllPlayers(Packet)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToAllPlayers(CustomPacketPayload payload) {
        sendToAllPlayers(toClientPacket(payload));
    }

    /**
     * Sends the given packet to all connected clients.
     *
     * @param packet The packet to send.
     */
    public static void sendToAllPlayers(Packet<?> packet) {
        getCurrentServer().getPlayerList().broadcastAll(packet);
    }

    /**
     * Overload of {@link #sendToAllAround(Packet, BlockPos, double, ResourceKey)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToAllAround(CustomPacketPayload payload, BlockPos pos, double range, ResourceKey<Level> dim) {
        sendToAllAround(toClientPacket(payload), pos, range, dim);
    }

    /**
     * Sends the given packet to all players in the specified dimension, within the specified radius, around
     * the specified origin.
     * <p>
     * For blocks, it is advised to instead use one of the {@link #sendToChunk} methods.
     *
     * @param packet The packet to send.
     * @param pos    The origin point to send around.
     * @param range  The range in which to send.
     * @param dim    The dimension to send to.
     */
    public static void sendToAllAround(Packet<?> packet, BlockPos pos, double range, ResourceKey<Level> dim) {
        sendToAllAround(packet, pos.getX(), pos.getY(), pos.getZ(), range, dim);
    }

    /**
     * Overload of {@link #sendToAllAround(Packet, double, double, double, double, ResourceKey)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToAllAround(CustomPacketPayload payload, double x, double y, double z, double range, ResourceKey<Level> dim) {
        sendToAllAround(toClientPacket(payload), x, y, z, range, dim);
    }

    /**
     * Sends the given packet to all players in the specified dimension, within the specified radius, around
     * the specified origin.
     * <p>
     * For blocks, it is advised to instead use one of the {@link #sendToChunk} methods.
     *
     * @param packet The packet to send.
     * @param x      The X origin point to send around.
     * @param y      The Y origin point to send around.
     * @param z      The Z origin point to send around.
     * @param range  The range in which to send.
     * @param dim    The dimension to send to.
     */
    public static void sendToAllAround(Packet<?> packet, double x, double y, double z, double range, ResourceKey<Level> dim) {
        getCurrentServer().getPlayerList().broadcast(null, x, y, z, range, dim, packet);
    }

    /**
     * Overload of {@link #sendToDimension(Packet, ResourceKey)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToDimension(CustomPacketPayload payload, ResourceKey<Level> dim) {
        sendToDimension(toClientPacket(payload), dim);
    }

    /**
     * Send the given packet to all players within the given dimension.
     *
     * @param packet The packet to send.
     * @param dim    The dimension to send to.
     */
    public static void sendToDimension(Packet<?> packet, ResourceKey<Level> dim) {
        getCurrentServer().getPlayerList().broadcastAll(packet, dim);
    }

    /**
     * Overload of {@link #sendToChunk(Packet, BlockEntity)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToChunk(CustomPacketPayload payload, BlockEntity tile) {
        sendToChunk(toClientPacket(payload), tile);
    }

    /**
     * Send the given packet to all players watching the chunk, that the given {@link BlockEntity}
     * is within.
     *
     * @param packet The packet to send.
     * @param tile   The {@link BlockEntity} of the watched chunk to send to.
     */
    public static void sendToChunk(Packet<?> packet, BlockEntity tile) {
        sendToChunk(packet, (ServerLevel) requireNonNull(tile.getLevel()), tile.getBlockPos());
    }

    /**
     * Overload of {@link #sendToChunk(Packet, ServerLevel, BlockPos)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToChunk(CustomPacketPayload payload, ServerLevel level, BlockPos pos) {
        sendToChunk(toClientPacket(payload), level, pos);
    }

    /**
     * Send the given packet to all players watching the chunk specified by the
     * given {@link BlockPos} in the specified dimension.
     *
     * @param packet The packet to send.
     * @param level  The level containing the chunk to send to.
     * @param pos    The {@link BlockPos} specifying the chunk to send to.
     */
    public static void sendToChunk(Packet<?> packet, ServerLevel level, BlockPos pos) {
        sendToChunk(packet, level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Overload of {@link #sendToChunk(Packet, ServerLevel, int, int)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToChunk(CustomPacketPayload payload, ServerLevel level, int chunkX, int chunkZ) {
        sendToChunk(toClientPacket(payload), level, chunkX, chunkZ);
    }

    /**
     * Send the given packet to all players watching the specified chunk in the specified dimension.
     *
     * @param packet The packet to send.
     * @param level  The level containing the chunk to send to.
     * @param chunkX The chunk X coordinate to send to.
     * @param chunkZ The chunk Z coordinate to send to.
     */
    public static void sendToChunk(Packet<?> packet, ServerLevel level, int chunkX, int chunkZ) {
        sendToChunk(packet, level, new ChunkPos(chunkX, chunkZ));
    }

    /**
     * Overload of {@link #sendToChunk(Packet, ServerLevel, ChunkPos)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToChunk(CustomPacketPayload payload, ServerLevel level, ChunkPos pos) {
        sendToChunk(toClientPacket(payload), level, pos);
    }

    /**
     * Send the given packet to all players watching the specified chunk in the specified dimension.
     *
     * @param packet The packet to send.
     * @param level  The level containing the chunk to send to.
     * @param pos    The {@link ChunkPos} to send to.
     */
    public static void sendToChunk(Packet<?> packet, ServerLevel level, ChunkPos pos) {
        level.getChunkSource().chunkMap.getPlayers(pos, false).forEach(e -> sendToPlayer(packet, e));
    }

    /**
     * Overload of {@link #sendToOps(Packet)} first wrapping {@code payload} to
     * a client bound packet.
     */
    public static void sendToOps(CustomPacketPayload payload) {
        sendToOps(toClientPacket(payload));
    }

    /**
     * Send the given packet to all server operators.
     *
     * @param packet The packet to send.
     */
    public static void sendToOps(Packet<?> packet) {
        PlayerList playerList = getCurrentServer().getPlayerList();
        ServerOpList opList = playerList.getOps();
        for (ServerPlayer player : playerList.getPlayers()) {
            if (opList.get(player.getGameProfile()) != null) {
                sendToPlayer(packet, player);
            }
        }
    }
    // endregion

    // region Client -> Server

    /**
     * Create a server bound packet for the given custom payload.
     *
     * @param payload The payload.
     * @return The packet.
     */
    public static Packet<?> toServerPacket(CustomPacketPayload payload) {
        return new ServerboundCustomPayloadPacket(payload);
    }

    /**
     * Overload of {@link #sendToServer(Packet)} first wrapping {@code payload} to
     * a server bound packet.
     */
    public static void sendToServer(CustomPacketPayload payload) {
        sendToServer(toServerPacket(payload));
    }

    /**
     * Send the given packet to the server.
     *
     * @param packet The packet to send.
     */
    public static void sendToServer(Packet<?> packet) {
        requireNonNull(Minecraft.getInstance().getConnection()).send(packet);
    }

    // endregion
}
