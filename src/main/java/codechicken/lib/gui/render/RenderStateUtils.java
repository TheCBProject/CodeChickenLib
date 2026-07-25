package codechicken.lib.gui.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3x2f;

/**
 * Created by covers1624 on 4/11/26.
 */
public class RenderStateUtils {

    public static void fill(VertexConsumer consumer, Matrix3x2f pose, double xMin, double yMin, double xMax, double yMax, int color) {
        if (xMax < xMin) {
            double min = xMax;
            xMax = xMin;
            xMin = min;
        }
        if (yMax < yMin) {
            double min = yMax;
            yMax = yMin;
            yMin = min;
        }

        consumer.addVertexWith2DPose(pose, (float) xMax, (float) yMax).setColor(color); //R-B
        consumer.addVertexWith2DPose(pose, (float) xMax, (float) yMin).setColor(color); //R-T
        consumer.addVertexWith2DPose(pose, (float) xMin, (float) yMin).setColor(color); //L-T
        consumer.addVertexWith2DPose(pose, (float) xMin, (float) yMax).setColor(color); //L-B
    }

    public static void fillGradientH(VertexConsumer consumer, Matrix3x2f pose, double x0, double x1, double y0, double y1, int col1, int col2) {
        consumer.addVertexWith2DPose(pose, (float) x1, (float) y1).setColor(col1);
        consumer.addVertexWith2DPose(pose, (float) x0, (float) y0).setColor(col1);
        consumer.addVertexWith2DPose(pose, (float) x1, (float) y0).setColor(col2);
        consumer.addVertexWith2DPose(pose, (float) x0, (float) y1).setColor(col2);
    }

    public static void fillGradientV(VertexConsumer consumer, Matrix3x2f pose, double x0, double x1, double y0, double y1, int col1, int col2) {
        consumer.addVertexWith2DPose(pose, (float) x1, (float) y1).setColor(col1);
        consumer.addVertexWith2DPose(pose, (float) x0, (float) y0).setColor(col2);
        consumer.addVertexWith2DPose(pose, (float) x1, (float) y0).setColor(col2);
        consumer.addVertexWith2DPose(pose, (float) x0, (float) y1).setColor(col1);
    }

    public static void borderFill(VertexConsumer consumer, Matrix3x2f pose, double x0, double x1, double y0, double y1, double border, int borderColor, int fillColor) {
        RenderStateUtils.fill(consumer, pose, x0, y0, x1, y0 + border, borderColor);                               //Top
        RenderStateUtils.fill(consumer, pose, x0, y0 + border, x0 + border, y1 - border, borderColor);             //Left
        RenderStateUtils.fill(consumer, pose, x0, y1 - border, x1, y1, borderColor);                               //Bottom
        RenderStateUtils.fill(consumer, pose, x1 - border, y0 + border, x1, y1 - border, borderColor);             //Right

        //No point rendering fill if there is no fill color
        if (fillColor != 0) {
            RenderStateUtils.fill(consumer, pose, x0 + border, y0 + border, x1 - border, y1 - border, fillColor); //Fill
        }
    }

    public static void shadedFill(VertexConsumer consumer, Matrix3x2f pose, double x0, double x1, double y0, double y1, double border, int topLeftColor, int bottomRightColor, int cornerMixColor, int fillColor) {
        fill(consumer, pose, x0, y0, x1 - border, y0 + border, topLeftColor);                     //Top
        fill(consumer, pose, x0, y0 + border, x0 + border, y1 - border, topLeftColor);            //Left
        fill(consumer, pose, x0 + border, -border, x1, y1, bottomRightColor);                   //Bottom
        fill(consumer, pose, x1 - border, y0 + border, x1, y1 - border, bottomRightColor);        //Right
        fill(consumer, pose, x1 - border, y0, x1, y0 + border, cornerMixColor);                   //Top Right Corner
        fill(consumer, pose, x0, y1 - border, x0 + border, y1, cornerMixColor);                   //Bottom Left Corner

        //No point rendering fill if there is no fill color
        if (fillColor != 0) {
            fill(consumer, pose, x0 + border, y0 + border, x1 - border, y1 - border, fillColor);  //Fill
        }
    }

    public static void blit(VertexConsumer consumer, Matrix3x2f pose, double x0, double x1, double y0, double y1, int rotation, float u0, float u1, float v0, float v1, int color) {
        float[] u = { u1, u0, u0, u1 };
        float[] v = { v0, v0, v1, v1 };
        consumer.addVertexWith2DPose(pose, (float) x0, (float) y0).setUv(u[(1 + rotation) % 4], v[(1 + rotation) % 4]).setColor(color);
        consumer.addVertexWith2DPose(pose, (float) x0, (float) y1).setUv(u[(2 + rotation) % 4], v[(2 + rotation) % 4]).setColor(color);
        consumer.addVertexWith2DPose(pose, (float) x1, (float) y1).setUv(u[(3 + rotation) % 4], v[(3 + rotation) % 4]).setColor(color);
        consumer.addVertexWith2DPose(pose, (float) x1, (float) y0).setUv(u[(0 + rotation) % 4], v[(0 + rotation) % 4]).setColor(color);
    }

    public static void blitDynamic(VertexConsumer consumer, Matrix3x2f pose, TextureAtlasSprite sprite, int xPos, int yPos, int xSize, int ySize, int topBorder, int leftBorder, int bottomBorder, int rightBorder, int color) {
        SpriteContents contents = sprite.contents();
        int texWidth = contents.width();
        int texHeight = contents.height();
        int trimWidth = texWidth - leftBorder - rightBorder;
        int trimHeight = texHeight - topBorder - bottomBorder;
        if (xSize <= texWidth) trimWidth = Math.min(trimWidth, xSize - rightBorder);
        if (xSize <= 0 || ySize <= 0 || trimWidth <= 0 || trimHeight <= 0) return;

        for (int x = 0; x < xSize; ) {
            int rWidth = Math.min(xSize - x, trimWidth);
            int trimU = 0;
            if (x != 0) {
                if (x + leftBorder + trimWidth <= xSize) {
                    trimU = leftBorder;
                } else {
                    trimU = (texWidth - (xSize - x));
                }
            }

            //Top & Bottom trim
            emitQuad(consumer, pose, sprite, xPos + x, yPos, trimU, 0, rWidth, topBorder, color);
            emitQuad(consumer, pose, sprite, xPos + x, yPos + ySize - bottomBorder, trimU, texHeight - bottomBorder, rWidth, bottomBorder, color);

            rWidth = Math.min(xSize - x - leftBorder - rightBorder, trimWidth);
            for (int y = 0; y < ySize; ) {
                int rHeight = Math.min(ySize - y - topBorder - bottomBorder, trimHeight);
                int trimV;
                if (y + (texHeight - topBorder - bottomBorder) <= ySize) {
                    trimV = topBorder;
                } else {
                    trimV = texHeight - (ySize - y);
                }

                //Left & Right trim
                if (x == 0 && y + topBorder < ySize - bottomBorder) {
                    emitQuad(consumer, pose, sprite, xPos, yPos + y + topBorder, 0, trimV, leftBorder, rHeight, color);
                    emitQuad(consumer, pose, sprite, xPos + xSize - rightBorder, yPos + y + topBorder, trimU + texWidth - rightBorder, trimV, rightBorder, rHeight, color);
                }

                //Core
                if (y + topBorder < ySize - bottomBorder && x + leftBorder < xSize - rightBorder) {
                    emitQuad(consumer, pose, sprite, xPos + x + leftBorder, yPos + y + topBorder, leftBorder, topBorder, rWidth, rHeight, color);
                }
                y += trimHeight;
            }
            x += trimWidth;
        }
    }

    public static void emitQuad(VertexConsumer builder, Matrix3x2f pose, TextureAtlasSprite sprite, int x, int y, float textureX, float textureY, int width, int height, int color) {
        int w = sprite.contents().width();
        int h = sprite.contents().height();

        //@formatter:off
        builder.addVertexWith2DPose(pose, x,         y + height).setUv(sprite.getU(textureX / w),           sprite.getV((textureY + height) / h)).setColor(color);
        builder.addVertexWith2DPose(pose, x + width, y + height).setUv(sprite.getU((textureX + width) / w), sprite.getV((textureY + height) / h)).setColor(color);
        builder.addVertexWith2DPose(pose, x + width, y         ).setUv(sprite.getU((textureX + width) / w), sprite.getV((textureY / h))         ).setColor(color);
        builder.addVertexWith2DPose(pose, x,         y         ).setUv(sprite.getU(textureX / w),           sprite.getV((textureY / h))         ).setColor(color);
        //@formatter:on
    }
}
