package codechicken.lib.render.item;

import codechicken.lib.internal.CCLLog;
import codechicken.lib.internal.ExceptionMessageEventHandler;
import codechicken.lib.internal.proxy.ProxyClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.ItemModelMesherForge;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.util.concurrent.TimeUnit;

import static codechicken.lib.util.LambdaUtils.tryOrNull;

/**
 * Created by covers1624 on 17/10/2016.
 */
public class CCRenderItem extends ItemRenderer {

    //private final ItemRenderer parent;

    private static CCRenderItem instance;
    private static boolean hasInit;

    //Because forge has this private.
    private static final Matrix4f flipX;

    //State fields.
    private TransformType lastKnownTransformType;

    public static long lastTime = 0L;

    static {
        flipX = new Matrix4f();
        flipX.setIdentity();
        flipX.m00 = -1;
    }

    public CCRenderItem(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
        super(textureManagerIn, modelManagerIn, itemColorsIn);
        instance = this;
    }

//    public CCRenderItem(ItemRenderer renderItem) {
//        super(renderItem.textureManager, renderItem.itemModelMesher.getModelManager(), renderItem.itemColors);
//        this.parent = renderItem;
//        //Force set these to what our parent had.
//        this.itemModelMesher = renderItem.itemModelMesher;
//        this.textureManager = renderItem.textureManager;
//        this.itemColors = renderItem.itemColors;
//    }
//
//    public static void init() {
//        if (!hasInit) {
//            instance = new CCRenderItem(Minecraft.getInstance().getItemRenderer());
//            Minecraft.getInstance().itemRenderer = instance;
//            hasInit = true;
//        }
//    }

//    /**
//     * Gets the current RenderItem instance, attempts to initialize CCL's if needed.
//     *
//     * @return The current RenderItem.
//     */
//    public static ItemRenderer getOverridenItemRender() {
//        init();
//        return Minecraft.getInstance().getItemRenderer();
//    }

    public static void notifyTransform(TransformType transformType) {
        instance.lastKnownTransformType = transformType;
    }

    @SuppressWarnings ("Convert2MethodRef")//Suppress these, the lambdas need to be synthetic functions instead of a method reference.
    private void handleCaughtException(int startMatrixDepth, Throwable t, ItemStack stack) {
        Item item = stack.getItem();

        StringBuilder builder = new StringBuilder("\nCCL Has caught an exception whilst rendering an item.\n");
        builder.append("  Item Class:     ").append(tryOrNull(() -> item.getClass())).append("\n");
        builder.append("  Registry Name:  ").append(tryOrNull(() -> item.getRegistryName())).append("\n");
        builder.append("  Damage:         ").append(stack.getDamage()).append("\n");
        builder.append("  NBT:            ").append(tryOrNull(() -> stack.getTag())).append("\n");
        builder.append("  Model Class:    ").append(tryOrNull(() -> itemModelMesher.getItemModel(stack).getClass())).append("\n");
        builder.append("  Model Location: ").append(((ItemModelMesherForge) itemModelMesher).getLocation(stack)).append("\n");
        if (ProxyClient.messagePlayerOnRenderExceptionCaught) {
            builder.append("You can turn off player messages in the CCL config file.\n");
        }
        if (ProxyClient.attemptRecoveryOnItemRenderException) {
            builder.append("WARNING: Exception recovery enabled! This may cause issues down the line!\n");
            BufferBuilder vanillaBuffer = Tessellator.getInstance().getBuffer();
            if (vanillaBuffer.isDrawing) {
                vanillaBuffer.finishDrawing();
            }
            String logMessage = builder.toString();
            String key = ExceptionUtils.getStackTrace(t) + logMessage;
            if (!ExceptionMessageEventHandler.exceptionMessageCache.contains(key)) {
                ExceptionMessageEventHandler.exceptionMessageCache.add(key);
                CCLLog.log(Level.ERROR, t, logMessage);
            }
            PlayerEntity player = Minecraft.getInstance().player;
            if (ProxyClient.messagePlayerOnRenderExceptionCaught && player != null) {
                long time = System.nanoTime();
                if (TimeUnit.NANOSECONDS.toSeconds(time - lastTime) > 5) {
                    lastTime = time;
                    player.sendMessage(new StringTextComponent("CCL Caught an exception rendering an item. See the log for info."));
                }
            }
            int matrixDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
            if (matrixDepth != startMatrixDepth) {
                for (int i = matrixDepth; i > startMatrixDepth; i--) {
                    GlStateManager.popMatrix();
                }
            }
        } else {
            //TODO, Rework this exception throwing to make it a little less cancer when we catch a ReportedException.
            builder.append("If you want CCL to attempt to recover the game next time, enable it in the CCL config.\n");
            String logMessage = builder.toString();
            CrashReport crashReport = CrashReport.makeCrashReport(t, logMessage);
            CrashReportCategory category = crashReport.makeCategory("Item being rendered");
            category.addDetail("Item Type", () -> String.valueOf(item.getItem()));
            category.addDetail("Registry Name", () -> String.valueOf(item.getItem().getRegistryName()));
            category.addDetail("Item Damage", () -> String.valueOf(stack.getDamage()));
            category.addDetail("Item NBT", () -> String.valueOf(stack.getTag()));
            category.addDetail("Item Foil", () -> String.valueOf(stack.hasEffect()));
            throw new ReportedException(crashReport);
        }
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

/*    private boolean isValidModel(IBakedModel model) {
        return model instanceof IItemRenderer;
    }

    @Override
    public boolean shouldRenderItemIn3D(ItemStack stack) {
        return parent.shouldRenderItemIn3D(stack);
    }

    @Override
    public void renderItem(ItemStack stack, TransformType cameraTransformType) {
        if (!stack.isEmpty()) {
            IBakedModel bakedModel = this.getModelWithOverrides(stack);
            if (isValidModel(bakedModel)) {
                this.renderItemModel(stack, bakedModel, cameraTransformType, false);
            } else {
                parent.zLevel = this.zLevel;
                parent.renderItem(stack, cameraTransformType);
            }
        }
    }

    @Override
    public void renderItem(ItemStack stack, LivingEntity entitylivingbaseIn, TransformType transform, boolean leftHanded) {
        if (!stack.isEmpty() && entitylivingbaseIn != null) {
            IBakedModel ibakedmodel = this.getModelWithOverrides(stack, entitylivingbaseIn.world, entitylivingbaseIn);
            if (isValidModel(ibakedmodel)) {
                this.renderItemModel(stack, ibakedmodel, transform, leftHanded);
            } else {
                parent.zLevel = zLevel;
                parent.renderItem(stack, entitylivingbaseIn, transform, leftHanded);
            }
        }
    }

    @Override
    public void renderItemModel(ItemStack stack, IBakedModel bakedmodel, TransformType transform, boolean leftHanded) {
        if (!stack.isEmpty()) {
            if (isValidModel(bakedmodel)) {
                this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.pushMatrix();

                notifyTransform(transform);//CCL: Add notify.
                bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, transform, leftHanded);

                this.renderItem(stack, bakedmodel);
                GlStateManager.cullFace(GlStateManager.CullFace.BACK);
                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            } else {
                parent.zLevel = this.zLevel;
                parent.renderItemModel(stack, bakedmodel, transform, leftHanded);
            }
        }
    }

    @Override
    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
        IBakedModel bakedModel = this.getModelWithOverrides(stack);
        if (isValidModel(bakedModel)) {
            this.renderItemModelIntoGUI(stack, x, y, bakedModel);
        } else {
            parent.zLevel = this.zLevel;
            parent.renderItemIntoGUI(stack, x, y);
        }
    }

    @Override
    public void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel) {
        if (isValidModel(bakedmodel)) {
            GlStateManager.pushMatrix();
            this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlphaTest();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.setupGuiTransform(x, y, bakedmodel.isGui3d());
            notifyTransform(lastKnownTransformType);
            bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
            this.renderItem(stack, bakedmodel);
            GlStateManager.disableAlphaTest();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        } else {
            parent.zLevel = this.zLevel;
            parent.renderItemModelIntoGUI(stack, x, y, bakedmodel);
        }
    }

    @Override
    public void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition) {
        this.renderItemAndEffectIntoGUI(Minecraft.getInstance().player, stack, xPosition, yPosition);
    }

    @Override
    public void renderItemAndEffectIntoGUI(@Nullable LivingEntity entityIn, final ItemStack stack, int x, int y) {
        if (!stack.isEmpty()) {
            int matrixDepth = -1;
            if (CodeChickenLib.attemptRecoveryOnItemRenderException) {
                matrixDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
            }
            try {
                IBakedModel model = getItemModelWithOverrides(stack, null, entityIn);
                if (isValidModel(model)) {
                    this.zLevel += 50.0F;
                    this.renderItemModelIntoGUI(stack, x, y, model);
                    this.zLevel -= 50.0F;
                    return;
                }
            } catch (Throwable throwable) {
                if (CodeChickenLib.catchItemRenderExceptions) {
                    handleCaughtException(matrixDepth, throwable, stack);
                    return;
                }
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering IItemRenderer item");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                crashreportcategory.addDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashreportcategory.addDetail("Item Damage", () -> String.valueOf(stack.getDamage()));
                crashreportcategory.addDetail("Item NBT", () -> String.valueOf(stack.getTag()));
                crashreportcategory.addDetail("Item Foil", () -> String.valueOf(stack.hasEffect()));
                throw new ReportedException(crashreport);
            }
            try {
                parent.zLevel = zLevel;
                parent.renderItemAndEffectIntoGUI(entityIn, stack, x, y);
                zLevel = parent.zLevel;
            } catch (Throwable t) {
                if (CodeChickenLib.catchItemRenderExceptions) {
                    handleCaughtException(matrixDepth, t, stack);
                    return;
                }
                throw t;
            }
        }
    }*/
}
