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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
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

    @Deprecated
    public static ServerPlayerEntity getPlayer(String playername) {
        return getServer().getPlayerList().getPlayerByName(playername);
    }

    public static List<ServerPlayerEntity> getPlayers() {
        return getServer().getPlayerList().getPlayers();
    }

    public static boolean isPlayerLoadingChunk(ServerPlayerEntity player, ChunkPos chunk) {
        return player.getLevel().getChunkSource().chunkMap.getPlayers(chunk, false).anyMatch(e -> e.getId() == player.getId());
    }

    public static File getSaveDirectory() {
        return getSaveDirectory(World.OVERWORLD);
    }

    public static File getSaveDirectory(RegistryKey<World> dimension) {
        return getServer().storageSource.getDimensionPath(dimension);
    }

    public static GameProfile getGameProfile(String username) {
        PlayerEntity player = getPlayer(username);
        if (player != null) {
            return player.getGameProfile();
        }

        //try and access it in the cache without forcing a save
        username = username.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry cachedEntry = getServer().getProfileCache().profilesByName.get(username);
        if (cachedEntry != null) {
            return cachedEntry.getProfile();
        }

        //load it from the cache
        return getServer().getProfileCache().get(username);
    }

    public static boolean isPlayerOP(UUID uuid) {
        GameProfile profile = getServer().getProfileCache().get(uuid);
        return profile != null && getServer().getPlayerList().isOp(profile);
    }

    public static boolean isPlayerOP(String username) {
        GameProfile prof = getGameProfile(username);
        return prof != null && getServer().getPlayerList().isOp(prof);
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
        if (player.level.isClientSide()) {
            return;
        }
        player.doCloseContainer();
        player.nextContainerCounter();
        int containerId = player.containerCounter;

        Container container = containerProvider.createMenu(containerId, player.inventory, player);
        ContainerType<?> type = container.getType();

        PacketCustom packet = new PacketCustom(CCLNetwork.NET_CHANNEL, C_OPEN_CONTAINER);
        packet.writeRegistryIdUnsafe(ForgeRegistries.CONTAINERS, type);
        packet.writeVarInt(containerId);
        packet.writeTextComponent(containerProvider.getDisplayName());
        packetConsumer.accept(packet);

        packet.sendToPlayer(player);
        player.containerMenu = container;
        player.containerMenu.addSlotListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
    }
}
