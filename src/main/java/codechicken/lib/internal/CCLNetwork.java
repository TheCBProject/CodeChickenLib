package codechicken.lib.internal;

import codechicken.lib.block.ModularBlockEntity;
import codechicken.lib.config.ConfigSyncManager;
import codechicken.lib.inventory.container.modular.ModularGuiContainerMenu;
import codechicken.lib.packet.StreamNetworkChannel;
import codechicken.lib.render.particle.CustomParticleHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 28/10/19.
 */
public class CCLNetwork {

    public static final StreamNetworkChannel CHANNEL = new StreamNetworkChannel(MOD_ID)
            .optional();

    public static final StreamNetworkChannel.BidirectionalPacketHandle GUI_SYNC = CHANNEL.playBidirectional("gui_sync",
            ModularGuiContainerMenu::handlePacketFromClient,
            ModularGuiContainerMenu::handlePacketFromServer
    );

    public static final StreamNetworkChannel.BidirectionalPacketHandle TILE_MESSAGE = CHANNEL.playBidirectional(
            "tile_message",
            CCLNetwork::handleTileMessageServer,
            CCLNetwork::handleTileMessageClient
    );

    public static void init(IEventBus modBus, ModContainer container) {
        CHANNEL.init(modBus, container);

        CHANNEL.configurationToClient("config_sync", ConfigSyncManager.ConfigSyncConfigurationTask::new, ConfigSyncManager::readSyncPacket);
    }

    private static void handleTileMessageServer(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        if (ctx.player().level().getBlockEntity(packet.readBlockPos()) instanceof ModularBlockEntity tile) {
            tile.onInternalServerPacket(packet, ctx);
        }
    }

    private static void handleTileMessageClient(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        if (ctx.player().level().getBlockEntity(packet.readBlockPos()) instanceof ModularBlockEntity tile) {
            tile.onInternalClientPacket(packet, ctx);
        }
    }
}
