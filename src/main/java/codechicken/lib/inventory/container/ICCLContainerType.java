package codechicken.lib.inventory.container;

import codechicken.lib.data.MCDataInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

/**
 * Created by covers1624 on 28/10/19.
 */
public interface ICCLContainerType<T extends Container> {

    static <T extends Container> ContainerType<T> create(ICCLContainerFactory<T> factory) {
        return new CCLContainerType<>(factory);
    }

    T create(int windowId, PlayerInventory inventory, MCDataInput packet);

    class CCLContainerType<T extends Container> extends ContainerType<T> implements ICCLContainerType<T> {

        public CCLContainerType(IFactory<T> factory) {
            super(factory);
        }

        @Override
        public T create(int windowId, PlayerInventory inventory, MCDataInput packet) {
            if (factory instanceof ICCLContainerFactory) {
                return ((ICCLContainerFactory<T>) factory).create(windowId, inventory, packet);
            }
            return null;
        }
    }
}
