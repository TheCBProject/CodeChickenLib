package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * Context menus get added to the root element when they are created so as long as no new elements are added after the menu is opened,
 * the menu will always be on top.
 * It will also automatically close when an option is selected, or when the user clicks outside the context menu.
 * <p>
 * Created by brandon3055 on 21/11/2023
 */
public class GuiContextMenu extends GuiElement<GuiContextMenu> {

    private BiFunction<GuiContextMenu, Supplier<Component>, GuiButton> buttonBuilder = (menu, label) -> GuiButton.flatColourButton(menu, label, hover -> hover ? 0xFF475b6a : 0xFF151515).constrain(HEIGHT, literal(12));
    private final Map<Supplier<Component>, Runnable> options = new LinkedHashMap<>();
    private final Map<Supplier<Component>, Supplier<List<Component>>> tooltips = new HashMap<>();
    private final List<GuiButton> buttons = new ArrayList<>();
    private boolean closeOnItemClicked = true;
    private boolean closeOnOutsideClick = true;
    private boolean actionOnClick = false;

    public GuiContextMenu(ModularGui gui) {
        super(gui.getRoot());
        jeiExclude();
        setOpaque(true);
    }

    public static GuiContextMenu tooltipStyleMenu(GuiParent<?> parent) {
        GuiContextMenu menu = new GuiContextMenu(parent.getModularGui());
        Constraints.bind(GuiRectangle.toolTipBackground(menu), menu);
        return menu;
    }

    public GuiContextMenu setCloseOnItemClicked(boolean closeOnItemClicked) {
        this.closeOnItemClicked = closeOnItemClicked;
        return this;
    }

    public GuiContextMenu setCloseOnOutsideClick(boolean closeOnOutsideClick) {
        this.closeOnOutsideClick = closeOnOutsideClick;
        return this;
    }

    /**
     * By default, the option action fires on mouse button release (like pretty much any UI button in existence)
     * Calling this will change that to action on mouse button press, (What minecraft uses for its buttons)
     */
    public GuiContextMenu actionOnClick() {
        this.actionOnClick = true;
        rebuildButtons();
        return this;
    }

    /**
     * Only height should be constrained, with will be set automatically to accommodate the provided label.
     */
    public GuiContextMenu setButtonBuilder(BiFunction<GuiContextMenu, Supplier<Component>, GuiButton> buttonBuilder) {
        this.buttonBuilder = buttonBuilder;
        return this;
    }

    public GuiContextMenu addOption(Supplier<Component> label, Runnable action) {
        options.put(label, action);
        rebuildButtons();
        return this;
    }

    public GuiContextMenu addOption(Supplier<Component> label, Supplier<List<Component>> tooltip, Runnable action) {
        options.put(label, action);
        tooltips.put(label, tooltip);
        rebuildButtons();
        return this;
    }

    public GuiContextMenu addOption(Supplier<Component> label, List<Component> tooltip, Runnable action) {
        return addOption(label, () -> tooltip, action);
    }

    public GuiContextMenu addOption(Supplier<Component> label, Runnable action, Component... tooltip) {
        return addOption(label, () -> List.of(tooltip), action);
    }

    private void rebuildButtons() {
        buttons.forEach(this::removeChild);
        buttons.clear();

        //Menu options can be dynamic so the width constraint needs to be dynamic.
        //This is probably a little expensive, but its only while a context menu is open.
        constrain(WIDTH, dynamic(() -> options.keySet().stream().mapToInt(e -> font().width(e.get())).max().orElse(0) + 6D + 4D));

        double height = 3;
        for (Supplier<Component> label : options.keySet()) {
            Runnable action = options.get(label);
            GuiButton button = buttonBuilder.apply(this, label)
                    .constrain(TOP, relative(get(TOP), height))
                    .constrain(LEFT, relative(get(LEFT), 3))
                    .constrain(RIGHT, relative(get(RIGHT), -3));
            if (actionOnClick) {
                button.onClick(action);
            } else {
                button.onPress(action);
            }
            if (tooltips.containsKey(label)) {
                button.setTooltip(tooltips.get(label));
            }
            button.getLabel().setScroll(false);
            buttons.add(button);
            height += button.ySize();
        }
        constrain(HEIGHT, literal(height + 3));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, boolean consumed) {
        consumed = super.mouseClicked(mouseX, mouseY, button, consumed);
        if (isMouseOver() || consumed) {
            if (actionOnClick) {
                if (consumed && closeOnItemClicked) {
                    close();
                }
                return true;
            }
        } else if (closeOnOutsideClick) {
            close();
            return true;
        }

        return consumed;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
        consumed = super.mouseReleased(mouseX, mouseY, button, consumed);
        if (isMouseOver() || consumed) {
            if (!actionOnClick) {
                if (consumed && closeOnItemClicked) {
                    close();
                }
                return true;
            }
        }
        return consumed;
    }

    public void close() {
        getParent().removeChild(this);
    }

    public GuiContextMenu setNormalizedPos(double x, double y) {
        constrain(LEFT, dynamic(() -> Math.min(Math.max(x, 0), scaledScreenWidth() - xSize())));
        constrain(TOP, dynamic(() -> Math.min(Math.max(y, 0), scaledScreenHeight() - ySize())));
        return this;
    }
}
