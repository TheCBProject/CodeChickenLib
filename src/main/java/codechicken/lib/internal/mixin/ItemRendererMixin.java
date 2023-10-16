package codechicken.lib.internal.mixin;

import codechicken.lib.render.item.IItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link IItemRenderer} model extension mixin.
 * <p>
 * Created by covers1624 on 9/8/20.
 */
@Mixin (ItemRenderer.class)
public class ItemRendererMixin {

    @Inject (
            method = "render",
            at = @At ("HEAD"),
            cancellable = true
    )
    public void onRenderItem(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack mStack, MultiBufferSource buffers, int packedLight, int packedOverlay, BakedModel modelIn, CallbackInfo ci) {
        if (modelIn instanceof IItemRenderer) {
            ci.cancel();
            mStack.pushPose();
            //If anyone doesnt return an IItemRenderer from here, your doing it wrong.
            IItemRenderer renderer = (IItemRenderer) ForgeHooksClient.handleCameraTransforms(mStack, modelIn, context, leftHand);
            mStack.translate(-0.5D, -0.5D, -0.5D);
            renderer.renderItem(stack, context, mStack, buffers, packedLight, packedOverlay);
            mStack.popPose();
        }
    }

}
