package codechicken.lib.internal.network;

import codechicken.lib.configuration.ConfigSyncManager;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static codechicken.lib.internal.network.CCLNetwork.L_CONFIG_SYNC;

/**
 * Created by covers1624 on 5/3/20.
 */
public class LoginPacketHandler implements ICustomPacketHandler.ILoginPacketHandler {

    @Override
    public void gatherLoginPackets(BiConsumer<String, Supplier<PacketCustom>> consumer) {
        ConfigSyncManager.handleLogin(consumer);
    }

    @Override
    public void handleLoginPacket(PacketCustom packet, Minecraft mc, IClientLoginNetHandler handler, NetworkEvent.Context context) {
        switch (packet.getType()) {
            case L_CONFIG_SYNC: {
                ConfigSyncManager.readSyncPacket(packet);
                break;
            }
        }
    }
}
