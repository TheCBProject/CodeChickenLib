package codechicken.lib.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;
import net.minecraft.network.codec.StreamCodec;

/**
 * Created by covers1624 on 10/31/25.
 */
public final class CCStreamCodecs {

    public static final StreamCodec<ByteBuf, byte[]> BYTE_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                buf.writeBytes(arr);
            },
            buf -> {
                int len = VarInt.read(buf);
                byte[] arr = new byte[len];
                buf.readBytes(arr);
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, char[]> CHAR_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (char v : arr) {
                    buf.writeChar(v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                char[] arr = new char[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = buf.readChar();
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, short[]> SHORT_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (short v : arr) {
                    buf.writeShort(v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                short[] arr = new short[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = buf.readShort();
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, int[]> INT_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (int v : arr) {
                    buf.writeInt(v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                int[] arr = new int[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = buf.readInt();
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, int[]> VAR_INT_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (int v : arr) {
                    VarInt.write(buf, v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                int[] arr = new int[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = VarInt.read(buf);
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, int[]> SIGNED_VAR_INT_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (int v : arr) {
                    SignedVarInt.write(buf, v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                int[] arr = new int[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = SignedVarInt.read(buf);
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, long[]> LONG_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (long v : arr) {
                    buf.writeLong(v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                long[] arr = new long[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = buf.readLong();
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, long[]> VAR_LONG_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (long v : arr) {
                    VarLong.write(buf, v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                long[] arr = new long[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = VarLong.read(buf);
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, long[]> SIGNED_VAR_LONG_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (long v : arr) {
                    SignedVarLong.write(buf, v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                long[] arr = new long[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = SignedVarLong.read(buf);
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, float[]> FLOAT_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (float v : arr) {
                    buf.writeFloat(v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                float[] arr = new float[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = buf.readFloat();
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, double[]> DOUBLE_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (double v : arr) {
                    buf.writeDouble(v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                double[] arr = new double[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = buf.readDouble();
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, boolean[]> BOOLEAN_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (boolean v : arr) {
                    buf.writeBoolean(v);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                boolean[] arr = new boolean[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = buf.readBoolean();
                }
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, String[]> STRING_ARRAY = StreamCodec.of(
            (buf, arr) -> {
                VarInt.write(buf, arr.length);
                for (String v : arr) {
                    Utf8String.write(buf, v, Short.MAX_VALUE);
                }
            },
            buf -> {
                int len = VarInt.read(buf);
                String[] arr = new String[len];
                for (int i = 0; i < len; i++) {
                    arr[i] = Utf8String.read(buf, Short.MAX_VALUE);
                }
                return arr;
            }
    );
}
