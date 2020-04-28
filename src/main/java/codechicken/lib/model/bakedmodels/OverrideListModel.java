package codechicken.lib.model.bakedmodels;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by covers1624 on 29/06/2017.
 */
public class OverrideListModel implements IBakedModel {

    private final ItemOverrideList overrides;

    public OverrideListModel(ItemOverrideList overrides) {
        this.overrides = overrides;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean func_230044_c_() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrides;
    }
}
