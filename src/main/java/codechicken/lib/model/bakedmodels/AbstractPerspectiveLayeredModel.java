package codechicken.lib.model.bakedmodels;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 13/07/2017.
 */
public abstract class AbstractPerspectiveLayeredModel extends AbstractBakedPropertiesModel {

    //The layer that designates general quads.
    protected BlockRenderLayer generalLayer;

    public AbstractPerspectiveLayeredModel(ModelProperties properties) {
        this(properties, BlockRenderLayer.SOLID);
    }

    public AbstractPerspectiveLayeredModel(ModelProperties properties, BlockRenderLayer generalLayer) {
        super(properties);
        this.generalLayer = generalLayer;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
        if (layer == null) {
            layer = generalLayer;
        }
        return getLayerQuads(state, side, layer, rand);
    }

    @Override
    protected List<BakedQuad> getAllQuads(IBlockState state) {
        List<BakedQuad> allQuads = new LinkedList<>();
        for (BlockRenderLayer layer : BlockRenderLayer.values()) {
            allQuads.addAll(getLayerQuads(state, null, layer, 0L));
            for (EnumFacing face : EnumFacing.VALUES) {
                allQuads.addAll(getLayerQuads(state, face, layer, 0L));
            }
        }
        return allQuads;
    }

    public abstract List<BakedQuad> getLayerQuads(@Nullable IBlockState state, @Nullable EnumFacing side, BlockRenderLayer layer, long rand);

}
