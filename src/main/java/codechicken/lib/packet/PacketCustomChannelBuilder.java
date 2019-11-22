package codechicken.lib.packet;

import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
    private IClientPacketHandler clientHandler;
    private IServerPacketHandler serverHandler;

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

    public PacketCustomChannelBuilder assignClientHandler(Supplier<Supplier<IClientPacketHandler>> clientHandler) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.clientHandler = clientHandler.get().get();
        }
        return this;
    }

    public PacketCustomChannelBuilder assignServerHandler(Supplier<Supplier<IServerPacketHandler>> serverHandler) {
        this.serverHandler = serverHandler.get().get();
        return this;
    }

    public EventNetworkChannel build() {
        EventNetworkChannel channel = parent.eventNetworkChannel();

        if (clientHandler != null) {
            channel.registerObject(new ClientHandler(clientHandler));
        }

        if (serverHandler != null) {
            channel.registerObject(new ServerHandler(serverHandler));
        }
        return channel;
    }

    public static class ClientHandler {

        private final IClientPacketHandler packetHandler;

        public ClientHandler(IClientPacketHandler packetHandler) {
            this.packetHandler = packetHandler;
        }

        @SubscribeEvent
        public void onClientPayload(NetworkEvent.ServerCustomPayloadEvent event) {
            PacketCustom packet = new PacketCustom(event.getPayload());
            NetworkEvent.Context ctx = event.getSource().get();
            INetHandler netHandler = ctx.getNetworkManager().getNetHandler();
            ctx.setPacketHandled(true);
            if (netHandler instanceof ClientPlayNetHandler) {
                ClientPlayNetHandler nh = (ClientPlayNetHandler) netHandler;
                ctx.enqueueWork(() -> packetHandler.handlePacket(packet, Minecraft.getInstance(), nh));
            }
        }
    }

    public static class ServerHandler {

        private final IServerPacketHandler packetHandler;

        public ServerHandler(IServerPacketHandler packetHandler) {
            this.packetHandler = packetHandler;
        }

        @SubscribeEvent
        public void onServerPayload(NetworkEvent.ClientCustomPayloadEvent event) {
            PacketCustom packet = new PacketCustom(event.getPayload());
            NetworkEvent.Context ctx = event.getSource().get();
            INetHandler netHandler = ctx.getNetworkManager().getNetHandler();
            ctx.setPacketHandled(true);
            if (netHandler instanceof ServerPlayNetHandler) {
                ServerPlayNetHandler nh = (ServerPlayNetHandler) netHandler;
                ctx.enqueueWork(() -> packetHandler.handlePacket(packet, nh.player, nh));
            }
        }
    }
}
