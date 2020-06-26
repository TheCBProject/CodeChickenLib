package codechicken.lib.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;

/**
 * Created by covers1624 on 21/5/20.
 */
public class SimpleClientPacketHandler implements IClientPacketHandler {

    private static final Logger logger = LogManager.getLogger();

    private static final Int2ObjectMap<IClientPacketHandler> handlers = new Int2ObjectArrayMap<>();

    public void addHandler(int id, IClientPacketHandler handler) {
        if (handlers.containsKey(id)) {
            throw new IllegalArgumentException("Handler already registered for ID: " + id);
        }
        handlers.put(id, handler);
    }

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler) {
        IClientPacketHandler h = handlers.get(packet.getType());
        if (h != null) {
            h.handlePacket(packet, mc, handler);
        } else {
            logger.warn("Received unknown packet on channel '{}' with descriptor '{}'.", packet.getChannel(), packet.getType());
        }
    }
}
