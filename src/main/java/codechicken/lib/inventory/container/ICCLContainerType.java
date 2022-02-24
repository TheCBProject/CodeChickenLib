package codechicken.lib.inventory.container;

import codechicken.lib.data.MCDataInput;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Created by covers1624 on 28/10/19.
 */
public interface ICCLContainerType<T extends AbstractContainerMenu> {

    static <T extends AbstractContainerMenu> MenuType<T> create(ICCLContainerFactory<T> factory) {
        return new CCLContainerType<>(factory);
    }

    T create(int windowId, Inventory inventory, MCDataInput packet);

    class CCLContainerType<T extends AbstractContainerMenu> extends MenuType<T> implements ICCLContainerType<T> {

        public CCLContainerType(MenuType.MenuSupplier<T> factory) {
            super(factory);
        }

        @Override
        public T create(int windowId, Inventory inventory, MCDataInput packet) {
            if (constructor instanceof ICCLContainerFactory) {
                return ((ICCLContainerFactory<T>) constructor).create(windowId, inventory, packet);
            }
            return null;
        }
    }
}
