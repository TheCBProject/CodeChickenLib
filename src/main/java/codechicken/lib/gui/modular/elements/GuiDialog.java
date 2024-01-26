package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * This class is designed to assist with the easy creation of a number of standard dialog windows.
 * <p>
 * Created by brandon3055 on 14/12/2023
 */
public class GuiDialog extends GuiElement<GuiDialog> {

    private boolean blockKeyInput = true;
    private boolean blockMouseInput = true;

    protected GuiDialog(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    /**
     * Option dialog builder with pre-configured background and button builders.
     *
     * @param parent     Can be any gui element (Will just be used to get the root element)
     * @param title      Sets a separate title that will be displayed above the main dialog text.  (Optional)
     * @param dialogText The main dialog text.
     * @param width      The dialog width, (Height will automatically adjust based on content.)
     * @param options    The list of options for this dialog.
     */
    public static GuiDialog optionsDialog(@NotNull GuiParent<?> parent, @Nullable Component title, Component dialogText, int width, Option... options) {
        return optionsDialog(parent, title, dialogText, GuiRectangle::toolTipBackground, GuiDialog::defaultButton, width, options);
    }

    /**
     * Option dialog builder with pre-configured background and button builders.
     *
     * @param parent     Can be any gui element (Will just be used to get the root element)
     * @param dialogText The main dialog text.
     * @param width      The dialog width, (Height will automatically adjust based on content.)
     * @param options    The list of options for this dialog.
     */
    public static GuiDialog optionsDialog(@NotNull GuiParent<?> parent, Component dialogText, int width, Option... options) {
        return optionsDialog(parent, null, dialogText, width, options);
    }

    /**
     * Creates a simple info dialog for displaying information to the user.
     * The dialog has a single "Ok" button that will close the dialog
     *
     * @param parent     Can be any gui element (Will just be used to get the root element)
     * @param title      Sets a separate title that will be displayed above the main dialog text.  (Optional)
     * @param dialogText The main dialog text.
     * @param width      The dialog width, (Height will automatically adjust based on content.)
     */
    public static GuiDialog infoDialog(@NotNull GuiParent<?> parent, @Nullable Component title, Component dialogText, int width, @Nullable Runnable okAction) {
        return optionsDialog(parent, title, dialogText, width, neutral(Component.translatable("gui.ok"), okAction));
    }

    /**
     * Creates a simple info dialog for displaying information to the user.
     * The dialog has a single "Ok" button that will close the dialog
     *
     * @param parent     Can be any gui element (Will just be used to get the root element)
     * @param title      Sets a separate title that will be displayed above the main dialog text.  (Optional)
     * @param dialogText The main dialog text.
     * @param width      The dialog width, (Height will automatically adjust based on content.)
     */
    public static GuiDialog infoDialog(@NotNull GuiParent<?> parent, @Nullable Component title, Component dialogText, int width) {
        return infoDialog(parent, title, dialogText, width, null);
    }

    /**
     * Creates a simple info dialog for displaying information to the user.
     * The dialog has a single "Ok" button that will close the dialog
     *
     * @param parent     Can be any gui element (Will just be used to get the root element)
     * @param dialogText The main dialog text.
     * @param width      The dialog width, (Height will automatically adjust based on content.)
     */
    public static GuiDialog infoDialog(@NotNull GuiParent<?> parent, Component dialogText, int width) {
        return infoDialog(parent, null, dialogText, width);
    }

    /**
     * Create a green "Primary" button option.
     */
    public static Option primary(Component text, @Nullable Runnable action) {
        return new Option(text, action, hovered -> hovered ? 0xFF44AA44 : 0xFF118811);
    }

    /**
     * Create a grey "Neutral" button option.
     */
    public static Option neutral(Component text, @Nullable Runnable action) {
        return new Option(text, action, hovered -> hovered ? 0xFF909090 : 0xFF505050);
    }

    /**
     * Create a red "Caution" button option.
     */
    public static Option caution(Component text, @Nullable Runnable action) {
        return new Option(text, action, hovered -> hovered ? 0xFFAA4444 : 0xFF881111);
    }

    /**
     * @param blockKeyInput Prevent keyboard inputs from being sent to the rest of the gui while this dialog is open.
     *                      Default: true.
     */
    public GuiDialog setBlockKeyInput(boolean blockKeyInput) {
        this.blockKeyInput = blockKeyInput;
        return this;
    }

    /**
     * @param blockMouseInput Prevent mouse inputs from being sent to the rest of the gui while this dialog is open.
     *                        Default: true.
     */
    public GuiDialog setBlockMouseInput(boolean blockMouseInput) {
        this.blockMouseInput = blockMouseInput;
        return this;
    }

    public void close() {
        getParent().removeChild(this);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        return blockKeyInput;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return blockMouseInput;
    }

    /**
     * This is the core dialog builder method.
     * It takes a title text component and a map of Component>Runnable map that is used to define the options.
     * Dialog will automatically be centered on the screen.
     *
     * @param parent            Can be any gui element (Will just be used to get the root element)
     * @param title             Sets a separate title that will be displayed above the main dialog text.  (Optional)
     * @param dialogText        The main dialog text.
     * @param backgroundBuilder A function that is used to create the background of the dialog.
     * @param buttonBuilder     A function that is used to create the dialog buttons.
     * @param width             The dialog width, (Height will automatically adjust based on content.)
     * @param options           The list of options for this dialog.
     */
    public static GuiDialog optionsDialog(@NotNull GuiParent<?> parent, @Nullable Component title, Component dialogText, Function<GuiDialog, GuiElement<?>> backgroundBuilder, BiFunction<GuiDialog, Option, GuiButton> buttonBuilder, int width, Option... options) {
        if (options.length == 0) throw new IllegalStateException("Can not create gui dialog with no options!");
        ModularGui gui = parent.getModularGui();

        GuiDialog dialog = new GuiDialog(gui.getRoot())
                .constrain(WIDTH, literal(width))
                .setOpaque(true);
        Constraints.bind(backgroundBuilder.apply(dialog), dialog);

        Constraint left = relative(dialog.get(LEFT), 5);
        Constraint right = relative(dialog.get(RIGHT), -5);

        if (title != null) {
            GuiText titleText = new GuiText(dialog, title)
                    .setWrap(true)
                    .constrain(TOP, relative(dialog.get(TOP), 5))
                    .constrain(LEFT, left)
                    .constrain(RIGHT, right)
                    .autoHeight();

            GuiText bodyText = new GuiText(dialog, dialogText)
                    .setWrap(true)
                    .constrain(TOP, relative(titleText.get(BOTTOM), 5))
                    .constrain(LEFT, left)
                    .constrain(RIGHT, right)
                    .autoHeight();
            dialog.constrain(HEIGHT, dynamic(() -> 5 + titleText.ySize() + 5 + bodyText.ySize() + 5 + 14 + 5));
        } else {
            GuiText bodyText = new GuiText(dialog, dialogText)
                    .setWrap(true)
                    .constrain(TOP, relative(dialog.get(TOP), 5))
                    .constrain(LEFT, left)
                    .constrain(RIGHT, right)
                    .autoHeight();
            dialog.constrain(HEIGHT, dynamic(() -> 5 + bodyText.ySize() + 5 + 14 + 5));
        }

        double totalWidth = FastStream.of(options).doubleSum(e -> dialog.font().width(e.text));
        double pos = 5;
        int spacing = 2;
        for (Option option : options) {
            double fraction = dialog.font().width(option.text) / totalWidth;
            double opWidth = (dialog.xSize() - 10 - ((options.length - 1) * spacing)) * fraction;
            buttonBuilder.apply(dialog, option)
                    .constrain(BOTTOM, relative(dialog.get(BOTTOM), -5))
                    .constrain(HEIGHT, literal(14))
                    .constrain(LEFT, relative(dialog.get(LEFT), pos).precise())
                    .constrain(WIDTH, literal(opWidth).precise());
            pos += opWidth + spacing;
        }

        dialog.constrain(TOP, midPoint(gui.get(TOP), gui.get(BOTTOM), () -> -(dialog.ySize() / 2D)));
        dialog.constrain(LEFT, midPoint(gui.get(LEFT), gui.get(RIGHT), -(width / 2D)));
        return dialog;
    }

    private static GuiButton defaultButton(GuiDialog dialog, Option option) {
        GuiButton button = new GuiButton(dialog);

        GuiRectangle background = new GuiRectangle(button)
                .fill(() -> option.colour.apply(button.isMouseOver()));
        Constraints.bind(background, button);

        GuiText text = new GuiText(button, option.text());
        button.setLabel(text);
        Constraints.bind(text, button, 0, 2, 0, 2);

        button.onPress(() -> {
            if (option.action != null) {
                option.action.run();
            }
            dialog.close();
        });

        return button;
    }

    public record Option(Component text, @Nullable Runnable action, Function<Boolean, Integer> colour) {}
}
