package codechicken.lib.model;

import codechicken.lib.util.ArrayUtils;
import codechicken.lib.util.LambdaUtils;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 13/02/2017.
 */
public class ItemQuadBakery {

    public static final SimpleModelState IDENTITY = new SimpleModelState(Transformation.identity());

    public static List<BakedQuad> bakeItem(TextureAtlasSprite... sprites) {
        return bakeItem(IDENTITY, sprites);
    }

    public static List<BakedQuad> bakeItem(ModelState state, TextureAtlasSprite... sprites) {
        LambdaUtils.checkArgument(sprites, "Sprites must not be Null or empty!", ArrayUtils::isNullOrContainsNull);

        List<BakedQuad> quads = new LinkedList<>();
        for (int i = 0; i < sprites.length; i++) {
            TextureAtlasSprite sprite = sprites[i];
            List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(i, sprite.contents());
            quads.addAll(UnbakedGeometryHelper.bakeElements(unbaked, e -> sprite, state, new ResourceLocation("ccl:dynamic")));
        }
        return quads;
    }

}
