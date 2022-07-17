package codechicken.lib.internal.network;

import codechicken.lib.inventory.container.ICCLContainerType;
import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import static codechicken.lib.internal.network.CCLNetwork.C_ADD_LANDING_EFFECTS;
import static codechicken.lib.internal.network.CCLNetwork.C_OPEN_CONTAINER;

/**
 * Created by covers1624 on 14/07/2017.
 */
public class ClientPacketHandler implements IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, ClientPacketListener handler) {
        switch (packet.getType()) {
            case C_ADD_LANDING_EFFECTS -> {
                BlockPos pos = packet.readPos();
                Vector3 vec = packet.readVector();
                int numParticles = packet.readVarInt();
                BlockState state = mc.level.getBlockState(pos);
                CustomParticleHandler.addLandingEffects(mc.level, pos, state, vec, numParticles);
            }
            case C_OPEN_CONTAINER -> handleOpenContainer(packet, mc);
        }
    }

    @SuppressWarnings ("unchecked")
    private void handleOpenContainer(PacketCustom packet, Minecraft mc) {
        MenuType<?> rawType = packet.readRegistryIdDirect(ForgeRegistries.MENU_TYPES);
        int windowId = packet.readVarInt();
        Component name = packet.readTextComponent();
        if (rawType instanceof ICCLContainerType<?> type) {
            MenuScreens.getScreenFactory(rawType, mc, windowId, name)
                    .map(e -> (MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>) e)
                    .ifPresent(screenFactory -> {
                        AbstractContainerMenu container = type.create(windowId, Minecraft.getInstance().player.getInventory(), packet);
                        Screen screen = screenFactory.create(container, mc.player.getInventory(), name);
                        mc.player.containerMenu = ((MenuAccess<?>) screen).getMenu();
                        mc.setScreen(screen);
                    });

        }
    }
}
