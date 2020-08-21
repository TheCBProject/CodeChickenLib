package codechicken.lib.internal.mixin;

import codechicken.lib.render.item.IItemRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
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

    @Inject (//
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",//
            at = @At ("HEAD"),//
            cancellable = true//
    )
    public void onRenderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHand, MatrixStack mStack, IRenderTypeBuffer buffers, int packedLight, int packedOverlay, IBakedModel modelIn, CallbackInfo ci) {
        if (modelIn instanceof IItemRenderer) {
            ci.cancel();
            mStack.push();
            //If anyone doesnt return an IItemRenderer from here, your doing it wrong.
            IItemRenderer renderer = (IItemRenderer) ForgeHooksClient.handleCameraTransforms(mStack, modelIn, transformType, leftHand);
            mStack.translate(-0.5D, -0.5D, -0.5D);
            renderer.renderItem(stack, transformType, mStack, buffers, packedLight, packedOverlay);
            mStack.pop();
        }
    }

}
