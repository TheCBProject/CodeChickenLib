package codechicken.lib.model.blockbakery;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by covers1624 on 12/02/2017.
 */
public interface IItemBakery {

    @SideOnly (Side.CLIENT)
    List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack);
}
