package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.BakedModelProperties;
import codechicken.lib.model.PerspectiveAwareModelProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 25/11/2016.
 */
public class PerspectiveAwareBakedModel implements IBakedModel {

    private final ImmutableMap<EnumFacing, List<BakedQuad>> faceQuads;
    private final ImmutableList<BakedQuad> generalQuads;
    private final PerspectiveAwareModelProperties properties;

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, IModelState state, BakedModelProperties properties) {
        this(faceQuads, ImmutableList.of(), state, properties);
    }

    public PerspectiveAwareBakedModel(List<BakedQuad> generalQuads, IModelState state, BakedModelProperties properties) {
        this(ImmutableMap.of(), generalQuads, state, properties);
    }

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, List<BakedQuad> generalQuads, IModelState state, BakedModelProperties properties) {
        this(faceQuads, generalQuads, new PerspectiveAwareModelProperties(state, properties));
    }

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, PerspectiveAwareModelProperties properties) {
        this(faceQuads, ImmutableList.of(), properties);
    }

    public PerspectiveAwareBakedModel(List<BakedQuad> generalQuads, PerspectiveAwareModelProperties properties) {
        this(ImmutableMap.of(), generalQuads, properties);
    }

    public PerspectiveAwareBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuads, List<BakedQuad> generalQuads, PerspectiveAwareModelProperties properties) {
        this.faceQuads = ImmutableMap.copyOf(faceQuads);
        this.generalQuads = ImmutableList.copyOf(generalQuads);
        this.properties = properties;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side == null) {
            return generalQuads;
        } else {
            if (faceQuads.containsKey(side)) {
                return faceQuads.get(side);
            }
        }
        return ImmutableList.of();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return PerspectiveMapWrapper.handlePerspective(this, properties.getModelState(), cameraTransformType);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return properties.getProperties().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return properties.getProperties().isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return properties.getProperties().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return properties.getProperties().getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
