package codechicken.lib.model.bakery;

import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by covers1624 on 28/10/2016.
 * For Blocks, implement this on your block class.
 * For Items, implement this on your item class.
 */
public interface IBakeryProvider {

    /**
     * Used to provide a bakery for the Item OR Block.
     * This should basically always return either ILayeredBlockBakery or ISimpleBlockBakery for blocks or,
     * IItemBakery for items.
     *
     * @return The Bakery!
     */
    @OnlyIn (Dist.CLIENT)
    IBakery getBakery();

}
