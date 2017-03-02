package codechicken.lib.packet;

import net.minecraft.network.NetHandlerPlayServer;

/**
 * Created by covers1624 on 2/03/2017.
 */
public interface IHandshakeHandler {

    void handshakeReceived(NetHandlerPlayServer netHandler);
}
