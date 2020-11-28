package codechicken.lib.world;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

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
        IChunk chunk = event.getChunk();
        Map<BlockPos, TileEntity> tiles = null;
        if (chunk instanceof ChunkPrimerWrapper) {
            chunk = ((ChunkPrimerWrapper) chunk).getChunk();
        }
        if (chunk instanceof Chunk) {
            tiles = ((Chunk) chunk).getTileEntityMap();
        }
        if (chunk instanceof ChunkPrimer) {
            tiles = ((ChunkPrimer) chunk).getTileEntities();
        }
        if (tiles != null) {
            for (TileEntity tile : tiles.values()) {
                if (tile instanceof IChunkLoadTile) {
                    ((IChunkLoadTile) tile).onChunkLoad();
                }
            }
        }
    }
}
