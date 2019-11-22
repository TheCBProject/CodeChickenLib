package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.util.VertexDataUtils;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
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
    public boolean isAmbientOcclusion() {
        return properties.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return properties.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return properties.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return properties.getParticleTexture();
    }

    protected List<BakedQuad> getAllQuads(BlockState state, IModelData modelData) {
        List<BakedQuad> allQuads = new ArrayList<>();
        allQuads.addAll(getQuads(state, null, new Random(0), modelData));
        for (Direction face : Direction.BY_INDEX) {
            allQuads.addAll(getQuads(state, face, new Random(0), modelData));
        }
        return allQuads;
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(@Nonnull BlockRayTraceResult traceResult, BlockState state, IEnviromentBlockReader world, BlockPos pos, IModelData modelData) {
        Vector3 vec = new Vector3(traceResult.getHitVec()).subtract(traceResult.getPos());
        return getAllQuads(state, modelData).stream()//
                .filter(quad -> quad.getFace() == traceResult.getFace())//
                .filter(quad -> checkDepth(quad, vec, traceResult.getFace()))//
                .map(BakedQuad::getSprite)//
                .collect(Collectors.toSet());//
    }

    protected boolean checkDepth(BakedQuad quad, Vector3 hit, Direction hitFace) {
        int[] quadData = quad.getVertexData();
        VertexFormat format = quad.getFormat();
        int e = VertexDataUtils.getPositionElement(format);

        Vector3 posVec = new Vector3();
        float[] pos = new float[4];
        for (int v = 0; v < 4; v++) {
            LightUtil.unpack(quadData, pos, format, v, e);
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
    public Set<TextureAtlasSprite> getDestroyEffects(BlockState state, IEnviromentBlockReader world, BlockPos pos, IModelData data) {
        return getAllQuads(state, data).stream().map(BakedQuad::getSprite).collect(Collectors.toSet());
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        if (properties instanceof PerspectiveProperties) {
            return PerspectiveMapWrapper.handlePerspective(this, ((PerspectiveProperties) properties).getModelState(), cameraTransformType);
        }
        return IModelParticleProvider.super.handlePerspective(cameraTransformType);
    }
}
