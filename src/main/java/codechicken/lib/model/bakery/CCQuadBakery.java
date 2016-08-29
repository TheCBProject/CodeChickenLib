package codechicken.lib.model.bakery;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.Vertex5;
import codechicken.lib.render.uv.UV;
import codechicken.lib.util.ArrayUtils;
import codechicken.lib.util.Copyable;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by covers1624 on 8/2/2016.
 * Bakes Quads..
 */
public class CCQuadBakery {

    private VertexFormat format;
    private TextureAtlasSprite sprite;
    private EnumFacing face;

    //Temp storage
    private UV uv = new UV();
    private Vector3 normal = null;
    private Colour colour = new ColourRGBA(0xFFFFFFFF);
    private UV lightMap = new UV();

    private boolean isBakingTriModel = false;
    private boolean applyDifuseLighting = true;
    private LinkedList<BakedQuad> bakedQuads = null;

    //Active storage.
    private CCQuad quad = null;
    private int index = 0;

    public CCQuadBakery(TextureAtlasSprite sprite) {
        this(DefaultVertexFormats.BLOCK, sprite);
    }

    public CCQuadBakery(VertexFormat format, TextureAtlasSprite sprite) {
        this.format = format;
        this.sprite = sprite;
    }

    public CCQuadBakery startBakingQuads() {
        return startBaking(false);
    }

    public CCQuadBakery startBakingTriangles() {
        return startBaking(true);
    }

    //TODO Have the QuadBakery bake from any DrawMode to any DrawMode and from any VertexFormat to any VertexFormat.
    //TODO Maybe a custom BakedQuad that holds all the raw info still, might make it more possible / cleaner for a VF > VF converter.
    private CCQuadBakery startBaking(boolean isTriangles) {
        if (quad != null || bakedQuads != null) {
            throw new IllegalStateException("Quads are still baking or baking has not finished yet!");
        }
        isBakingTriModel = isTriangles;
        bakedQuads = new LinkedList<BakedQuad>();
        return this;
    }

    public ImmutableList<BakedQuad> finishBaking() {
        if (quad != null) {
            throw new IllegalStateException("Quads are still baking!");
        }
        if (bakedQuads == null) {
            throw new IllegalStateException("The bakery has no baked quads!");
        }
        ImmutableList<BakedQuad> returnQuads = ImmutableList.copyOf(bakedQuads);
        reset();
        return returnQuads;
    }

    public void reset() {
        applyDifuseLighting = true;
        isBakingTriModel = false;
        bakedQuads = null;
        quad = null;
        index = 0;
    }

    public CCQuadBakery setSprite(TextureAtlasSprite sprite) {
        if (quad != null) {
            throw new IllegalStateException("Unable to set sprite whilst quad is still baking!");
        }
        this.sprite = sprite;
        return this;
    }

    public CCQuadBakery setFace(EnumFacing face) {
        if (quad != null) {
            throw new IllegalStateException("Unable to set face whilst quad is still baking!");
        }
        this.face = face;
        return this;
    }

    public CCQuadBakery disableDifuseLighting() {
        return setDifuseLightingState(false);
    }

    public CCQuadBakery setDifuseLightingState(boolean state) {
        applyDifuseLighting = state;
        return this;
    }

    public CCQuadBakery setColour(int colour) {
        return setColour(new ColourRGBA(colour));
    }

    public CCQuadBakery setColour(Colour colour) {
        this.colour = colour.copy();
        return this;
    }

    public CCQuadBakery setLightMap(int brightness) {
        return setLightMap(new UV((double) ((brightness >> 4) & 15 * 32) / 65535, (double) ((brightness >> 20) & 15 * 32) / 65535));
    }

    public CCQuadBakery setLightMap(UV lightMap) {
        this.lightMap = lightMap.copy();
        return this;
    }

    public CCQuadBakery setNormal(Vector3 normal) {
        this.normal = normal;
        return this;
    }

    public CCQuadBakery setUV(double u, double v) {
        return setUV(new UV(u, v));
    }

    public CCQuadBakery setUV(UV uv) {
        this.uv = uv.copy();
        return this;
    }

    public CCQuadBakery addVertexWithUV(Vector3 vertex, double u, double v) {
        return addVertexWithUV(vertex, new UV(u, v));
    }

    public CCQuadBakery addVertexWithUV(double x, double y, double z, UV uv) {
        return addVertexWithUV(new Vector3(x, y, z), uv);
    }

    public CCQuadBakery addVertexWithUV(double x, double y, double z, double u, double v) {
        return addVertexWithUV(new Vector3(x, y, z), new UV(u, v));
    }

    public CCQuadBakery addVertexWithUV(Vector3 vertex, UV uv) {
        return addVertexWithUV(new Vertex5(vertex, uv));
    }

    public CCQuadBakery addVertexWithUV(Vertex5 vertex) {
        setUV(vertex.uv);
        return addVertex(vertex.vec);
    }

    public CCQuadBakery addVertex(double x, double y, double z) {
        return addVertex(new Vector3(x, y, z));
    }

    public CCQuadBakery addVertex(Vector3 vertex) {
        if (quad == null) {
            quad = new CCQuad();
            if (face != null) {
                quad.face = face;
            }
            quad.sprite = sprite;
            quad.applyDifuseLighting = applyDifuseLighting;
            index = 0;
        }
        quad.vertices[index] = new Vertex5(vertex.copy(), uv.copy());
        quad.normals[index] = normal != null ? normal.copy() : null;
        quad.colours[index] = colour.copy();
        quad.lightMaps[index] = lightMap.copy();
        index++;

        int max = isBakingTriModel ? 3 : 4;

        if (index == max) {
            index = 0;
            bakedQuads.add(quad.bake(format));
            quad = null;
        }
        return this;
    }

    /**
     * Created by covers1624 on 8/20/2016.
     * Basically just a holder for quads before baking.
     * TODO Allow this to accept the transform system.
     */
    public static class CCQuad implements Copyable<CCQuad> {
        public Vertex5[] vertices = new Vertex5[4];
        public Vector3[] normals = new Vector3[4];
        public Colour[] colours = new Colour[4];
        public UV[] lightMaps = new UV[4];

        public EnumFacing face = null;
        public boolean applyDifuseLighting = true;
        public TextureAtlasSprite sprite;

        public CCQuad() {
        }

        public CCQuad(Vertex5... vertices) {
            if (vertices.length > 4) {
                throw new IllegalArgumentException("CCQuad is a... Quad.. only 3 or 4 vertices allowed!");
            }
            for (int i = 0; i < 4; i++) {
                this.vertices[i] = vertices[i].copy();
            }
        }

        public CCQuad(BakedQuad quad) {
            this();
            VertexFormat format = quad.getFormat();

            //[vertex][element][data]
            float[][][] vertexData = new float[4][format.getElementCount()][4];
            for (int v = 0; v < 4; v++) {
                for (int e = 0; e < format.getElementCount(); e++) {
                    LightUtil.unpack(quad.getVertexData(), vertexData[v][e], format, v, e);
                }
            }
            face = quad.getFace();
            Arrays.fill(vertices, new Vertex5());
            for (int v = 0; v < 4; v++) {
                for (int e = 0; e < format.getElementCount(); e++) {
                    float[] data = vertexData[v][e];
                    switch (format.getElement(e).getUsage()) {
                        case POSITION:
                            vertices[v].vec.set(data[0], data[1], data[2]);
                            break;
                        case NORMAL:
                            normals[v] = new Vector3(data[0], data[1], data[2]);
                            break;
                        case COLOR:
                            colours[v] = new ColourRGBA(data[0], data[1], data[2], data[3]);
                            break;
                        case UV:
                            if (format.getElement(e).getIndex() == 0) {
                                vertices[v].uv.set(data[0], data[1]);
                            } else {
                                lightMaps[v] = new UV(data[0], data[1]);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            if (!format.hasColor()) {
                ArrayUtils.fillArray(colours, new ColourRGBA(0xFFFFFFFF));
            }
            if (!format.hasUvOffset(1)) {
                ArrayUtils.fillArray(lightMaps, new UV());
            }
            if (!format.hasNormal()) {
                computeNormals();
            }
        }

        public CCQuad(CCQuad quad) {
            this();
            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = quad.vertices[i].copy();
            }
            for (int i = 0; i < vertices.length; i++) {
                normals[i] = quad.normals[i].copy();
            }
            for (int i = 0; i < vertices.length; i++) {
                colours[i] = quad.colours[i].copy();
            }
            for (int i = 0; i < vertices.length; i++) {
                lightMaps[i] = quad.lightMaps[i].copy();
            }
            face = quad.face;
        }

        public boolean isQuads() {
            int counter = ArrayUtils.countNoNull(vertices);
            return counter == 4;
        }

        /**
         * Quadulates the quad by copying any element at index 2 to index 3 only if there are 3 of any given element.
         */
        public void quadulate() {
            int verticesCount = ArrayUtils.countNoNull(vertices);
            int normalCount = ArrayUtils.countNoNull(normals);
            int colourCount = ArrayUtils.countNoNull(colours);
            int lightMapCount = ArrayUtils.countNoNull(lightMaps);
            if (verticesCount == 3) {
                vertices[3] = vertices[2].copy();
            }
            if (normalCount == 3) {
                normals[3] = normals[2].copy();
            }
            if (colourCount == 3) {
                colours[3] = colours[2].copy();
            }
            if (lightMapCount == 3) {
                lightMaps[3] = lightMaps[2].copy();
            }
        }

        /**
         * Creates a set of normals for the quad.
         * Will attempt to Quadulate the model first.
         */
        public void computeNormals() {
            quadulate();
            Vector3 diff1 = vertices[1].vec.copy().subtract(vertices[0].vec);
            Vector3 diff2 = vertices[3].vec.copy().subtract(vertices[0].vec);
            Vector3 normal = diff1.crossProduct(diff2).normalize();

            for (int i = 0; i < 4; i++) {
                normals[i] = normal.copy();
            }
        }

        public EnumFacing getQuadFace() {
            if (face == null) {
                if (ArrayUtils.countNoNull(normals) != 4) {
                    computeNormals();
                }
                face = CCModel.calcNormalSide(normals[0]);
            }
            return face;
        }

        public BakedQuad bake() {
            return bake(DefaultVertexFormats.BLOCK);
        }

        public BakedQuad bake(VertexFormat format) {
            quadulate();
            computeNormals();
            UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(format);
            quadBuilder.setApplyDiffuseLighting(applyDifuseLighting);
            quadBuilder.setTexture(sprite);
            quadBuilder.setQuadOrientation(getQuadFace());
            for (int v = 0; v < 4; v++) {
                for (int e = 0; e < format.getElementCount(); e++) {
                    VertexFormatElement element = format.getElement(e);
                    switch (element.getUsage()) {
                        case POSITION:
                            Vector3 pos = vertices[v].vec;
                            quadBuilder.put(e, (float) pos.x, (float) pos.y, (float) pos.z, 1);
                            break;
                        case NORMAL:
                            Vector3 normal = normals[v];
                            quadBuilder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0);
                            break;
                        case COLOR:
                            Colour colour = colours[v];
                            quadBuilder.put(e, (colour.r & 0xFF) / 255, (colour.g & 0xFF) / 255, (colour.b & 0xFF) / 255, (colour.a & 0xFF) / 255);
                            break;
                        case UV:
                            UV uv = element.getIndex() == 0 ? vertices[v].uv : lightMaps[v];
                            quadBuilder.put(e, (float) uv.u, (float) uv.v, 0, 1);
                            break;
                        case PADDING:
                        case GENERIC:
                        default:
                            quadBuilder.put(e);
                    }
                }
            }
            return quadBuilder.build();
        }

        @Override
        public CCQuad copy() {
            return new CCQuad(this);
        }
    }
}
