package codechicken.lib.model;

import codechicken.lib.util.ArrayUtils;
import codechicken.lib.util.LambdaUtils;
import codechicken.lib.util.TransformUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by covers1624 on 13/02/2017.
 */
public class ItemQuadBakery {

    public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites) {
        return bakeItem(sprites, DefaultVertexFormats.ITEM, TransformUtils.DEFAULT_ITEM);
    }

    public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites, IModelState state) {
        return bakeItem(sprites, DefaultVertexFormats.ITEM, state);
    }

    public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites, VertexFormat format) {
        return bakeItem(sprites, format, TransformUtils.DEFAULT_ITEM);
    }

    public static List<BakedQuad> bakeItem(List<TextureAtlasSprite> sprites, VertexFormat format, IModelState state) {
        return bakeItem(format, state, sprites.toArray(new TextureAtlasSprite[0]));
    }

    public static List<BakedQuad> bakeItem(TextureAtlasSprite... sprites) {
        return bakeItem(TransformUtils.DEFAULT_ITEM, sprites);
    }

    public static List<BakedQuad> bakeItem(IModelState state, TextureAtlasSprite... sprites) {
        return bakeItem(DefaultVertexFormats.ITEM, state, sprites);
    }

    public static List<BakedQuad> bakeItem(VertexFormat format, TextureAtlasSprite... sprites) {
        return bakeItem(format, TransformUtils.DEFAULT_ITEM, sprites);
    }

    public static List<BakedQuad> bakeItem(VertexFormat format, IModelState state, TextureAtlasSprite... sprites) {

        LambdaUtils.checkArgument(sprites, "Sprites must not be Null or empty!", ArrayUtils::isNullOrContainsNull);

        List<BakedQuad> quads = new LinkedList<>();
        Optional<TRSRTransformation> transform = state.apply(Optional.empty());
        for (int i = 0; i < sprites.length; i++) {
            TextureAtlasSprite sprite = sprites[i];
            quads.addAll(ItemLayerModel.getQuadsForSprite(i, sprite, format, transform));
        }
        return quads;
    }

}
