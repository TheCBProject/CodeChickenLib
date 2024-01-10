package codechicken.lib.gui.modular.lib.geometry;

import com.google.common.annotations.Beta;
import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.elements.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This is the base interface that allows an element or screen to define its basic geometry.
 * As well as defining the primary methods for handling child elements.
 * <p>
 * It also provides a way to access some common minecraft fields.
 * <p>
 * Created by brandon3055 on 29/06/2023
 */
public interface GuiParent<T extends GuiParent<?>> {

    /**
     * @return The position of the Left edge of this element.
     */
    double xMin();

    /**
     * @return The position of the Right edge of this element.
     */
    double xMax();

    /**
     * @return The Width of this element.
     */
    double xSize();

    /**
     * @return The position of the Top edge of this element.
     */
    double yMin();

    /**
     * @return The position of the Bottom edge of this element.
     */
    double yMax();

    /**
     * @return The Height of this element.
     */
    double ySize();

    /**
     * Returns a reference to the specified geometry parameter.
     * This is primarily used when defining geometry constraints.
     * But it can also be used as a simple {@link Supplier<Integer>}
     * that will return the current parameter value when requested.
     * <p>
     * Note: The returned geometry reference will always be valid
     *
     * @param param The geometry parameter.
     * @return A Geometry Reference
     */
    default GeoRef get(GeoParam param) {
        return new GeoRef(this, param);
    }

    /**
     * @param param The geometry parameter.
     * @return The current value of the specified parameter.
     */
    default double getValue(GeoParam param) {
        switch (param) {
            case LEFT:
                return xMin();
            case RIGHT:
                return xMax();
            case WIDTH:
                return xSize();
            case TOP:
                return yMin();
            case BOTTOM:
                return yMax();
            case HEIGHT:
                return ySize();
            default:
                throw new IllegalStateException("Param: \"" + param + "\" Shouldn't Exist! Someone has broken my code! Go yell at them!");
        }
    }

    /**
     * @return An unmodifiable list of all assigned child elements assigned to this parent. The list should be sorted in the order they were added.
     */
    List<GuiElement<?>> getChildren();

    /**
     * Adds a new child element to this parent.
     * You should almost never need to use this because this is handled automatically when an element is created.
     * <p>
     * Note: Due to the way relative coordinates work with the new geometry system,
     * Transferring an element to a different parent can have unpredictable results.
     * Therefor, to help avoid confusion it is not possible to transfer a child to a new parent using this method.
     *
     * @param child The child element to be added.
     * @throws UnsupportedOperationException - If child has previously been assigned to a different parent.
     * @see #adoptChild(GuiElement)
     */
    void addChild(GuiElement<?> child);

    /**
     * This meant to be a convenience method that allows builder style addition of a child element.
     * I'm not sure how useful it will be yet, so it may or may not stay.
     *
     * @param createChild A consumer that is given this element to be used in the construction of the child element.
     * @return The parent element
     */
    @Beta
    @SuppressWarnings ("unchecked")
    default T addChild(Consumer<T> createChild) {
        createChild.accept((T) this);
        return (T) this;
    }

    /**
     * This method can be used to transfer an already initialized child to this parent element.
     * This automatically handles removing the element from its previous parent, adds it to this element.
     * Note: This will most likely break any relative constraints on the child's geometry.
     * To fix this you will need to re-apply geometry constraints after the transfer.
     *
     * @param child The child element to be adopted.
     */
    void adoptChild(GuiElement<?> child);

    /**
     * Allows the removal of a child element.
     * Child removal is not instantaneous, Instead all removals occur at the end of the current screen thick.
     * This is to avoid any possible concurrency issues.
     *
     * @param child The child element to be removed.
     */
    void removeChild(GuiElement<?> child);

    /**
     * Checks if this element is a descendant of the specified.
     * @return true if the specified element is a parent or grandparent etc... of this element.
     */
    default boolean isDescendantOf(GuiElement<?> ancestor) {
        return false;
    }

    /**
     * @return The minecraft instance.
     */
    Minecraft mc();

    /**
     * @return The active font instance.
     */
    Font font();

    /**
     * @return The current gui screen width, As returned by mc.getWindow().getGuiScaledWidth()
     */
    int scaledScreenWidth();

    /**
     * @return The current gui screen height, As returned by mc.getWindow().getGuiScaledHeight()
     */
    int scaledScreenHeight();

    /**
     * @return the parent ModularGui instance.
     */
    ModularGui getModularGui();

    /**
     * Called when the minecraft Screen is initialised or resized.
     *
     * @param mc           The Minecraft instance.
     * @param font         The active font.
     * @param screenWidth  The current guiScaledWidth.
     * @param screenHeight The current guiScaledHeight.
     */
    default void onScreenInit(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        getChildren().forEach(e -> e.onScreenInit(mc, font, screenWidth, screenHeight));
    }

    //TODO, May still keep this, but i currently have a different focus system in mind that *may* be better
//    /**
//     * Not sure how much this will get used, if at all, but allows an element to be specified as the globally focused element.
//     * This does not have any effect at all on the base functionality of Modular Gui, it is up to individual elements to choose how they handle focus.
//     *
//     * @param element the element to be set as the current focused element, or null to clear focused element.
//     */
//    void setFocused(@Nullable GuiElement<?> element);
//
//    /**
//     * @return the current gloabally focused element, or null if no element is focused.
//     */
//    @Nullable GuiElement<?> getFocused();

    /**
     * Allows an element to override the {@link GuiElement#isMouseOver()} method of its children.
     * This is primarily used for things like scroll elements where mouseover interactions need to be blocked outside the view area.
     *
     * @param element The element on which isMouseOver is getting called.
     * @return true if mouse-over interaction should be blocked for this child element.
     */
    default boolean blockMouseOver(GuiElement<?> element, double mouseX, double mouseY) {
        return false;
    }
}
