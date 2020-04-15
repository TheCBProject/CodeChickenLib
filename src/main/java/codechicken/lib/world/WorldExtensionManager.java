package codechicken.lib.world;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent.UnWatch;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorldExtensionManager {

    public static class WorldExtensionEventHandler {

        @SubscribeEvent
        public void onChunkDataLoad(ChunkDataEvent.Load event) {
            if (!hasExtensions(event.getWorld())) {
                WorldExtensionManager.onWorldLoad(event.getWorld());
            }

            createChunkExtension(event.getWorld(), event.getChunk());

            for (WorldExtension extension : getExtensions(event.getWorld())) {
                extension.loadChunkData(event.getChunk(), event.getData());
            }
        }

        @SubscribeEvent
        public void onChunkDataSave(ChunkDataEvent.Save event) {
            for (WorldExtension extension : getExtensions(event.getWorld())) {
                extension.saveChunkData(event.getChunk(), event.getData());
            }

            ChunkPos pos = event.getChunk().getPos();
            if (!event.getWorld().chunkExists(pos.x, pos.z)) {
                removeChunk(event.getWorld(), event.getChunk());
            }
        }

        @SubscribeEvent
        public void onChunkLoad(ChunkEvent.Load event) {
            IWorld world = event.getWorld();
            IChunk chunk = event.getChunk();
            if (world == null && chunk instanceof ChunkPrimerWrapper) {
                world = ((ChunkPrimerWrapper) chunk).func_217336_u().getWorld();
            }
            if (hasExtensions(world)) {
                WorldExtensionManager.onWorldLoad(world);

                createChunkExtension(world,chunk);

                for (WorldExtension extension : getExtensions(world)) {
                    extension.loadChunk(chunk);
                }
            }
        }

        @SubscribeEvent
        public void onChunkUnLoad(ChunkEvent.Unload event) {
            for (WorldExtension extension : getExtensions(event.getWorld())) {
                extension.unloadChunk(event.getChunk());
            }

            if (event.getWorld().isRemote()) {
                removeChunk(event.getWorld(), event.getChunk());
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event) {
            if (hasExtensions(event.getWorld())) {
                for (WorldExtension extension : getExtensions(event.getWorld())) {
                    extension.save();
                }
            }
        }

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            if (!hasExtensions(event.getWorld())) {
                WorldExtensionManager.onWorldLoad(event.getWorld());
            }
        }

        @SubscribeEvent
        public void onWorldUnLoad(WorldEvent.Unload event) {
            if (hasExtensions(event.getWorld()))//because force closing unloads a world twice
            {
                for (WorldExtension extension : worldMap.remove(event.getWorld().getDimension().getType())) {
                    extension.unload();
                }
            }
        }

        @SubscribeEvent
        public void onChunkWatch(Watch event) {
            Chunk chunk = event.getPlayer().world.getChunk(event.getPos().x, event.getPos().z);
            for (WorldExtension extension : getExtensions(event.getPlayer().world)) {
                extension.watchChunk(chunk, event.getPlayer());
            }
        }

        @SubscribeEvent
        @OnlyIn (Dist.CLIENT)
        public void onChunkUnWatch(UnWatch event) {
            Chunk chunk = event.getPlayer().world.getChunk(event.getPos().x, event.getPos().z);
            for (WorldExtension extension : getExtensions(event.getPlayer().world)) {
                extension.unwatchChunk(chunk, event.getPlayer());
            }
        }

        @SubscribeEvent
        @OnlyIn (Dist.CLIENT)
        public void clientTick(TickEvent.ClientTickEvent event) {
            World world = Minecraft.getInstance().world;
            if (hasExtensions(world)) {
                if (event.phase == TickEvent.Phase.START) {
                    preTick(world);
                } else {
                    postTick(world);
                }
            }
        }

        @SubscribeEvent
        public void serverTick(TickEvent.WorldTickEvent event) {
            if (!hasExtensions(event.world)) {
                WorldExtensionManager.onWorldLoad(event.world);
            }

            if (event.phase == TickEvent.Phase.START) {
                preTick(event.world);
            } else {
                postTick(event.world);
            }
        }
    }

    private static boolean initialised;
    private static ArrayList<WorldExtensionInstantiator> extensionIntialisers = new ArrayList<>();

    public static void registerWorldExtension(WorldExtensionInstantiator init) {
        if (!initialised) {
            init();
        }

        init.instantiatorID = extensionIntialisers.size();
        extensionIntialisers.add(init);
    }

    private static void init() {
        initialised = true;
        MinecraftForge.EVENT_BUS.register(new WorldExtensionEventHandler());
    }

    private static Map<DimensionType, WorldExtension[]> worldMap = new HashMap<>();
    private static final WorldExtension[] empty = new WorldExtension[0];

    private static WorldExtension[] getExtensions(IWorld world) {
        if (world != null) {
            if (hasExtensions(world)) {
                return worldMap.get(world.getDimension().getType());
            }
        }
        return empty;
    }

    public static WorldExtension getWorldExtension(World world, int instantiatorID) {
        return getExtensions(world)[instantiatorID];
    }

    private static boolean hasExtensions(IWorld world) {
        return world == null || worldMap.containsKey(world.getDimension().getType());
    }

    private static void onWorldLoad(IWorld world) {
        WorldExtension[] extensions = new WorldExtension[extensionIntialisers.size()];
        for (int i = 0; i < extensions.length; i++) {
            extensions[i] = extensionIntialisers.get(i).createWorldExtension(world);
        }

        worldMap.put(world.getDimension().getType(), extensions);

        for (WorldExtension extension : extensions) {
            extension.load();
        }
    }

    private static void createChunkExtension(IWorld world, IChunk chunk) {
        WorldExtension[] extensions = getExtensions(world);
        for (int i = 0; i < extensionIntialisers.size(); i++) {
            if (!extensions[i].containsChunk(chunk)) {
                extensions[i].addChunk(extensionIntialisers.get(i).createChunkExtension(chunk, extensions[i]));
            }
        }
    }

    private static void removeChunk(IWorld world, IChunk chunk) {
        for (WorldExtension extension : getExtensions(world)) {
            extension.remChunk(chunk);
        }
    }

    private static void preTick(IWorld world) {
        for (WorldExtension extension : getExtensions(world)) {
            extension.preTick();
        }
    }

    private static void postTick(IWorld world) {
        for (WorldExtension extension : getExtensions(world)) {
            extension.postTick();
        }
    }
}
