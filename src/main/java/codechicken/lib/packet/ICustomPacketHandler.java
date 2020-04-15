package codechicken.lib.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 2/03/2017.
 */
public interface ICustomPacketHandler {

    interface IClientPacketHandler extends ICustomPacketHandler {

        /**
         * Called on the client to handle a packet sent from the server.
         *
         * @param packet  The packet.
         * @param mc      The Minecraft instance.
         * @param handler The ClientPlayNetHandler.
         */
        @OnlyIn (Dist.CLIENT)
        void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler);
    }

    interface IServerPacketHandler extends ICustomPacketHandler {

        /**
         * Called on the server to handle a packet sent from a client.
         *
         * @param packet  The Packet.
         * @param sender  The player who sent the packet.
         * @param handler The ServerPlayNetHandler
         */
        void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler);
    }

    interface ILoginPacketHandler extends ICustomPacketHandler {

        /**
         * Called on the server to gather all login packets to be sent to the client.
         * Unlike usual PacketCustom operation, Login packets are handled differently in FML,
         * and require an acknowledgement response to be sent. Login packets must be sent using,
         * this.
         *
         * @param consumer The consumer to accept any packets to be sent,
         *                 The first generic parameter being a descriptive name for the packet used
         *                 in FML logging, the second generic being a supplier used to generate the
         *                 packet. Supplier is used for cleaner scope variable name hiding.
         */
        void gatherLoginPackets(BiConsumer<String, Supplier<PacketCustom>> consumer);

        /**
         * Called on the client to handle a login packet provided through {@link #gatherLoginPackets}.
         * This method unlike the other handlers, does not sync to the main thread for processing, instead
         * it is fired on the network thread, this is to allow mods to choose if the packet is important
         * in the handshake cycle and must be handled before anything else can continue. If the data isn't
         * critical to the handshake process, feel free to use {@link NetworkEvent.Context#enqueueWork(Runnable)}
         * to throw things on the main thread.
         *
         * @param packet  The packet to handle.
         * @param mc      The Minecraft instance.
         * @param handler Vanilla's NetHandler.
         * @param context The network context.
         */
        @OnlyIn (Dist.CLIENT)
        void handleLoginPacket(PacketCustom packet, Minecraft mc, IClientLoginNetHandler handler, NetworkEvent.Context context);
    }
}
