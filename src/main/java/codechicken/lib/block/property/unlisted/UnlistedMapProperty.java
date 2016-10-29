package codechicken.lib.block.property.unlisted;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by covers1624 on 25/10/2016.
 */
public class UnlistedMapProperty extends UnlistedPropertyBase<Map> {

    public UnlistedMapProperty(String name) {
        super(name);
    }

    @Override
    public Class<Map> getType() {
        return Map.class;
    }

    @Override
    public String valueToString(Map value) {
        return value.toString();
    }

}
