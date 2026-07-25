package codechicken.lib.gui.modular.elements;

import codechicken.lib.colour.Colour;
import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import static java.util.Objects.requireNonNull;

/**
 * Used to draw a simple rectangle on the screen.
 * Can specify separate (or no) border colours and fill colours.
 * Can also render using the "shadedRectangle" render type.
 * <p>
 * Created by brandon3055 on 28/08/2023
 */
public class GuiRectangle extends GuiElement<GuiRectangle> implements BackgroundRender {

    private @Nullable IntSupplier fill = null;
    private @Nullable IntSupplier border = null;

    private DoubleSupplier borderWidth = () -> 1D;

    private @Nullable IntSupplier shadeTopLeft;
    private @Nullable IntSupplier shadeBottomRight;
    private @Nullable IntSupplier shadeCorners;

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiRectangle(GuiParent<?> parent) {
        super(parent);
    }

    /**
     * Creates a rectangle that mimics the appearance of a vanilla inventory slot.
     * Uses shadedRect to create the 3D "inset" look.
     */
    public static GuiRectangle vanillaSlot(GuiParent<?> parent) {
        return new GuiRectangle(parent).shadedRect(0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b);
    }

    /**
     * Creates a rectangle that mimics the appearance of a vanilla inventory slot, except inverted
     * Uses shadedRect to create the 3D "popped out" appearance
     */
    public static GuiRectangle invertedSlot(GuiParent<?> parent) {
        return new GuiRectangle(parent).shadedRect(0xFFffffff, 0xFF373737, 0xFF8b8b8b, 0xFF8b8b8b);
    }

    /**
     * Creates a rectangle similar in appearance to a vanilla button, but with no texture and no black border.
     */
    public static GuiRectangle planeButton(GuiParent<?> parent) {
        return new GuiRectangle(parent).shadedRect(0xFFaaaaaa, 0xFF545454, 0xFF6f6f6f);
    }

    public static GuiRectangle toolTipBackground(GuiParent<?> parent) {
        return toolTipBackground(parent, 0xF0100010, 0x505000FF, 0x5028007f);
    }

    public static GuiRectangle toolTipBackground(GuiParent<?> parent, int backgroundColour, int borderColourTop, int borderColourBottom) {
        return toolTipBackground(parent, backgroundColour, backgroundColour, borderColourTop, borderColourBottom);
    }

    public static GuiRectangle toolTipBackground(GuiParent<?> parent, int backgroundColourTop, int backgroundColourBottom, int borderColourTop, int borderColourBottom) {
        return new GuiRectangle(parent) {
            @Override
            public void renderBehind(GuiGraphics graphics, double mouseX, double mouseY, float partialTicks) {
                graphics.cc$tooltipBackground(xMin(), yMin(), xSize(), ySize(), backgroundColourTop, backgroundColourBottom, borderColourTop, borderColourBottom, false);
            }
        };
    }

    public GuiRectangle border(int border) {
        return border(() -> border);
    }

    public GuiRectangle border(@Nullable IntSupplier border) {
        this.border = border;
        return this;
    }

    public GuiRectangle fill(int fill) {
        return fill(() -> fill);
    }

    public GuiRectangle fill(IntSupplier fill) {
        this.fill = fill;
        return this;
    }

    public GuiRectangle rectangle(int fill, int border) {
        return rectangle(() -> fill, () -> border);
    }

    public GuiRectangle rectangle(IntSupplier fill, IntSupplier border) {
        this.fill = fill;
        this.border = border;
        return this;
    }

    public GuiRectangle shadedRect(int topLeft, int bottomRight, int fill) {
        return shadedRect(() -> topLeft, () -> bottomRight, () -> fill);
    }

    public GuiRectangle shadedRect(IntSupplier topLeft, IntSupplier bottomRight, IntSupplier fill) {
        return shadedRect(topLeft, bottomRight, () -> Colour.mid(topLeft.getAsInt(), bottomRight.getAsInt()), fill);
    }

    public GuiRectangle shadedRect(int topLeft, int bottomRight, int cornerMix, int fill) {
        return shadedRect(() -> topLeft, () -> bottomRight, () -> cornerMix, () -> fill);
    }

    public GuiRectangle shadedRect(IntSupplier topLeft, IntSupplier bottomRight, IntSupplier cornerMix, IntSupplier fill) {
        this.fill = fill;
        this.shadeTopLeft = topLeft;
        this.shadeBottomRight = bottomRight;
        this.shadeCorners = cornerMix;
        return this;
    }

    public GuiRectangle setShadeTopLeft(IntSupplier shadeTopLeft) {
        this.shadeTopLeft = shadeTopLeft;
        return this;
    }

    public GuiRectangle setShadeBottomRight(IntSupplier shadeBottomRight) {
        this.shadeBottomRight = shadeBottomRight;
        return this;
    }

    public GuiRectangle setShadeCorners(IntSupplier shadeCorners) {
        this.shadeCorners = shadeCorners;
        return this;
    }

    public GuiRectangle setShadeCornersAuto() {
        this.shadeCorners = () -> {
            var topLeft = requireNonNull(shadeTopLeft, "shadeTopLeft required for corners.").getAsInt();
            var bottomRight = requireNonNull(shadeBottomRight, "shadeBottomRight required for corners.").getAsInt();
            return Colour.mid(topLeft, bottomRight);
        };
        return this;
    }

    public GuiRectangle borderWidth(double borderWidth) {
        return borderWidth(() -> borderWidth);
    }

    public GuiRectangle borderWidth(DoubleSupplier borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public double getBorderWidth() {
        return borderWidth.getAsDouble();
    }

    @Override
    public void renderBehind(GuiGraphics render, double mouseX, double mouseY, float partialTicks) {
        if (shadeTopLeft != null && shadeBottomRight != null && shadeCorners != null) {
            render.cc$shadedRect(getRectangle(), getBorderWidth(), shadeTopLeft.getAsInt(), shadeBottomRight.getAsInt(), shadeCorners.getAsInt(), fill == null ? 0 : fill.getAsInt());
        } else if (border != null) {
            render.cc$borderRect(getRectangle(), getBorderWidth(), fill == null ? 0 : fill.getAsInt(), border.getAsInt());
        } else if (fill != null) {
            render.cc$fill(getRectangle(), fill.getAsInt());
        }
    }
}
