package codechicken.lib.internal.network;

import codechicken.lib.inventory.container.ICCLContainerType;
import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

import static codechicken.lib.internal.network.CCLNetwork.C_ADD_LANDING_EFFECTS;
import static codechicken.lib.internal.network.CCLNetwork.C_OPEN_CONTAINER;

/**
 * Created by covers1624 on 14/07/2017.
 */
public class ClientPacketHandler implements IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, IClientPlayNetHandler handler) {
        switch (packet.getType()) {
            case C_ADD_LANDING_EFFECTS:
                BlockPos pos = packet.readPos();
                Vector3 vec = packet.readVector();
                int numParticles = packet.readVarInt();
                BlockState state = mc.world.getBlockState(pos);
                CustomParticleHandler.addLandingEffects(mc.world, pos, state, vec, numParticles);
                break;
            case C_OPEN_CONTAINER:
                handleOpenContainer(packet, mc);
                break;
            case 20:
                //                ConfigSyncManager.readSyncPacket(packet);
                break;
        }
    }

    @SuppressWarnings ("unchecked")
    private void handleOpenContainer(PacketCustom packet, Minecraft mc) {
        int containerId = packet.readVarInt();
        int windowId = packet.readVarInt();
        ITextComponent name = packet.readTextComponent();
        ContainerType<?> rawType = Registry.MENU.getByValue(containerId);
        if (rawType instanceof ICCLContainerType<?>) {
            ICCLContainerType<?> type = (ICCLContainerType<?>) rawType;
            ScreenManager.getScreenFactory(rawType, mc, windowId, name)//
                    .map(e -> (ScreenManager.IScreenFactory<Container, ?>) e)//
                    .ifPresent(screenFactory -> {
                        Container container = type.create(windowId, Minecraft.getInstance().player.inventory, packet);
                        Screen screen = screenFactory.create(container, mc.player.inventory, name);
                        mc.player.openContainer = ((IHasContainer<?>) screen).getContainer();
                        mc.displayGuiScreen(screen);
                    });

        }
    }
}
