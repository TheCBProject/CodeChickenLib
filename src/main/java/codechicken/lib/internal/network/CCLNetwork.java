package codechicken.lib.internal.network;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.packet.PacketCustomChannel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

/**
 * Created by covers1624 on 28/10/19.
 */
public class CCLNetwork {

    public static final ResourceLocation NET_CHANNEL = new ResourceLocation("ccl:internal");
    public static final PacketCustomChannel channel = new PacketCustomChannel(NET_CHANNEL)
            .optional()
            .versioned(CodeChickenLib.container().getModInfo().getVersion().toString())
            .clientConfiguration(() -> ClientConfigurationPacketHandler::new)
            .client(() -> ClientPacketHandler::new)
            .server(() -> ServerPacketHandler::new);

    //Client handled.
    public static final int C_ADD_LANDING_EFFECTS = 1;
    public static final int C_OPEN_CONTAINER = 10;
    public static final int C_GUI_SYNC = 20;

    //Server handled.
    public static final int S_GUI_SYNC = 20;

    //Login handled.
    public static final int L_CONFIG_SYNC = 1;

    public static void init(IEventBus modBus) {
        channel.init(modBus);
    }
}
