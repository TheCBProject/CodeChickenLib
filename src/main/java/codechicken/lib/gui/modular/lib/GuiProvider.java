package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.elements.GuiElement;

/**
 * This interface is used to build modular gui Screens.
 * For modular gui container screens use {@link codechicken.lib.gui.modular.lib.container.ContainerGuiProvider}
 *
 * Created by brandon3055 on 19/08/2023
 */
public interface GuiProvider {

    /**
     * Override this to defile a custom root gui element.
     * Useful if you want to use something like a background texture or a manipulable element as the root element.
     *
     * @param gui The modular GUI.
     * @return the root gui element.
     */
    default GuiElement<?> createRootElement(ModularGui gui) {
        return new GuiElement<>(gui);
    }

    /**
     * Use this method to build the modular gui.
     * <p>
     * Initialize the gui root element with {@link ModularGui#initStandardGui(int, int)}, {@link ModularGui#initFullscreenGui()}
     * This applies bindings to fix the size and position of the root element, you can also do this manually for custom configurations.
     * <p>
     * Build your gui by adding and configuring your desired gui elements.
     * Elements must be added to the root gui element which is obtainable via gui.getRoot()
     * <p>
     * Note: gui elements are added on construction, meaning you do not need to use element.addChild.
     * Instead, just construct the elements, and pass in the root element (or any other initialized element) as the parent.
     *
     * @param gui The modular gui instance.
     */
    void buildGui(ModularGui gui);

}
