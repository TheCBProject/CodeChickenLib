package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.PerspectiveModel;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.util.VertexUtils;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 1/02/2017.
 */
public abstract class AbstractBakedPropertiesModel implements PerspectiveModel, IModelParticleProvider, BakedModel {

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

    protected List<BakedQuad> getAllQuads(BlockState state, ModelData modelData) {
        List<List<BakedQuad>> quadsList = new LinkedList<>();
        for (RenderType layer : getRenderTypes(state, RandomSource.create(42), modelData)) {
            quadsList.add(getQuads(state, null, RandomSource.create(0), modelData, layer));
            for (Direction face : Direction.BY_3D_DATA) {
                quadsList.add(getQuads(state, face, RandomSource.create(0), modelData, layer));
            }
        }
        return ConcatenatedListView.of(quadsList);
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(@Nonnull BlockHitResult traceResult, BlockState state, BlockAndTintGetter world, BlockPos pos, ModelData modelData) {
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
            VertexUtils.unpack(quadData, pos, format.format, v, format.positionIndex);
            posVec.add(pos[0], pos[1], pos[2]);
        }
        posVec.divide(4);

        double diff = switch (hitFace.getAxis()) {
            case X -> Math.abs(hit.x - posVec.x);
            case Y -> Math.abs(hit.y - posVec.y);
            case Z -> Math.abs(hit.z - posVec.z);
        };
        return !(diff > 0.01);
    }

    @Override
    public Set<TextureAtlasSprite> getDestroyEffects(BlockState state, BlockAndTintGetter world, BlockPos pos, ModelData data) {
        return getAllQuads(state, data).stream().map(BakedQuad::getSprite).collect(Collectors.toSet());
    }

    @Nullable
    @Override
    public PerspectiveModelState getModelState() {
        return properties instanceof ModelProperties.PerspectiveProperties props ? props.getTransforms() : null;
    }

    @Override
    public BakedModel applyTransform(TransformType transformType, PoseStack pStack, boolean leftFlip) {
        return PerspectiveModel.super.applyTransform(transformType, pStack, leftFlip);
    }
}
