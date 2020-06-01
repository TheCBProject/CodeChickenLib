package codechicken.lib.render.item;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

/**
 * Currently this class is injected with 'META-INF/coremods/item_renderer.js'
 * by replacing the instance creation of ItemRenderer, with CCRenderItem.
 * TODO, Replace with mixin.
 * <p>
 * Created by covers1624 on 17/10/2016.
 */
public class CCRenderItem extends ItemRenderer {

    private static CCRenderItem instance;

    private ItemCameraTransforms.TransformType lastKnownTransformType;

    public CCRenderItem(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
        super(textureManagerIn, modelManagerIn, itemColorsIn);
        instance = this;
    }

    public static void notifyTransform(ItemCameraTransforms.TransformType transformType) {
        instance.lastKnownTransformType = transformType;
    }

    @Override
    public void renderItem(ItemStack stack, IBakedModel model) {
        if (!stack.isEmpty() && model instanceof IItemRenderer) {
            IItemRenderer renderer = (IItemRenderer) model;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-0.5F, -0.5F, -0.5F);

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            renderer.renderItem(stack, lastKnownTransformType);
            GlStateManager.popMatrix();
            return;

        }
        super.renderItem(stack, model);
    }
}
