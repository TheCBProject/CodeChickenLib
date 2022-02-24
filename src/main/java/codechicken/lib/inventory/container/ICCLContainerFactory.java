package codechicken.lib.inventory.container;

import codechicken.lib.data.MCDataInput;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Created by covers1624 on 28/10/19.
 */
public interface ICCLContainerFactory<T extends AbstractContainerMenu> extends MenuType.MenuSupplier<T> {

    T create(int windowId, Inventory inventory, MCDataInput packet);

    @Override
    default T create(int windowId, Inventory inventory) {
        return create(windowId, inventory, null);
    }
}
