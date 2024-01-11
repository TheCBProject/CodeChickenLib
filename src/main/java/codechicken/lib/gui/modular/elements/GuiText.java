package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.ForegroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.Align;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.lib.geometry.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Align.MAX;
import static codechicken.lib.gui.modular.lib.geometry.Align.MIN;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.dynamic;

/**
 * Created by brandon3055 on 31/08/2023
 */
public class GuiText extends GuiElement<GuiText> implements ForegroundRender {
    private Supplier<Component> text;
    private Supplier<Boolean> shadow = () -> true;
    private Supplier<Integer> textColour = () -> 0xFFFFFFFF;
    private Supplier<Double> rotation = null;
    private Position rotatePoint = Position.create(() -> xSize() / 2, () -> ySize() / 2);
    private boolean trim = false;
    private boolean wrap = false;
    private boolean scroll = true;
    private Align alignment = Align.CENTER;
    //TODO, Arbitrary rotation is fun, But may want to switch to Axis, with option for "reverse"

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiText(@NotNull GuiParent<?> parent) {
        this(parent, () -> null);
    }

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiText(@NotNull GuiParent<?> parent, @Nullable Component text) {
        this(parent, () -> text);
    }

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiText(@NotNull GuiParent<?> parent, @NotNull Supplier<@Nullable Component> text) {
        super(parent);
        this.text = text;
    }

    /**
     * Apply a dynamic height constraint that sets the height based on text height (accounting for wrapping)
     */
    public GuiText autoHeight() {
        constrain(GeoParam.HEIGHT, dynamic(() -> wrap ? (double) font().wordWrapHeight(getText(), (int) xSize()) : font().lineHeight));
        return this;
    }

    public GuiText setTextSupplier(@NotNull Supplier<@Nullable Component> textSupplier) {
        this.text = textSupplier;
        return this;
    }

    public GuiText setText(@Nullable Component text) {
        this.text = () -> text;
        return this;
    }

    public GuiText setText(@NotNull String text) {
        this.text = () -> Component.literal(text);
        return this;
    }

    public GuiText setTranslatable(@NotNull String translationKey) {
        this.text = () -> Component.translatable(translationKey);
        return this;
    }

    @Nullable
    public Component getText() {
        return text.get();
    }

    public GuiText setAlignment(Align alignment) {
        this.alignment = alignment;
        return this;
    }

    public Align getAlignment() {
        return alignment;
    }

    /**
     * If set to true the text will be trimmed if it is too long to fit within the bounds on the element.
     * Default disabled.
     * Setting this to true will automatically disable wrap and scroll ether are enabled.
     */
    public GuiText setTrim(boolean trim) {
        this.trim = trim;
        if (trim) wrap = scroll = false;
        return this;
    }

    public boolean getTrim() {
        return trim;
    }

    /**
     * Set to true the text will be wrapped (rendered as multiple lines of text) if it is too long to fit within the size of the element.
     * Default disabled.
     * Setting this to true will automatically disable trim and scroll ether are enabled.
     */
    public GuiText setWrap(boolean wrap) {
        this.wrap = wrap;
        if (wrap) trim = scroll = false;
        return this;
    }

    public boolean getWrap() {
        return wrap;
    }

    /**
     * Set to true the text scroll using the same logic as vanillas widgets if the text is too long to fit within the elements bounds
     * Default enabled.
     * Setting this to true will automatically disable trim and wrap ether are enabled.
     */
    public GuiText setScroll(boolean scroll) {
        this.scroll = scroll;
        if (scroll) trim = wrap = false;
        return this;
    }

    public boolean getScroll() {
        return scroll;
    }

    public GuiText setShadow(@NotNull Supplier<Boolean> shadow) {
        this.shadow = shadow;
        return this;
    }

    public GuiText setShadow(boolean shadow) {
        this.shadow = () -> shadow;
        return this;
    }

    public boolean getShadow() {
        return shadow.get();
    }

    public GuiText setTextColour(Supplier<Integer> textColour) {
        this.textColour = textColour;
        return this;
    }

    public GuiText setTextColour(int textColour) {
        this.textColour = () -> textColour;
        return this;
    }

    public int getTextColour() {
        return textColour.get();
    }

    /**
     * Set rotation angle in degrees
     * //TODO, Does not work when text scroll is used
     */
    public GuiText setRotation(double rotation) {
        return setRotation(() -> rotation);
    }

    /**
     * Dynamic rotation control, Set to null to disable rotation.
     * Rotation angle is in degrees
     * //TODO, Does not work when text scroll is used
     */
    public GuiText setRotation(@Nullable Supplier<Double> rotation) {
        this.rotation = rotation;
        return this;
    }

    /**
     * Sets the point around which the text rotates relative to the top left of the element.
     * Default rotation point is a dynamic point set to the dead center of the element.
     */
    public GuiText setRotatePoint(Position rotatePoint) {
        this.rotatePoint = rotatePoint;
        return this;
    }

    @Override
    public double getForegroundDepth() {
        return 0.035;
    }

    @Override
    public void renderForeground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        Component component = getText();
        if (component == null) return;
        Font font = render.font();

        int textHeight = font.lineHeight;
        int textWidth = font.width(component);
        boolean tooLong = textWidth > xSize();
        double yPos = (yMin() + ySize() / 2 - textHeight / 2D) + 1; //Adding 1 here makes the text look 'visually' centered, Text height includes the height of the optional underline.

        PoseStack stack = render.pose();
        if (rotation != null) {
            stack.pushPose();
            stack.translate(xMin() + rotatePoint.x(), yMin() + rotatePoint.y(), 0);
            stack.mulPose(Axis.ZP.rotationDegrees(rotation.get().floatValue()));
            stack.translate(-xMin() - rotatePoint.x(), -yMin() - rotatePoint.y(), 0);
        }

        //Draw Trimmed
        if (tooLong && getTrim()) {
            Component tail = Component.literal("...").setStyle(getText().getStyle());
            FormattedText head = font.getSplitter().headByWidth(component, (int) xSize() - font.width(tail), getText().getStyle());
            FormattedCharSequence formatted = Language.getInstance().getVisualOrder(FormattedText.composite(head, tail));
            textWidth = font.width(formatted);

            double xPos = alignment == MIN ? xMin() : alignment == MAX ? xMax() - textWidth : xMin() + xSize() / 2 - textWidth / 2D;
            render.drawString(formatted, xPos, yPos, getTextColour(), getShadow());
        }
        //Draw Wrapped
        else if (tooLong && wrap) {
            textHeight = font.wordWrapHeight(component, (int) xSize());
            List<FormattedCharSequence> list = font.split(component, (int) xSize());

            yPos = yMin() + ySize() / 2 - textHeight / 2D;
            for (FormattedCharSequence line : list) {
                int lineWidth = font.width(line);
                double xPos = alignment == MIN ? xMin() : alignment == MAX ? xMax() - lineWidth : xMin() + xSize() / 2 - lineWidth / 2D;
                render.drawString(line, xPos, yPos, getTextColour(), getShadow());
                yPos += font.lineHeight;
            }
        }
        //Draw Scrolling
        else if (tooLong && scroll) {
            render.pushScissorRect(getRectangle());
            render.drawScrollingString(component, xMin(), yPos, xMax(), getTextColour(), getShadow(), false);
            render.popScissor();
        }
        //Draw
        else {
            double xPos = alignment == MIN ? xMin() : alignment == MAX ? xMax() - textWidth : xMin() + xSize() / 2 - textWidth / 2D;
            render.drawString(component, xPos, yPos, getTextColour(), getShadow());
        }

        if (rotation != null) {
            stack.popPose();
        }
    }
}