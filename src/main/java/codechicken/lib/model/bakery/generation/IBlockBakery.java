package codechicken.lib.model.bakery.generation;

import codechicken.lib.model.bakery.ModelBakery;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface IBlockBakery extends IItemBakery {

    /**
     * Used to pass extended state handling from your block to your bakery.
     * Call {@link ModelBakery#handleExtendedState(IExtendedBlockState, IBlockAccess, BlockPos)} from {@link Block#getExtendedState(IBlockState, IBlockAccess, BlockPos)}
     *
     * @param state  The current state of your block.
     * @param access The world you are in.
     * @param pos    The position in that world.
     * @return Modified state.
     */
    //@SideOnly (Side.CLIENT)
    //IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos);
}
