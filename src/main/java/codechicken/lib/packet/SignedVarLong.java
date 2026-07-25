package codechicken.lib.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;

/**
 * Created by covers1624 on 10/31/25.
 */
public final class SignedVarLong {

    public static ByteBuf write(ByteBuf buf, long i) {
        return VarLong.write(buf, i >= 0 ? 2 * i : -2 * (i + 1) + 1);
    }

    public static long read(ByteBuf buf) {
        long i = VarLong.read(buf);
        return (i & 1) == 0 ? i >>> 1 : -(i >>> 1) - 1;
    }
}
