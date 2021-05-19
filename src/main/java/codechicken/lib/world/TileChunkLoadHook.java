package codechicken.lib.world;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
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
        IChunk iChunk = event.getChunk();
        Chunk chunk;
        if (iChunk instanceof Chunk) {
            chunk = (Chunk) iChunk;
        } else if (iChunk instanceof ChunkPrimerWrapper) {
            chunk = ((ChunkPrimerWrapper) iChunk).getWrapped();
        } else {
            return;
        }

        for (TileEntity tile : chunk.getBlockEntities().values()) {
            if (tile instanceof IChunkLoadTile) {
                ((IChunkLoadTile) tile).onChunkLoad(chunk);
            }
        }
    }
}
