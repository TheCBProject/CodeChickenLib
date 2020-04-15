package codechicken.lib.packet;

import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.ILoginPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    private final ResourceLocation channelName;
    private IClientPacketHandler clientHandler;
    private IServerPacketHandler serverHandler;
    private ILoginPacketHandler loginHandler;

    private PacketCustomChannelBuilder(ResourceLocation channelName) {
        this.channelName = channelName;
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

    public PacketCustomChannelBuilder assignLoginHandler(Supplier<Supplier<ILoginPacketHandler>> loginHandler) {
        this.loginHandler = loginHandler.get().get();
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
        if (loginHandler != null) {
            channel.registerObject(new LoginHandler(loginHandler));
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
            if (event instanceof NetworkEvent.ServerCustomPayloadLoginEvent) {
                return;
            }
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

    public class LoginHandler {

        private final ILoginPacketHandler packetHandler;

        public LoginHandler(ILoginPacketHandler packetHandler) {
            this.packetHandler = packetHandler;
        }

        @SubscribeEvent
        public void onGatherLoginPayloads(NetworkEvent.GatherLoginPayloadsEvent event) {
            packetHandler.gatherLoginPackets((ctx, packetSupplier) -> {
                PacketCustom packet = packetSupplier.get();
                event.add(packet.toPacketBuffer(), packet.getChannel(), ctx);
            });
        }

        @SubscribeEvent
        public void onClientPayload(NetworkEvent.LoginPayloadEvent event) {
            PacketCustom packet = new PacketCustom(event.getPayload());
            NetworkEvent.Context ctx = event.getSource().get();
            INetHandler netHandler = ctx.getNetworkManager().getNetHandler();
            ctx.setPacketHandled(true);
            if (netHandler instanceof ClientLoginNetHandler) {
                ClientLoginNetHandler nh = (ClientLoginNetHandler) netHandler;
                packetHandler.handleLoginPacket(packet, Minecraft.getInstance(), nh, ctx);
                //For _some_ reason sending this response packet in FML is private. So just spoof the packet :D
                ctx.getPacketDispatcher().sendPacket(new ResourceLocation("fml:handshake"), new PacketBuffer(Unpooled.buffer().writeByte(99)));
            }
        }
    }
}
