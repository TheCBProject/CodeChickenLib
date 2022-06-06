package codechicken.lib.util;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.packet.PacketCustom;
import com.mojang.authlib.GameProfile;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import static codechicken.lib.internal.network.CCLNetwork.C_OPEN_CONTAINER;

/**
 * Created by covers1624 on 22/10/2016.
 */
public class ServerUtils {

    @Deprecated
    public static MinecraftServer getServer() {
        return (MinecraftServer) LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
    }

    @Deprecated
    public static ServerPlayer getPlayer(String playername) {
        return getServer().getPlayerList().getPlayerByName(playername);
    }

    public static List<ServerPlayer> getPlayers() {
        return getServer().getPlayerList().getPlayers();
    }

    public static boolean isPlayerLoadingChunk(ServerPlayer player, ChunkPos chunk) {
        return player.getLevel().getChunkSource().chunkMap.getPlayers(chunk, false).stream().anyMatch(e -> e.getId() == player.getId());
    }

    public static Path getSaveDirectory() {
        return getSaveDirectory(Level.OVERWORLD);
    }

    public static Path getSaveDirectory(ResourceKey<Level> dimension) {
        return getServer().storageSource.getDimensionPath(dimension);
    }

    public static GameProfile getGameProfile(String username) {
        Player player = getPlayer(username);
        if (player != null) {
            return player.getGameProfile();
        }

        //try and access it in the cache without forcing a save
        username = username.toLowerCase(Locale.ROOT);
        GameProfileCache.GameProfileInfo cachedEntry = getServer().getProfileCache().profilesByName.get(username);
        if (cachedEntry != null) {
            return cachedEntry.getProfile();
        }

        //load it from the cache
        return getServer().getProfileCache().get(username).orElse(null);
    }

    public static boolean isPlayerOP(UUID uuid) {
        GameProfile profile = getServer().getProfileCache().get(uuid).orElse(null);
        return profile != null && getServer().getPlayerList().isOp(profile);
    }

    public static boolean isPlayerOP(String username) {
        GameProfile prof = getGameProfile(username);
        return prof != null && getServer().getPlayerList().isOp(prof);
    }

    public static void openContainer(ServerPlayer player, MenuProvider containerProvider) {
        openContainer(player, containerProvider, e -> {
        });
    }

    public static void openContainer(ServerPlayer player, MenuProvider containerProvider, Consumer<MCDataOutput> packetConsumer) {
        if (player.level.isClientSide()) {
            return;
        }
        player.doCloseContainer();
        player.nextContainerCounter();
        int containerId = player.containerCounter;

        AbstractContainerMenu container = containerProvider.createMenu(containerId, player.getInventory(), player);
        MenuType<?> type = container.getType();

        PacketCustom packet = new PacketCustom(CCLNetwork.NET_CHANNEL, C_OPEN_CONTAINER);
        packet.writeRegistryIdUnsafe(ForgeRegistries.CONTAINERS, type);
        packet.writeVarInt(containerId);
        packet.writeTextComponent(containerProvider.getDisplayName());
        packetConsumer.accept(packet);

        packet.sendToPlayer(player);
        player.containerMenu = container;
        player.initMenu(player.containerMenu);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
    }
}
