package codechicken.lib.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent.UnWatch;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;

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

            if (!event.getChunk().isLoaded()) {
                removeChunk(event.getWorld(), event.getChunk());
            }
        }

        @SubscribeEvent
        public void onChunkLoad(ChunkEvent.Load event) {
            if (!hasExtensions(event.getWorld())) {
                WorldExtensionManager.onWorldLoad(event.getWorld());
            }

            createChunkExtension(event.getWorld(), event.getChunk());

            for (WorldExtension extension : getExtensions(event.getWorld())) {
                extension.loadChunk(event.getChunk());
            }
        }

        @SubscribeEvent
        public void onChunkUnLoad(ChunkEvent.Unload event) {
            if (event.getChunk().isEmpty()) {
                return;
            }
            for (WorldExtension extension : getExtensions(event.getWorld())) {
                extension.unloadChunk(event.getChunk());
            }

            if (event.getWorld().isRemote) {
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
                for (WorldExtension extension : worldMap.remove(event.getWorld().provider.getDimension())) {
                    extension.unload();
                }
            }
        }

        @SubscribeEvent
        public void onChunkWatch(Watch event) {
            Chunk chunk = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().chunkXPos, event.getChunk().chunkZPos);
            for (WorldExtension extension : getExtensions(event.getPlayer().world)) {
                extension.watchChunk(chunk, event.getPlayer());
            }
        }

        @SubscribeEvent
        @SideOnly (Side.CLIENT)
        public void onChunkUnWatch(UnWatch event) {
            Chunk chunk = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().chunkXPos, event.getChunk().chunkZPos);
            for (WorldExtension extension : getExtensions(event.getPlayer().world)) {
                extension.unwatchChunk(chunk, event.getPlayer());
            }
        }

        @SubscribeEvent
        @SideOnly (Side.CLIENT)
        public void clientTick(TickEvent.ClientTickEvent event) {
            World world = Minecraft.getMinecraft().world;
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

    private static HashMap<Integer, WorldExtension[]> worldMap = new HashMap<>();
    private static final WorldExtension[] empty = new WorldExtension[0];

    private static WorldExtension[] getExtensions(World world) {
        if (world != null) {
            if (hasExtensions(world)) {
                return worldMap.get(world.provider.getDimension());
            }
        }
        return empty;
    }

    public static WorldExtension getWorldExtension(World world, int instantiatorID) {
        return getExtensions(world)[instantiatorID];
    }

    private static boolean hasExtensions(World world) {
        return world == null || worldMap.containsKey(world.provider.getDimension());
    }

    private static void onWorldLoad(World world) {
        WorldExtension[] extensions = new WorldExtension[extensionIntialisers.size()];
        for (int i = 0; i < extensions.length; i++) {
            extensions[i] = extensionIntialisers.get(i).createWorldExtension(world);
        }

        worldMap.put(world.provider.getDimension(), extensions);

        for (WorldExtension extension : extensions) {
            extension.load();
        }
    }

    private static void createChunkExtension(World world, Chunk chunk) {
        WorldExtension[] extensions = getExtensions(world);
        for (int i = 0; i < extensionIntialisers.size(); i++) {
            if (!extensions[i].containsChunk(chunk)) {
                extensions[i].addChunk(extensionIntialisers.get(i).createChunkExtension(chunk, extensions[i]));
            }
        }
    }

    private static void removeChunk(World world, Chunk chunk) {
        for (WorldExtension extension : getExtensions(world)) {
            extension.remChunk(chunk);
        }
    }

    private static void preTick(World world) {
        for (WorldExtension extension : getExtensions(world)) {
            extension.preTick();
        }
    }

    private static void postTick(World world) {
        for (WorldExtension extension : getExtensions(world)) {
            extension.postTick();
        }
    }
}
