package codechicken.lib.model.bakery;

import codechicken.lib.model.BakedModelProperties;
import codechicken.lib.model.SimplePerspectiveAwareBakedModel;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.texture.TextureUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Arrays;

/**
 * Created by covers1624 on 7/25/2016.
 */
public class SimplePerspectiveAwareLayerModelBakery {

    private final ResourceLocation baseTexture;
    private final ResourceLocation[] layers;

    public SimplePerspectiveAwareLayerModelBakery(ResourceLocation baseTexture, ResourceLocation... layers) {
        this.baseTexture = baseTexture;
        this.layers = layers;
    }

    public IBakedModel bake(IModelState state) {

        ImmutableList.Builder<BakedQuad> quadBuilder = ImmutableList.builder();
        ImmutableList<ResourceLocation> textureLayers = ImmutableList.<ResourceLocation>builder().add(baseTexture).addAll(Arrays.asList(layers)).build();

        IBakedModel layerModel = new ItemLayerModel(textureLayers).bake(state, DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter);
        quadBuilder.addAll(layerModel.getQuads(null, null, 0));

        return new PerspectiveAwareBakedModel(quadBuilder.build(), state, BakedModelProperties.createFromModel(layerModel));
    }
}
