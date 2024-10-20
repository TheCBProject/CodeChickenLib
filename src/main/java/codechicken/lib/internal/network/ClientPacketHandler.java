package codechicken.lib.internal.network;

import codechicken.lib.inventory.container.modular.ModularGuiContainerMenu;
import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static codechicken.lib.internal.network.CCLNetwork.C_ADD_LANDING_EFFECTS;
import static codechicken.lib.internal.network.CCLNetwork.C_GUI_SYNC;
import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 14/07/2017.
 */
public class ClientPacketHandler implements IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc) {
        switch (packet.getType()) {
            case C_ADD_LANDING_EFFECTS -> {
                BlockPos pos = packet.readPos();
                Vector3 vec = packet.readVector();
                int numParticles = packet.readVarInt();
                BlockState state = requireNonNull(mc.level).getBlockState(pos);
                CustomParticleHandler.addLandingEffects(mc.level, pos, state, vec, numParticles);
            }
            case C_GUI_SYNC -> ModularGuiContainerMenu.handlePacketFromServer(requireNonNull(mc.player), packet);
        }
    }
}
