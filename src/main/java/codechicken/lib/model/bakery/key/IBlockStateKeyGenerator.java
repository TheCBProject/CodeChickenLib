package codechicken.lib.model.bakery.key;

import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.data.IModelData;

/**
 * Created by covers1624 on 26/11/2016.
 */
public interface IBlockStateKeyGenerator {

    /**
     * Used to tell the bakery what BlockStates are the same.
     * The bakery implementation is global, so make sure you prefix your blocks registry name and meta.
     * E.g. "thermalexpansion:machine|6"
     * Just remember, this key defines a model in the cache, if a model with the provided key is not found, your bakery will be called to generate one.
     * <p>
     * The stack is always as follows CCBakeryModel > Bakery > Gen Key > Hey does this key exist in cache ? ret model : Call block specific bakery to gen model > cache > ret.
     *
     * @param state
     * @return
     */
    String generateKey(BlockState state, IModelData modelData);

}
