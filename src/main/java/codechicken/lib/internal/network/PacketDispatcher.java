package codechicken.lib.internal.network;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by covers1624 on 14/07/2017.
 */
public class PacketDispatcher {

    public static String NET_CHANNEL = "CCL_INTERNAL";

    public static void dispatchLandingEffects(World world, BlockPos pos, EntityLivingBase entity, int numParticles) {
        PacketCustom packet = new PacketCustom(NET_CHANNEL, 1);
        packet.writePos(pos);
        packet.writeVector(Vector3.fromEntity(entity));
        packet.writeInt(numParticles);
        packet.sendToChunk(world, pos.getX() >> 4, pos.getZ() >> 4);
    }


}
