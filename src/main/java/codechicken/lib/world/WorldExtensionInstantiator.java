package codechicken.lib.world;

import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

public abstract class WorldExtensionInstantiator {

    public int instantiatorID;

    public abstract WorldExtension createWorldExtension(IWorld world);

    public abstract ChunkExtension createChunkExtension(IChunk chunk, WorldExtension world);

    public WorldExtension getExtension(World world) {
        return WorldExtensionManager.getWorldExtension(world, instantiatorID);
    }
}
