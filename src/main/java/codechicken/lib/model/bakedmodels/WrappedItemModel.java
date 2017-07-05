package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A simple wrapper item model.
 * Created by covers1624 on 5/07/2017.
 */
public abstract class WrappedItemModel implements IBakedModel {

    protected IBakedModel wrapped;
    @Nullable
    protected EntityLivingBase entity;
    @Nullable
    protected World world;

    public WrappedItemModel(Supplier<ModelResourceLocation> wrappedModel) {
        ModelRegistryHelper.registerPreBakeCallback(modelRegistry -> wrapped = modelRegistry.getObject(wrappedModel.get()));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return new ArrayList<>();
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
        IBakedModel model = wrapped.getOverrides().handleItemState(wrapped, stack, world, entity);
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.renderModel(model, stack);
    }

    private ItemOverrideList overrideList = new ItemOverrideList() {
        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            WrappedItemModel.this.entity = entity;
            WrappedItemModel.this.world = world == null ? entity == null ? null : entity.world : null;
            return originalModel;
        }
    };

}
