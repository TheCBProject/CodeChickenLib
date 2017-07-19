package codechicken.lib.render.particle;

import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by covers1624 on 12/07/2017.
 */
public interface IModelParticleProvider extends IBakedModel {

    @Override
    default TextureAtlasSprite getParticleTexture() {
        return TextureUtils.getMissingSprite();
    }

    /**
     * Used to retrieve the particles to randomly choose from for hit particles.
     *
     * @param traceResult The trace result.
     * @param state       The state, getActualState and getExtendedState has been called.
     * @param world       The world.
     * @param pos         The pos.
     * @return A Set of Textures to use.
     */
    Set<TextureAtlasSprite> getHitEffects(@Nonnull RayTraceResult traceResult, IBlockState state, IBlockAccess world, BlockPos pos);

    /**
     * Used to retrieve the destroy particles for the block.
     *
     * @param state The state, getActualState and getExtendedState has been called.
     * @param world The world.
     * @param pos   The pos.
     * @return A Set of Textures to use.
     */
    Set<TextureAtlasSprite> getDestroyEffects(IBlockState state, IBlockAccess world, BlockPos pos);

}
