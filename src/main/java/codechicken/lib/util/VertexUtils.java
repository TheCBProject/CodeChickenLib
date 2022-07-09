package codechicken.lib.util;

import codechicken.lib.model.IVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by covers1624 on 9/7/22.
 */
public class VertexUtils {

    private static final ConcurrentMap<Pair<VertexFormat, VertexFormat>, int[]> formatMaps = new ConcurrentHashMap<>();

    private static final int[] DEFAULT_MAPPING = generateMapping(DefaultVertexFormat.BLOCK, DefaultVertexFormat.BLOCK);

    public static int[] mapFormats(VertexFormat from, VertexFormat to) {
        if (from.equals(DefaultVertexFormat.BLOCK) && to.equals(DefaultVertexFormat.BLOCK)) return DEFAULT_MAPPING;

        return formatMaps.computeIfAbsent(Pair.of(from, to), pair -> generateMapping(pair.getLeft(), pair.getRight()));
    }

    public static void putQuad(IVertexConsumer consumer, BakedQuad quad) {
        consumer.setTexture(quad.getSprite());
        consumer.setQuadOrientation(quad.getDirection());
        if (quad.isTinted()) {
            consumer.setQuadTint(quad.getTintIndex());
        }
        consumer.setApplyDiffuseLighting(quad.isShade());
        float[] data = new float[4];
        VertexFormat formatFrom = consumer.getVertexFormat();
        VertexFormat formatTo = DefaultVertexFormat.BLOCK;
        int countFrom = formatFrom.getElements().size();
        int countTo = formatTo.getElements().size();
        int[] eMap = mapFormats(formatFrom, formatTo);
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < countFrom; e++) {
                if (eMap[e] != countTo) {
                    unpack(quad.getVertices(), data, formatTo, v, eMap[e]);
                    consumer.put(e, data);
                } else {
                    consumer.put(e);
                }
            }
        }
    }

    public static void unpack(int[] from, float[] to, VertexFormat formatFrom, int v, int e) {
        int length = Math.min(4, to.length);
        VertexFormatElement element = formatFrom.getElements().get(e);
        int vertexStart = v * formatFrom.getVertexSize() + formatFrom.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.Type type = element.getType();
        VertexFormatElement.Usage usage = element.getUsage();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < length; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = from[index];
                bits = bits >>> (offset * 8);
                if ((pos + size - 1) / 4 != index) {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= mask;
                if (type == VertexFormatElement.Type.FLOAT) {
                    to[i] = Float.intBitsToFloat(bits);
                } else if (type == VertexFormatElement.Type.UBYTE || type == VertexFormatElement.Type.USHORT) {
                    to[i] = (float) bits / mask;
                } else if (type == VertexFormatElement.Type.UINT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / 0xFFFFFFFFL);
                } else if (type == VertexFormatElement.Type.BYTE) {
                    to[i] = ((float) (byte) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.Type.SHORT) {
                    to[i] = ((float) (short) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.Type.INT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / (0xFFFFFFFFL >> 1));
                }
            } else {
                to[i] = (i == 3 && usage == VertexFormatElement.Usage.POSITION) ? 1 : 0;
            }
        }
    }

    public static void pack(float[] from, int[] to, VertexFormat formatTo, int v, int e) {
        VertexFormatElement element = formatTo.getElements().get(e);
        int vertexStart = v * formatTo.getVertexSize() + formatTo.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.Type type = element.getType();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < 4; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = 0;
                float f = i < from.length ? from[i] : 0;
                if (type == VertexFormatElement.Type.FLOAT) {
                    bits = Float.floatToRawIntBits(f);
                } else if (
                        type == VertexFormatElement.Type.UBYTE ||
                                type == VertexFormatElement.Type.USHORT ||
                                type == VertexFormatElement.Type.UINT
                ) {
                    bits = Math.round(f * mask);
                } else {
                    bits = Math.round(f * (mask >> 1));
                }
                to[index] &= ~(mask << (offset * 8));
                to[index] |= (((bits & mask) << (offset * 8)));
                // TODO handle overflow into to[index + 1]
            }
        }
    }

    private static int[] generateMapping(VertexFormat from, VertexFormat to) {
        int fromCount = from.getElements().size();
        int toCount = to.getElements().size();
        int[] eMap = new int[fromCount];

        for (int e = 0; e < fromCount; e++) {
            VertexFormatElement expected = from.getElements().get(e);
            int e2;
            for (e2 = 0; e2 < toCount; e2++) {
                VertexFormatElement current = to.getElements().get(e2);
                if (expected.getUsage() == current.getUsage() && expected.getIndex() == current.getIndex()) {
                    break;
                }
            }
            eMap[e] = e2;
        }
        return eMap;
    }
}
