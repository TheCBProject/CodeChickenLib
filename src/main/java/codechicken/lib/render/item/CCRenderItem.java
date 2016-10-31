package codechicken.lib.render.item;

import codechicken.lib.asm.ObfMapping;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.util.ReflectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

import javax.annotation.Nullable;

/**
 * Created by covers1624 on 17/10/2016.
 */
public class CCRenderItem extends RenderItem {

    private final RenderItem parent;

    public CCRenderItem(RenderItem renderItem) {
        super(renderItem.textureManager, renderItem.itemModelMesher.getModelManager(), renderItem.itemColors);
        ReflectionManager.setField(new ObfMapping("net/minecraft/client/renderer/RenderItem", "field_175059_m", ""), renderItem, renderItem.itemModelMesher);
        this.parent = renderItem;
    }

    public static void init(){
        CCRenderItem renderItem = new CCRenderItem(Minecraft.getMinecraft().getRenderItem());
        ObfMapping mapping = new ObfMapping("net/minecraft/client/Minecraft", "field_175621_X", "");
        ReflectionManager.setField(mapping, Minecraft.getMinecraft(), renderItem);
    }

    @Override
    public void renderItem(ItemStack stack, IBakedModel model) {
        if (stack != null && model instanceof IItemRenderer) {
            IItemRenderer renderer = (IItemRenderer) model;
            boolean shouldHandleRender = true;
            try {//Catch AME's from new method.
                shouldHandleRender = true;//renderer.shouldHandleRender(stack);
            } catch (AbstractMethodError ignored) {
            }
            if (shouldHandleRender) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(-0.5F, -0.5F, -0.5F);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                GlStateManagerHelper.pushState();
                renderer.renderItem(stack);
                GlStateManagerHelper.popState();
                GlStateManager.popMatrix();
                return;
            }
        }
        parent.renderItem(stack, model);
    }

    private IBakedModel handleTransforms(IBakedModel model, TransformType transformType, boolean isLeftHand) {
        if (model instanceof IMatrixTransform) {
            ((IMatrixTransform) model).getTransform(transformType, isLeftHand).glApply();
        } else if (model instanceof IGLTransform) {
            ((IGLTransform) model).applyTransforms(transformType, isLeftHand);
        } else if (model instanceof IPerspectiveAwareModel) {
            model = ForgeHooksClient.handleCameraTransforms(model, transformType, isLeftHand);
        }
        return model;
    }

    @Override
    public void renderItemModel(ItemStack stack, IBakedModel bakedModel, TransformType transform, boolean leftHanded) {
        if (stack.getItem() != null) {
            if (bakedModel instanceof IItemRenderer || bakedModel instanceof IGLTransform || bakedModel instanceof IMatrixTransform) {
                this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.pushMatrix();

                bakedModel = handleTransforms(bakedModel, transform, leftHanded);

                this.renderItem(stack, bakedModel);
                GlStateManager.cullFace(GlStateManager.CullFace.BACK);
                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            } else {
                parent.renderItemModel(stack, bakedModel, transform, leftHanded);
            }
        }
    }

    @Override
    public void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedModel) {
        if (bakedModel instanceof IItemRenderer || bakedModel instanceof IGLTransform || bakedModel instanceof IMatrixTransform) {
            GlStateManager.pushMatrix();
            this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.setupGuiTransform(x, y, bakedModel.isGui3d());
            bakedModel = handleTransforms(bakedModel, ItemCameraTransforms.TransformType.GUI, false);
            this.renderItem(stack, bakedModel);
            GlStateManager.disableAlpha();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        } else {
            parent.renderItemModelIntoGUI(stack, x, y, bakedModel);
        }
    }

    // region parentOverrides
    @Override
    public void registerItems() {
        //We don't want to register any more items as we are just a wrapper.
    }

    @Override
    public void registerItem(Item item, int subType, String identifier) {
        //Pass this through because why not.
        parent.registerItem(item, subType, identifier);
    }

    @Override
    public void isNotRenderingEffectsInGUI(boolean isNot) {
        parent.isNotRenderingEffectsInGUI(isNot);
    }

    @Override
    public ItemModelMesher getItemModelMesher() {
        return parent.getItemModelMesher();
    }

    @Override
    public boolean shouldRenderItemIn3D(ItemStack stack) {
        return parent.shouldRenderItemIn3D(stack);
    }

    @Override
    public IBakedModel getItemModelWithOverrides(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entitylivingbaseIn) {
        return parent.getItemModelWithOverrides(stack, worldIn, entitylivingbaseIn);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        parent.onResourceManagerReload(resourceManager);
    }

    //endregion
}
