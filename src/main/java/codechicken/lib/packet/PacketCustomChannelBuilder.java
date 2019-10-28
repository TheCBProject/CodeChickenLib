package codechicken.lib.packet;

import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 28/10/19.
 */
public class PacketCustomChannelBuilder {

    private final NetworkRegistry.ChannelBuilder parent;
    private ICustomPacketHandler packetHandler;

    private PacketCustomChannelBuilder(ResourceLocation channelName) {
        parent = NetworkRegistry.ChannelBuilder.named(channelName);
    }

    public static PacketCustomChannelBuilder named(ResourceLocation channelName) {
        return new PacketCustomChannelBuilder(channelName);
    }

    public PacketCustomChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion) {
        parent.networkProtocolVersion(networkProtocolVersion);
        return this;
    }

    public PacketCustomChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions) {
        parent.clientAcceptedVersions(clientAcceptedVersions);
        return this;
    }

    public PacketCustomChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions) {
        parent.serverAcceptedVersions(serverAcceptedVersions);
        return this;
    }

    public PacketCustomChannelBuilder assignHandler(ICustomPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
        return this;
    }

    public EventNetworkChannel build() {
        EventNetworkChannel channel = parent.eventNetworkChannel();
        channel.registerObject(new EventHandler());
        return channel;
    }

    public class EventHandler {

        @SubscribeEvent
        public void onServerPayload(NetworkEvent.ServerCustomPayloadEvent event) {
            if (packetHandler instanceof IServerPacketHandler) {
                IServerPacketHandler ph = (IServerPacketHandler) packetHandler;
                PacketCustom packet = new PacketCustom(event.getPayload());
                NetworkEvent.Context ctx = event.getSource().get();
                INetHandler netHandler = ctx.getNetworkManager().getNetHandler();
                if (netHandler instanceof ServerPlayNetHandler) {
                    ServerPlayNetHandler nh = (ServerPlayNetHandler) netHandler;
                    ctx.enqueueWork(() -> ph.handlePacket(packet, nh.player, nh));
                }
            }
        }

        @SubscribeEvent
        public void onClientPayload(NetworkEvent.ServerCustomPayloadEvent event) {
            if (packetHandler instanceof IClientPacketHandler) {
                IClientPacketHandler ph = (IClientPacketHandler) packetHandler;
                PacketCustom packet = new PacketCustom(event.getPayload());
                NetworkEvent.Context ctx = event.getSource().get();
                INetHandler netHandler = ctx.getNetworkManager().getNetHandler();
                if (netHandler instanceof ClientPlayNetHandler) {
                    ClientPlayNetHandler nh = (ClientPlayNetHandler) netHandler;
                    ctx.enqueueWork(() -> ph.handlePacket(packet, Minecraft.getInstance(), nh));
                }
            }
        }
    }

}
