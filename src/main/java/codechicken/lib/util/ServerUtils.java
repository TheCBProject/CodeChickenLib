package codechicken.lib.util;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.packet.PacketCustom;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import static codechicken.lib.internal.network.CCLNetwork.C_OPEN_CONTAINER;

/**
 * Created by covers1624 on 22/10/2016.
 */
public class ServerUtils {

    public static MinecraftServer getServer() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    public static ServerPlayerEntity getPlayer(String playername) {
        return getServer().getPlayerList().getPlayerByUsername(playername);
    }

    public static List<ServerPlayerEntity> getPlayers() {
        return getServer().getPlayerList().getPlayers();
    }

    @Deprecated// ServerWorld.getPlayers()
    public static ArrayList<ServerPlayerEntity> getPlayersInDimension(int dimension) {
        return null;
    }

    public static boolean isPlayerLoadingChunk(ServerPlayerEntity player, ChunkPos chunk) {
        return player.getServerWorld().getChunkProvider().chunkManager.getTrackingPlayers(chunk, false).anyMatch(e -> e.getEntityId() == player.getEntityId());
    }

    public static File getSaveDirectory() {
        return getSaveDirectory(DimensionType.OVERWORLD);
    }

    public static File getSaveDirectory(DimensionType dimension) {
        return getServer().getWorld(dimension).getSaveHandler().getWorldDirectory();
    }

    public static GameProfile getGameProfile(String username) {
        PlayerEntity player = getPlayer(username);
        if (player != null) {
            return player.getGameProfile();
        }

        //try and access it in the cache without forcing a save
        username = username.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry cachedEntry = getServer().getPlayerProfileCache().usernameToProfileEntryMap.get(username);
        if (cachedEntry != null) {
            return cachedEntry.getGameProfile();
        }

        //load it from the cache
        return getServer().getPlayerProfileCache().getGameProfileForUsername(username);
    }

    public static boolean isPlayerOP(UUID uuid) {
        GameProfile profile = getServer().getPlayerProfileCache().getProfileByUUID(uuid);
        return profile != null && getServer().getPlayerList().canSendCommands(profile);
    }

    public static boolean isPlayerOP(String username) {
        GameProfile prof = getGameProfile(username);
        return prof != null && getServer().getPlayerList().canSendCommands(prof);
    }
    //
    //    public static boolean isPlayerOwner(String username) {
    //        return mc().isSinglePlayer() && mc().getServerOwner().equalsIgnoreCase(username);
    //    }
    //
    //    public static void sendChatToAll(ITextComponent msg) {
    //        for (EntityPlayer p : getPlayers()) {
    //            p.sendMessage(msg);
    //        }
    //    }

    public static void openContainer(ServerPlayerEntity player, INamedContainerProvider containerProvider) {
        openContainer(player, containerProvider, e -> {
        });
    }

    public static void openContainer(ServerPlayerEntity player, INamedContainerProvider containerProvider, Consumer<MCDataOutput> packetConsumer) {
        if (player.world.isRemote()) {
            return;
        }
        player.closeContainer();
        player.getNextWindowId();
        int containerId = player.currentWindowId;

        Container container = containerProvider.createMenu(containerId, player.inventory, player);
        ContainerType<?> type = container.getType();

        PacketCustom packet = new PacketCustom(CCLNetwork.NET_CHANNEL, C_OPEN_CONTAINER);
        packet.writeVarInt(Registry.MENU.getId(type));
        packet.writeVarInt(containerId);
        packet.writeTextComponent(containerProvider.getDisplayName());
        packetConsumer.accept(packet);

        packet.sendToPlayer(player);
        player.openContainer = container;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
    }
}
