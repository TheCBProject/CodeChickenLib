package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.TextState;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * TODO, Re write this, Its currently mostly pulled from the TextField in Gui v2
 * <p>
 * Created by brandon3055 on 03/09/2023
 */
public class GuiTextField extends GuiElement<GuiTextField> implements BackgroundRender {
    private static final RenderType HIGHLIGHT_TYPE = RenderType.create("text_field_highlight", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
            .setColorLogicState(RenderStateShard.OR_REVERSE_COLOR_LOGIC)
            .createCompositeState(false));

    private int tick;
    private int cursorPos;
    private int maxLength = 32;
    private int displayPos;
    private int highlightPos;
    private boolean focused;
    private boolean shiftPressed;

    private Supplier<Boolean> isEditable = () -> true;
    private Supplier<Boolean> isFocusable = () -> true;
    private Supplier<Boolean> canLoseFocus = () -> true;

    private Runnable onEditComplete = null;
    private Runnable onEnterPressed = null;

    private TextState textState = TextState.simpleState("");
    private Supplier<Boolean> shadow = () -> true;
    private Supplier<Integer> textColor = () -> 0xe0e0e0;

    private Supplier<Component> suggestion = null;
    private Supplier<Integer> suggestionColour = () -> 0x7f7f80;

    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = (string, pos) -> FormattedCharSequence.forward(string, Style.EMPTY);

    public GuiTextField(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    /**
     * Creates a simple text box with a simple bordered background.
     * Using colours 0xFF000000, 0xFFFFFFFF, 0xE0E0E0 will get you a text box identical to the one in a command block
     */
    public static TextField create(GuiElement<?> parent, int backgroundColour, int borderColour, int textColour) {
        GuiRectangle background = new GuiRectangle(parent)
                .rectangle(backgroundColour, borderColour);

        GuiTextField textField = new GuiTextField(background)
                .setTextColor(textColour)
                .constrain(TOP, Constraint.relative(background.get(TOP), 1))
                .constrain(BOTTOM, Constraint.relative(background.get(BOTTOM), -1))
                .constrain(LEFT, Constraint.relative(background.get(LEFT), 4))
                .constrain(RIGHT, Constraint.relative(background.get(RIGHT), -4));

        return new TextField(background, textField);
    }

    //=== Text field setup ===//

    /**
     * Called when the user clicks outside the text box, or when they press enter
     */
    public GuiTextField setOnEditComplete(Runnable onEditComplete) {
        this.onEditComplete = onEditComplete;
        return this;
    }

    /**
     * Called when the user presses enter key (Including numpad enter) with the text box ion focus
     */
    public GuiTextField setEnterPressed(Runnable onEnterPressed) {
        this.onEnterPressed = onEnterPressed;
        return this;
    }

    /**
     * The {@link TextState} is an accessor for the current text value.
     * It simply contains string getter and setter methods.
     * You can use this to link the text field to some external value.
     */
    public GuiTextField setTextState(TextState textState) {
        this.textState = textState;
        return this;
    }

    public TextState getTextState() {
        return textState;
    }

    public GuiTextField setTextColor(Supplier<Integer> textColor) {
        this.textColor = textColor;
        return this;
    }

    public GuiTextField setTextColor(int textColor) {
        return setTextColor(() -> textColor);
    }

    /**
     * Should the text be rendered with a shadow?
     */
    public GuiTextField setShadow(Supplier<Boolean> shadow) {
        this.shadow = shadow;
        return this;
    }

    /**
     * Should the text be rendered with a shadow?
     */
    public GuiTextField setShadow(boolean shadow) {
        return setShadow(() -> shadow);
    }

    /**
     * Set the "suggestion" text that is displayed when the text field is empty.
     */
    public GuiTextField setSuggestion(Component suggestion) {
        return setSuggestion(suggestion == null ? null : () -> suggestion);
    }

    /**
     * Set the "suggestion" text that is displayed when the text field is empty.
     */
    public GuiTextField setSuggestion(@Nullable Supplier<Component> suggestion) {
        this.suggestion = suggestion;
        return this;
    }

    /**
     * Set the colour of the suggestion text.
     */
    public void setSuggestionColour(Supplier<Integer> suggestionColour) {
        this.suggestionColour = suggestionColour;
    }

    /**
     * Allows you to apply a fielder to this text field.
     * Whenever this field's value is updated, If the new value does not pass this filter
     * It will not be applied.
     */
    public GuiTextField setFilter(Predicate<String> filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Formats the current text value for display to the user.
     * The integer is the current display start position within the current field value.
     */
    public GuiTextField setFormatter(BiFunction<String, Integer, FormattedCharSequence> formatter) {
        this.formatter = formatter;
        return this;
    }

    /**
     * If set to false, it will not be possible for the text field to lose focus via normal means.
     * Focus can still be set via {@link #setFocus(boolean)}
     */
    public GuiTextField setCanLoseFocus(boolean canLoseFocus) {
        return setCanLoseFocus(() -> canLoseFocus);
    }

    /**
     * If set to false, it will not be possible for the text field to lose focus via normal means.
     * Focus can still be set via {@link #setFocus(boolean)}
     */
    public GuiTextField setCanLoseFocus(Supplier<Boolean> canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
        return this;
    }

    /**
     * If false, It will not be possible to focus this element by clicking on it.
     */
    public GuiTextField setFocusable(boolean focusable) {
        return setFocusable(() -> focusable);
    }

    /**
     * If false, It will not be possible to focus this element by clicking on it.
     */
    public GuiTextField setFocusable(Supplier<Boolean> focusable) {
        this.isFocusable = focusable;
        return this;
    }

    /**
     * If false, It will not be possible for the user to edit the value of this text field.
     */
    public GuiTextField setEditable(boolean editable) {
        return setEditable(() -> editable);
    }

    /**
     * If false, It will not be possible for the user to edit the value of this text field.
     */
    public GuiTextField setEditable(Supplier<Boolean> editable) {
        this.isEditable = editable;
        return this;
    }

    /**
     * Sets the maximum allowed text length
     */
    public GuiTextField setMaxLength(int newWidth) {
        String value = getValue();
        maxLength = newWidth;
        if (value.length() > newWidth) {
            textState.setText(value.substring(0, newWidth));
        }
        return this;
    }

    private int getMaxLength() {
        return maxLength;
    }

    //=== Text field logic ===//

    /**
     * Note, Initial value should be set after element is constrained,
     * If element width is zero when set, nothing will render until the field is updated.
     * TODO, I need to fix this. Element size should be able to change dynamically without things breaking.
     */
    public GuiTextField setValue(String newValue) {
        if (this.filter.test(newValue)) {
            if (newValue.length() > maxLength) {
                textState.setText(newValue.substring(0, maxLength));
            } else {
                textState.setText(newValue);
            }
            moveCursorToEnd();
            setHighlightPos(cursorPos);
        }
        return this;
    }

    public String getValue() {
        return textState.getText();
    }

    public String getHighlighted() {
        int i = Math.min(cursorPos, highlightPos);
        int j = Math.max(cursorPos, highlightPos);
        return getValue().substring(i, j);
    }

    public void insertText(String text) {
        String value = getValue();
        int selectStart = Math.min(cursorPos, highlightPos);
        int selectEnd = Math.max(cursorPos, highlightPos);
        int freeSpace = maxLength - value.length() - (selectStart - selectEnd);
        String toInsert = SharedConstants.filterText(text);
        int insertLen = toInsert.length();
        if (freeSpace < insertLen) {
            toInsert = toInsert.substring(0, freeSpace);
            insertLen = freeSpace;
        }

        String newValue = (new StringBuilder(value)).replace(selectStart, selectEnd, toInsert).toString();
        if (filter.test(newValue)) {
            textState.setText(newValue);
            setCursorPosition(selectStart + insertLen);
            setHighlightPos(cursorPos);
        }
    }

    private void deleteText(int i) {
        if (Screen.hasControlDown()) {
            deleteWords(i);
        } else {
            deleteChars(i);
        }
    }

    public void deleteWords(int i) {
        if (!getValue().isEmpty()) {
            if (highlightPos != cursorPos) {
                insertText("");
            } else {
                deleteChars(getWordPosition(i) - cursorPos);
            }
        }
    }

    public void deleteChars(int i1) {
        String value = getValue();
        if (!value.isEmpty()) {
            if (highlightPos != cursorPos) {
                insertText("");
            } else {
                int i = getCursorPos(i1);
                int j = Math.min(i, cursorPos);
                int k = Math.max(i, cursorPos);
                if (j != k) {
                    String s = (new StringBuilder(value)).delete(j, k).toString();
                    if (filter.test(s)) {
                        textState.setText(s);
                        moveCursorTo(j);
                    }
                }
            }
        }
    }

    public int getWordPosition(int i) {
        return getWordPosition(i, getCursorPosition());
    }

    private int getWordPosition(int i, int i1) {
        return getWordPosition(i, i1, true);
    }

    private int getWordPosition(int i1, int i2, boolean b) {
        String value = getValue();
        int i = i2;
        boolean flag = i1 < 0;
        int j = Math.abs(i1);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = value.length();
                i = value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (b && i < l && value.charAt(i) == ' ') ++i;
                }
            } else {
                while (b && i > 0 && value.charAt(i - 1) == ' ') --i;
                while (i > 0 && value.charAt(i - 1) != ' ') --i;
            }
        }

        return i;
    }

    public void moveCursor(int pos) {
        moveCursorTo(getCursorPos(pos));
    }

    private int getCursorPos(int i) {
        return Util.offsetByCodepoints(getValue(), cursorPos, i);
    }

    public void moveCursorTo(int pos, boolean notify) {
        setCursorPosition(pos);
        if (!shiftPressed) {
            setHighlightPos(cursorPos);
        }
    }

    public void moveCursorTo(int pos) {
        moveCursorTo(pos, true);
    }

    public void setCursorPosition(int pos) {
        cursorPos = Mth.clamp(pos, 0, getValue().length());
    }

    public void moveCursorToStart() {
        moveCursorTo(0);
    }

    public void moveCursorToEnd(boolean notify) {
        moveCursorTo(getValue().length(), notify);
    }

    public void moveCursorToEnd() {
        moveCursorToEnd(true);
    }

    public boolean isEditable() {
        return isEditable.get();
    }

    public void setFocus(boolean focused) {
        if (this.focused && !focused && onEditComplete != null) {
            onEditComplete.run();
        }
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public int getCursorPosition() {
        return cursorPos;
    }

    public void setHighlightPos(int newPos) {
        String value = getValue();
        int length = value.length();
        highlightPos = Mth.clamp(newPos, 0, length);

        if (displayPos > length) {
            displayPos = length;
        }

        int width = (int) xSize();
        String visibleText = font().plainSubstrByWidth(value.substring(displayPos), width);
        int endPos = displayPos + visibleText.length();
        if (highlightPos == displayPos) {
            displayPos -= font().plainSubstrByWidth(value, width, true).length();
        }

        if (highlightPos > endPos) {
            displayPos += highlightPos - endPos;
        } else if (highlightPos <= displayPos) {
            displayPos -= displayPos - highlightPos;
        }

        displayPos = Mth.clamp(displayPos, 0, length);
    }

    //=== Input Handling ===//

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (!canConsumeInput()) {
            return false;
        } else {
            shiftPressed = Screen.hasShiftDown();
            if (Screen.isSelectAll(key)) {
                moveCursorToEnd();
                setHighlightPos(0);
                return true;
            } else if (Screen.isCopy(key)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(getHighlighted());
                return true;
            } else if (Screen.isPaste(key)) {
                if (isEditable()) {
                    insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                }

                return true;
            } else if (Screen.isCut(key)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(getHighlighted());
                if (isEditable()) {
                    insertText("");
                }

                return true;
            } else {
                switch (key) {
                    case InputConstants.KEY_BACKSPACE:
                        if (isEditable()) {
                            shiftPressed = false;
                            deleteText(-1);
                            shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case InputConstants.KEY_NUMPADENTER:
                    case InputConstants.KEY_RETURN: {
                        if (onEditComplete != null) {
                            onEditComplete.run();
                        }
                        if (onEnterPressed != null) {
                            onEnterPressed.run();
                        }
                    }
                    case InputConstants.KEY_INSERT:
                    case InputConstants.KEY_DOWN:
                    case InputConstants.KEY_UP:
                    case InputConstants.KEY_PAGEUP:
                    case InputConstants.KEY_PAGEDOWN:
                    default:
                        //Consume key presses when we are typing so we dont do something dumb like close the screen when you type e
                        return key != GLFW.GLFW_KEY_ESCAPE;
                    case InputConstants.KEY_DELETE:
                        if (isEditable()) {
                            shiftPressed = false;
                            deleteText(1);
                            shiftPressed = Screen.hasShiftDown();
                        }
                        return true;
                    case InputConstants.KEY_RIGHT:
                        if (Screen.hasControlDown()) {
                            moveCursorTo(getWordPosition(1));
                        } else {
                            moveCursor(1);
                        }
                        return true;
                    case InputConstants.KEY_LEFT:
                        if (Screen.hasControlDown()) {
                            moveCursorTo(getWordPosition(-1));
                        } else {
                            moveCursor(-1);
                        }
                        return true;
                    case InputConstants.KEY_HOME:
                        moveCursorToStart();
                        return true;
                    case InputConstants.KEY_END:
                        moveCursorToEnd();
                        return true;
                }
            }
        }
    }

    public boolean canConsumeInput() {
        return isFocused() && isEditable() && isEnabled();
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers, boolean consumed) {
        this.shiftPressed = Screen.hasShiftDown();
        return super.keyReleased(key, scancode, modifiers, consumed);
    }

    @Override
    public boolean charTyped(char charTyped, int charCode) {
        if (!canConsumeInput()) {
            return false;
        } else if (SharedConstants.isAllowedChatCharacter(charTyped)) {
            if (isEditable()) {
                insertText(Character.toString(charTyped));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, boolean consumed) {
        consumed = super.mouseClicked(mouseX, mouseY, button, consumed);

        boolean mouseOver = isMouseOver();
        if (isFocused() && !mouseOver) {
            setFocus(isFocusable.get() && !canLoseFocus.get());
        }

        if (consumed) return true;

        if (canLoseFocus.get()) {
            setFocus(mouseOver && isFocusable.get());
        } else {
            setFocus(isFocusable.get());
        }

        if (isFocused() && mouseOver && button == 0) {
            int i = (int) (Mth.floor(mouseX) - xMin());
            String s = font().plainSubstrByWidth(getValue().substring(displayPos), (int) xSize());
            moveCursorTo(font().plainSubstrByWidth(s, i).length() + displayPos);
            return true;
        } else {
            return false;
        }
    }

    //=== Rendering ===//

    @Override
    public double getBackgroundDepth() {
        return 0.04;
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        String value = getValue();
        int colour = textColor.get();

        int textStart = cursorPos - displayPos;
        int highlightStart = highlightPos - displayPos;
        String displayText = font().plainSubstrByWidth(value.substring(displayPos), (int) xSize());
        boolean flag = textStart >= 0 && textStart <= displayText.length();
        boolean cursorBlink = isFocused() && tick / 6 % 2 == 0 && flag;
        double drawX = xMin();
        double drawY = yMin() + ((ySize() - 8) / 2);
        int drawEnd = (int) drawX;

        if (highlightStart > displayText.length()) {
            highlightStart = displayText.length();
        }

        if (!displayText.isEmpty()) {
            String drawString = flag ? displayText.substring(0, textStart) : displayText;
            drawEnd = render.drawString(formatter.apply(drawString, displayPos), drawX, drawY, colour, shadow.get());
        }

        boolean flag2 = cursorPos < value.length() || value.length() >= getMaxLength();
        int k1 = drawEnd;

        if (!flag) {
            k1 = (int) (textStart > 0 ? drawX + xSize() : drawX);
        } else if (flag2) {
            k1 = drawEnd - 1;
            --drawEnd;
        }

        if (!displayText.isEmpty() && flag && textStart < displayText.length()) {
            render.drawString(formatter.apply(displayText.substring(textStart), cursorPos), drawEnd, drawY, colour, shadow.get());
        }

        if (suggestion != null && value.isEmpty()) {
            render.drawString(suggestion.get(), (float) (k1 - 1), (float) drawY, suggestionColour.get(), shadow.get());
        }

        if (cursorBlink) {
            if (flag2) {
                render.fill(k1, drawY - 1, k1 + 1, drawY + 1 + 9, -3092272);
            } else {
                render.drawString("_", (float) k1, (float) drawY, colour, shadow.get());
            }
        }

        if (highlightStart != textStart) {
            int l1 = (int) (drawX + font().width(displayText.substring(0, highlightStart)));
            render.pose().translate(0, 0, 0.035);
            render.fill(HIGHLIGHT_TYPE, k1, drawY - 1, l1 - 1, drawY + 1 + 9, 0xFF0000FF);
            render.pose().translate(0, 0, -0.035);
        }
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        tick++;
    }

    public record TextField(GuiRectangle container, GuiTextField field) {}
}

