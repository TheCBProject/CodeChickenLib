package codechicken.lib.packet;

import codechicken.lib.internal.CCLNetwork;
import io.netty.buffer.Unpooled;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A simple Stream based network channel, very similar to the NeoForge api, however
 * provides raw, stream-based packets.
 * <p>
 * You would construct one of these and store it statically, see {@link CCLNetwork} for inspiration.
 * <p>
 * Created by covers1624 on 10/26/25.
 */
public final class StreamNetworkChannel {

    private final CrashLock LOCK = new CrashLock("Already initialized.");

    private final String namespace;
    private boolean optional;

    private final Map<Identifier, PacketType<?>> packets = new HashMap<>();

    /**
     * Create a new network channel.
     *
     * @param namespace The namespace, usually your mod id.
     */
    public StreamNetworkChannel(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Called with your mod event bus and mod container.
     *
     * @param bus       Your mod event bus.
     * @param container Your mod container.
     */
    public void init(IEventBus bus, ModContainer container) {
        LOCK.lock();
        bus.addListener(RegisterPayloadHandlersEvent.class, e -> onRegisterPayloadHandlerEvent(e, container));
        bus.addListener(this::onGameConfigurationEvent);
    }

    /**
     * Mark this network channel as optional, not required on the client/server.
     *
     * @return The same channel.
     */
    public StreamNetworkChannel optional() {
        this.optional = true;
        return this;
    }

    /**
     * Register a packet to be set to the client during gameplay.
     *
     * @param name    The name of the packet.
     * @param handler The handler for the packet.
     * @return A hande to construct packets.
     */
    public ClientPacketHandle playToClient(String name, PlayHandler handler) {
        var type = createType("play", name, PlayPacketType::new);
        type.clientHandler = handler;
        return type;
    }

    /**
     * Register a packet to be set to the server during gameplay.
     *
     * @param name    The name of the packet.
     * @param handler The handler for the packet.
     * @return A hande to construct packets.
     */
    public ServerPacketHandle playToServer(String name, PlayHandler handler) {
        var type = createType("play", name, PlayPacketType::new);
        type.serverHandler = handler;
        return type;
    }

    /**
     * Register a packet to be set to either the client or the server during gameplay.
     *
     * @param name          The name of the packet.
     * @param serverHandler The handler for the packet server-side.
     * @param clientHandler The handler for the packet client-side.
     * @return A hande to construct packets.
     */
    public BidirectionalPacketHandle playBidirectional(String name, PlayHandler serverHandler, PlayHandler clientHandler) {
        var type = createType("play", name, PlayPacketType::new);
        type.clientHandler = clientHandler;
        type.serverHandler = serverHandler;
        return type;
    }

    /**
     * Register a packet to configure a connecting client.
     *
     * @param name    The name of the packet.
     * @param factory The factory to construct a {@link ICustomConfigurationTask}.
     * @param handler The handler for the packet client-side.
     */
    public void configurationToClient(String name, ConfigurationTaskFactory factory, ConfigurationHandler handler) {
        createType("configuration", name, id -> new ConfigurationPacketType(id, factory, handler));
    }

    // region Events
    private void onRegisterPayloadHandlerEvent(RegisterPayloadHandlersEvent event, ModContainer container) {
        var registrar = event.registrar(container.getModInfo().getVersion().toString());
        if (optional) registrar = registrar.optional();

        for (PacketType<?> type : packets.values()) {
            type.register(registrar);
        }
    }

    private void onGameConfigurationEvent(RegisterConfigurationTasksEvent event) {
        for (PacketType<?> type : packets.values()) {
            if (type instanceof ConfigurationPacketType cType) {
                event.register(cType.getTask(event.getListener()));
            }
        }
    }
    // endregion

    private <T extends PacketType<?>> T createType(String phase, String name, Function<Identifier, ? extends T> func) {
        var id = makeId("play", name);
        if (packets.containsKey(id)) throw new IllegalArgumentException("Packet with name " + name + " for the " + phase + " phase is already registered.");

        var type = func.apply(id);
        packets.put(id, type);
        return type;
    }

    private Identifier makeId(String prefix, String name) {
        return Identifier.fromNamespaceAndPath(namespace, prefix + "/" + name);
    }

    // region Registration internal
    private static abstract sealed class PacketType<T extends CustomPacketPayload> permits PlayPacketType, ConfigurationPacketType {

        protected final CustomPacketPayload.Type<T> type;

        private PacketType(Identifier id) {
            type = new CustomPacketPayload.Type<>(id);
        }

        public abstract void register(PayloadRegistrar registrar);
    }

    private static final class PlayPacketType extends PacketType<StreamPacket> implements BidirectionalPacketHandle {

        private @Nullable PlayHandler clientHandler;
        private @Nullable PlayHandler serverHandler;

        private PlayPacketType(Identifier id) {
            super(id);
        }

        @Override
        public void register(PayloadRegistrar registrar) {
            var codec = StreamPacket.createCodec(type);

            if (clientHandler != null && serverHandler != null) {
                registrar.playBidirectional(type, codec, serverHandler::accept, clientHandler::accept);
            } else if (clientHandler != null) {
                registrar.playToClient(type, codec, clientHandler::accept);
            } else if (serverHandler != null) {
                registrar.playToServer(type, codec, serverHandler::accept);
            } else {
                throw new IllegalStateException("Expected clientHandler or serverHandler to be non-null? This should not actually be possible.");
            }
        }

        @Override
        public StreamPacket.ToClient toClient(RegistryAccess registryAccess) {
            return new StreamPacket.ToClient(
                    type,
                    Unpooled.buffer(),
                    registryAccess,
                    ConnectionType.NEOFORGE
            );
        }

        @Override
        public StreamPacket.ToServer toServer() {
            return new StreamPacket.ToServer(
                    type,
                    Unpooled.buffer(),
                    Minecraft.getInstance().getConnection().registryAccess(),
                    ConnectionType.NEOFORGE
            );
        }
    }

    private static final class ConfigurationPacketType extends PacketType<ConfigurationStreamPacket> {

        public final ConfigurationTask.Type configurationType;
        private final ConfigurationTaskFactory factory;
        private final ConfigurationHandler handler;

        private ConfigurationPacketType(Identifier id, ConfigurationTaskFactory factory, ConfigurationHandler handler) {
            super(id);
            this.factory = factory;
            this.handler = handler;
            configurationType = new ConfigurationTask.Type(type.id());
        }

        @Override
        public void register(PayloadRegistrar registrar) {
            var codec = ConfigurationStreamPacket.createCodec(type);

            registrar.configurationToClient(
                    type,
                    codec,
                    handler::accept
            );
        }

        public ConfigurationTask getTask(ServerConfigurationPacketListener listener) {
            var context = new ConfigurationContext() {

                @Override
                public ConfigurationStreamPacket newPacket() {
                    return new ConfigurationStreamPacket(type, Unpooled.buffer());
                }

                @Override
                public ServerConfigurationPacketListener packetListener() {
                    return listener;
                }
            };
            return factory.apply(configurationType, context);
        }
    }
    // endregion

    /**
     * A helpful interface for the gameplay packet handler type.
     */
    public interface PlayHandler extends BiConsumer<RegistryFriendlyByteBuf, IPayloadContext> {

        @Override
        void accept(RegistryFriendlyByteBuf packet, IPayloadContext ctx);
    }

    /**
     * A helpful interface for the configuration phase packet handler type.
     */
    public interface ConfigurationHandler extends BiConsumer<FriendlyByteBuf, IPayloadContext> {

        @Override
        void accept(FriendlyByteBuf packet, IPayloadContext ctx);
    }

    /**
     * The context available when executing a configuration task.
     */
    public interface ConfigurationContext {

        /**
         * Create a new packet to be sent on the configuration channel.
         *
         * @return The packet.
         */
        ConfigurationStreamPacket newPacket();

        /**
         * @return The active configuration packet listener.
         */
        ServerConfigurationPacketListener packetListener();
    }

    /**
     * A factory for constructing {@link ICustomConfigurationTask} instances. Usually a record, storing both the type and context.
     */
    public interface ConfigurationTaskFactory extends BiFunction<ConfigurationTask.Type, ConfigurationContext, ICustomConfigurationTask> { }

    /**
     * A handle for constructing client-bound packets.
     */
    public interface ClientPacketHandle {

        /**
         * Construct a packet bound for a client.
         *
         * @param registryAccess The {@link RegistryAccess} instance.
         * @return The packet.
         */
        StreamPacket.ToClient toClient(RegistryAccess registryAccess);

        /**
         * Construct a packet bound for a client.
         *
         * @param level A level to grab the {@link RegistryAccess} from.
         * @return The packet.
         */
        default StreamPacket.ToClient toClient(Level level) {
            return toClient(level.registryAccess());
        }

        /**
         * Construct a packet bound for a client.
         *
         * @param player A player to grab the {@link RegistryAccess} from.
         * @return The packet.
         */
        default StreamPacket.ToClient toClient(Player player) {
            return toClient(player.registryAccess());
        }

        /**
         * Construct a packet bound for a client.
         *
         * @param tile A BlockEntity to grab the {@link RegistryAccess} from.
         * @return The packet.
         */
        default StreamPacket.ToClient toClient(BlockEntity tile) {
            return toClient(tile.getLevel());
        }

        /**
         * Construct a packet bound for a client.
         *
         * @param entity An entity to grab the {@link RegistryAccess} from.
         * @return The packet.
         */
        default StreamPacket.ToClient toClient(Entity entity) {
            return toClient(entity.level());
        }
    }

    /**
     * A handle for constructing server-bound packets.
     */
    public interface ServerPacketHandle {

        /**
         * Construct a packet bound for the server.
         *
         * @return The packet.
         */
        StreamPacket.ToServer toServer();
    }

    /**
     * A handle for constructing a packet bound for a client or the server.
     */
    public interface BidirectionalPacketHandle extends ClientPacketHandle, ServerPacketHandle { }
}
