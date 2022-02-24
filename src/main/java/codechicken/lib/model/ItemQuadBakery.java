package codechicken.lib.model;

import codechicken.lib.util.ArrayUtils;
import codechicken.lib.util.LambdaUtils;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.model.ItemLayerModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 13/02/2017.
 */
public class ItemQuadBakery {

    public static List<BakedQuad> bakeItem(TextureAtlasSprite... sprites) {
        return bakeItem(Transformation.identity(), sprites);
    }

    public static List<BakedQuad> bakeItem(Transformation transform, TextureAtlasSprite... sprites) {

        LambdaUtils.checkArgument(sprites, "Sprites must not be Null or empty!", ArrayUtils::isNullOrContainsNull);

        List<BakedQuad> quads = new LinkedList<>();
        for (int i = 0; i < sprites.length; i++) {
            TextureAtlasSprite sprite = sprites[i];
            quads.addAll(ItemLayerModel.getQuadsForSprite(i, sprite, transform));
        }
        return quads;
    }

}
