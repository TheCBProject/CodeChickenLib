package codechicken.lib.model;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.util.VertexDataUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vertex5;
import codechicken.lib.vec.uv.IconTransformation;
import codechicken.lib.vec.uv.UVTransformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

/**
 * Created by covers1624 on 26/10/2016.
 */
public class PlanarFaceBakery {

    public static BakedQuad bakeFace(EnumFacing face, TextureAtlasSprite sprite) {
        return bakeFace(face, sprite, DefaultVertexFormats.ITEM);
    }

    public static BakedQuad bakeFace(EnumFacing face, TextureAtlasSprite sprite, VertexFormat format) {
        return bakeFace(face, sprite, format, 0xFFFFFFFF);
    }

    public static BakedQuad bakeFace(EnumFacing face, TextureAtlasSprite sprite, VertexFormat format, int colour) {
        return bakeFace(face, sprite, format, new ColourRGBA(colour));
    }

    public static BakedQuad bakeFace(EnumFacing face, TextureAtlasSprite sprite, VertexFormat format, Colour colour) {
        UVTransformation t = new IconTransformation(sprite);

        double x1 = Cuboid6.full.min.x;
        double x2 = Cuboid6.full.max.x;
        double y1 = Cuboid6.full.min.y;
        double y2 = Cuboid6.full.max.y;
        double z1 = Cuboid6.full.min.z;
        double z2 = Cuboid6.full.max.z;
        double u1;
        double u2;
        double v1;
        double v2;
        Vertex5 vert1;
        Vertex5 vert2;
        Vertex5 vert3;
        Vertex5 vert4;

        switch (face) {
            case DOWN:
                u1 = x1;
                v1 = z1;
                u2 = x2;
                v2 = z2;
                vert1 = new Vertex5(x1, y1, z2, u1, v2);
                vert2 = new Vertex5(x1, y1, z1, u1, v1);
                vert3 = new Vertex5(x2, y1, z1, u2, v1);
                vert4 = new Vertex5(x2, y1, z2, u2, v2);
                return VertexDataUtils.buildQuad(format, sprite, face, colour, t, vert1, vert2, vert3, vert4);
            case UP:
                u1 = x1;
                v1 = z1;
                u2 = x2;
                v2 = z2;
                vert1 = new Vertex5(x2, y2, z2, u2, v2);
                vert2 = new Vertex5(x2, y2, z1, u2, v1);
                vert3 = new Vertex5(x1, y2, z1, u1, v1);
                vert4 = new Vertex5(x1, y2, z2, u1, v2);
                return VertexDataUtils.buildQuad(format, sprite, face, colour, t, vert1, vert2, vert3, vert4);
            case NORTH:
                u1 = 1 - x1;
                v1 = 1 - y2;
                u2 = 1 - x2;
                v2 = 1 - y1;
                vert1 = new Vertex5(x1, y1, z1, u1, v2);
                vert2 = new Vertex5(x1, y2, z1, u1, v1);
                vert3 = new Vertex5(x2, y2, z1, u2, v1);
                vert4 = new Vertex5(x2, y1, z1, u2, v2);
                return VertexDataUtils.buildQuad(format, sprite, face, colour, t, vert1, vert2, vert3, vert4);
            case SOUTH:
                u1 = x1;
                v1 = 1 - y2;
                u2 = x2;
                v2 = 1 - y1;
                vert1 = new Vertex5(x2, y1, z2, u2, v2);
                vert2 = new Vertex5(x2, y2, z2, u2, v1);
                vert3 = new Vertex5(x1, y2, z2, u1, v1);
                vert4 = new Vertex5(x1, y1, z2, u1, v2);
                return VertexDataUtils.buildQuad(format, sprite, face, colour, t, vert1, vert2, vert3, vert4);
            case WEST:
                u1 = z1;
                v1 = 1 - y2;
                u2 = z2;
                v2 = 1 - y1;
                vert1 = new Vertex5(x1, y1, z2, u2, v2);
                vert2 = new Vertex5(x1, y2, z2, u2, v1);
                vert3 = new Vertex5(x1, y2, z1, u1, v1);
                vert4 = new Vertex5(x1, y1, z1, u1, v2);
                return VertexDataUtils.buildQuad(format, sprite, face, colour, t, vert1, vert2, vert3, vert4);

            case EAST:
                u1 = 1 - z1;
                v1 = 1 - y2;
                u2 = 1 - z2;
                v2 = 1 - y1;
                vert1 = new Vertex5(x2, y1, z1, u1, v2);
                vert2 = new Vertex5(x2, y2, z1, u1, v1);
                vert3 = new Vertex5(x2, y2, z2, u2, v1);
                vert4 = new Vertex5(x2, y1, z2, u2, v2);
                return VertexDataUtils.buildQuad(format, sprite, face, colour, t, vert1, vert2, vert3, vert4);
        }
        //This case will never happen. only here due to INTELLIJ NOT SHUTTING UP ABOUT POTENTIAL NULLPOINTERS!
        return new BakedQuad(null, 1, null, null, true, null);
    }
}
