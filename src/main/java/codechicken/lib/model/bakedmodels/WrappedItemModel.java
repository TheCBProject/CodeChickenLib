package codechicken.lib.model.bakedmodels;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple wrapper item model.
 * Created by covers1624 on 5/07/2017.
 */
public abstract class WrappedItemModel implements BakedModel {

    protected BakedModel wrapped;
    @Nullable
    protected LivingEntity entity;
    @Nullable
    protected ClientLevel world;

    private final ItemOverrides overrideList = new ItemOverrides() {
        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            WrappedItemModel.this.entity = entity;
            WrappedItemModel.this.world = world == null ? entity == null ? null : (ClientLevel) entity.level : null;
            return originalModel;
        }
    };

    public WrappedItemModel(BakedModel wrapped) {
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
    public ItemOverrides getOverrides() {
        return overrideList;
    }

    protected void renderWrapped(ItemStack stack, TransformType transformType, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay, boolean fabulous) {
        BakedModel model = wrapped.getOverrides().resolve(wrapped, stack, world, entity, 0);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderType rType = ItemBlockRenderTypes.getRenderType(stack, fabulous);
        VertexConsumer builder = ItemRenderer.getFoilBuffer(getter, rType, true, stack.hasFoil());
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, mStack, builder);
    }
}
