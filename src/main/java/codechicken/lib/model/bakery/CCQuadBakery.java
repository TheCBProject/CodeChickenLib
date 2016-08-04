package codechicken.lib.model.bakery;

import codechicken.lib.colour.Colour;
import codechicken.lib.render.Vertex5;
import codechicken.lib.render.uv.UV;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.common.FMLLog;

import java.util.LinkedList;

/**
 * Created by covers1624 on 8/2/2016.
 *///TODO Support adding more than one quad and baking in to an array instead.

public class CCQuadBakery {

    //TODO Change these to arrays.
    private LinkedList<Vertex5> vertices = new LinkedList<Vertex5>();
    private LinkedList<Vector3> normals = new LinkedList<Vector3>();
    private LinkedList<Colour> colours = new LinkedList<Colour>();
    private LinkedList<UV> lightMaps = new LinkedList<UV>();

    private VertexFormat format;
    private TextureAtlasSprite sprite;
    private EnumFacing face;

    public CCQuadBakery(TextureAtlasSprite sprite, EnumFacing face) {
        this(DefaultVertexFormats.BLOCK, sprite, face);
    }

    public CCQuadBakery(VertexFormat format, TextureAtlasSprite sprite, EnumFacing face) {
        this.format = format;
        this.sprite = sprite;
        this.face = face;
    }

    public CCQuadBakery putVertex(double xPos, double yPos, double zPos, UV uv, Colour colour) {
        return putVertex(new Vector3(xPos, yPos, zPos), uv, colour);
    }

    public CCQuadBakery putVertex(double xPos, double yPos, double zPos, double u, double v, Colour colour) {
        return putVertex(new Vector3(xPos, yPos, zPos), new UV(u, v), colour);
    }

    public CCQuadBakery putVertex(Vector3 pos, UV uv, Colour colour) {
        return putVertex(new Vertex5(pos, uv), null, colour, null);
    }

    public CCQuadBakery putVertex(Vector3 pos, UV uv, Vector3 normal, Colour colour) {
        return putVertex(new Vertex5(pos, uv), normal, colour, null);
    }

    public CCQuadBakery putVertex(Vector3 pos, UV uv, Vector3 normal, Colour colour, UV lightMap) {
        return putVertex(new Vertex5(pos, uv), normal, colour, lightMap);
    }

    public CCQuadBakery putVertex(Vertex5 vertex, Vector3 normal, Colour colour, UV lightMap) {
        if (vertices.size() == 4) {
            throw new IllegalArgumentException("Unable to add 5 vertices to a quad!");
        }
        vertices.add(vertex);
        if (normal != null) {
            normals.add(normal);
        }
        if (colour != null) {
            colours.add(colour);
        }
        if (lightMap != null) {
            lightMaps.add(lightMap);
        }
        return this;
    }

    private void quadulate() {
        if (vertices.size() == 3) {
            if (normals.size() != 0 && normals.size() != 3) {
                throw new IllegalArgumentException("Unable to quadulate triangle model as not all normals exist!");
            }
            vertices.add(vertices.get(2));
            if (normals.size() != 0) {
                normals.add(normals.get(2));
            }
        }
    }

    @Deprecated
    public CCQuadBakery interpolateUVs() {
        LinkedList<Vertex5> verts = new LinkedList<Vertex5>(vertices);
        vertices = new LinkedList<Vertex5>();
        for (Vertex5 vert : verts) {
            vert.uv.u = sprite.getInterpolatedU(vert.uv.u);
            vert.uv.v = sprite.getInterpolatedV(vert.uv.v);
            vertices.add(vert);
        }
        return this;
    }

    private void fill() {
        if (colours.size() == 1) {
            Colour colour = colours.get(0);
            for (int i = 1; i < 4; i++) {
                colours.add(colour);
            }
        }

        if (lightMaps.size() == 1) {
            UV lightMap = lightMaps.get(0);
            for (int i = 1; i < 4; i++) {
                lightMaps.add(lightMap);
            }
        }
    }

    private void computeNormals() {
        if (normals.size() == 0) {
            if (vertices.size() == 3) {
                quadulate();
            }
            Vector3 diff1 = vertices.get(1).vec.copy().subtract(vertices.get(0).vec);
            Vector3 diff2 = vertices.get(3).vec.copy().subtract(vertices.get(0).vec);
            Vector3 normal = diff1.crossProduct(diff2).normalize();

            normals = new LinkedList<Vector3>();
            normals.add(normal.copy());
            normals.add(normal.copy());
            normals.add(normal.copy());
            normals.add(normal.copy());
        }
    }

    public BakedQuad bake() {
        quadulate();
        fill();
        if (format.hasNormal()) {
            computeNormals();
        }

        UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(format);
        quadBuilder.setTexture(sprite);
        quadBuilder.setQuadOrientation(face);
        for (int i = 0; i < vertices.size(); i++) {
            for (int e = 0; e < format.getElementCount(); e++) {
                switch (format.getElement(e).getUsage()) {
                case POSITION:
                    Vector3 pos = vertices.get(i).vec;
                    quadBuilder.put(e, (float) pos.x, (float) pos.y, (float) pos.z);
                    break;
                case NORMAL:
                    Vector3 normal = normals.get(i);
                    quadBuilder.put(e, (float) normal.x, (float) normal.y, (float) normal.z);
                    break;
                case COLOR:
                    Colour colour = colours.get(i);
                    quadBuilder.put(e, (colour.r & 0xFF) / 255, (colour.g & 0xFF) / 255, (colour.b & 0xFF) / 255, (colour.a & 0xFF) / 255);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        UV uv = vertices.get(i).uv;
                        quadBuilder.put(e, (float) uv.u, (float) uv.v);
                    } else {
                        if (lightMaps.size() == 0) {
                            quadBuilder.put(e);
                        } else {
                            UV uv = lightMaps.get(i);
                            quadBuilder.put(e, (float) uv.u, (float) uv.v);
                        }
                    }

                    break;
                case PADDING:
                default:
                    quadBuilder.put(e);
                }
            }
        }

        return quadBuilder.build();
    }

}
