package codechicken.lib.internal.network;

import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;

/**
 * Created by covers1624 on 14/07/2017.
 */
public class ClientPacketHandler implements IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler) {
        switch (packet.getType()) {
            case 1:
                BlockPos pos = packet.readPos();
                Vector3 vec = packet.readVector();
                int numParticles = packet.readInt();
                IBlockState state = mc.world.getBlockState(pos);
                CustomParticleHandler.addLandingEffects(mc.world, pos, state, vec, numParticles);
                break;
        }
    }
}
