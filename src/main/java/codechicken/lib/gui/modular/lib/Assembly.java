package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.elements.GuiElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This represents multiple gui elements that have been assembled into a functional component.
 * This is primarily for used by the built-in element builders. But could be used in custom builders.
 * <p>
 * This is needed because a builder method for an element, cant always return just the relevant element.
 * Take the scroll bar for example.
 * The builtin scroll bar construction consists of a background element,
 * with the scroll bar itself being a child of the background element
 * constrained to the background element.
 * <p>
 * So the create method for a scroll bar needs to return the background element
 * so that you can constrain int appropriately, but it also needs to return the slider element
 * so that you can actually use it. That's what this container class is for.
 * <p>
 * Created by brandon3055 on 03/09/2023
 */
public class Assembly<C extends GuiElement<?>, E extends GuiElement<?>> {
    /**
     * This is the root/container element, Apply any relevant constraints to this element.
     */
    public final C container;
    /**
     * This is the actual primary / functional element.
     */
    public final E primary;
    /**
     * Contains any other elements that are part of this assembly
     */
    public final List<GuiElement<?>> parts = new ArrayList<>();

    public Assembly(C container, E primary) {
        this.container = container;
        this.primary = primary;
    }

    public Assembly<C, E> addParts(GuiElement<?>... parts) {
        this.parts.addAll(Arrays.asList(parts));
        return this;
    }

    public GuiElement<?> getPart(int index) {
        return parts.get(index);
    }
}
