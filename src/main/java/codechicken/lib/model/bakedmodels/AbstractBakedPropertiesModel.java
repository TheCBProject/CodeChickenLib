package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.LightUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 1/02/2017.
 */
public abstract class AbstractBakedPropertiesModel implements IModelParticleProvider, IBakedModel {

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
        return false;//TODO What?
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return properties.getParticleTexture();
    }

    protected List<BakedQuad> getAllQuads(BlockState state, IModelData modelData) {
        List<BakedQuad> allQuads = new ArrayList<>();
        allQuads.addAll(getQuads(state, null, new Random(0), modelData));
        for (Direction face : Direction.BY_3D_DATA) {
            allQuads.addAll(getQuads(state, face, new Random(0), modelData));
        }
        return allQuads;
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(@Nonnull BlockRayTraceResult traceResult, BlockState state, IBlockReader world, BlockPos pos, IModelData modelData) {
        Vector3 vec = new Vector3(traceResult.getLocation()).subtract(traceResult.getBlockPos());
        return getAllQuads(state, modelData).stream()//
                .filter(quad -> quad.getDirection() == traceResult.getDirection())//
                .filter(quad -> checkDepth(quad, vec, traceResult.getDirection()))//
                .map(BakedQuad::getSprite)//
                .collect(Collectors.toSet());//
    }

    protected boolean checkDepth(BakedQuad quad, Vector3 hit, Direction hitFace) {
        int[] quadData = quad.getVertices();
        CachedFormat format = CachedFormat.lookup(DefaultVertexFormats.BLOCK);

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
    public Set<TextureAtlasSprite> getDestroyEffects(BlockState state, IBlockReader world, BlockPos pos, IModelData data) {
        return getAllQuads(state, data).stream().map(BakedQuad::getSprite).collect(Collectors.toSet());
    }

    @Override
    public boolean doesHandlePerspectives() {
        return true;
    }

    @Override
    public IBakedModel handlePerspective(TransformType transformType, MatrixStack mat) {
        if (properties instanceof PerspectiveProperties) {
            IModelTransform transforms = ((PerspectiveProperties) properties).getTransforms();
            return PerspectiveMapWrapper.handlePerspective(this, transforms, transformType, mat);
        }
        return IModelParticleProvider.super.handlePerspective(transformType, mat);
    }
}
