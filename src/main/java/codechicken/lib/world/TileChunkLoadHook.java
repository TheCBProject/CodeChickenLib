package codechicken.lib.world;

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
        event.getChunk().getTileEntitiesPos().stream()//
                .map(event.getWorld()::getTileEntity)//
                .filter(e -> e instanceof IChunkLoadTile)//
                .map(e -> (IChunkLoadTile) e)//
                .forEach(IChunkLoadTile::onChunkLoad);
    }
}
