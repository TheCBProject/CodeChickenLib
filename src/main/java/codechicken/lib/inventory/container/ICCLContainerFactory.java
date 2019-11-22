package codechicken.lib.inventory.container;

import codechicken.lib.data.MCDataInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

/**
 * Created by covers1624 on 28/10/19.
 */
public interface ICCLContainerFactory<T extends Container> extends ContainerType.IFactory<T> {

    T create(int windowId, PlayerInventory inventory, MCDataInput packet);

    @Override
    default T create(int windowId, PlayerInventory inventory) {
        return create(windowId, inventory, null);
    }
}
