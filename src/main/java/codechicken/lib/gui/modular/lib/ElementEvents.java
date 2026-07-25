package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.elements.GuiElement;
import com.google.common.collect.Lists;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.List;

/**
 * This class defines the default implementation for all Screen events.
 * Input events in Modular GUI v2 work similar to v2, events are passed to all elements recursively in a top-down order,
 * and if any element 'consumes' the event, it will not be passed any further down the chain.
 * However, this approach had issues in v2, because there are certain situations where an element needs to receive an event even if it has been consumed.
 * To deal with that we now have two methods for each event, the main handler method that will always get called, and uses a 'consumed' flag to track whether the event has been consumed,
 * as well as a simpler convenience method that will only get called if the event has not already been canceled, and does not require you to call super.
 * <p>
 * Created by brandon3055 on 09/08/2023
 */
public interface ElementEvents {

    /**
     * @return An unmodifiable list of all assigned child elements assigned to this parent. The list should be sorted in the order they were added.
     */
    List<GuiElement<?>> getChildren();

    //=== Mouse Events ==//

    /**
     * Called whenever the cursor position changes.
     * Vanillas mouseDragged is not passed through because it is redundant.
     * All mouse drag functionality can be archived using available events.
     *
     * @param mouseX new mouse X position
     * @param mouseY new mouse Y position
     */
    default void mouseMoved(double mouseX, double mouseY) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                child.mouseMoved(mouseX, mouseY);
            }
        }
    }

    /**
     * Override this method to implement handling for the mouseClicked event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #mouseClicked(MouseButtonEvent, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param event The mouse button event. Position, Key, and modifiers.
     * @return true to consume event.
     */
    default boolean mouseClicked(MouseButtonEvent event) {
        return false;
    }

    /**
     * Root handler for mouseClick event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of mouseClick handling should be implemented via {@link #mouseClicked(MouseButtonEvent)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param event    The mouse button event. Position, Key, and modifiers.
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.mouseClicked(event, consumed);
            }
        }
        return consumed || mouseClicked(event) || blockMouseEvents();
    }

    /**
     * Override this method to implement handling for the mouseReleased event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #mouseReleased(MouseButtonEvent, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param event The mouse button event. Position, Key, and modifiers.
     * @return true to consume event.
     */
    default boolean mouseReleased(MouseButtonEvent event) {
        return false;
    }

    /**
     * Root handler for mouseReleased event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of mouseReleased handling should be implemented via {@link #mouseReleased(MouseButtonEvent)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param event    The mouse button event. Position, Key, and modifiers.
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean mouseReleased(MouseButtonEvent event, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.mouseReleased(event, consumed);
            }
        }
        return consumed || mouseReleased(event) || blockMouseEvents();
    }

    /**
     * Override this method to implement handling for the mouseScrolled event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #mouseScrolled(double, double, double, double, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param mouseX  Mouse X position
     * @param mouseY  Mouse Y position
     * @param scrollX Scroll direction and amount
     * @param scrollY Scroll direction and amount
     * @return true to consume event.
     */
    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    /**
     * Root handler for mouseScrolled event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of mouseScrolled handling should be implemented via {@link #mouseScrolled(double, double, double, double)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param mouseX   Mouse X position
     * @param mouseY   Mouse Y position
     * @param scrollX  Scroll direction and amount
     * @param scrollY  Scroll direction and amount
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.mouseScrolled(mouseX, mouseY, scrollX, scrollY, consumed);
            }
        }
        return consumed || mouseScrolled(mouseX, mouseY, scrollX, scrollY) || blockMouseEvents();
    }

    /**
     * @return True to prevent mouse events from being passed to elements bellow this element.
     */
    default boolean blockMouseEvents() {
        return false;
    }

    //=== Keyboard Events ==//

    /**
     * Override this method to implement handling for the keyPressed event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #keyPressed(KeyEvent, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param event The key event. Key, scancode, and modifiers.
     * @return true to consume event.
     */
    default boolean keyPressed(KeyEvent event) {
        return false;
    }

    /**
     * Root handler for keyPressed event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of keyPressed handling should be implemented via {@link #keyPressed(KeyEvent)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param event    The key event. Key, scancode, and modifiers.
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean keyPressed(KeyEvent event, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.keyPressed(event, consumed);
            }
        }
        return consumed || keyPressed(event);
    }

    /**
     * Override this method to implement handling for the keyReleased event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #keyReleased(KeyEvent, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param event The key event. Key, scancode, and modifiers.
     * @return true to consume event.
     */
    default boolean keyReleased(KeyEvent event) {
        return false;
    }

    /**
     * Root handler for keyReleased event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of keyReleased handling should be implemented via {@link #keyReleased(KeyEvent)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param event    The key event. Key, scancode, and modifiers.
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean keyReleased(KeyEvent event, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.keyReleased(event, consumed);
            }
        }
        return consumed || keyReleased(event);
    }

    /**
     * Override this method to implement handling for the charTyped event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #charTyped(CharacterEvent, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param event The character event, codepoint and modifiers.
     * @return true to consume event.
     */
    default boolean charTyped(CharacterEvent event) {
        return false;
    }

    /**
     * Root handler for charTyped event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of charTyped handling should be implemented via {@link #charTyped(CharacterEvent)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param event    The character event, codepoint and modifiers.
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean charTyped(CharacterEvent event, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.charTyped(event, consumed);
            }
        }
        return consumed || charTyped(event);
    }

}
