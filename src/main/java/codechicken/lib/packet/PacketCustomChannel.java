package codechicken.lib.packet;

import codechicken.lib.packet.ICustomPacketHandler.IClientConfigurationPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerConfigurationPacketHandler;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.ISynchronizedWorkHandler;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
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

    private void onRegisterPayloadHandlerEvent(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(channel.getNamespace());

        if (optional) registrar = registrar.optional();
        if (version != null) registrar = registrar.versioned(version);

        if (client != null || server != null) {
            registrar.play(
                    channel,
                    e -> new PacketCustom.Pkt(channel, e.retain()),
                    handlers -> {
                        handlers.client((payload, context) -> {
                            if (client != null) {
                                enqueue(context.workHandler(), payload, () -> {
                                    client.handlePacket(new PacketCustom(payload), Minecraft.getInstance());
                                });
                            }
                        });
                        handlers.server((payload, context) -> {
                            if (server != null) {
                                enqueue(context.workHandler(), payload, () -> {
                                    server.handlePacket(new PacketCustom(payload), (ServerPlayer) context.player().orElseThrow());
                                });
                            }
                        });
                    }
            );
        }
        if (clientConfiguration != null || serverConfiguration != null) {
            registrar.configuration(
                    channel,
                    e -> new PacketCustom.Pkt(channel, e.retain()),
                    handlers -> {
                        handlers.client((payload, context) -> {
                            enqueue(context.workHandler(), payload, () -> {
                                clientConfiguration.handlePacket(new PacketCustom(payload), Minecraft.getInstance());
                            });
                        });
                    }
            );
        }
    }

    private static void enqueue(ISynchronizedWorkHandler workHandler, PacketCustom.Pkt payload, Runnable runnable) {
        workHandler.submitAsync(() -> {
            try {
                runnable.run();
            } finally {
                payload.data().release();
            }
        }).exceptionally(ex -> {
            LOGGER.error("Error processing packet on channel {}", payload.id(), ex);
            return null;
        });
    }
}
