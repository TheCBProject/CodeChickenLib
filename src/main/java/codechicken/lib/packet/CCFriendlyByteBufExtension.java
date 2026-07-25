package codechicken.lib.packet;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

/**
 * An extension interface containing various helpers
 * for reading and writing to {@link FriendlyByteBuf}.
 * <p>
 * Created by covers1624 on 10/31/25.
 * @see FriendlyByteBuf
 * @see CCRegistryFriendlyByteBufExtension
 */
public interface CCFriendlyByteBufExtension {

    private FriendlyByteBuf self() {
        return (FriendlyByteBuf) this;
    }

    // region Signed var primitives.

    /**
     * Writes a Signed Variable length int.
     * Favourable for numbers {@code <= -1}
     *
     * @param i The int.
     * @return The same stream.
     */
    default FriendlyByteBuf cc$writeSignedVarInt(int i) {
        SignedVarInt.write(self(), i);
        return self();
    }

    /**
     * Reads a Variable length signed int.
     *
     * @return The int.
     * @see CCFriendlyByteBufExtension#cc$writeSignedVarInt
     */
    default int cc$readSignedVarInt() {
        return SignedVarInt.read(self());
    }

    /**
     * Writes a Signed Variable length long.
     * Favourable for numbers {@code <= -1}
     *
     * @param i The long.
     * @return The same stream.
     */
    default FriendlyByteBuf cc$writeSignedVarLong(long i) {
        SignedVarLong.write(self(), i);
        return self();
    }

    /**
     * Reads a Variable length signed long.
     *
     * @return The long.
     * @see CCFriendlyByteBufExtension#cc$writeSignedVarLong
     */
    default long cc$readSignedVarLong() {
        return SignedVarLong.read(self());
    }
    // endregion

    /**
     * Write to the stream with a {@link StreamEncoder} / {@link StreamCodec}.
     *
     * @param codec The codec.
     * @param thing The thing to write.
     * @return The same stream.
     */
    default <T> FriendlyByteBuf cc$writeWithCodec(StreamEncoder<? super FriendlyByteBuf, ? super T> codec, T thing) {
        codec.encode(self(), thing);
        return self();
    }

    /**
     * Read from the stream with a {@link StreamDecoder} / {@link StreamCodec}.
     *
     * @param codec The codec.
     * @return The thing that was read.
     */
    default <T> T cc$readWithCodec(StreamDecoder<? super FriendlyByteBuf, ? extends T> codec) {
        return codec.decode(self());
    }

    /**
     * Write a {@link Vector3} to the stream.
     *
     * @param vec The vector.
     * @return The same stream.
     */
    default FriendlyByteBuf writeVector3(Vector3 vec) {
        return cc$writeWithCodec(Vector3.STREAM_CODEC, vec);
    }

    /**
     * Read a {@link Vector3} from the stream.
     *
     * @return The vector.
     */
    default Vector3 readVector3() {
        return cc$readWithCodec(Vector3.STREAM_CODEC);
    }

    /**
     * Write a {@link Cuboid6} to the stream.
     *
     * @param cuboid The cuboid.
     * @return The same stream.
     */
    default FriendlyByteBuf writeCuboid6(Cuboid6 cuboid) {
        return cc$writeWithCodec(Cuboid6.STREAM_CODEC, cuboid);
    }

    /**
     * Read a {@link Cuboid6} from the stream.
     *
     * @return The cuboid.
     */
    default Cuboid6 readCuboid6() {
        return cc$readWithCodec(Cuboid6.STREAM_CODEC);
    }
}
