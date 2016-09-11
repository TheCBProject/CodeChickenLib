package codechicken.lib.render.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by covers1624 on 8/09/2016.
 */
public interface ICCBlockRenderer {

    void handleRenderBlockDamage(IBlockAccess world, BlockPos pos, IBlockState state, TextureAtlasSprite sprite, VertexBuffer buffer);

    boolean renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, VertexBuffer buffer);

    void renderBrightness(IBlockState state, float brightness);
}
