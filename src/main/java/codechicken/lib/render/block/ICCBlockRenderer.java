package codechicken.lib.render.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by covers1624 on 8/09/2016.
 * TODO, Some sort of automatic block breaking texture stuff.
 */
public interface ICCBlockRenderer {

    /**
     * Exactly the same as {@link #renderBlock} Except you MUST use the provided sprite.
     *
     * @param world  World.
     * @param pos    Position.
     * @param state  Your state.
     * @param sprite The overriden sprite.
     * @param buffer The buffer.
     */
    void handleRenderBlockDamage(IBlockAccess world, BlockPos pos, IBlockState state, TextureAtlasSprite sprite, BufferBuilder buffer);

    /**
     * Called to render your block in world.
     * You MUST use the provided VertexBuffer.
     * THE BUFFER IS ALREADY DRAWING!
     * YOU MAY BE FIRED ON THE CHUNK BATCHING THREAD!
     *
     * @param world  World.
     * @param pos    Position.
     * @param state  Your state.
     * @param buffer The buffer.
     * @return If quads were added.
     */
    boolean renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, BufferBuilder buffer);

    /**
     * Only ever called for Golems holding things, so don't really bother with it.
     *
     * @param state      State.
     * @param brightness Brightness.
     */
    void renderBrightness(IBlockState state, float brightness);

    /**
     * Register your textures.
     *
     * @param map The map!
     */
    @Deprecated //Implement IIconRegister
    void registerTextures(TextureMap map);
}
