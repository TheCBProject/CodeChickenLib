package codechicken.lib.gui.modular.lib.container;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.lib.GuiProvider;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Created by brandon3055 on 08/09/2023
 */
public abstract class ContainerGuiProvider<T extends AbstractContainerMenu> implements GuiProvider {

    private ContainerScreenAccess<T> screenAccess;

    public void setMenuAccess(ContainerScreenAccess<T> screenAccess) {
        this.screenAccess = screenAccess;
    }

    @Override
    public final void buildGui(ModularGui gui) {
        buildGui(gui, screenAccess);
    }

    /**
     * This is the same as {@link GuiProvider#buildGui(ModularGui)} except you have access to the {@link MenuAccess}
     * The given menu accessor should always be the parent screen unless your using some custom modular gui implementation.
     *
     * @param gui          The modular gui instance.
     * @param screenAccess The screen access (This will be a gui class that extends {@link codechicken.lib.gui.modular.ModularGuiContainer}
     */
    public abstract void buildGui(ModularGui gui, ContainerScreenAccess<T> screenAccess);
}
