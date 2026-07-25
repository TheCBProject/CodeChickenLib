package codechicken.lib.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Created by covers1624 on 10/31/25.
 */
public class ConfigurationStreamPacket extends FriendlyByteBuf implements CustomPacketPayload {

    private final CustomPacketPayload.Type<ConfigurationStreamPacket> type;

    ConfigurationStreamPacket(CustomPacketPayload.Type<ConfigurationStreamPacket> type, ByteBuf source) {
        super(source);
        this.type = type;
    }

    public static StreamCodec<FriendlyByteBuf, ConfigurationStreamPacket> createCodec(Type<ConfigurationStreamPacket> type) {
        return StreamCodec.of(
                (buf, p) -> buf.writeBytes(p, 0, p.writerIndex()),
                (buf) -> {
                    var packet = new ConfigurationStreamPacket(type, Unpooled.buffer(buf.readableBytes()));
                    buf.readBytes(packet);
                    return packet;
                }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }
}
