package codechicken.lib.world;

import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.IChunk;
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
        IChunk chunk = event.getChunk();
        if(chunk instanceof ChunkPrimerWrapper) {
            chunk = ((ChunkPrimerWrapper) chunk).func_217336_u();
        }
        chunk.getTileEntitiesPos().stream()//
                .map(chunk::getTileEntity)//
                .filter(e -> e instanceof IChunkLoadTile)//
                .map(e -> (IChunkLoadTile) e)//
                .forEach(IChunkLoadTile::onChunkLoad);
    }
}
