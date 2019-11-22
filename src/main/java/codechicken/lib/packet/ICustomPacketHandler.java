package codechicken.lib.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by covers1624 on 2/03/2017.
 */
public interface ICustomPacketHandler {

    interface IClientPacketHandler extends ICustomPacketHandler {

        @OnlyIn (Dist.CLIENT)
        void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler);
    }

    interface IServerPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler);
    }
}
