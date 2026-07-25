package codechicken.lib.gui.render;

import codechicken.lib.colour.Colour;
import codechicken.lib.gui.modular.lib.geometry.Borders;
import codechicken.lib.gui.modular.lib.geometry.Rectangle;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An extension interface providing many various GUI rendering helpers.
 *
 * @see GuiGraphics
 */
// TODO document all these, they mostly came from the old GuiRender class from ModularGui, and all had documentation.
public interface GuiGraphicsExtension {

    private GuiGraphics self() {
        return (GuiGraphics) this;
    }

    default void cc$fillGradientH(Rectangle rect, int col1, int col2) {
        cc$fillGradientH(rect.x(), rect.y(), rect.xMax(), rect.yMax(), col1, col2);
    }

    default void cc$fillGradientH(double xMin, double yMin, double xMax, double yMax, int col1, int col2) {
        cc$fillGradient(xMin, yMin, xMax, yMax, col1, col2, true);
    }

    default void cc$fillGradientV(Rectangle rect, int col1, int col2) {
        cc$fillGradientV(rect.x(), rect.y(), rect.xMax(), rect.yMax(), col1, col2);
    }

    default void cc$fillGradientV(double xMin, double yMin, double xMax, double yMax, int col1, int col2) {
        cc$fillGradient(xMin, yMin, xMax, yMax, col1, col2, true);
    }

    default void cc$fill(Rectangle rect, int color) {
        cc$fill(rect.x(), rect.y(), rect.xMax(), rect.yMax(), color);
    }

    default void cc$fill(double xMin, double yMin, double xMax, double yMax, int color) {
        cc$fillGradient(xMin, yMin, xMax, yMax, color, color, false);
    }

    default void cc$fill(RenderPipeline pipeline, double xMin, double yMin, double xMax, double yMax, int color) {
        cc$fillGradient(pipeline, xMin, yMin, xMax, yMax, color, color, false);
    }

    default void cc$fillGradient(double xMin, double yMin, double xMax, double yMax, int col1, int col2, boolean hoz) {
        cc$fillGradient(RenderPipelines.GUI, xMin, yMin, xMax, yMax, col1, col2, hoz);
    }

    default void cc$fillGradient(RenderPipeline pipeline, double xMin, double yMin, double xMax, double yMax, int col1, int col2, boolean hoz) {
        cc$submitColoredRectangle(pipeline, TextureSetup.noTexture(),
                xMin, xMax,
                yMin, yMax,
                col1, col2,
                hoz
        );
    }

    private void cc$submitColoredRectangle(RenderPipeline pipeline, TextureSetup texture, double x0, double x1, double y0, double y1, int col1, int col2, boolean hoz) {
        cc$submitCustom(
                pipeline, texture,
                x0, x1,
                y0, y1,
                (consumer, pose) -> {
                    if (hoz) {
                        RenderStateUtils.fillGradientH(consumer, pose, x0, x1, y0, y1, col1, col2);
                    } else {
                        RenderStateUtils.fillGradientV(consumer, pose, x0, x1, y0, y1, col1, col2);
                    }
                }
        );
    }

    default void cc$borderRect(Rectangle rect, double border, int fillColor, int borderColor) {
        cc$borderFill(rect.x(), rect.y(), rect.xMax(), rect.yMax(), border, fillColor, borderColor);
    }

    default void cc$borderRect(double x, double y, double width, double height, double border, int fillColor, int borderColor) {
        cc$borderFill(x, y, x + width, y + height, border, fillColor, borderColor);
    }

    default void cc$borderFill(double xMin, double yMin, double xMax, double yMax, double border, int fillColor, int borderColor) {
        cc$submitBorderFill(RenderPipelines.GUI, TextureSetup.noTexture(), xMin, xMax, yMin, yMax, border, fillColor, borderColor);
    }

    private void cc$submitBorderFill(RenderPipeline pipeline, TextureSetup texture, double x0, double x1, double y0, double y1, double border, int fillColor, int borderColor) {
        cc$submitCustom(
                pipeline, texture,
                x0, x1,
                y0, y1,
                (consumer, pose) -> RenderStateUtils.borderFill(
                        consumer, pose,
                        x0, x1,
                        y0, y1,
                        border,
                        fillColor,
                        borderColor
                )
        );
    }

    default void cc$shadedRect(Rectangle rect, double border, int topLeftColor, int bottomRightColor, int fillColor) {
        cc$shadedFill(rect.x(), rect.y(), rect.xMax(), rect.yMax(), border, topLeftColor, bottomRightColor, fillColor);
    }

    default void cc$shadedRect(Rectangle rect, double border, int topLeftColor, int bottomRightColor, int cornerMixColor, int fillColor) {
        cc$shadedFill(rect.x(), rect.y(), rect.xMax(), rect.yMax(), border, topLeftColor, bottomRightColor, cornerMixColor, fillColor);
    }

    default void cc$shadedRect(double x, double y, double width, double height, double border, int topLeftColor, int bottomRightColor, int fillColor) {
        cc$shadedFill(x, y, x + width, y + height, border, topLeftColor, bottomRightColor, fillColor);
    }

    default void cc$shadedRect(double x, double y, double width, double height, double border, int topLeftColor, int bottomRightColor, int cornerMixColor, int fillColor) {
        cc$shadedFill(x, y, x + width, y + height, border, topLeftColor, bottomRightColor, cornerMixColor, fillColor);
    }

    default void cc$shadedFill(double xMin, double yMin, double xMax, double yMax, double border, int topLeftColor, int bottomRightColor, int fillColor) {
        cc$shadedFill(xMin, yMin, xMax, yMax, border, topLeftColor, bottomRightColor, Colour.mid(topLeftColor, bottomRightColor), fillColor);
    }

    default void cc$shadedFill(double xMin, double yMin, double xMax, double yMax, double border, int topLeftColor, int bottomRightColor, int cornerMixColor, int fillColor) {
        cc$submitShadedFill(RenderPipelines.GUI, TextureSetup.noTexture(),
                xMin, xMax,
                yMin, yMax,
                border,
                topLeftColor, bottomRightColor,
                cornerMixColor, fillColor
        );
    }

    private void cc$submitShadedFill(RenderPipeline pipeline, TextureSetup texture, double x0, double x1, double y0, double y1, double border, int topLeftColor, int bottomRightColor, int cornerMixColor, int fillColor) {
        cc$submitCustom(
                pipeline, texture,
                x0, x1,
                y0, y1,
                (consumer, pose) -> RenderStateUtils.shadedFill(
                        consumer, pose,
                        x0, x1,
                        y0, y1,
                        border,
                        topLeftColor, bottomRightColor,
                        cornerMixColor, fillColor
                )
        );
    }

    default void cc$tooltipBackground(double x, double y, double width, double height, int bgColorTop, int bgColorBottom, int borderColorTop, int borderColorBottom, boolean empty) {
        cc$tooltipBackground(RenderPipelines.GUI, TextureSetup.noTexture(), x, y, width, height, bgColorTop, bgColorBottom, borderColorTop, borderColorBottom, empty);
    }

    default void cc$tooltipBackground(RenderPipeline pipeline, TextureSetup texture, double x, double y, double width, double height, int bgColorTop, int bgColorBottom, int borderColorTop, int borderColorBottom, boolean empty) {
        cc$submitTooltipBackground(pipeline, texture, x, x + width, y, y + height, bgColorTop, bgColorBottom, borderColorTop, borderColorBottom, empty);
    }

    private void cc$submitTooltipBackground(RenderPipeline pipeline, TextureSetup texture, double x0, double x1, double y0, double y1, int bgColorTop, int bgColorBottom, int borderColorTop, int borderColorBottom, boolean empty) {
        cc$submitCustom(pipeline, texture, x0, x1, y0, y1, (consumer, pose) -> {
            RenderStateUtils.fill(consumer, pose, x0 + 1, y0, x1 - 1, y0 + 1, bgColorTop);                                      // Top
            RenderStateUtils.fill(consumer, pose, x0 + 1, y1 - 1, x1 - 1, y1, bgColorBottom);                                   // Bottom
            RenderStateUtils.fillGradientV(consumer, pose, x0, x0 + 1, y0 + 1, y1 - 1, bgColorTop, bgColorBottom);     // Left
            RenderStateUtils.fillGradientV(consumer, pose, x1 - 1, x1, y0 + 1, y1 - 1, bgColorTop, bgColorBottom);     // Right
            if (!empty) {
                RenderStateUtils.fillGradientV(consumer, pose, x0 + 1, x1 - 1, y0 + 1, y1 - 1, bgColorTop, bgColorBottom);   // Fill
            }
            RenderStateUtils.fillGradientV(consumer, pose, x0 + 1, x0 + 2, y0 + 1, y1 - 1, borderColorTop, borderColorBottom);         // Left Accent
            RenderStateUtils.fillGradientV(consumer, pose, x1 - 2, x1 - 1, y0 + 1, y1 - 1, borderColorTop, borderColorBottom);         // Right Accent
            RenderStateUtils.fill(consumer, pose, x0 + 2, y0 + 1, x1 - 2, y0 + 2, borderColorTop);                                      // Top Accent
            RenderStateUtils.fill(consumer, pose, x0 + 2, y1 - 2, x1 - 2, y1 - 1, borderColorBottom);                                   // Bottom Accent
        });
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, Rectangle rect) {
        cc$blitSprite(pipeline, sprite, rect, 0xFFFFFFFF);
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, Rectangle rect, int argb) {
        cc$blitSprite(pipeline, sprite, rect.x(), rect.y(), rect.width(), rect.height(), argb);
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, double x, double y, double width, double height) {
        cc$blitSprite(pipeline, sprite, x, y, width, height, 0xFFFFFFFF);
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, double x, double y, double width, double height, int argb) {
        cc$blitSprite(pipeline, sprite, 0, x, y, width, height, argb);
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int rotation, Rectangle rect) {
        cc$blitSprite(pipeline, sprite, rotation, rect, 0xFFFFFFFF);
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int rotation, Rectangle rect, int argb) {
        cc$blitSprite(pipeline, sprite, rotation, rect.x(), rect.y(), rect.width(), rect.height(), argb);
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int rotation, double x, double y, double width, double height) {
        cc$blitSprite(pipeline, sprite, rotation, x, y, width, height, 0xFFFFFFFF);
    }

    default void cc$blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int rotation, double x, double y, double width, double height, int argb) {
        cc$blit(
                pipeline, sprite.atlasLocation(),
                x, x + width,
                y, y + height,
                rotation,
                sprite.getU0(), sprite.getU1(),
                sprite.getV0(), sprite.getV1(),
                argb
        );
    }

    default void cc$blitPartialSprite(RenderPipeline pipeline, double x0, double y0, double x1, double y1, TextureAtlasSprite sprite, float uMin, float vMin, float uMax, float vMax) {
        cc$blitPartialSprite(pipeline, x0, y0, x1, y1, sprite, uMin, vMin, uMax, vMax, 0xFFFFFFFF);
    }

    default void cc$blitPartialSprite(RenderPipeline pipeline, double x0, double y0, double x1, double y1, TextureAtlasSprite sprite, float uMin, float vMin, float uMax, float vMax, int argb) {
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float ul = u1 - u0;
        float vl = v1 - v0;
        cc$blit(
                pipeline,
                sprite.atlasLocation(),
                x0, x1,
                y0, y1,
                0,
                u0 + (uMin * ul),
                u0 + (uMax * ul),
                v0 + (vMin * vl),
                v0 + (vMax * vl),
                argb
        );
    }

    private void cc$blit(RenderPipeline pipeline, Identifier atlas,
            double x0, double x1,
            double y0, double y1,
            int rotation,
            float u0, float u1,
            float v0, float v1,
            int color) {
        var texture = self().minecraft.getTextureManager().getTexture(atlas);
        cc$submitCustom(
                pipeline, TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()),
                x0, x1,
                y0, y1,
                (consumer, pose) -> RenderStateUtils.blit(
                        consumer, pose,
                        x0, x1,
                        y0, y1,
                        rotation,
                        u0, u1,
                        v0, v1,
                        color
                )
        );
    }

    default void cc$blitDynamicSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, Rectangle rect, Borders borders) {
        cc$blitDynamicSprite(pipeline, sprite, rect, borders, 0xFFFFFFFF);
    }

    default void cc$blitDynamicSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, Rectangle rect, Borders borders, int color) {
        cc$blitDynamicSprite(pipeline, sprite, (int) rect.x(), (int) rect.y(), (int) rect.width(), (int) rect.height(), (int) borders.top(), (int) borders.left(), (int) borders.bottom(), (int) borders.right(), color);
    }

    default void cc$blitDynamicSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, Rectangle rect, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        cc$blitDynamicSprite(pipeline, sprite, rect, topBorder, leftBorder, bottomBorder, rightBorder, 0xFFFFFFFF);
    }

    default void cc$blitDynamicSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, Rectangle rect, int topBorder, int leftBorder, int bottomBorder, int rightBorder, int color) {
        cc$blitDynamicSprite(pipeline, sprite, (int) rect.x(), (int) rect.y(), (int) rect.width(), (int) rect.height(), topBorder, leftBorder, bottomBorder, rightBorder, color);
    }

    default void cc$blitDynamicSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        cc$blitDynamicSprite(pipeline, sprite, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, 0xFFFFFFFF);
    }

    default void cc$blitDynamicSprite(RenderPipeline pipeline, TextureAtlasSprite sprite,
            int x, int y,
            int width, int height,
            int topBorder, int leftBorder,
            int bottomBorder, int rightBorder,
            int color) {
        var texture = self().minecraft.getTextureManager().getTexture(sprite.atlasLocation());
        cc$submitCustom(
                pipeline,
                TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()),
                x, x + width,
                y, y + height,
                (consumer, pose) -> RenderStateUtils.blitDynamic(
                        consumer, pose,
                        sprite,
                        x, y,
                        width, height,
                        topBorder, leftBorder,
                        bottomBorder, rightBorder,
                        color
                )
        );
    }

    private void cc$submitCustom(RenderPipeline pipeline, TextureSetup texture, double x0, double x1, double y0, double y1, CCCustomRenderState.VertBuilder builder) {
        var pose = self().pose();
        var scissor = self().peekScissorStack();
        var bounds = BlitRenderState.getBounds((int) x0, (int) y0, (int) x1, (int) y1, pose, scissor);
        self().submitGuiElementRenderState(new CCCustomRenderState(
                pipeline,
                texture,
                pose,
                scissor, bounds,
                builder
        ));
    }

    default void cc$drawCenteredString(Font font, String text, double x, double y, int color) {
        cc$drawCenteredString(font, text, x, y, color, true);
    }

    default void cc$drawCenteredString(Font font, String text, double x, double y, int color, boolean drawShadow) {
        cc$drawCenteredString(font, Language.getInstance().getVisualOrder(FormattedText.of(text)), x, y, color, drawShadow);
    }

    default void cc$drawCenteredString(Font font, Component text, double x, double y, int color) {
        cc$drawCenteredString(font, text, x, y, color, true);
    }

    default void cc$drawCenteredString(Font font, Component text, double x, double y, int color, boolean drawShadow) {
        cc$drawCenteredString(font, text.getVisualOrderText(), x, y, color, drawShadow);
    }

    default void cc$drawCenteredString(Font font, FormattedCharSequence text, double x, double y, int color) {
        cc$drawCenteredString(font, text, x, y, color, true);
    }

    default void cc$drawCenteredString(Font font, FormattedCharSequence text, double x, double y, int color, boolean drawShadow) {
        cc$drawString(font, text, x - (double) font.width(text) / 2, y, color, drawShadow);
    }

    default void cc$drawString(Font font, @Nullable String text, double x, double y, int color) {
        cc$drawString(font, text, x, y, color, true);
    }

    default void cc$drawString(Font font, @Nullable String text, double x, double y, int color, boolean drawShadow) {
        if (text == null) return;

        cc$drawString(font, Language.getInstance().getVisualOrder(FormattedText.of(text)), x, y, color, drawShadow);
    }

    default void cc$drawString(Font font, Component text, double x, double y, int color) {
        cc$drawString(font, text, x, y, color, true);
    }

    default void cc$drawString(Font font, Component text, double x, double y, int color, boolean drawShadow) {
        cc$drawString(font, text.getVisualOrderText(), x, y, color, drawShadow);
    }

    default void cc$drawString(Font font, FormattedCharSequence text, double x, double y, int color) {
        cc$drawString(font, text, x, y, color, true);
    }

    default void cc$drawString(Font font, FormattedCharSequence text, double x, double y, int color, boolean drawShadow) {
        self().drawString(font, text, (int) x, (int) y, color, drawShadow);
    }

    default void cc$drawScrollingString(Font font, Component text, double x, double y, double maxX, int color, boolean shadow, boolean scissor) {
        int textWidth = font.width(text);
        double width = maxX - x;
        if (textWidth > width) {
            double outside = textWidth - width;
            double anim = (double) Util.getMillis() / 1000.0;
            double e = Math.max(outside * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * anim / e)) / 2.0 + 0.5;
            double offset = Mth.lerp(f, 0.0, outside);
            if (scissor) cc$enableScissor(x, y - 1, maxX, y + font.lineHeight + 1);
            cc$drawString(font, text, x - offset, y, color, shadow);
            if (scissor) cc$disableScissor();
        } else {
            cc$drawCenteredString(font, text, (x + maxX) / 2, y, color, shadow);
        }
    }

    default void cc$textHighlight(double minX, double minY, double maxX, double maxY) {
        cc$fill(RenderPipelines.GUI_INVERT, minX, minY, maxX, maxY, -1);
        cc$fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, minX, minY, maxX, maxY, -16776961);
    }

    default void cc$enableScissor(Rectangle rect) {
        cc$enableScissor(rect.x(), rect.y(), rect.xMax(), rect.yMax());
    }

    default void cc$enableScissor(double minX, double minY, double maxX, double maxY) {
        self().enableScissor((int) minX, (int) minY, (int) maxX, (int) maxY);
    }

    /**
     * Only exists for visual symetry for cc$enableScissor.
     */
    default void cc$disableScissor() {
        self().disableScissor();
    }

    default void cc$renderItem(ItemStack stack, double x, double y, int rand) {
        self().renderItem(stack, (int) x, (int) y, rand);
    }

    default void cc$renderItem(@Nullable LivingEntity entity, ItemStack stack, double x, double y, int rand) {
        if (entity == null) {
            self().renderItem(stack, (int) x, (int) y, rand);
        } else {
            self().renderItem(entity, stack, (int) x, (int) y, rand);
        }
    }

    default void cc$renderItemDecorations(Font font, ItemStack stack, double x, double y) {
        cc$renderItemDecorations(font, stack, x, y, null);
    }

    default void cc$renderItemDecorations(Font font, ItemStack stack, double x, double y, @Nullable String text) {
        self().renderItemDecorations(font, stack, (int) x, (int) y, text);
    }
}
