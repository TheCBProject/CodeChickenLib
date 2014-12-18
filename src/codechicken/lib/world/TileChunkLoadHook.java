package codechicken.lib.world;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.ArrayList;
import java.util.List;

public class TileChunkLoadHook
{
    private static boolean init;
    public static void init() {
        if(init) return;
        init = true;

        MinecraftForge.EVENT_BUS.register(new TileChunkLoadHook());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        List<TileEntity> list = new ArrayList<TileEntity>(event.getChunk().chunkTileEntityMap.values());
        for(TileEntity t : list)
            if(t instanceof IChunkLoadTile)
                ((IChunkLoadTile)t).onChunkLoad();
    }
}
