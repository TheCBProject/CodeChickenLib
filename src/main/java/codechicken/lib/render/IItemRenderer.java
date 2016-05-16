package codechicken.lib.render;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

/**
 * Hooks to the location of RenderItem#renderItem(ItemStack, IBakedModel)
 */
public interface IItemRenderer extends IPerspectiveAwareModel {

    /**
     * Used as a CallBack to render a TileEntity / Item after Transforms have been applied.
     *
     * @param item Stack to render.
     */
    void renderItem(ItemStack item);
}
