package codechicken.lib.internal.network;

import codechicken.lib.config.ConfigSyncManager;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.client.Minecraft;

import static codechicken.lib.internal.network.CCLNetwork.L_CONFIG_SYNC;

/**
 * Created by covers1624 on 5/3/20.
 */
public class ClientConfigurationPacketHandler implements ICustomPacketHandler.IClientConfigurationPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc) {
        switch (packet.getType()) {
            case L_CONFIG_SYNC -> ConfigSyncManager.readSyncPacket(packet);
        }
    }
}
