package codechicken.lib.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.IServerPlayNetHandler;

/**
 * Created by covers1624 on 2/03/2017.
 */
public interface ICustomPacketHandler {

    interface IClientPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler);
    }

    interface IServerPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler);
    }
}
