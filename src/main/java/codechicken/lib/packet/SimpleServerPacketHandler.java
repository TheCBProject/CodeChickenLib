package codechicken.lib.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.IServerPlayNetHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;

/**
 * Created by covers1624 on 21/5/20.
 */
public class SimpleServerPacketHandler implements IServerPacketHandler {

    private static final Logger logger = LogManager.getLogger();

    private static final Int2ObjectMap<IServerPacketHandler> handlers = new Int2ObjectArrayMap<>();

    public void addHandler(int id, IServerPacketHandler handler) {
        if (handlers.containsKey(id)) {
            throw new IllegalArgumentException("Handler already registered for ID: " + id);
        }
        handlers.put(id, handler);
    }

    @Override
    public void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler) {
        IServerPacketHandler h = handlers.get(packet.getType());
        if (h != null) {
            h.handlePacket(packet, sender, handler);
        } else {
            logger.warn("Received unknown packet on channel '{}' with descriptor '{}'.", packet.getChannel(), packet.getType());
        }
    }
}
