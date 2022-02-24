package codechicken.lib.config;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.config.ConfigTag.SyncException;
import codechicken.lib.config.ConfigTag.SyncType;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.PacketCustom;
import com.google.common.base.Joiner;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static codechicken.lib.internal.network.CCLNetwork.L_CONFIG_SYNC;
import static codechicken.lib.internal.network.CCLNetwork.NET_CHANNEL;

/**
 * Master registry in charge of config syncing.
 * Created by covers1624 on 15/06/18.
 */
public class ConfigSyncManager {

    private static final Logger logger = LogManager.getLogger();

    //Always contains master object.
    private static final Map<ResourceLocation, ConfigTag> syncMap = new HashMap<>();
    //Contains copies of the individual values that can be synced.
    private static final Map<ResourceLocation, SyncState> clientRollbackMap = new HashMap<>();

    /**
     * Use this to register your config for syncing.
     * You can only register one.
     *
     * @param id  The modID to associate with the tag.
     *            Only used in the network stack to identify configs on either side.
     * @param tag The tag to sync,
     *            Only tags that have called {@link ConfigTag#setSyncToClient()} will be synced.
     */
    public static void registerSync(ResourceLocation id, ConfigTag tag) {
        syncMap.put(id, tag);
    }

    //Internal
    public static void handleLogin(BiConsumer<String, Supplier<PacketCustom>> consumer) {
        if (syncMap.isEmpty()) {
            logger.info("Skipping config sync, No mods have registered a syncable config.");
            return;
        }
        consumer.accept("config_sync", () -> {
            PacketCustom packet = new PacketCustom(NET_CHANNEL, L_CONFIG_SYNC);
            packet.writeVarInt(syncMap.size());
            for (Map.Entry<ResourceLocation, ConfigTag> entry : syncMap.entrySet()) {
                packet.writeResourceLocation(entry.getKey());
                SyncState.create(entry.getValue()).write(packet);
            }
            String mods = Joiner.on(", ").join(syncMap.keySet());
            logger.info("Sending config sync packet to player. Mods: " + mods);
            return packet;
        });
    }

    //Internal
    public static void readSyncPacket(PacketCustom packet) {
        int numStates = packet.readVarInt();
        for (int i = 0; i < numStates; i++) {
            ResourceLocation ident = packet.readResourceLocation();
            logger.log(Level.INFO, "Applying config sync for {}.", ident);
            ConfigTag found = syncMap.get(ident);
            SyncState state = SyncState.create(found.copy());
            clientRollbackMap.put(ident, state);
            SyncState.applyTo(packet, found);
        }
    }

    public static class SyncState {

        public List<ConfigTag> syncTags = new ArrayList<>();

        public static SyncState create(ConfigTag tag) {
            SyncState state = new SyncState();
            tag.walkTags(e -> {
                if (!e.isCategory() && e.requiresSync()) {
                    state.syncTags.add(e);
                }
            });
            return state;
        }

        public static void applyTo(MCDataInput in, ConfigTag parent) {
            SyncState master = SyncState.create(parent);
            Map<String, ConfigTag> lookup = master.syncTags.stream()//
                    .collect(Collectors.toMap(ConfigTag::getQualifiedName, Function.identity()));
            int numTags = in.readVarInt();
            for (int i = 0; i < numTags; i++) {
                String ident = in.readString();
                ConfigTag found = lookup.get(ident);
                if (found == null) {
                    throw new RuntimeException("Unable to apply server sync, tag does not exist! " + ident);
                }
                found.readNetwork(in);
            }
            try {
                parent.runSync(SyncType.CONNECT);
            } catch (SyncException e) {
                throw new RuntimeException("Unable to apply server sync, SyncException thrown!", e);
            }

        }

        public void revert(ConfigTag parent) {
            SyncState master = SyncState.create(parent);
            Map<String, ConfigTag> lookup = new HashMap<>();
            for (ConfigTag tag : master.syncTags) {
                lookup.put(tag.getQualifiedName(), tag);
            }
            for (ConfigTag tag : syncTags) {
                ConfigTag found = lookup.get(tag.getQualifiedName());
                if (found == null) {
                    throw new RuntimeException("Unable to revert config state, tag no longer exists.. " + tag.getQualifiedName());
                }
                found.networkRestore();
            }
            try {
                parent.runSync(SyncType.DISCONNECT);
            } catch (SyncException e) {
                throw new RuntimeException("Unable to revert server sync, SyncException thrown!", e);
            }
        }

        public void write(MCDataOutput out) {
            out.writeVarInt(syncTags.size());
            for (ConfigTag tag : syncTags) {
                out.writeString(tag.getQualifiedName());
                tag.write(out);
            }
        }
    }

    @EventBusSubscriber (modid = CodeChickenLib.MOD_ID, value = Dist.CLIENT)
    public static class EventHandler {

        @SubscribeEvent
        public static void onClientDisconnected(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            for (Map.Entry<ResourceLocation, SyncState> entry : clientRollbackMap.entrySet()) {
                logger.log(Level.INFO, "Client disconnect, rolling back config for {}.", entry.getKey());
                entry.getValue().revert(syncMap.get(entry.getKey()));
            }
            clientRollbackMap.clear();
        }
    }
}
