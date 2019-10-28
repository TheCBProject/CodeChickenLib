package codechicken.lib.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * Created by covers1624 on 30/10/2016.
 * TODO Document.
 */
public interface IItemBlockTextureProvider {

    TextureAtlasSprite getTexture(Direction side, ItemStack stack);

}
