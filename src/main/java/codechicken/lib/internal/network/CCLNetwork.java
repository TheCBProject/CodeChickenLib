package codechicken.lib.internal.network;

import codechicken.lib.packet.PacketCustomChannelBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.event.EventNetworkChannel;

/**
 * Created by covers1624 on 28/10/19.
 */
public class CCLNetwork {

    public static final ResourceLocation NET_CHANNEL = new ResourceLocation("ccl:internal");
    public static EventNetworkChannel netChannel;

    //Client handled.
    public static final int C_ADD_LANDING_EFFECTS = 1;
    public static final int C_OPEN_CONTAINER = 10;

    //Login handled.
    public static final int L_CONFIG_SYNC = 1;

    public static void init() {
        netChannel = PacketCustomChannelBuilder.named(NET_CHANNEL)//
                .assignClientHandler(() -> ClientPacketHandler::new)//
                .assignLoginHandler(() -> LoginPacketHandler::new)//
                .build();
    }

}
