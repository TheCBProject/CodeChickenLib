package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 19/11/2016.
 */
public class PerspectiveAwareLayeredModelWrapper extends AbstractPerspectiveLayeredModel {

    private final ImmutableMap<BlockRenderLayer, IBakedModel> layerModelMap;

    public PerspectiveAwareLayeredModelWrapper(Map<BlockRenderLayer, IBakedModel> layerModelMap, PerspectiveProperties properties) {
        super(properties);
        this.layerModelMap = ImmutableMap.copyOf(layerModelMap);
    }

    @Override
    public List<BakedQuad> getLayerQuads(@Nullable IBlockState state, @Nullable EnumFacing side, BlockRenderLayer layer, long rand) {
        if (layerModelMap.containsKey(layer)) {
            return layerModelMap.get(layer).getQuads(state, side, rand);
        }
        return Collections.emptyList();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
