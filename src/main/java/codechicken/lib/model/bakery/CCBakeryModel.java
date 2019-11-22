package codechicken.lib.model.bakery;

import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by covers1624 on 26/10/2016.
 */
public class CCBakeryModel implements IBakedModel, IModelParticleProvider {

    public CCBakeryModel() {
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        return ModelBakery.getCachedModel(state, extraData).getQuads(state, side, rand, extraData);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return TextureUtils.getMissingSprite();
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(BlockRayTraceResult traceResult, BlockState state, IEnviromentBlockReader world, BlockPos pos, IModelData data) {
        IBakedModel model = ModelBakery.getCachedModel(state, data);
        if (model instanceof IModelParticleProvider) {
            return ((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos, data);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<TextureAtlasSprite> getDestroyEffects(BlockState state, IEnviromentBlockReader world, BlockPos pos, IModelData data) {
        //TODO, Destroy may need IModelData
        IBakedModel model = ModelBakery.getCachedModel(state, EmptyModelData.INSTANCE);
        if (model instanceof IModelParticleProvider) {
            return ((IModelParticleProvider) model).getDestroyEffects(state, world, pos, data);
        }
        return Collections.emptySet();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList() {
            @Override
            public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
                IBakedModel model = ModelBakery.getCachedItemModel(stack);
                if (model == null) {
                    return originalModel;
                }
                return model;
            }
        };
    }
}
