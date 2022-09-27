package codechicken.lib.model.bakedmodels;

import codechicken.lib.util.TransformUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * A simple wrapper item model.
 * Created by covers1624 on 5/07/2017.
 */
public abstract class WrappedItemModel implements BakedModel {

    protected final BakedModel wrapped;
    protected final ModelState parentState;
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
        parentState = TransformUtils.stateFromItemTransforms(wrapped.getTransforms());
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return wrapped.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return wrapped.getParticleIcon(data);
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }

    /**
     * Render the wrapped model.
     * <p>
     * This does not take into account all the special edge cases hardcoded into
     * {@link ItemRenderer#render(ItemStack, ItemTransforms.TransformType, boolean, PoseStack, MultiBufferSource, int, int, BakedModel)}.
     *
     * @param stack         The stack.
     * @param pStack        The pose stack.
     * @param buffers       The {@link MultiBufferSource}.
     * @param packedLight   The packed light coords. See {@link LightTexture}.
     * @param packedOverlay The packed Overlay coords. See {@link OverlayTexture}.
     * @param fabulous      If fabulous is required. (not sure on this desc, might be inaccurate as its value in vanilla
     *                      is mixed with the aforementioned hardcoded edge cases.)
     */
    protected void renderWrapped(ItemStack stack, PoseStack pStack, MultiBufferSource buffers, int packedLight, int packedOverlay, boolean fabulous) {
        renderWrapped(stack, pStack, buffers, packedLight, packedOverlay, fabulous, Function.identity());
    }

    /**
     * Overload of {@link #renderWrapped(ItemStack, PoseStack, MultiBufferSource, int, int, boolean)}.
     * <p>
     * Except, with a callback to wrap the {@link VertexConsumer} used.
     *
     * @param stack         The stack.
     * @param pStack        The pose stack.
     * @param buffers       The {@link MultiBufferSource}.
     * @param packedLight   The packed light coords. See {@link LightTexture}.
     * @param packedOverlay The packed Overlay coords. See {@link OverlayTexture}.
     * @param fabulous      If fabulous is required. (not sure on this desc, might be inaccurate as its value in vanilla
     *                      is mixed with the aforementioned hardcoded edge cases.)
     */
    // TODO this needs to be redesigned so other IItemRenderers can be used as override models.
    protected void renderWrapped(ItemStack stack, PoseStack pStack, MultiBufferSource buffers, int packedLight, int packedOverlay, boolean fabulous, Function<VertexConsumer, VertexConsumer> consOverride) {
        BakedModel model = wrapped.getOverrides().resolve(wrapped, stack, world, entity, 0);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderType rType = ItemBlockRenderTypes.getRenderType(stack, fabulous);
        VertexConsumer builder = ItemRenderer.getFoilBuffer(buffers, rType, true, stack.hasFoil());
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, pStack, consOverride.apply(builder));
    }
}
