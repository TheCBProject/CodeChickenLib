package codechicken.lib.model.bakedmodels;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
        if (layer == null) {
            layer = generalLayer;
        }
        return getLayerQuads(state, side, layer, rand, data);
    }

    @Override
    protected List<BakedQuad> getAllQuads(BlockState state, IModelData data) {
        List<BakedQuad> allQuads = new ArrayList<>();
        for (BlockRenderLayer layer : BlockRenderLayer.values()) {
            allQuads.addAll(getLayerQuads(state, null, layer, new Random(0), data));
            for (Direction face : Direction.BY_INDEX) {
                allQuads.addAll(getLayerQuads(state, face, layer, new Random(0), data));
            }
        }
        return allQuads;
    }

    public abstract List<BakedQuad> getLayerQuads(BlockState state, Direction side, BlockRenderLayer layer, Random rand, IModelData data);

}
