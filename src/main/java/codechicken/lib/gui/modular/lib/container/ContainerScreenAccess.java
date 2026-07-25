package codechicken.lib.gui.modular.lib.container;

import codechicken.lib.gui.modular.elements.GuiSlots;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/**
 * Used by {@link ContainerGuiProvider}
 * Provides access to the ContainerMenu as well as some essential functions.
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public interface ContainerScreenAccess<T extends AbstractContainerMenu> extends MenuAccess<T> {

    /**
     * This is the modular gui friendly method used by elements such as {@link GuiSlots} to render inventory item stacks.
     */
    void doRenderSlot(GuiGraphics render, Slot slot);

}
