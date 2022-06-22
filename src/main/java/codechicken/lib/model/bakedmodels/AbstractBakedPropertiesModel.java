package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.LightUtil;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 1/02/2017.
 */
public abstract class AbstractBakedPropertiesModel implements IModelParticleProvider, BakedModel {

    protected final ModelProperties properties;

    public AbstractBakedPropertiesModel(ModelProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return properties.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return properties.isGui3d();
    }

    @Override
    public boolean isCustomRenderer() {
        return properties.isBuiltInRenderer();
    }

    @Override
    public boolean usesBlockLight() {
        return properties.usesBlockLight();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return properties.getParticleTexture();
    }

    protected List<BakedQuad> getAllQuads(BlockState state, IModelData modelData) {
        List<BakedQuad> allQuads = new LinkedList<>();
        allQuads.addAll(getQuads(state, null, RandomSource.create(0), modelData));
        for (Direction face : Direction.BY_3D_DATA) {
            allQuads.addAll(getQuads(state, face, RandomSource.create(0), modelData));
        }
        return allQuads;
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(@Nonnull BlockHitResult traceResult, BlockState state, BlockAndTintGetter world, BlockPos pos, IModelData modelData) {
        Vector3 vec = new Vector3(traceResult.getLocation()).subtract(traceResult.getBlockPos());
        return getAllQuads(state, modelData).stream()
                .filter(quad -> quad.getDirection() == traceResult.getDirection())
                .filter(quad -> checkDepth(quad, vec, traceResult.getDirection()))
                .map(BakedQuad::getSprite)
                .collect(Collectors.toSet());
    }

    protected boolean checkDepth(BakedQuad quad, Vector3 hit, Direction hitFace) {
        int[] quadData = quad.getVertices();
        CachedFormat format = CachedFormat.lookup(DefaultVertexFormat.BLOCK);

        Vector3 posVec = new Vector3();
        float[] pos = new float[4];
        for (int v = 0; v < 4; v++) {
            LightUtil.unpack(quadData, pos, format.format, v, format.positionIndex);
            posVec.add(pos[0], pos[1], pos[2]);
        }
        posVec.divide(4);

        double diff = 0;
        switch (hitFace.getAxis()) {
            case X:
                diff = Math.abs(hit.x - posVec.x);
                break;
            case Y:
                diff = Math.abs(hit.y - posVec.y);
                break;
            case Z:
                diff = Math.abs(hit.z - posVec.z);
                break;
        }
        return !(diff > 0.01);
    }

    @Override
    public Set<TextureAtlasSprite> getDestroyEffects(BlockState state, BlockAndTintGetter world, BlockPos pos, IModelData data) {
        return getAllQuads(state, data).stream().map(BakedQuad::getSprite).collect(Collectors.toSet());
    }

    @Override
    public boolean doesHandlePerspectives() {
        return true;
    }

    @Override
    public BakedModel handlePerspective(TransformType transformType, PoseStack mat) {
        if (properties instanceof PerspectiveProperties) {
            ModelState transforms = ((PerspectiveProperties) properties).getTransforms();
            return PerspectiveMapWrapper.handlePerspective(this, transforms, transformType, mat);
        }
        return IModelParticleProvider.super.handlePerspective(transformType, mat);
    }
}
