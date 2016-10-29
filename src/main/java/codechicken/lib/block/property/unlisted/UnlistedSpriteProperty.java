package codechicken.lib.block.property.unlisted;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * Created by covers1624 on 25/10/2016.
 */
@SideOnly(Side.CLIENT)
public class UnlistedSpriteProperty extends UnlistedPropertyBase<TextureAtlasSprite> {

    public UnlistedSpriteProperty(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
    public Class<TextureAtlasSprite> getType() {
        return TextureAtlasSprite.class;
    }

    @Override
    public String valueToString(TextureAtlasSprite value) {
        return value.toString();
    }

}
