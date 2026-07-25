package codechicken.lib.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

/**
 * An extension interface containing various helpers
 * for reading and writing to {@link RegistryFriendlyByteBuf}.
 * <p>
 * Created by covers1624 on 10/31/25.
 *
 * @see RegistryFriendlyByteBuf
 * @see CCFriendlyByteBufExtension
 */
public interface CCRegistryFriendlyByteBufExtension extends CCFriendlyByteBufExtension {

    private RegistryFriendlyByteBuf self() {
        return (RegistryFriendlyByteBuf) this;
    }

    /**
     * Write to the stream with a {@link StreamEncoder} / {@link StreamCodec}.
     *
     * @param codec The codec.
     * @param thing The thing to write.
     * @return The same stream.
     */
    default <T> RegistryFriendlyByteBuf cc$writeWithRegistryCodec(StreamEncoder<RegistryFriendlyByteBuf, ? super T> codec, T thing) {
        codec.encode(self(), thing);
        return self();
    }

    /**
     * Read from the stream with a {@link StreamDecoder} / {@link StreamCodec}.
     *
     * @param codec The codec.
     * @return The thing that was read.
     */
    default <T> T cc$readWithRegistryCodec(StreamDecoder<RegistryFriendlyByteBuf, ? extends T> codec) {
        return codec.decode(self());
    }
}
