package codechicken.lib.internal.network;

import codechicken.lib.inventory.container.modular.ModularGuiContainerMenu;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import static codechicken.lib.internal.network.CCLNetwork.S_GUI_SYNC;

/**
 * Created by covers1624 on 14/07/2017.
 */
public class ServerPacketHandler implements IServerPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, ServerPlayer sender) {
        switch (packet.getType()) {
            case S_GUI_SYNC -> ModularGuiContainerMenu.handlePacketFromClient(sender, packet);
        }
    }
}
