package codechicken.lib.gui.modular.lib;

import com.google.common.collect.Lists;
import codechicken.lib.gui.modular.elements.GuiElement;

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
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #mouseClicked(double, double, int, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse Button
     * @return true to consume event.
     */
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Root handler for mouseClick event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of mouseClick handling should be implemented via {@link #mouseClicked(double, double, int)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param mouseX   Mouse X position
     * @param mouseY   Mouse Y position
     * @param button   Mouse Button
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean mouseClicked(double mouseX, double mouseY, int button, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.mouseClicked(mouseX, mouseY, button, consumed);
            }
        }
        return consumed || mouseClicked(mouseX, mouseY, button) || blockMouseEvents();
    }

    /**
     * Override this method to implement handling for the mouseReleased event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #mouseReleased(double, double, int, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse Button
     * @return true to consume event.
     */
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Root handler for mouseReleased event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of mouseReleased handling should be implemented via {@link #mouseReleased(double, double, int)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param mouseX   Mouse X position
     * @param mouseY   Mouse Y position
     * @param button   Mouse Button
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.mouseReleased(mouseX, mouseY, button, consumed);
            }
        }
        return consumed || mouseReleased(mouseX, mouseY, button) || blockMouseEvents();
    }

    /**
     * Override this method to implement handling for the mouseScrolled event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #mouseScrolled(double, double, double, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param scroll Scroll direction and amount
     * @return true to consume event.
     */
    default boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return false;
    }

    /**
     * Root handler for mouseScrolled event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of mouseScrolled handling should be implemented via {@link #mouseScrolled(double, double, double)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param mouseX   Mouse X position
     * @param mouseY   Mouse Y position
     * @param scroll   Scroll direction and amount
     * @param consumed Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean mouseScrolled(double mouseX, double mouseY, double scroll, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.mouseScrolled(mouseX, mouseY, scroll, consumed);
            }
        }
        return consumed || mouseScrolled(mouseX, mouseY, scroll) || blockMouseEvents();
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
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #keyPressed(int, int, int, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param key       the keyboard key that was pressed.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true to consume event.
     */
    default boolean keyPressed(int key, int scancode, int modifiers) {
        return false;
    }

    /**
     * Root handler for keyPressed event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of keyPressed handling should be implemented via {@link #keyPressed(int, int, int)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param key       the keyboard key that was pressed.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @param consumed  Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean keyPressed(int key, int scancode, int modifiers, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.keyPressed(key, scancode, modifiers, consumed);
            }
        }
        return consumed || keyPressed(key, scancode, modifiers);
    }

    /**
     * Override this method to implement handling for the keyReleased event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #keyReleased(int, int, int, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param key       the keyboard key that was released.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true to consume event.
     */
    default boolean keyReleased(int key, int scancode, int modifiers) {
        return false;
    }

    /**
     * Root handler for keyReleased event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of keyReleased handling should be implemented via {@link #keyReleased(int, int, int)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param key       the keyboard key that was released.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @param consumed  Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean keyReleased(int key, int scancode, int modifiers, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.keyReleased(key, scancode, modifiers, consumed);
            }
        }
        return consumed || keyReleased(key, scancode, modifiers);
    }

    /**
     * Override this method to implement handling for the charTyped event.
     * This event propagates through the entire gui element stack from top to bottom, If eny element consumes the event it will not propagate any further.
     * For rare cases where you need to receive this even if it has been consumed, you can override {@link #charTyped(char, int, boolean)}
     * <p>
     * Note: You do not need to call super when overriding this interface method.
     *
     * @param character The character typed.
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true to consume event.
     */
    default boolean charTyped(char character, int modifiers) {
        return false;
    }

    /**
     * Root handler for charTyped event. This method will always be called for all elements even if the event has already been consumed.
     * There are a few uses for this method, but the fast majority of charTyped handling should be implemented via {@link #charTyped(char, int)}
     * <p>
     * Note: If overriding this method, do so with caution, You must either return true (if you wish to consume the event) or you must return the result of the super call.
     *
     * @param character The character typed.
     * @param modifiers bitfield describing which modifier keys were held down.
     * @param consumed  Will be true if this action has already been consumed.
     * @return true if this event has been consumed.
     */
    default boolean charTyped(char character, int modifiers, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.charTyped(character, modifiers, consumed);
            }
        }
        return consumed || charTyped(character, modifiers);
    }

}