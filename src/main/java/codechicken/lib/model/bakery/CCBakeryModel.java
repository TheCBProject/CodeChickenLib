package codechicken.lib.model.bakery;

import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by covers1624 on 26/10/2016.
 */
public class CCBakeryModel implements IBakedModel, IModelParticleProvider {


    public CCBakeryModel() {
    }

    @Deprecated
    public CCBakeryModel(String nope) {
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return ModelBakery.getCachedModel((IExtendedBlockState) state).getQuads(state, side, rand);
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
    public Set<TextureAtlasSprite> getHitEffects(@Nonnull RayTraceResult traceResult, IBlockState state, IBlockAccess world, BlockPos pos) {
        IBakedModel model = ModelBakery.getCachedModel((IExtendedBlockState) state);
        if (model instanceof IModelParticleProvider) {
            return ((IModelParticleProvider) model).getHitEffects(traceResult, state, world, pos);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<TextureAtlasSprite> getDestroyEffects(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBakedModel model = ModelBakery.getCachedModel((IExtendedBlockState) state);
        if (model instanceof IModelParticleProvider) {
            return ((IModelParticleProvider) model).getDestroyEffects(state, world, pos);
        }
        return Collections.emptySet();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(ImmutableList.of()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                IBakedModel model = ModelBakery.getCachedItemModel(stack);
                if (model == null) {
                    return originalModel;
                }
                return model;
            }
        };
    }
}
