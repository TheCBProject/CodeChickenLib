package codechicken.lib.render;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;

/**
 * Hooks the location in RenderItem where TileEntityItemStackRenderer.renderByItem is called.
 * Be sure to override isBuiltInRenderer true
 */
public interface IItemRenderer extends IBakedModel {
    void renderItem(ItemStack item);
}
