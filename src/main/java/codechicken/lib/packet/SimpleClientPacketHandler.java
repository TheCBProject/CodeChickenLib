package codechicken.lib.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;

/**
 * Created by covers1624 on 21/5/20.
 */
public class SimpleClientPacketHandler implements IClientPacketHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Int2ObjectMap<IClientPacketHandler> handlers = new Int2ObjectArrayMap<>();

    protected void addHandler(int id, IClientPacketHandler handler) {
        if (handlers.containsKey(id)) {
            throw new IllegalArgumentException("Handler already registered for ID: " + id);
        }
        handlers.put(id, handler);
    }

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc) {
        IClientPacketHandler h = handlers.get(packet.getType());
        if (h != null) {
            h.handlePacket(packet, mc);
        } else {
            LOGGER.warn("Received unknown packet on channel '{}' with descriptor '{}'.", packet.getChannel(), packet.getType());
        }
    }
}
