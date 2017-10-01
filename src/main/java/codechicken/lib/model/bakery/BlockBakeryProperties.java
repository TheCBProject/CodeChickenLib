package codechicken.lib.model.bakery;

import codechicken.lib.block.property.unlisted.UnlistedMapProperty;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by covers1624 on 26/12/2016.
 */
public class BlockBakeryProperties {

    @Deprecated//As of 1.13 this will be going away.
    public static final UnlistedMapProperty LAYER_FACE_SPRITE_MAP;

    static {
        LAYER_FACE_SPRITE_MAP = new UnlistedMapProperty("layer_face_sprite");

        LAYER_FACE_SPRITE_MAP.setStringGenerator(map -> {
            StringBuilder builder = new StringBuilder();
            for (Entry<BlockRenderLayer, Map<EnumFacing, TextureAtlasSprite>> layerEntry : ((Map<BlockRenderLayer, Map<EnumFacing, TextureAtlasSprite>>) map).entrySet()) {
                builder.append(layerEntry.getKey().toString()).append(",");
                for (Entry<EnumFacing, TextureAtlasSprite> faceSpriteEntry : layerEntry.getValue().entrySet()) {
                    builder.append(faceSpriteEntry.getKey()).append(",").append(faceSpriteEntry.getValue().getIconName()).append(",");
                }
            }
            return builder.toString();
        });
    }
}
