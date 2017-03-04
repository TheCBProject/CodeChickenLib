package codechicken.lib.model.bakery.generation;

import codechicken.lib.model.bakery.ModelBakery;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    @SideOnly (Side.CLIENT)
    IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos);
}
