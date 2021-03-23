package codechicken.lib.model.bakedmodels;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple wrapper item model.
 * Created by covers1624 on 5/07/2017.
 */
public abstract class WrappedItemModel implements IBakedModel {

    protected IBakedModel wrapped;
    @Nullable
    protected LivingEntity entity;
    @Nullable
    protected ClientWorld world;

    private final ItemOverrideList overrideList = new ItemOverrideList() {
        @Override
        public IBakedModel resolve(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
            WrappedItemModel.this.entity = entity;
            WrappedItemModel.this.world = world == null ? entity == null ? null : (ClientWorld) entity.level : null;
            return originalModel;
        }
    };

    public WrappedItemModel(IBakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrideList;
    }

    protected void renderWrapped(ItemStack stack, TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay, boolean fabulous) {
        IBakedModel model = wrapped.getOverrides().resolve(wrapped, stack, world, entity);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderType rType = RenderTypeLookup.getRenderType(stack, fabulous);
        IVertexBuilder builder = ItemRenderer.getFoilBuffer(getter, rType, true, stack.hasFoil());
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, mStack, builder);
    }
}
