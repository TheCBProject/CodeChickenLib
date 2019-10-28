package codechicken.lib.world;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

import java.util.HashMap;

public abstract class WorldExtension {

    public final World world;
    public HashMap<IChunk, ChunkExtension> chunkMap = new HashMap<>();

    public WorldExtension(World world) {
        this.world = world;
    }

    public void load() {
    }

    public void unload() {
    }

    public void save() {
    }

    public void preTick() {
    }

    public void postTick() {
    }

    protected final void addChunk(ChunkExtension extension) {
        chunkMap.put(extension.chunk, extension);
    }

    protected final void loadChunk(IChunk chunk) {
        ChunkExtension extension = chunkMap.get(chunk);
        if (extension != null) {
            extension.load();
        }
    }

    protected final void unloadChunk(IChunk chunk) {
        ChunkExtension extension = chunkMap.get(chunk);
        if (extension != null) {
            extension.unload();
        }
    }

    protected final void loadChunkData(IChunk chunk, CompoundNBT tag) {
        ChunkExtension extension = chunkMap.get(chunk);
        if (extension != null) {
            extension.loadData(tag);
        }
    }

    protected final void saveChunkData(IChunk chunk, CompoundNBT tag) {
        ChunkExtension extension = chunkMap.get(chunk);
        if (extension != null) {
            extension.saveData(tag);
        }
    }

    protected final void remChunk(IChunk chunk) {
        chunkMap.remove(chunk);
    }

    protected final void watchChunk(IChunk chunk, ServerPlayerEntity player) {
        ChunkExtension extension = chunkMap.get(chunk);
        if (extension != null) {
            extension.watchPlayer(player);
        }
    }

    protected final void unwatchChunk(IChunk chunk, ServerPlayerEntity player) {
        ChunkExtension extension = chunkMap.get(chunk);
        if (extension != null) {
            extension.unwatchPlayer(player);
        }
    }

    protected final void sendChunkUpdates(IChunk chunk) {
        ChunkExtension extension = chunkMap.get(chunk);
        if (extension != null) {
            extension.sendUpdatePackets();
        }
    }

    public boolean containsChunk(IChunk chunk) {
        return chunkMap.containsKey(chunk);
    }

    public ChunkExtension getChunkExtension(int chunkXPos, int chunkZPos) {
        if (!world.isBlockLoaded(new BlockPos(chunkXPos << 4, 128, chunkZPos << 4))) {
            return null;
        }

        return chunkMap.get(world.getChunk(chunkXPos, chunkZPos));
    }
}
