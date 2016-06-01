package codechicken.lib.packet;

import io.netty.channel.ChannelHandler;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by covers1624 on 5/28/2016.
 */
@ChannelHandler.Sharable
public class SPacketUpdateTileEntityFMLWrapper extends SPacketUpdateTileEntity {

    private static final Logger logger = LogManager.getLogger("CCL");

}
