package codechicken.lib.model.bakedmodels;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
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
    protected RenderType generalLayer;

    public AbstractPerspectiveLayeredModel(ModelProperties properties) {
        this(properties, RenderType.solid());
    }

    public AbstractPerspectiveLayeredModel(ModelProperties properties, RenderType generalLayer) {
        super(properties);
        this.generalLayer = generalLayer;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, IModelData data) {
        RenderType layer = MinecraftForgeClient.getRenderType();
        if (layer == null) {
            layer = generalLayer;
        }
        return getLayerQuads(state, side, layer, rand, data);
    }

    @Override
    protected List<BakedQuad> getAllQuads(BlockState state, IModelData data) {
        List<BakedQuad> allQuads = new ArrayList<>();
        for (RenderType layer : RenderType.chunkBufferLayers()) {
            allQuads.addAll(getLayerQuads(state, null, layer, RandomSource.create(0), data));
            for (Direction face : Direction.BY_3D_DATA) {
                allQuads.addAll(getLayerQuads(state, face, layer, RandomSource.create(0), data));
            }
        }
        return allQuads;
    }

    public abstract List<BakedQuad> getLayerQuads(BlockState state, Direction side, RenderType layer, RandomSource rand, IModelData data);

}
