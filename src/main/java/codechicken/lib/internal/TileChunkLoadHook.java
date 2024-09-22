package codechicken.lib.internal;

import codechicken.lib.world.IChunkLoadTile;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;

public class TileChunkLoadHook {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static void init() {
        LOCK.lock();

        NeoForge.EVENT_BUS.addListener(TileChunkLoadHook::onChunkLoad);
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        ChunkAccess iChunk = event.getChunk();
        LevelChunk chunk;
        if (iChunk instanceof LevelChunk) {
            chunk = (LevelChunk) iChunk;
        } else if (iChunk instanceof ImposterProtoChunk) {
            chunk = ((ImposterProtoChunk) iChunk).getWrapped();
        } else {
            return;
        }

        for (BlockEntity tile : chunk.getBlockEntities().values()) {
            if (tile instanceof IChunkLoadTile) {
                ((IChunkLoadTile) tile).onChunkLoad(chunk);
            }
        }
    }
}
