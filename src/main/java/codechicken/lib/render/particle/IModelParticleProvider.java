package codechicken.lib.render.particle;

import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by covers1624 on 12/07/2017.
 */
public interface IModelParticleProvider extends BakedModel {

    @Override
    default TextureAtlasSprite getParticleIcon() {
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
    Set<TextureAtlasSprite> getHitEffects(@Nonnull BlockHitResult traceResult, BlockState state, BlockAndTintGetter world, BlockPos pos, IModelData modelData);

    /**
     * Used to retrieve the destroy particles for the block.
     *
     * @param state The state, getActualState and getExtendedState has been called.
     * @param world The world.
     * @param pos   The pos.
     * @return A Set of Textures to use.
     */
    Set<TextureAtlasSprite> getDestroyEffects(BlockState state, BlockAndTintGetter world, BlockPos pos, IModelData data);

}
