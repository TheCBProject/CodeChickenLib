package codechicken.lib.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;

/**
 * Created by covers1624 on 2/03/2017.
 */
public interface ICustomPacketHandler {

    interface IClientPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler);
    }

    interface IServerPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, EntityPlayerMP sender, INetHandlerPlayServer handler);
    }
}
