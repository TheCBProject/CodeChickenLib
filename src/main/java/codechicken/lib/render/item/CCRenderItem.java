package codechicken.lib.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * Currently this class is injected with 'META-INF/coremods/item_renderer.js'
 * by replacing the instance creation of ItemRenderer, with CCRenderItem.
 * TODO, Replace with mixin.
 * <p>
 * Created by covers1624 on 17/10/2016.
 */
public class CCRenderItem extends ItemRenderer {

    public CCRenderItem(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
        super(textureManagerIn, modelManagerIn, itemColorsIn);
    }

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHand, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay, IBakedModel modelIn) {
        if (modelIn instanceof IItemRenderer) {
            mStack.push();
            IBakedModel handled = ForgeHooksClient.handleCameraTransforms(mStack, modelIn, transformType, leftHand);
            mStack.translate(-0.5D, -0.5D, -0.5D);
            //If anyone doesnt return an IItemRenderer from here, your doing it wrong.
            ((IItemRenderer) handled).renderItem(stack, transformType, mStack, getter, packedLight, packedOverlay);
            mStack.pop();
        }
        super.renderItem(stack, transformType, leftHand, mStack, getter, packedLight, packedOverlay, modelIn);
    }
}
