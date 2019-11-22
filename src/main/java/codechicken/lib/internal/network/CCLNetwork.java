package codechicken.lib.internal.network;

import codechicken.lib.packet.PacketCustomChannelBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

/**
 * Created by covers1624 on 28/10/19.
 */
public class CCLNetwork {

    public static final ResourceLocation NET_CHANNEL = new ResourceLocation("ccl:internal");
    public static EventNetworkChannel netChannel;

    public static final int C_ADD_LANDING_EFFECTS = 1;
    public static final int C_OPEN_CONTAINER = 10;

    public static void init() {
        netChannel = PacketCustomChannelBuilder.named(NET_CHANNEL)//
                .networkProtocolVersion(() -> "1")//
                .clientAcceptedVersions(e -> true)//
                .serverAcceptedVersions(e -> true)//
                .assignClientHandler(() -> ClientPacketHandler::new)//
                .build();
    }

}
