package codechicken.lib.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache.ProfileEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Created by covers1624 on 22/10/2016.
 */
public class ServerUtils {

//    public static MinecraftServer mc() {
//        return FMLCommonHandler.instance().getMinecraftServerInstance();
//    }
//
//    public static EntityPlayerMP getPlayer(String playername) {
//        return mc().getPlayerList().getPlayerByUsername(playername);
//    }
//
//    public static List<EntityPlayerMP> getPlayers() {
//        return mc().getPlayerList().getPlayers();
//    }

    @Deprecated// ServerWorld.getPlayers()
    public static ArrayList<ServerPlayerEntity> getPlayersInDimension(int dimension) {
        return null;
    }

    public static boolean isPlayerLoadingChunk(ServerPlayerEntity player, ChunkPos chunk) {
        return player.getServerWorld().getChunkProvider().chunkManager.getTrackingPlayers(chunk, false)
                .anyMatch(e -> e.getEntityId() == player.getEntityId());
    }

//    public static GameProfile getGameProfile(String username) {
//        EntityPlayer player = getPlayer(username);
//        if (player != null) {
//            return player.getGameProfile();
//        }
//
//        //try and access it in the cache without forcing a save
//        username = username.toLowerCase(Locale.ROOT);
//        ProfileEntry cachedEntry = mc().getPlayerProfileCache().usernameToProfileEntryMap.get(username);
//        if (cachedEntry != null) {
//            return cachedEntry.getGameProfile();
//        }
//
//        //load it from the cache
//        return mc().getPlayerProfileCache().getGameProfileForUsername(username);
//    }

//    public static boolean isPlayerOP(UUID uuid) {
//        GameProfile profile = mc().getPlayerProfileCache().getProfileByUUID(uuid);
//        return profile != null && mc().getPlayerList().canSendCommands(profile);
//    }
//
//    public static boolean isPlayerOP(String username) {
//        GameProfile prof = getGameProfile(username);
//        return prof != null && mc().getPlayerList().canSendCommands(prof);
//    }
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
//
//    public static void openSMPContainer(EntityPlayerMP player, Container container, BiConsumer<EntityPlayerMP, Integer> packetSender) {
//        player.getNextWindowId();
//        player.closeContainer();
//        packetSender.accept(player, player.currentWindowId);
//        player.openContainer = container;
//        player.openContainer.windowId = player.currentWindowId;
//        player.openContainer.addListener(player);
//    }
}
