package codechicken.lib.util;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.file.Path;
import java.util.function.Consumer;

import static codechicken.lib.internal.network.CCLNetwork.C_OPEN_CONTAINER;
import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 22/10/2016.
 */
public class ServerUtils {

    public static Path getSaveDirectory() {
        return getSaveDirectory(Level.OVERWORLD);
    }

    public static Path getSaveDirectory(ResourceKey<Level> dimension) {
        return ServerLifecycleHooks.getCurrentServer().storageSource.getDimensionPath(dimension);
    }

    public static void openContainer(ServerPlayer player, MenuProvider containerProvider) {
        openContainer(player, containerProvider, e -> {
        });
    }

    public static void openContainer(ServerPlayer player, MenuProvider containerProvider, Consumer<MCDataOutput> packetConsumer) {
        if (player.level().isClientSide()) {
            return;
        }
        player.doCloseContainer();
        player.nextContainerCounter();
        int containerId = player.containerCounter;

        AbstractContainerMenu container = containerProvider.createMenu(containerId, player.getInventory(), player);
        MenuType<?> type = requireNonNull(container).getType();

        PacketCustom packet = new PacketCustom(CCLNetwork.NET_CHANNEL, C_OPEN_CONTAINER);
        packet.writeRegistryIdDirect(BuiltInRegistries.MENU, type);
        packet.writeVarInt(containerId);
        packet.writeTextComponent(containerProvider.getDisplayName());
        packetConsumer.accept(packet);

        packet.sendToPlayer(player);
        player.containerMenu = container;
        player.initMenu(player.containerMenu);
        NeoForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
    }
}
