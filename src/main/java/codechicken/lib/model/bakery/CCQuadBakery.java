package codechicken.lib.model.bakery;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.Vertex5;
import codechicken.lib.render.uv.UV;
import codechicken.lib.util.ArrayUtils;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

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

    public CCQuadBakery(TextureAtlasSprite sprite, EnumFacing face) {
        this(DefaultVertexFormats.BLOCK, sprite, face);
    }

    public CCQuadBakery(VertexFormat format, TextureAtlasSprite sprite, EnumFacing face) {
        this.format = format;
        this.sprite = sprite;
        this.face = face;
    }

    public CCQuadBakery startBakingQuads() {
        return startBaking(false);
    }

    public CCQuadBakery startBakingTriangles(){
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
        return setLightMap(new UV(brightness >> 16 & 65535, brightness & 65535));//TODO Is this the correct way?
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

    public CCQuadBakery addVertexWithUV(Vertex5 vertex){
        setUV(vertex.uv);
        return addVertex(vertex.vec);
    }

    public CCQuadBakery addVertex(double x, double y, double z) {
        return addVertex(new Vector3(x, y, z));
    }

    public CCQuadBakery addVertex(Vector3 vertex) {
        if (quad == null) {
            quad = new CCQuad();
            index = 0;
        }
        quad.vertices[index] = new Vertex5(vertex.copy(), uv.copy());
        quad.normals[index] = normal != null ? normal.copy() : null;
        quad.vertexColour[index] = colour.copy();
        quad.vertexLightMap[index] = lightMap.copy();
        index++;

        int max = isBakingTriModel ? 3 : 4;

        if (index == max) {
            index = 0;
            quad.quadulate();
            if (format.hasNormal()) {
                quad.computeNormals();
            }

            UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(format);
            quadBuilder.setApplyDiffuseLighting(applyDifuseLighting);
            quadBuilder.setTexture(sprite);
            quadBuilder.setQuadOrientation(face);
            for (int index = 0; index < 4; index++) {
                for (int e = 0; e < format.getElementCount(); e++) {
                    switch (format.getElement(e).getUsage()) {
                    case POSITION:
                        Vector3 pos = quad.vertices[index].vec;
                        quadBuilder.put(e, (float) pos.x, (float) pos.y, (float) pos.z, 1);
                        break;
                    case NORMAL:
                        Vector3 normal = quad.normals[index];
                        quadBuilder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0);
                        break;
                    case COLOR:
                        Colour colour = quad.vertexColour[index];
                        quadBuilder.put(e, (colour.r & 0xFF) / 255, (colour.g & 0xFF) / 255, (colour.b & 0xFF) / 255, (colour.a & 0xFF) / 255);
                        break;
                    case UV:
                        if (format.getElement(e).getIndex() == 0) {
                            UV uv = quad.vertices[index].uv;
                            quadBuilder.put(e, (float) uv.u, (float) uv.v, 0, 1);
                        } else {
                            UV uv = quad.vertexLightMap[index];
                            quadBuilder.put(e, (float) uv.u, (float) uv.v, 0, 1);
                        }

                        break;
                    case PADDING:
                    default:
                        quadBuilder.put(e);
                    }
                }
            }
            bakedQuads.add(quadBuilder.build());
            quad = null;
        }
        return this;
    }

    /**
     * Created by covers1624 on 8/20/2016.
     * Basically just a holder for quads before baking.
     */
    public static class CCQuad {
        public Vertex5[] vertices = new Vertex5[4];
        public Vector3[] normals = new Vector3[4];
        public Colour[] vertexColour = new Colour[4];
        public UV[] vertexLightMap = new UV[4];
        public EnumFacing face = EnumFacing.UP;

        public boolean isQuads() {
            int counter = ArrayUtils.countNoNull(vertices);
            return counter == 4;
        }

        /**
         * Quadulates the quad by copying any element at index 2 to index 3 only if there are 3 of any given element.
         */
        public void quadulate() {
            if (!isQuads()) {
                vertices[3] = vertices[2].copy();
                int normalCount = ArrayUtils.countNoNull(normals);
                int colourCount = ArrayUtils.countNoNull(vertexColour);
                int lightMapCount = ArrayUtils.countNoNull(vertexLightMap);
                if (normalCount == 3) {
                    normals[3] = normals[2].copy();
                }
                if (colourCount == 3) {
                    vertexColour[3] = vertexColour[2].copy();
                }
                if (lightMapCount == 3) {
                    vertexLightMap[3] = vertexLightMap[2].copy();
                }
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
    }
}
