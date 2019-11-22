package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A simple wrapper item model.
 * Created by covers1624 on 5/07/2017.
 */
public abstract class WrappedItemModel implements IBakedModel {

    protected IBakedModel wrapped;
    @Nullable
    protected LivingEntity entity;
    @Nullable
    protected World world;

    public WrappedItemModel(IBakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }

    protected void renderWrapped(ItemStack stack) {
        IBakedModel model = wrapped.getOverrides().getModelWithOverrides(wrapped, stack, world, entity);
        ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
        renderItem.renderModel(model, stack);
    }

    private ItemOverrideList overrideList = new ItemOverrideList() {
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            WrappedItemModel.this.entity = entity;
            WrappedItemModel.this.world = world == null ? entity == null ? null : entity.world : null;
            return originalModel;
        }
    };

}
