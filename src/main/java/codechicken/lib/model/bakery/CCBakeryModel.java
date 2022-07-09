package codechicken.lib.model.bakery;

import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by covers1624 on 26/10/2016.
 */
public class CCBakeryModel implements BakedModel, IModelParticleProvider {

    public CCBakeryModel() {
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        return getQuads(state, side, rand, ModelData.EMPTY, null);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType layer) {
        return ModelBakery.getCachedModel(state, modelData).getQuads(state, side, rand, modelData, layer);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return TextureUtils.getMissingSprite();
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(BlockHitResult traceResult, BlockState state, BlockAndTintGetter world, BlockPos pos, ModelData data) {
        BakedModel model = ModelBakery.getCachedModel(state, data);
        if (model instanceof IModelParticleProvider) {
            return ((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos, data);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<TextureAtlasSprite> getDestroyEffects(BlockState state, BlockAndTintGetter world, BlockPos pos, ModelData data) {
        BakedModel model = ModelBakery.getCachedModel(state, data);
        if (model instanceof IModelParticleProvider) {
            return ((IModelParticleProvider) model).getDestroyEffects(state, world, pos, data);
        }
        return Collections.emptySet();
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides() {
            @Override
            public BakedModel resolve(BakedModel originalModel, ItemStack stack, ClientLevel world, LivingEntity entity, int seed) {
                BakedModel model = ModelBakery.getCachedItemModel(stack);
                return Objects.requireNonNullElse(model, originalModel);
            }
        };
    }
}
