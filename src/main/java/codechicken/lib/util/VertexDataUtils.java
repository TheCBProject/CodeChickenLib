package codechicken.lib.util;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.uv.UV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;

/**
 * Created by covers1624 on 4/10/2016.
 */
public class VertexDataUtils {

    public static int getPositionOffset(VertexFormat format) {
        for (int e = 0; e < format.getElementCount(); e++) {
            if (format.getElement(e).isPositionElement()) {
                return e;
            }
        }
        return -1;
    }

    public static TextureAtlasSprite getSpriteForUV(TextureMap textureMap, UV uv) {
        for (TextureAtlasSprite sprite : textureMap.mapUploadedSprites.values()){
            if (MathHelper.between(sprite.getMinU(), uv.u, sprite.getMaxU()) && MathHelper.between(sprite.getMinV(), uv.v, sprite.getMaxV())){
                return sprite;
            }
        }
        return textureMap.getMissingSprite();
    }

}
