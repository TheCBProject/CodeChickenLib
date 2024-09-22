package codechicken.lib.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * Created by covers1624 on 2/03/2017.
 */
public interface ICustomPacketHandler {

    interface IClientPacketHandler extends ICustomPacketHandler {

        /**
         * Called on the client to handle a packet sent from the server.
         *
         * @param packet The packet.
         * @param mc     The Minecraft instance.
         */
        void handlePacket(PacketCustom packet, Minecraft mc);
    }

    interface IServerPacketHandler extends ICustomPacketHandler {

        /**
         * Called on the server to handle a packet sent from a client.
         *
         * @param packet The Packet.
         * @param sender The player who sent the packet.
         */
        void handlePacket(PacketCustom packet, ServerPlayer sender);
    }

    /**
     * Used with {@link OnGameConfigurationEvent} to send packets to the client during the configuration phase.
     */
    interface IClientConfigurationPacketHandler extends ICustomPacketHandler {

        /**
         * Called on the client to handle a configuration phase packet.
         *
         * @param packet The packet.
         * @param mc     The Minecraft instance.
         */
        void handlePacket(PacketCustom packet, Minecraft mc);
    }

    // TODO this is tricky, as we don't have any context to identify the sending client and have a meaningful back/forth.
    //  Its likely not incredibly useful for our uses cases here.
    @ApiStatus.Experimental
    interface IServerConfigurationPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, ServerPlayer sender, Consumer<ConfigurationTask.Type> onTaskCompleted);
    }
}
