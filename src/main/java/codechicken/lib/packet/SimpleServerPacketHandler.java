package codechicken.lib.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;

/**
 * Created by covers1624 on 21/5/20.
 */
public class SimpleServerPacketHandler implements IServerPacketHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Int2ObjectMap<IServerPacketHandler> handlers = new Int2ObjectArrayMap<>();

    protected void addHandler(int id, IServerPacketHandler handler) {
        if (handlers.containsKey(id)) {
            throw new IllegalArgumentException("Handler already registered for ID: " + id);
        }
        handlers.put(id, handler);
    }

    @Override
    public void handlePacket(PacketCustom packet, ServerPlayer sender) {
        IServerPacketHandler h = handlers.get(packet.getType());
        if (h != null) {
            h.handlePacket(packet, sender);
        } else {
            LOGGER.warn("Received unknown packet on channel '{}' with descriptor '{}'.", packet.getChannel(), packet.getType());
        }
    }
}
