package codechicken.lib.util;

import codechicken.lib.colour.Colour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;
import codechicken.lib.vec.uv.UV;
import codechicken.lib.vec.uv.UVTransformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import java.util.*;

/**
 * Created by covers1624 on 4/10/2016.
 * Utilities for anything to do with raw vertex data access.
 */
public class VertexDataUtils {

    /**
     * Gets the position for the element 'position' in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format The format.
     * @return The element position, -1 if it does not exist.
     */
    public static int getPositionElement(VertexFormat format) {
        for (int e = 0; e < format.getElementCount(); e++) {
            if (format.getElement(e).isPositionElement()) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Gets the position for the element 'normal' in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format The format.
     * @return The element position, -1 if it does not exist.
     */
    public static int getNormalElement(VertexFormat format) {
        for (int e = 0; e < format.getElementCount(); e++) {
            if (format.getElement(e).getUsage() == EnumUsage.NORMAL) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Gets the position for the element 'uv' in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format The format.
     * @return The element position, -1 if it does not exist.
     */
    public static int getUVElement(VertexFormat format) {
        for (int e = 0; e < format.getElementCount(); e++) {
            if (format.getElement(e).getUsage() == EnumUsage.UV && format.getElement(e).getIndex() == 0) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Gets the position for the element provided in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format  The format.
     * @param element THe element to get.
     * @return The element position, -1 if it does not exist.
     */
    public static int getElement(VertexFormat format, VertexFormatElement element) {
        for (int e = 0; e < format.getElementCount(); e++) {
            if (format.getElement(e).equals(element)) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Attempts to get the TextureAtlasSprite for a given UV mapping.
     * This is not threaded and will search EVERY sprite loaded in the texture map.
     * This is meant to be a last resort, where possible always try to avoid using this or have this be fired.
     * TODO Improve searching by caching value ranges somehow.
     *
     * @param textureMap The TextureMap to search.
     * @param uv         The UV mapping to find.
     * @return The TextureAtlasSprite found, returns missing icon if it hasn't been found.
     */
    public static TextureAtlasSprite getSpriteForUV(TextureMap textureMap, UV uv) {
        for (TextureAtlasSprite sprite : textureMap.mapUploadedSprites.values()) {
            if (MathHelper.between(sprite.getMinU(), uv.u, sprite.getMaxU()) && MathHelper.between(sprite.getMinV(), uv.v, sprite.getMaxV())) {
                return sprite;
            }
        }
        return textureMap.getMissingSprite();
    }

    /**
     * Copies the data from a UnpackedBakedQuad to a normal baked quad to save space in ram.
     *
     * @param quad The UnpackedBakedQuad to copy from.
     * @return The copied BakedQuad.
     */
    public static BakedQuad copyQuad(UnpackedBakedQuad quad) {
        return new BakedQuad(quad.getVertexData(), quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
    }

    public static Map<EnumFacing, List<BakedQuad>> sortFaceData(List<BakedQuad> quads) {
        Map<EnumFacing, List<BakedQuad>> faceQuadMap = new HashMap<>();
        for (BakedQuad quad : quads) {
            List<BakedQuad> faceQuads = faceQuadMap.computeIfAbsent(quad.getFace(), k -> new ArrayList<>());
            faceQuads.add(quad);
        }
        return faceQuadMap;
    }

    public static void fullyPackQuads(int[] packedData, float[][][] unpackedData, VertexFormat format) {
        for (int e = 0; e < format.getElementCount(); e++) {
            for (int v = 0; v < 4; v++) {
                LightUtil.pack(unpackedData[v][e], packedData, format, v, e);
            }
        }
    }

    public static void fullyUnPackQuads(int[] packedData, float[][][] unpackedData, VertexFormat format) {
        for (int e = 0; e < format.getElementCount(); e++) {
            for (int v = 0; v < 4; v++) {
                LightUtil.unpack(packedData, unpackedData[v][e], format, v, e);
            }
        }
    }

    public static float[][] unpackElements(int[] packed, VertexFormat format, VertexFormatElement element) {
        float[][] data = new float[4][4];
        int e = getElement(format, element);
        for (int v = 0; v < 4; v++) {
            LightUtil.unpack(packed, data[v], format, v, e);
        }
        return data;
    }

    public static BakedQuad buildQuad(VertexFormat format, TextureAtlasSprite sprite, EnumFacing face, Colour colour, UVTransformation t, Vertex5 v1, Vertex5 v2, Vertex5 v3, Vertex5 v4) {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(-1);
        builder.setQuadOrientation(face);
        builder.setTexture(sprite);

        t.apply(v1.uv);
        t.apply(v2.uv);
        t.apply(v3.uv);
        t.apply(v4.uv);
        putVertex(builder, format, face, v1, colour);
        putVertex(builder, format, face, v2, colour);
        putVertex(builder, format, face, v3, colour);
        putVertex(builder, format, face, v4, colour);

        return copyQuad(builder.build());
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, EnumFacing face, Vertex5 vert, Colour colour) {
        for (int e = 0; e < format.getElementCount(); e++) {
            VertexFormatElement element = format.getElement(e);
            switch (element.getUsage()) {

                case POSITION:
                    Vector3 vec = vert.vec;
                    builder.put(e, (float) vec.x, (float) vec.y, (float) vec.z, 1);
                    break;
                case NORMAL:
                    builder.put(e, face.getFrontOffsetX(), face.getFrontOffsetY(), face.getFrontOffsetZ(), 0);
                    break;
                case COLOR:
                    builder.put(e, (colour.r & 0xFF) / 255F, (colour.g & 0xFF) / 255F, (colour.b & 0xFF) / 255F, (colour.a & 0xFF) / 255F);
                    break;
                case UV:
                    UV uv = vert.uv;
                    builder.put(e, (float) uv.u, (float) uv.v, 0, 1);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    public static List<BakedQuad> shadeQuadFaces(BakedQuad... quads) {
        return shadeQuadFaces(Arrays.asList(quads));
    }

    public static List<BakedQuad> shadeQuadFaces(List<BakedQuad> quads) {
        LinkedList<BakedQuad> shadedQuads = new LinkedList<>();
        for (BakedQuad quad : quads) {
            int[] rawData = quad.getVertexData();
            for (int v = 0; v < 4; v++) {
                for (int e = 0; e < quad.getFormat().getElementCount(); e++) {
                    VertexFormatElement element = quad.getFormat().getElement(e);
                    if (element.getUsage() == EnumUsage.COLOR) {
                        float[] data = new float[4];
                        LightUtil.unpack(rawData, data, quad.getFormat(), v, e);

                        data = diffuseFaceLight(quad.getFace(), data);

                        LightUtil.pack(data, rawData, quad.getFormat(), v, e);
                    }
                }
            }
            shadedQuads.add(new BakedQuad(rawData, quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
        }

        return shadedQuads;
    }

    private static float[] diffuseFaceLight(EnumFacing face, float[] colour) {
        double diffuse;
        switch (face) {
            case DOWN:
                diffuse = 0.5D;
                break;
            case NORTH:
            case SOUTH:
                diffuse = 0.8D;
                break;
            case WEST:
            case EAST:
                diffuse = 0.6D;
                break;
            case UP:
            default:
                diffuse = 1.0D;
                break;
        }

        colour[0] *= diffuse;
        colour[1] *= diffuse;
        colour[2] *= diffuse;

        return colour;
    }
}
