package codechicken.lib.model.bakery;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.uv.UV;
import codechicken.lib.render.uv.UVRotation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLLog;

import java.util.LinkedList;

import static codechicken.lib.math.MathHelper.torad;

/**
 * Created by covers1624 on 8/3/2016.
 */
public class BlockModelBakery {

    public boolean flipTexture;
    public boolean renderFromInside = false;

    public double renderMinX;
    public double renderMaxX;
    public double renderMinY;
    public double renderMaxY;
    public double renderMinZ;
    public double renderMaxZ;

    public Colour colourTopLeft = new ColourRGBA(255, 255, 255, 255);
    public Colour colourBottomLeft = new ColourRGBA(255, 255, 255, 255);
    public Colour colourBottomRight = new ColourRGBA(255, 255, 255, 255);
    public Colour colourTopRight = new ColourRGBA(255, 255, 255, 255);

    private UVRotation rotation = new UVRotation(0 * torad);

    private LinkedList<BakedQuad> quadList = new LinkedList<BakedQuad>();
    private VertexFormat format = DefaultVertexFormats.BLOCK;

    public void setVertexFormat(VertexFormat format) {
        FMLLog.info("" + rotation.isRedundant());
        this.format = format;
    }

    public LinkedList<BakedQuad> getBakedQuads() {
        return new LinkedList<BakedQuad>(quadList);
    }

    public void setRotationAngle(int i) {
        rotation = new UVRotation((i * 90) * torad);
    }

    public void resetRotationAngle() {
        rotation = new UVRotation(0 * torad);
    }

    public void reset() {
        quadList.clear();
    }

    public void setRenderBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {

        this.renderMinX = minX;
        this.renderMaxX = maxX;
        this.renderMinY = minY;
        this.renderMaxY = maxY;
        this.renderMinZ = minZ;
        this.renderMaxZ = maxZ;

    }

    public void bakeFaceYNeg(double posX, double posY, double posZ, TextureAtlasSprite sprite) {
        EnumFacing face = renderFromInside ? EnumFacing.UP : EnumFacing.DOWN;
        CCQuadBakery quadBakery = new CCQuadBakery(format, sprite, face);

        double d3 = (double) sprite.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) sprite.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double) sprite.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double) sprite.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double) sprite.getMinU();
            d4 = (double) sprite.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d5 = (double) sprite.getMinV();
            d6 = (double) sprite.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        double d11 = posX + this.renderMinX;
        double d12 = posX + this.renderMaxX;
        double d13 = posY + this.renderMinY;
        double d14 = posZ + this.renderMinZ;
        double d15 = posZ + this.renderMaxZ;

        if (this.renderFromInside) {
            d11 = posX + this.renderMaxX;
            d12 = posX + this.renderMinX;
        }

        UV uv1 = new UV(d8, d10);
        UV uv2 = new UV(d3, d5);
        UV uv3 = new UV(d7, d9);
        UV uv4 = new UV(d4, d6);
        if (!rotation.isRedundant()) {
            rotation.apply(uv1);
            rotation.apply(uv2);
            rotation.apply(uv3);
            rotation.apply(uv4);
        }

        quadBakery.putVertex(d11, d13, d15, uv1, colourTopLeft);
        quadBakery.putVertex(d11, d13, d14, uv2, colourBottomLeft);
        quadBakery.putVertex(d12, d13, d14, uv3, colourBottomRight);
        quadBakery.putVertex(d12, d13, d15, uv4, colourTopRight);
        quadList.add(quadBakery.bake());
    }

    public void bakeFaceYPos(double posX, double posY, double posZ, TextureAtlasSprite sprite) {
        EnumFacing face = renderFromInside ? EnumFacing.DOWN : EnumFacing.UP;
        CCQuadBakery quadBakery = new CCQuadBakery(format, sprite, face);

        double d3 = (double) sprite.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) sprite.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double) sprite.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double) sprite.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double) sprite.getMinU();
            d4 = (double) sprite.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d5 = (double) sprite.getMinV();
            d6 = (double) sprite.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        double d11 = posX + this.renderMinX;
        double d12 = posX + this.renderMaxX;
        double d13 = posY + this.renderMaxY;
        double d14 = posZ + this.renderMinZ;
        double d15 = posZ + this.renderMaxZ;

        if (this.renderFromInside) {
            d11 = posX + this.renderMaxX;
            d12 = posX + this.renderMinX;
        }

        UV uv1 = new UV(d4, d6);
        UV uv2 = new UV(d7, d9);
        UV uv3 = new UV(d3, d5);
        UV uv4 = new UV(d8, d10);
        if (!rotation.isRedundant()) {
            rotation.apply(uv1);
            rotation.apply(uv2);
            rotation.apply(uv3);
            rotation.apply(uv4);
        }

        quadBakery.putVertex(d12, d13, d15, uv1, colourTopLeft);
        quadBakery.putVertex(d12, d13, d14, uv2, colourBottomLeft);
        quadBakery.putVertex(d11, d13, d14, uv3, colourBottomRight);
        quadBakery.putVertex(d11, d13, d15, uv4, colourTopRight);
        quadList.add(quadBakery.bake());
    }

    public void bakeFaceZNeg(double posX, double posY, double posZ, TextureAtlasSprite sprite) {
        EnumFacing face = renderFromInside ? EnumFacing.SOUTH : EnumFacing.NORTH;
        CCQuadBakery quadBakery = new CCQuadBakery(format, sprite, face);

        double d3 = (double) sprite.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) sprite.getInterpolatedU(this.renderMaxX * 16.0D);

        double d5 = (double) sprite.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) sprite.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double) sprite.getMinU();
            d4 = (double) sprite.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double) sprite.getMinV();
            d6 = (double) sprite.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        double d11 = posX + this.renderMinX;
        double d12 = posX + this.renderMaxX;
        double d13 = posY + this.renderMinY;
        double d14 = posY + this.renderMaxY;
        double d15 = posZ + this.renderMinZ;

        if (this.renderFromInside) {
            d11 = posX + this.renderMaxX;
            d12 = posX + this.renderMinX;
        }

        UV uv1 = new UV(d7, d9);
        UV uv2 = new UV(d3, d5);
        UV uv3 = new UV(d8, d10);
        UV uv4 = new UV(d4, d6);
        if (!rotation.isRedundant()) {
            rotation.apply(uv1);
            rotation.apply(uv2);
            rotation.apply(uv3);
            rotation.apply(uv4);
        }

        quadBakery.putVertex(d11, d14, d15, uv1, colourTopLeft);
        quadBakery.putVertex(d12, d14, d15, uv2, colourBottomLeft);
        quadBakery.putVertex(d12, d13, d15, uv3, colourBottomRight);
        quadBakery.putVertex(d11, d13, d15, uv4, colourTopRight);
        quadList.add(quadBakery.bake());
    }

    public void bakeFaceZPos(double posX, double posY, double posZ, TextureAtlasSprite sprite) {
        EnumFacing face = renderFromInside ? EnumFacing.NORTH : EnumFacing.SOUTH;
        CCQuadBakery quadBakery = new CCQuadBakery(format, sprite, face);

        double d3 = (double) sprite.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) sprite.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double) sprite.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) sprite.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            d3 = (double) sprite.getMinU();
            d4 = (double) sprite.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double) sprite.getMinV();
            d6 = (double) sprite.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        double d11 = posX + this.renderMinX;
        double d12 = posX + this.renderMaxX;
        double d13 = posY + this.renderMinY;
        double d14 = posY + this.renderMaxY;
        double d15 = posZ + this.renderMaxZ;

        if (this.renderFromInside) {
            d11 = posX + this.renderMaxX;
            d12 = posX + this.renderMinX;
        }

        UV uv1 = new UV(d3, d5);
        UV uv2 = new UV(d8, d10);
        UV uv3 = new UV(d4, d6);
        UV uv4 = new UV(d7, d9);
        if (!rotation.isRedundant()) {
            rotation.apply(uv1);
            rotation.apply(uv2);
            rotation.apply(uv3);
            rotation.apply(uv4);
        }

        quadBakery.putVertex(d11, d14, d15, uv1, colourTopLeft);
        quadBakery.putVertex(d11, d13, d15, uv2, colourBottomLeft);
        quadBakery.putVertex(d12, d13, d15, uv3, colourBottomRight);
        quadBakery.putVertex(d12, d14, d15, uv4, colourTopRight);
        quadList.add(quadBakery.bake());
    }

    public void bakeFaceXNeg(double posX, double posY, double posZ, TextureAtlasSprite sprite) {
        EnumFacing face = renderFromInside ? EnumFacing.EAST : EnumFacing.WEST;
        CCQuadBakery quadBakery = new CCQuadBakery(format, sprite, face);

        double d3 = (double) sprite.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double) sprite.getInterpolatedU(this.renderMaxZ * 16.0D);
        double d5 = (double) sprite.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) sprite.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d3 = (double) sprite.getMinU();
            d4 = (double) sprite.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double) sprite.getMinV();
            d6 = (double) sprite.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        double d11 = posX + this.renderMinX;
        double d12 = posY + this.renderMinY;
        double d13 = posY + this.renderMaxY;
        double d14 = posZ + this.renderMinZ;
        double d15 = posZ + this.renderMaxZ;

        if (this.renderFromInside) {
            d14 = posZ + this.renderMaxZ;
            d15 = posZ + this.renderMinZ;
        }

        UV uv1 = new UV(d7, d9);
        UV uv2 = new UV(d3, d5);
        UV uv3 = new UV(d8, d10);
        UV uv4 = new UV(d4, d6);
        if (!rotation.isRedundant()) {
            rotation.apply(uv1);
            rotation.apply(uv2);
            rotation.apply(uv3);
            rotation.apply(uv4);
        }

        quadBakery.putVertex(d11, d13, d15, uv1, colourTopLeft);
        quadBakery.putVertex(d11, d13, d14, uv2, colourBottomLeft);
        quadBakery.putVertex(d11, d12, d14, uv3, colourBottomRight);
        quadBakery.putVertex(d11, d12, d15, uv4, colourTopRight);
        quadList.add(quadBakery.bake());
    }

    public void bakeFaceXPos(double posX, double posY, double posZ, TextureAtlasSprite sprite) {
        EnumFacing face = renderFromInside ? EnumFacing.WEST : EnumFacing.EAST;
        CCQuadBakery quadBakery = new CCQuadBakery(format, sprite, face);

        double d3 = (double) sprite.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double) sprite.getInterpolatedU(this.renderMaxZ * 16.0D);

        double d5 = (double) sprite.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) sprite.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture) {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            d3 = (double) sprite.getMinU();
            d4 = (double) sprite.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
            d5 = (double) sprite.getMinV();
            d6 = (double) sprite.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        double d11 = posX + this.renderMaxX;
        double d12 = posY + this.renderMinY;
        double d13 = posY + this.renderMaxY;
        double d14 = posZ + this.renderMinZ;
        double d15 = posZ + this.renderMaxZ;

        if (this.renderFromInside) {
            d14 = posZ + this.renderMaxZ;
            d15 = posZ + this.renderMinZ;
        }

        UV uv1 = new UV(d8, d10);
        UV uv2 = new UV(d4, d6);
        UV uv3 = new UV(d7, d9);
        UV uv4 = new UV(d3, d5);
        if (!rotation.isRedundant()) {
            rotation.apply(uv1);
            rotation.apply(uv2);
            rotation.apply(uv3);
            rotation.apply(uv4);
        }

        quadBakery.putVertex(d11, d12, d15, uv1, colourTopLeft);
        quadBakery.putVertex(d11, d12, d14, uv2, colourBottomLeft);
        quadBakery.putVertex(d11, d13, d14, uv3, colourBottomRight);
        quadBakery.putVertex(d11, d13, d15, uv4, colourTopRight);
        quadList.add(quadBakery.bake());
    }
}
