package codechicken.lib.texture;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * Created by covers1624 on 30/10/2016.
 */
public interface IBlockTextureProvider {

    TextureAtlasSprite getTexture(EnumFacing side, int metadata);

}
