package codechicken.lib.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;

/**
 * Created by covers1624 on 10/31/25.
 */
public final class SignedVarInt {

    public static ByteBuf write(ByteBuf buf, int i) {
        return VarInt.write(buf, i >= 0 ? 2 * i : -2 * (i + 1) + 1);
    }

    public static int read(ByteBuf buf) {
        int i = VarInt.read(buf);
        return (i & 1) == 0 ? i >>> 1 : -(i >>> 1) - 1;
    }
}
