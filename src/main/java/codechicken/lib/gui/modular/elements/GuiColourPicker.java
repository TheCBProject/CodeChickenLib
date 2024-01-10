package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.*;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 17/11/2023
 */
public class GuiColourPicker extends GuiManipulable {

    private ColourState colourState = ColourState.create();
    private GuiButton okButton;
    private GuiButton cancelButton;

    public GuiColourPicker(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    public GuiColourPicker setColourState(ColourState colourState) {
        this.colourState = colourState;
        return this;
    }

    public ColourState getState() {
        return colourState;
    }

    public SliderState sliderStateAlpha() {
        return SliderState.forSlider(() -> (double) colourState.alpha(), e -> colourState.setAlpha(e.floatValue()), () -> -1D / (Screen.hasShiftDown() ? 16 : 64));
    }

    public SliderState sliderStateRed() {
        return SliderState.forSlider(() -> (double) colourState.red(), e -> colourState.setRed(e.floatValue()), () -> -1D / (Screen.hasShiftDown() ? 16 : 64));
    }

    public SliderState sliderStateGreen() {
        return SliderState.forSlider(() -> (double) colourState.green(), e -> colourState.setGreen(e.floatValue()), () -> -1D / (Screen.hasShiftDown() ? 16 : 64));
    }

    public SliderState sliderStateBlue() {
        return SliderState.forSlider(() -> (double) colourState.blue(), e -> colourState.setBlue(e.floatValue()), () -> -1D / (Screen.hasShiftDown() ? 16 : 64));
    }

    public TextState getTextState() {
        return TextState.create(colourState::getHexColour, colourState::setHexColour);
    }

    public GuiButton getOkButton() {
        return okButton;
    }

    /**
     * If cancel button is disabled, ok button will automatically resize.
     * */
    public GuiButton getCancelButton() {
        return cancelButton;
    }

    public static GuiColourPicker create(GuiParent<?> guiParent, ColourState colourState) {
        return create(guiParent, colourState, true);
    }

    public static GuiColourPicker create(GuiParent<?> guiParent, ColourState colourState, boolean hasAlpha) {
        int initialColour = colourState.get();
        GuiColourPicker picker = new GuiColourPicker(guiParent.getModularGui().getRoot());
        picker.setOpaque(true);
        picker.setColourState(colourState);
        Constraints.size(picker, 80, hasAlpha ? 80 : 68);

        GuiRectangle background = GuiRectangle.toolTipBackground(picker.getContentElement());
        Constraints.bind(background, picker.getContentElement());

        var hexField = GuiTextField.create(background, 0xFF000000, 0xFF505050, 0xe0e0e0);
        hexField.primary
                .setTextState(picker.getTextState())
                .setMaxLength(hasAlpha ? 8 : 6)
                .setFilter(s -> s.isEmpty() || validHex(s));
        hexField.container
                .setOpaque(true)
                .constrain(HEIGHT, literal(12))
                .constrain(TOP, relative(background.get(TOP), 4))
                .constrain(LEFT, relative(background.get(LEFT), 4))
                .constrain(RIGHT, relative(background.get(RIGHT), -4));

        SliderBG slider = makeSlider(background, 0xFFFF0000, picker.sliderStateRed())
                .constrain(TOP, relative(hexField.container.get(BOTTOM), 2));

        slider = makeSlider(background, 0xFF00FF00, picker.sliderStateGreen())
                .constrain(TOP, relative(slider.get(BOTTOM), 1));

        slider = makeSlider(background, 0xFF0000FF, picker.sliderStateBlue())
                .constrain(TOP, relative(slider.get(BOTTOM), 1));

        if (hasAlpha) {
            slider = makeSlider(background, 0xFFFFFFFF, picker.sliderStateAlpha())
                    .constrain(TOP, relative(slider.get(BOTTOM), 1));
        } else {
            colourState.setAlpha(0);
        }

        ColourPreview preview = new ColourPreview(background, () -> hasAlpha ? colourState.get() : (colourState.get() | 0xFF000000))
                .setOpaque(true)
                .constrain(HEIGHT, literal(6))
                .constrain(TOP, relative(slider.get(BOTTOM), 2))
                .constrain(LEFT, relative(background.get(LEFT), 4))
                .constrain(RIGHT, relative(background.get(RIGHT), -4));

        picker.cancelButton = GuiButton.flatColourButton(background, () -> Component.translatable("gui.cancel"), e -> 0xFF000000, e -> e ? 0xFF777777 : 0xFF555555)
                .setOpaque(true)
                .onPress(() -> {
                    colourState.set(initialColour);
                    picker.getParent().removeChild(picker);
                })
                .constrain(HEIGHT, literal(10))
                .constrain(TOP, relative(preview.get(BOTTOM), 2))
                .constrain(LEFT, midPoint(background.get(LEFT), background.get(RIGHT), -4))
                .constrain(RIGHT, relative(background.get(RIGHT), -4));

        picker.okButton = GuiButton.flatColourButton(background, () -> Component.translatable("gui.ok"), e -> 0xFF000000, e -> e ? 0xFF777777 : 0xFF555555)
                .setOpaque(true)
                .onPress(() -> picker.getParent().removeChild(picker))
                .constrain(HEIGHT, literal(10))
                .constrain(TOP, relative(preview.get(BOTTOM), 2))
                .constrain(LEFT, relative(background.get(LEFT), 4))
                .constrain(RIGHT, dynamic(() -> picker.cancelButton.isEnabled() ? picker.cancelButton.xMin() - 2 : background.xMax() - 4));

        return picker;
    }

    public static SliderBG makeSlider(GuiElement<?> background, int colour, SliderState state) {
        SliderBG slideBG = new SliderBG(background, 0xFF505050, 0x30FFFFFF)
                .setOpaque(true)
                .constrain(HEIGHT, literal(9))
                .constrain(LEFT, relative(background.get(LEFT), 4))
                .constrain(RIGHT, relative(background.get(RIGHT), -4));

        GuiSlider slider = new GuiSlider(slideBG, Axis.X)
                .setSliderState(state);
        Constraints.bind(slider, slideBG, 0, 1, 0, 1);
        slideBG.slider = slider;

        GuiRectangle handle = new GuiRectangle(slider)
                .fill(colour)
                .border(0xFF000000)
                .borderWidth(0.5)
                .constrain(WIDTH, literal(4));

        slider.installSlider(handle)
                .bindSliderWidth();

        return slideBG;
    }

    private static boolean validHex(String value) {
        try {
            Integer.parseUnsignedInt(value, 16);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static class SliderBG extends GuiElement<SliderBG> implements BackgroundRender {
        public int colour;
        public int highlight;
        public GuiSlider slider;
        public boolean pressed = false;

        public SliderBG(@NotNull GuiParent<?> parent, int colour, int highlight) {
            super(parent);
            this.colour = colour;
            this.highlight = highlight;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button, boolean consumed) {
            pressed = button == 0;
            return super.mouseClicked(mouseX, mouseY, button, consumed);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
            if (button == 0) pressed = false;
            return super.mouseReleased(mouseX, mouseY, button, consumed);
        }

        @Override
        public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            render.fill(xMin(), yMin(), xMin() + 1, yMax(), colour);
            render.fill(xMax() - 1, yMin(), xMax(), yMax(), colour);
            render.fill(xMin() + 1, yCenter() - 0.5, xMax() - 1, yCenter() + 0.5, colour);

            if ((isMouseOver() && !pressed) || slider.isDragging()) {
                render.rect(getRectangle(), highlight);
            }
        }
    }

    public static class ColourPreview extends GuiElement<ColourPreview> implements BackgroundRender {
        private final Supplier<Integer> colour;
        public int colourA = 0xFF999999;
        public int colourB = 0xFF666666;

        public ColourPreview(@NotNull GuiParent<?> parent, Supplier<Integer> colour) {
            super(parent);
            this.colour = colour;
        }

        @Override
        public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            render.pushScissorRect(xMin(), yMin(), xSize(), ySize());
            for (int x = 0; xMin() + (x * 2) < xMax(); x++) {
                for (int y = 0; yMin() + (y * 2) < yMax(); y++) {
                    int col = (y & 1) == 0 ? ((x & 1) == 0 ? colourA : colourB) : ((x & 1) == 0 ? colourB : colourA);
                    render.rect(xMin() + (x * 2), yMin() + (y * 2), 2, 2, col);
                }
            }
            render.popScissor();
            render.rect(getRectangle(), colour.get());
        }
    }
}
























