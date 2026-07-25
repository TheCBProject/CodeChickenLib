package codechicken.lib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;

/**
 * Created by covers1624 on 7/26/26.
 */
public class CCCodecs {

    public static Codec<RegistryFriendlyByteBuf> embeddedPacket(RegistryAccess registryAccess) {
        return new PrimitiveCodec<>() {
            @Override
            public <T> DataResult<RegistryFriendlyByteBuf> read(DynamicOps<T> ops, T input) {
                return Codec.BYTE_BUFFER.read(ops, input)
                        .map(b -> new RegistryFriendlyByteBuf(
                                Unpooled.wrappedBuffer(b),
                                registryAccess,
                                ConnectionType.NEOFORGE
                        ));
            }

            @Override
            public <T> T write(DynamicOps<T> ops, RegistryFriendlyByteBuf buf) {
                buf.readerIndex(0);
                return Codec.BYTE_BUFFER.write(ops, buf.nioBuffer());
            }
        };
    }
}
