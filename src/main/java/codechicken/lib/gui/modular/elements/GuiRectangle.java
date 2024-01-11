package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Used to draw a simple rectangle on the screen.
 * Can specify separate (or no) border colours and fill colours.
 * Can also render using the "shadedRectangle" render type.
 * <p>
 * Created by brandon3055 on 28/08/2023
 */
public class GuiRectangle extends GuiElement<GuiRectangle> implements BackgroundRender {
    private Supplier<Integer> fill = null;
    private Supplier<Integer> border = null;

    private Supplier<Double> borderWidth = () -> 1D;

    private Supplier<Integer> shadeTopLeft;
    private Supplier<Integer> shadeBottomRight;
    private Supplier<Integer> shadeCorners;

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiRectangle(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    /**
     * Creates a rectangle that mimics the appearance of a vanilla inventory slot.
     * Uses shadedRect to create the 3D "inset" look.
     */
    public static GuiRectangle vanillaSlot(@NotNull GuiParent<?> parent) {
        return new GuiRectangle(parent).shadedRect(0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b);
    }

    /**
     * Creates a rectangle that mimics the appearance of a vanilla inventory slot, except inverted
     * Uses shadedRect to create the 3D "popped out" appearance
     */
    public static GuiRectangle invertedSlot(@NotNull GuiParent<?> parent) {
        return new GuiRectangle(parent).shadedRect(0xFFffffff, 0xFF373737, 0xFF8b8b8b, 0xFF8b8b8b);
    }

    /**
     * Creates a rectangle similar in appearance to a vanilla button, but with no texture and no black border.
     */
    public static GuiRectangle planeButton(@NotNull GuiParent<?> parent) {
        return new GuiRectangle(parent).shadedRect(0xFFaaaaaa, 0xFF545454, 0xFF6f6f6f);
    }

    public static GuiRectangle toolTipBackground(@NotNull GuiParent<?> parent) {
        return toolTipBackground(parent, 0xF0100010, 0x505000FF, 0x5028007f);
    }

    public static GuiRectangle toolTipBackground(@NotNull GuiParent<?> parent, int backgroundColour, int borderColourTop, int borderColourBottom) {
        return toolTipBackground(parent, backgroundColour, backgroundColour, borderColourTop, borderColourBottom);
    }

    public static GuiRectangle toolTipBackground(@NotNull GuiParent<?> parent, int backgroundColourTop, int backgroundColourBottom, int borderColourTop, int borderColourBottom) {
        return new GuiRectangle(parent) {
            @Override
            public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
                render.toolTipBackground(xMin(), yMin(), xSize(), ySize(), backgroundColourTop, backgroundColourBottom, borderColourTop, borderColourBottom, false);
            }
        };
    }

    public GuiRectangle border(int border) {
        return border(() -> border);
    }

    public GuiRectangle border(Supplier<Integer> border) {
        this.border = border;
        return this;
    }

    public GuiRectangle fill(int fill) {
        return fill(() -> fill);
    }

    public GuiRectangle fill(Supplier<Integer> fill) {
        this.fill = fill;
        return this;
    }

    public GuiRectangle rectangle(int fill, int border) {
        return rectangle(() -> fill, () -> border);
    }

    public GuiRectangle rectangle(Supplier<Integer> fill, Supplier<Integer> border) {
        this.fill = fill;
        this.border = border;
        return this;
    }

    public GuiRectangle shadedRect(int topLeft, int bottomRight, int fill) {
        return shadedRect(() -> topLeft, () -> bottomRight, () -> fill);
    }

    public GuiRectangle shadedRect(Supplier<Integer> topLeft, Supplier<Integer> bottomRight, Supplier<Integer> fill) {
        return shadedRect(topLeft, bottomRight, () -> GuiRender.midColour(topLeft.get(), bottomRight.get()), fill);
    }

    public GuiRectangle shadedRect(int topLeft, int bottomRight, int cornerMix, int fill) {
        return shadedRect(() -> topLeft, () -> bottomRight, () -> cornerMix, () -> fill);
    }

    public GuiRectangle shadedRect(Supplier<Integer> topLeft, Supplier<Integer> bottomRight, Supplier<Integer> cornerMix, Supplier<Integer> fill) {
        this.fill = fill;
        this.shadeTopLeft = topLeft;
        this.shadeBottomRight = bottomRight;
        this.shadeCorners = cornerMix;
        return this;
    }

    public GuiRectangle setShadeTopLeft(Supplier<Integer> shadeTopLeft) {
        this.shadeTopLeft = shadeTopLeft;
        return this;
    }

    public GuiRectangle setShadeBottomRight(Supplier<Integer> shadeBottomRight) {
        this.shadeBottomRight = shadeBottomRight;
        return this;
    }

    public GuiRectangle setShadeCorners(Supplier<Integer> shadeCorners) {
        this.shadeCorners = shadeCorners;
        return this;
    }

    public GuiRectangle setShadeCornersAuto() {
        this.shadeCorners = () -> GuiRender.midColour(shadeTopLeft.get(), shadeBottomRight.get());
        return this;
    }

    public GuiRectangle borderWidth(double borderWidth) {
        return borderWidth(() -> borderWidth);
    }

    public GuiRectangle borderWidth(Supplier<Double> borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public double getBorderWidth() {
        return borderWidth.get();
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        if (shadeTopLeft != null && shadeBottomRight != null && shadeCorners != null) {
            render.shadedRect(getRectangle(), getBorderWidth(), shadeTopLeft.get(), shadeBottomRight.get(), shadeCorners.get(), fill == null ? 0 : fill.get());
        } else if (border != null) {
            render.borderRect(getRectangle(), getBorderWidth(), fill == null ? 0 : fill.get(), border.get());
        } else if (fill != null) {
            render.rect(getRectangle(), fill.get());
        }
    }
}
