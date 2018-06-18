package codechicken.lib.configuration;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.configuration.IConfigTag.SyncException;
import codechicken.lib.configuration.IConfigTag.SyncType;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.internal.network.PacketDispatcher;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Master registry in charge of config syncing.
 * Created by covers1624 on 15/06/18.
 */
@SuppressWarnings ("unchecked")
@EventBusSubscriber (modid = CodeChickenLib.MOD_ID)
public class ConfigSyncManager {

    private static final Logger logger = LogManager.getLogger("CodeChickenLib-ConfigSync");

    //Always contains master object.
    private static Map<String, IConfigTag<IConfigTag>> syncMap = new HashMap<>();
    //Contains copies of the individual values that can be synced.
    private static Map<String, SyncState> clientRollbackMap = new HashMap<>();

    /**
     * Use this to register your config for syncing.
     * You can only register one.
     *
     * @param modId The modID to associate with the tag.
     *              Only used in the network stack to identify configs on either side.
     * @param tag   The tag to sync,
     *              Only tags that have called {@link IConfigTag#setSyncToClient()} will be synced.
     */
    public static void registerSync(String modId, IConfigTag tag) {
        syncMap.put(modId, tag);
    }

    //Internal
    public static void handshakeReceived(NetHandlerPlayServer netHandler) {
        PacketCustom packet = new PacketCustom(PacketDispatcher.NET_CHANNEL, 20);
        packet.writeVarInt(syncMap.size());
        for (Entry<String, IConfigTag<IConfigTag>> entry: syncMap.entrySet()) {
            packet.writeString(entry.getKey());
            SyncState.create(entry.getValue()).write(packet);
        }
        logger.info("Sending config sync packet to player.");
        netHandler.sendPacket(packet.toPacket());
    }

    //Internal
    public static void readSyncPacket(PacketCustom packet) {
        int numStates = packet.readVarInt();
        for (int i = 0; i < numStates; i++) {
            String ident = packet.readString();
            logger.log(Level.INFO, "Applying config sync for {}.", ident);
            IConfigTag<IConfigTag> found = syncMap.get(ident);
            SyncState state = SyncState.create(found.copy());
            clientRollbackMap.put(ident, state);
            SyncState.applyTo(packet, found);
        }
    }

    @SubscribeEvent
    public static void onClientDisconnected(ClientDisconnectionFromServerEvent event) {
        for (Entry<String, SyncState> entry: clientRollbackMap.entrySet()) {
            logger.log(Level.INFO, "Client disconnect, rolling back config for {}.", entry.getKey());
            entry.getValue().revert(syncMap.get(entry.getKey()));
        }
    }

    public static class SyncState {

        public List<IConfigTag<IConfigTag>> syncTags = new ArrayList<>();

        public static SyncState create(IConfigTag<IConfigTag> tag) {
            SyncState state = new SyncState();
            tag.walkTags(e -> {
                if (!e.isCategory() && e.requiresSync()) {
                    //noinspection unchecked
                    state.syncTags.add(e);
                }
            });
            return state;
        }

        public static void applyTo(MCDataInput in, IConfigTag<IConfigTag> parent) {
            SyncState master = SyncState.create(parent);
            Map<String, IConfigTag<IConfigTag>> lookup = new HashMap<>();
            for (IConfigTag<IConfigTag> tag: master.syncTags) {
                lookup.put(tag.getUnlocalizedName(), tag);
            }
            int numTags = in.readVarInt();
            for (int i = 0; i < numTags; i++) {
                String ident = in.readString();
                IConfigTag<IConfigTag> found = lookup.get(ident);
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

        public void revert(IConfigTag<IConfigTag> parent) {
            SyncState master = SyncState.create(parent);
            Map<String, IConfigTag<IConfigTag>> lookup = new HashMap<>();
            for (IConfigTag<IConfigTag> tag: master.syncTags) {
                lookup.put(tag.getUnlocalizedName(), tag);
            }
            for (IConfigTag<IConfigTag> tag: syncTags) {
                IConfigTag<IConfigTag> found = lookup.get(tag.getUnlocalizedName());
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
            for (IConfigTag<?> tag: syncTags) {
                out.writeString(tag.getUnlocalizedName());
                tag.write(out);
            }
        }
    }
}
