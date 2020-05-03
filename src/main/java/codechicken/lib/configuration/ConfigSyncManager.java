package codechicken.lib.configuration;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.configuration.ConfigTag.SyncException;
import codechicken.lib.configuration.ConfigTag.SyncType;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.PacketCustom;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Master registry in charge of config syncing.
 * Created by covers1624 on 15/06/18.
 */
@EventBusSubscriber (modid = CodeChickenLib.MOD_ID)
public class ConfigSyncManager {

    private static final Logger logger = LogManager.getLogger("CodeChickenLib-ConfigSync");

    //Always contains master object.
    private static Map<String, ConfigTag> syncMap = new HashMap<>();
    //Contains copies of the individual values that can be synced.
    private static Map<String, SyncState> clientRollbackMap = new HashMap<>();

    /**
     * Use this to register your config for syncing.
     * You can only register one.
     *
     * @param modId The modID to associate with the tag.
     *              Only used in the network stack to identify configs on either side.
     * @param tag   The tag to sync,
     *              Only tags that have called {@link ConfigTag#setSyncToClient()} will be synced.
     */
    public static void registerSync(String modId, ConfigTag tag) {
        syncMap.put(modId, tag);
    }

    //Internal
    //    public static void handshakeReceived(NetHandlerPlayServer netHandler) {
    //        if(syncMap.isEmpty()) {
    //            logger.info("Skipping config sync, No mods have registered a syncable config.");
    //            return;
    //        }
    //        PacketCustom packet = new PacketCustom(PacketDispatcher.NET_CHANNEL, 20);
    //        packet.writeVarInt(syncMap.size());
    //        for (Entry<String, IConfigTag<IConfigTag>> entry: syncMap.entrySet()) {
    //            packet.writeString(entry.getKey());
    //            SyncState.create(entry.getValue()).write(packet);
    //        }
    //        String mods = Joiner.on(", ").join(syncMap.keySet());
    //        logger.info("Sending config sync packet to player. Mods: " + mods);
    //        netHandler.sendPacket(packet.toPacket());
    //    }

    //Internal
    public static void readSyncPacket(PacketCustom packet) {
        int numStates = packet.readVarInt();
        for (int i = 0; i < numStates; i++) {
            String ident = packet.readString();
            logger.log(Level.INFO, "Applying config sync for {}.", ident);
            ConfigTag found = syncMap.get(ident);
            SyncState state = SyncState.create(found.copy());
            clientRollbackMap.put(ident, state);
            SyncState.applyTo(packet, found);
        }
    }

    //    @SubscribeEvent
    //    public static void onClientDisconnected(ClientDisconnectionFromServerEvent event) {
    //        for (Entry<String, SyncState> entry: clientRollbackMap.entrySet()) {
    //            logger.log(Level.INFO, "Client disconnect, rolling back config for {}.", entry.getKey());
    //            entry.getValue().revert(syncMap.get(entry.getKey()));
    //        }
    //        clientRollbackMap.clear();
    //    }

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
            Map<String, ConfigTag> lookup = new HashMap<>();
            for (ConfigTag tag : master.syncTags) {
                lookup.put(tag.getUnlocalizedName(), tag);
            }
            int numTags = in.readVarInt();
            for (int i = 0; i < numTags; i++) {
                String ident = in.readString();
                ConfigTag found = lookup.get(ident);
                if (found == null) {
                    throw new RuntimeException("Unable to apply server sync, tag does not exist! " + ident);
                }
                found.read(in);
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
                lookup.put(tag.getUnlocalizedName(), tag);
            }
            for (ConfigTag tag : syncTags) {
                ConfigTag found = lookup.get(tag.getUnlocalizedName());
                if (found == null) {
                    throw new RuntimeException("Unable to revert config state, tag no longer exists.. " + tag.getUnlocalizedName());
                }
                found.copyFrom(tag);
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
                out.writeString(tag.getUnlocalizedName());
                tag.write(out);
            }
        }
    }
}
