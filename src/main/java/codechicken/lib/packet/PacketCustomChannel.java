package codechicken.lib.packet;

import codechicken.lib.packet.ICustomPacketHandler.IClientConfigurationPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerConfigurationPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Created by covers1624 on 3/7/24.
 */
public class PacketCustomChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketCustomChannel.class);

    public final ResourceLocation channel;

    private @Nullable String version;
    private boolean optional;
    private @Nullable IClientConfigurationPacketHandler clientConfiguration;
    private @Nullable IServerConfigurationPacketHandler serverConfiguration;

    private @Nullable IClientPacketHandler client;
    private @Nullable IServerPacketHandler server;

    public PacketCustomChannel(ResourceLocation channel) {
        this.channel = channel;
    }

    public PacketCustomChannel versioned(String version) {
        this.version = version;
        return this;
    }

    public PacketCustomChannel optional() {
        optional = true;
        return this;
    }

    public PacketCustomChannel clientConfiguration(Supplier<Supplier<IClientConfigurationPacketHandler>> supplier) {
        if (FMLEnvironment.dist.isClient()) {
            clientConfiguration = supplier.get().get();
        }
        return this;
    }

    // TODO
//    public PacketCustomChannel serverConfiguration(Supplier<Supplier<IServerConfigurationPacketHandler>> supplier) {
//        serverConfiguration = supplier.get().get();
//        return this;
//    }

    public PacketCustomChannel client(Supplier<Supplier<IClientPacketHandler>> supplier) {
        if (FMLEnvironment.dist.isClient()) {
            client = supplier.get().get();
        }
        return this;
    }

    public PacketCustomChannel server(Supplier<Supplier<IServerPacketHandler>> supplier) {
        server = supplier.get().get();
        return this;
    }

    public void init(IEventBus modBus) {
        modBus.addListener(this::onRegisterPayloadHandlerEvent);
    }

    private void onRegisterPayloadHandlerEvent(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(channel.getNamespace());

        if (optional) registrar = registrar.optional();
        if (version != null) registrar = registrar.versioned(version);
        registrar = registrar.executesOn(HandlerThread.NETWORK);

        CustomPacketPayload.Type<PacketCustom.Pkt> type = new CustomPacketPayload.Type<>(channel);
        StreamCodec<FriendlyByteBuf, PacketCustom.Pkt> codec = StreamCodec.of(
                (buf, p) -> {
                    p.data().markReaderIndex();
                    buf.writeBytes(p.data());
                    p.data().resetReaderIndex();
                },
                buf -> {
                    RegistryAccess access = buf instanceof RegistryFriendlyByteBuf b ? b.registryAccess() : null;
                    return new PacketCustom.Pkt(type, access, buf.readBytes(buf.readableBytes()));
                }
        );
        registrar.playBidirectional(
                type,
                codec,
                (payload, context) -> {
                    switch (context.flow()) {
                        case CLIENTBOUND -> {
                            if (client != null) {
                                enqueue(context, payload, () -> {
                                    client.handlePacket(new PacketCustom(payload), Minecraft.getInstance());
                                });
                            }
                        }
                        case SERVERBOUND -> {
                            if (server != null) {
                                enqueue(context, payload, () -> {
                                    server.handlePacket(new PacketCustom(payload), (ServerPlayer) context.player());
                                });
                            }
                        }
                    }

                }
        );
        registrar.configurationToClient(
                type,
                codec,
                (payload, context) -> {
                    if (clientConfiguration != null) {
                        enqueue(context, payload, () -> {
                            clientConfiguration.handlePacket(new PacketCustom(payload), Minecraft.getInstance());
                        });
                    }
                }
        );
    }

    private static void enqueue(IPayloadContext context, PacketCustom.Pkt payload, Runnable runnable) {
        context.enqueueWork(() -> {
            try {
                runnable.run();
            } finally {
                payload.data().release();
            }
        }).exceptionally(ex -> {
            LOGGER.error("Error processing packet on channel {}", payload.type().id(), ex);
            return null;
        });
    }
}
