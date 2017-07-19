package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.util.VertexDataUtils;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.LinkedList;
import java.util.List;
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

    protected List<BakedQuad> getAllQuads(IBlockState state) {
        List<BakedQuad> allQuads = new LinkedList<>();
        allQuads.addAll(getQuads(state, null, 0L));
        for (EnumFacing face : EnumFacing.VALUES) {
            allQuads.addAll(getQuads(state, face, 0L));
        }
        return allQuads;
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(@Nonnull RayTraceResult traceResult, IBlockState state, IBlockAccess world, BlockPos pos) {
        Vector3 vec = new Vector3(traceResult.hitVec).subtract(traceResult.getBlockPos());
        return getAllQuads(state).stream()//
                .filter(quad -> quad.getFace() == traceResult.sideHit)//
                .filter(quad -> checkDepth(quad, vec, traceResult.sideHit))//
                .map(BakedQuad::getSprite)//
                .collect(Collectors.toSet());//
    }

    protected boolean checkDepth(BakedQuad quad, Vector3 hit, EnumFacing hitFace) {
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
    public Set<TextureAtlasSprite> getDestroyEffects(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getAllQuads(state).stream().map(BakedQuad::getSprite).collect(Collectors.toSet());
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        if (properties instanceof PerspectiveProperties) {
            return PerspectiveMapWrapper.handlePerspective(this, ((PerspectiveProperties) properties).getModelState(), cameraTransformType);
        }
        return IModelParticleProvider.super.handlePerspective(cameraTransformType);
    }
}
