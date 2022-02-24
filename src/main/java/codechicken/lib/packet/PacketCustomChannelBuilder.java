package codechicken.lib.packet;

import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.ILoginPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Used to build a network channel for use with {@link PacketCustom}.
 * Similar in design to {@link NetworkRegistry.ChannelBuilder}.
 * <p>
 * Created by covers1624 on 28/10/19.
 */
public class PacketCustomChannelBuilder {

    private static final Supplier<String> CONST_1 = () -> "1";
    private static final Predicate<String> TRUE = e -> true;

    private final NetworkRegistry.ChannelBuilder parent;
    private final ResourceLocation channelName;

    private Supplier<String> networkProtocolVersion = CONST_1;
    private Predicate<String> clientAcceptedVersions = TRUE;
    private Predicate<String> serverAcceptedVersions = TRUE;

    private IClientPacketHandler clientHandler;
    private IServerPacketHandler serverHandler;
    private ILoginPacketHandler loginHandler;

    private PacketCustomChannelBuilder(ResourceLocation channelName) {
        this.channelName = channelName;
        parent = NetworkRegistry.ChannelBuilder.named(channelName);
    }

    /**
     * Create a new {@link PacketCustomChannelBuilder} with the given channel name.
     * Channel names are global and must be unique per mod. Just use this format:
     * "mod_id:network_name". For example: 'codechickenlib:internal'
     *
     * @param channelName The Channel name.
     * @return The builder.
     */
    public static PacketCustomChannelBuilder named(ResourceLocation channelName) {
        return new PacketCustomChannelBuilder(channelName);
    }

    /**
     * Register a Supplier that provides the protocol version associated with this channel.
     * You are able to compare this value each side of the connection against its local version,
     * if you wish to talk across protocol versions. The default is to provide '1' as the version.
     *
     * @param networkProtocolVersion The version Supplier.
     * @return The same builder.
     */
    public PacketCustomChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion) {
        this.networkProtocolVersion = networkProtocolVersion;
        return this;
    }

    /**
     * Register a Predicate to check the Server side protocol version on the Client. The
     * default will accept any remote versions.
     *
     * @param clientAcceptedVersions The Predicate.
     * @return The same builder.
     */
    public PacketCustomChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions) {
        this.clientAcceptedVersions = clientAcceptedVersions;
        return this;
    }

    /**
     * Register a Predicate to check the Client side protocol version on the Server. The
     * default will accept any remote versions.
     *
     * @param serverAcceptedVersions The Predicate.
     * @return The same builder.
     */
    public PacketCustomChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions) {
        this.serverAcceptedVersions = serverAcceptedVersions;
        return this;
    }

    /**
     * Register a double Supplier, to construct the {@link IClientPacketHandler} for the client side.
     * The double Supplier is used to avoid class loading issues on the server side, you do NOT
     * need any external sided checks if you call using the following example:
     * <br/>
     * <code>builder.assignClientHandler(() -> ClientPacketHandler::new);</code>
     * <p/>
     *
     * @param clientHandler The Supplier.
     * @return The same builder.
     */
    public PacketCustomChannelBuilder assignClientHandler(Supplier<Supplier<IClientPacketHandler>> clientHandler) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.clientHandler = clientHandler.get().get();
        }
        return this;
    }

    /**
     * Register a double Supplier, to construct the {@link IServerPacketHandler} for the client side.
     * The double Supplier is used to avoid confusion with {@link #assignClientHandler}.
     * Example usage: <code>builder.assignClientHandler(() -> ServerPacketHandler::new);</code>
     *
     * @param serverHandler The Supplier.
     * @return The same builder.
     */

    public PacketCustomChannelBuilder assignServerHandler(Supplier<Supplier<IServerPacketHandler>> serverHandler) {
        this.serverHandler = serverHandler.get().get();
        return this;
    }

    /**
     * Register a double Supplier, to construct the {@link ILoginPacketHandler} for sending packets
     * to the client during handshake. See {@link ILoginPacketHandler} for more information.
     * Example usage: <code>builder.assignClientHandler(() -> ServerPacketHandler::new);</code>
     *
     * @param loginHandler The Supplier.
     * @return The same builder.
     */
    public PacketCustomChannelBuilder assignLoginHandler(Supplier<Supplier<ILoginPacketHandler>> loginHandler) {
        this.loginHandler = loginHandler.get().get();
        return this;
    }

    /**
     * Actually build and register the channel with Forge.
     *
     * @return The underlying {@link EventNetworkChannel}
     */
    public synchronized EventNetworkChannel build() {
        EventNetworkChannel channel = parent//
                .networkProtocolVersion(networkProtocolVersion)//
                .clientAcceptedVersions(clientAcceptedVersions)//
                .serverAcceptedVersions(serverAcceptedVersions)//
                .eventNetworkChannel();

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

    /**
     * Default handler for Client received packets.
     */
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
            PacketListener netHandler = ctx.getNetworkManager().getPacketListener();
            ctx.setPacketHandled(true);
            if (netHandler instanceof ClientPacketListener nh) {
                ctx.enqueueWork(() -> packetHandler.handlePacket(packet, Minecraft.getInstance(), nh));
            }
        }
    }

    /**
     * Default handler for Server received packets.
     */
    public static class ServerHandler {

        private final IServerPacketHandler packetHandler;

        public ServerHandler(IServerPacketHandler packetHandler) {
            this.packetHandler = packetHandler;
        }

        @SubscribeEvent
        public void onServerPayload(NetworkEvent.ClientCustomPayloadEvent event) {
            PacketCustom packet = new PacketCustom(event.getPayload());
            NetworkEvent.Context ctx = event.getSource().get();
            PacketListener netHandler = ctx.getNetworkManager().getPacketListener();
            ctx.setPacketHandled(true);
            if (netHandler instanceof ServerGamePacketListenerImpl nh) {
                ctx.enqueueWork(() -> packetHandler.handlePacket(packet, nh.player, nh));
            }
        }
    }

    /**
     * Default handler for Login packets.
     */
    public static class LoginHandler {

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
            PacketListener netHandler = ctx.getNetworkManager().getPacketListener();
            ctx.setPacketHandled(true);
            if (netHandler instanceof ClientLoginPacketListener nh) {
                packetHandler.handleLoginPacket(packet, Minecraft.getInstance(), nh, ctx);
                //For _some_ reason sending this response packet in FML is private. So just spoof the packet :D
                ctx.getPacketDispatcher().sendPacket(new ResourceLocation("fml:handshake"), new FriendlyByteBuf(Unpooled.buffer().writeByte(99)));
            }
        }
    }
}
