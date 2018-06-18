package codechicken.lib.internal.proxy;

import codechicken.lib.internal.network.PacketDispatcher;
import codechicken.lib.internal.network.ServerHandshakeHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Created by covers1624 on 23/11/2016.
 */
public class Proxy {

    public void preInit() {
        PacketCustom.assignHandshakeHandler(PacketDispatcher.NET_CHANNEL, new ServerHandshakeHandler());
    }

    public void init() {
    }

    public void postInit() {
    }

    public void loadConfig() {
    }

    public void serverStarting(FMLServerStartingEvent event) {
    }

    public boolean isClient() {
        return false;
    }

}
