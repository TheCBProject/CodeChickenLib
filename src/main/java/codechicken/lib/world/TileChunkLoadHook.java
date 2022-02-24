package codechicken.lib.world;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TileChunkLoadHook {

    private static boolean init;

    public static void init() {
        if (init) {
            return;
        }
        init = true;

        MinecraftForge.EVENT_BUS.register(new TileChunkLoadHook());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
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
