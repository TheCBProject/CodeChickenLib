package codechicken.lib.world;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

import java.util.HashSet;

public abstract class ChunkExtension {

    public final IChunk chunk;
    public final ChunkPos coord;
    public final WorldExtension world;
    public HashSet<ServerPlayerEntity> watchedPlayers;

    public ChunkExtension(IChunk chunk, WorldExtension world) {
        this.chunk = chunk;
        coord = chunk.getPos();
        this.world = world;
        watchedPlayers = new HashSet<>();
    }

    public void loadData(CompoundNBT tag) {
    }

    public void saveData(CompoundNBT tag) {
    }

    public void load() {
    }

    public void unload() {
    }

    public final void sendPacketToPlayers(IPacket<?> packet) {
        for (ServerPlayerEntity player : watchedPlayers) {
            player.connection.sendPacket(packet);
        }
    }

    public final void watchPlayer(ServerPlayerEntity player) {
        watchedPlayers.add(player);
        onWatchPlayer(player);
    }

    public void onWatchPlayer(ServerPlayerEntity player) {
    }

    public final void unwatchPlayer(ServerPlayerEntity player) {
        watchedPlayers.remove(player);
        onUnWatchPlayer(player);
    }

    public void onUnWatchPlayer(ServerPlayerEntity player) {
    }

    public void sendUpdatePackets() {
    }

    @Override
    public int hashCode() {
        return coord.x ^ coord.z;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ChunkExtension && ((ChunkExtension) o).coord.equals(coord)) || (o instanceof ChunkPos && coord.equals(o)) || (o instanceof Long && (Long) o == (((long) coord.x) << 32 | coord.z));
    }
}
