package codechicken.lib.render.item;

import codechicken.lib.asm.ObfMapping;
import codechicken.lib.render.state.GlStateTracker;
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
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

/**
 * Created by covers1624 on 17/10/2016.
 * TODO, Generify how this works. might be different from vanilla in the end but we should probably try and sniff off if the model is ours at an earlier date.
 * TODO, Some sort of registry over IMC for mods to add their own hooks to this.
 */
public class CCRenderItem extends RenderItem {

    private final RenderItem parent;
    private static CCRenderItem instance;
    private static boolean hasInit;

    //Because forge has this private.
    private static final Matrix4f flipX;

    //State fields.
    private TransformType lastKnownTransformType;

    static {
        flipX = new Matrix4f();
        flipX.setIdentity();
        flipX.m00 = -1;
    }

    public CCRenderItem(RenderItem renderItem) {
        super(renderItem.textureManager, renderItem.itemModelMesher.getModelManager(), renderItem.itemColors);
        this.parent = renderItem;
    }

    public static void init() {
        if (!hasInit) {
            instance = new CCRenderItem(Minecraft.getMinecraft().getRenderItem());
            ObfMapping mapping = new ObfMapping("net/minecraft/client/Minecraft", "field_175621_X", "");
            ReflectionManager.setField(mapping, Minecraft.getMinecraft(), instance);
            hasInit = true;
        }
    }

    /**
     * Gets the current RenderItem instance, attempts to initialize CCL's if needed.
     *
     * @return The current RenderItem.
     */
    public static RenderItem getOverridenRenderItem() {
        init();
        return Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void renderItem(ItemStack stack, IBakedModel model) {
        if (!stack.isEmpty() && model instanceof IItemRenderer) {
            IItemRenderer renderer = (IItemRenderer) model;
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateTracker.pushState();
            renderer.renderItem(stack, lastKnownTransformType);
            GlStateTracker.popState();
            GlStateManager.popMatrix();
            return;

        }
        parent.renderItem(stack, model);
    }

    private IBakedModel handleTransforms(ItemStack stack, IBakedModel model, TransformType transformType, boolean isLeftHand) {
        lastKnownTransformType = transformType;
        if (model instanceof IMatrixTransform) {
            ((IMatrixTransform) model).getTransform(transformType, isLeftHand).glApply();
        } else if (model instanceof IGLTransform) {
            ((IGLTransform) model).applyTransforms(transformType, isLeftHand);
        }
        return model = ForgeHooksClient.handleCameraTransforms(model, transformType, isLeftHand);
    }

    private boolean isValidModel(IBakedModel model) {
        return model instanceof IItemRenderer || model instanceof IGLTransform || model instanceof IMatrixTransform;
    }

    @Override
    public void renderItemModel(ItemStack stack, IBakedModel bakedModel, TransformType transform, boolean leftHanded) {
        if (!stack.isEmpty()) {
            if (isValidModel(bakedModel)) {
                this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.pushMatrix();

                bakedModel = handleTransforms(stack, bakedModel, transform, leftHanded);

                this.renderItem(stack, bakedModel);
                GlStateManager.cullFace(GlStateManager.CullFace.BACK);
                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            } else {
                parent.zLevel = this.zLevel;
                parent.renderItemModel(stack, bakedModel, transform, leftHanded);
            }
        }
    }

    @Override
    public void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedModel) {
        if (isValidModel(bakedModel)) {
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

            bakedModel = handleTransforms(stack, bakedModel, ItemCameraTransforms.TransformType.GUI, false);

            this.renderItem(stack, bakedModel);
            GlStateManager.disableAlpha();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        } else {
            parent.zLevel = this.zLevel;
            parent.renderItemModelIntoGUI(stack, x, y, bakedModel);
        }
    }

    // region Other Overrides

    @Override
    public void renderItem(ItemStack stack, TransformType cameraTransformType) {
        if (!stack.isEmpty()) {
            IBakedModel bakedModel = this.getItemModelWithOverrides(stack, null, null);
            if (isValidModel(bakedModel)) {
                this.renderItemModel(stack, bakedModel, cameraTransformType, false);
            }
            parent.zLevel = this.zLevel;
            parent.renderItem(stack, cameraTransformType);
        }
    }

    @Override
    public void renderItem(ItemStack stack, EntityLivingBase livingBase, TransformType transform, boolean leftHanded) {
        if (!stack.isEmpty() && livingBase != null) {
            IBakedModel bakedModel = this.getItemModelWithOverrides(stack, livingBase.world, livingBase);
            if (isValidModel(bakedModel)) {
                this.renderItemModel(stack, bakedModel, transform, leftHanded);
            } else {
                parent.zLevel = this.zLevel;
                parent.renderItem(stack, livingBase, transform, leftHanded);
            }
        }
    }

    @Override
    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
        IBakedModel bakedModel = this.getItemModelWithOverrides(stack, null, null);
        if (isValidModel(bakedModel)) {
            this.renderItemModelIntoGUI(stack, x, y, bakedModel);
        } else {
            parent.zLevel = this.zLevel;
            parent.renderItemIntoGUI(stack, x, y);
        }
    }

    @Override
    public void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition) {
        this.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, stack, xPosition, yPosition);
    }

    @Override
    public void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase livingBase, final ItemStack stack, int x, int y) {
        if (!stack.isEmpty()) {
            try {

                IBakedModel model = this.getItemModelWithOverrides(stack, null, livingBase);
                if (isValidModel(model)) {
                    this.zLevel += 50.0F;
                    this.renderItemModelIntoGUI(stack, x, y, model);
                    this.zLevel -= 50.0F;
                } else {
                    parent.zLevel = this.zLevel;
                    parent.renderItemAndEffectIntoGUI(livingBase, stack, x, y);
                }

            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                crashreportcategory.addDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashreportcategory.addDetail("Item Aux", () -> String.valueOf(stack.getMetadata()));
                crashreportcategory.addDetail("Item NBT", () -> String.valueOf(stack.getTagCompound()));
                crashreportcategory.addDetail("Item Foil", () -> String.valueOf(stack.hasEffect()));
                throw new ReportedException(crashreport);
            }
        }
    }

    // endregion

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
