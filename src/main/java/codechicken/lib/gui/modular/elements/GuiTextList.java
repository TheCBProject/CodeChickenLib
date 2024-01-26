package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.ForegroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.Align;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Align.MAX;
import static codechicken.lib.gui.modular.lib.geometry.Align.MIN;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.dynamic;

/**
 * Created by brandon3055 on 10/10/2023
 */
public class GuiTextList extends GuiElement<GuiTextList> implements ForegroundRender {
    private Supplier<List<? extends Component>> text;
    private Supplier<Boolean> shadow = () -> true;
    private Supplier<Integer> textColour = () -> 0xFFFFFFFF;
    private boolean scroll = true;
    private Align horizontalAlign = Align.CENTER;
    private Align verticalAlign = Align.TOP;
    private int lineSpacing = 0;

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiTextList(@NotNull GuiParent<?> parent) {
        this(parent, () -> null);
    }

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiTextList(@NotNull GuiParent<?> parent, List<? extends Component> text) {
        this(parent, () -> text);
    }

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiTextList(@NotNull GuiParent<?> parent, @NotNull Supplier<List<? extends Component>> text) {
        super(parent);
        this.text = text;
    }

    /**
     * Apply a dynamic height constraint that sets the height based on text height (accounting for wrapping)
     */
    public GuiTextList autoHeight() {
        constrain(GeoParam.HEIGHT, dynamic(() -> (double) ((font().lineHeight + lineSpacing) * getText().size()) - 1));
        return this;
    }

    public GuiTextList setTextSupplier(@NotNull Supplier<List<? extends Component>> textSupplier) {
        this.text = textSupplier;
        return this;
    }

    public GuiTextList setText(List<? extends Component> text) {
        this.text = () -> text;
        return this;
    }

    public List<? extends Component> getText() {
        return text.get();
    }

    public GuiTextList setHorizontalAlign(Align horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
        return this;
    }

    public GuiTextList setVerticalAlign(Align verticalAlign) {
        this.verticalAlign = verticalAlign;
        return this;
    }

    public Align getHorizontalAlign() {
        return horizontalAlign;
    }

    public Align getVerticalAlign() {
        return verticalAlign;
    }

    public GuiTextList setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
        return this;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    /**
     * Set to true the text scroll using the same logic as vanillas widgets if the text is too long to fit within the elements bounds
     * Default enabled.
     * Setting this to true will automatically disable trim and wrap ether are enabled.
     */
    public GuiTextList setScroll(boolean scroll) {
        this.scroll = scroll;
        return this;
    }

    public boolean getScroll() {
        return scroll;
    }

    public GuiTextList setShadow(@NotNull Supplier<Boolean> shadow) {
        this.shadow = shadow;
        return this;
    }

    public GuiTextList setShadow(boolean shadow) {
        this.shadow = () -> shadow;
        return this;
    }

    public boolean getShadow() {
        return shadow.get();
    }

    public GuiTextList setTextColour(Supplier<Integer> textColour) {
        this.textColour = textColour;
        return this;
    }

    public GuiTextList setTextColour(int textColour) {
        this.textColour = () -> textColour;
        return this;
    }

    public int getTextColour() {
        return textColour.get();
    }

    @Override
    public double getForegroundDepth() {
        return 0.035;
    }

    @Override
    public void renderForeground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        List<? extends Component> list = getText();
        if (list.isEmpty()) return;
        Font font = render.font();

        double height = (list.size() * (font.lineHeight + lineSpacing)) - lineSpacing;
        double yPos = verticalAlign == MIN ? yMin() : verticalAlign == MAX ? yMax() - height : (yCenter() - (height / 2)) + 1;
        for (Component line : list) {
            int textWidth = font.width(line);
            boolean tooLong = textWidth > xSize();

            if (tooLong && scroll) {
                render.pushScissorRect(getRectangle());
                render.drawScrollingString(line, xMin(), yPos, xMax(), getTextColour(), getShadow(), false);
                render.popScissor();
            } else {
                double xPos = horizontalAlign == MIN ? xMin() : horizontalAlign == MAX ? xMax() - textWidth : xMin() + xSize() / 2 - textWidth / 2D;
                render.drawString(line, xPos, yPos, getTextColour(), getShadow());
            }

            yPos += font.lineHeight + lineSpacing;
        }
    }
}