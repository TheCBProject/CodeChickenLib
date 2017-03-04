package codechicken.lib.model.bakery.key;

import net.minecraft.item.ItemStack;

/**
 * Created by covers1624 on 2/11/2016.
 * TODO Document.
 */
public interface IItemStackKeyGenerator {

    String generateKey(ItemStack stack);

}
