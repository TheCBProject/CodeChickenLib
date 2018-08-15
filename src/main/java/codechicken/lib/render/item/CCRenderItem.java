package codechicken.lib.render.item;

import codechicken.lib.internal.CCLLog;
import codechicken.lib.internal.ExceptionMessageEventHandler;
import codechicken.lib.internal.proxy.ProxyClient;
import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import codechicken.lib.render.state.GlStateTracker;
import codechicken.lib.util.LambdaUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static codechicken.lib.util.LambdaUtils.*;

/**
 * Created by covers1624 on 17/10/2016.
 */
public class CCRenderItem extends RenderItem {

    private final RenderItem parent;

    private static CCRenderItem instance;
    private static boolean hasInit;

    //Because forge has this private.
    private static final Matrix4f flipX;

    //State fields.
    private TransformType lastKnownTransformType;

    //Model lookup cache.
    private static Map<IRegistryDelegate<Item>, Int2ObjectMap<ModelResourceLocation>> immf_locationsCache;
    private static Map<IRegistryDelegate<Item>, Int2ObjectMap<IBakedModel>> immf_modelsCache;
    private static Map<Item, ItemMeshDefinition> imm_shapersCache;

    public static long lastTime = 0L;

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

    public static void notifyTransform(TransformType transformType) {
        instance.lastKnownTransformType = transformType;
    }

    //TODO 1.13
    //Until https://github.com/MinecraftForge/MinecraftForge/pull/5017 is merged.
    public static ModelResourceLocation getModelForStack(ItemStack stack) {
        pullCache();
        Item item = stack.getItem();
        if (stack.isEmpty() || item == null) {
            return ModelBakery.MODEL_MISSING;
        }
        ModelResourceLocation loc = null;
        if (immf_modelsCache.containsKey(item.delegate)) {
            loc = immf_locationsCache.get(item.delegate).get(stack.getMaxDamage() > 0 ? 0 : stack.getMetadata());
        } else {
            ItemMeshDefinition mesher = imm_shapersCache.get(item);
            if (mesher != null) {
                loc = mesher.getModelLocation(stack);
            }
        }
        if (loc == null) {
            loc = ModelBakery.MODEL_MISSING;
        }
        return loc;
    }

    @SuppressWarnings ("unchecked")
    private static void pullCache() {
        try {
            if (immf_locationsCache == null || immf_modelsCache == null || imm_shapersCache == null) {
                RenderItem renderItem = getOverridenRenderItem();
                ItemModelMesher mesher = renderItem.getItemModelMesher();
                String cls = ItemModelMesherForge.class.getName().replace(".", "/");
                String cls2 = ItemModelMesher.class.getName().replace(".", "/");
                ObfMapping locationsMapping = new ObfMapping(cls, "locations", "Ljava/util/Map;");
                ObfMapping modelsMapping = new ObfMapping(cls, "locations", "Ljava/util/Map;");
                ObfMapping shapersField = new ObfMapping(cls2, "field_178092_c", "Ljava/util/Map;");
                immf_locationsCache = ReflectionManager.getField(locationsMapping, mesher, Map.class);
                immf_modelsCache = ReflectionManager.getField(modelsMapping, mesher, Map.class);
                imm_shapersCache = ReflectionManager.getField(shapersField, mesher, Map.class);
            }
        } catch (Exception e) {
            CCLLog.log(Level.ERROR, e, "Unable to pull cache.");
            throw new RuntimeException("Unable to update cache, see log.");
        }
    }

    @SuppressWarnings ("Convert2MethodRef")//Suppress these, the lambdas need to be synthetic functions instead of a method reference.
    private void handleCaughtException(int startMatrixDepth, Throwable t, ItemStack stack) {
        Item item = stack.getItem();

        StringBuilder builder = new StringBuilder("\nCCL Has caught an exception whilst rendering an item.\n");
        builder.append("  Item Class:     ").append(tryOrNull(() -> item.getClass())).append("\n");
        builder.append("  Registry Name:  ").append(tryOrNull(() -> item.getRegistryName())).append("\n");
        builder.append("  Metadata:       ").append(stack.getMetadata()).append("\n");
        builder.append("  NBT:            ").append(tryOrNull(() -> stack.getTagCompound())).append("\n");
        builder.append("  Model Class:    ").append(tryOrNull(() -> itemModelMesher.getItemModel(stack).getClass())).append("\n");
        builder.append("  Model Location: ").append(getModelForStack(stack)).append("\n");
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
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (ProxyClient.messagePlayerOnRenderExceptionCaught && player != null) {
                long time = System.nanoTime();
                if (TimeUnit.NANOSECONDS.toSeconds(time - lastTime) > 5) {
                    lastTime = time;
                    player.sendMessage(new TextComponentString("CCL Caught an exception rendering an item. See the log for info."));
                }
            }
            int matrixDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
            if (matrixDepth != startMatrixDepth) {
                for (int i = matrixDepth; i > startMatrixDepth; i--) {
                    GlStateManager.popMatrix();
                }
            }
        } else {
            builder.append("If you want CCL to attempt to recover the game next time, enable it in the CCL config.\n");
            String logMessage = builder.toString();
            CrashReport crashReport = CrashReport.makeCrashReport(t, logMessage);
            CrashReportCategory category = crashReport.makeCategory("Item being rendered");
            category.addDetail("Item Type", () -> String.valueOf(stack.getItem()));
            category.addDetail("Item Aux", () -> String.valueOf(stack.getMetadata()));
            category.addDetail("Item NBT", () -> String.valueOf(stack.getTagCompound()));
            category.addDetail("Item Foil", () -> String.valueOf(stack.hasEffect()));
            throw new ReportedException(crashReport);
        }
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
        return ForgeHooksClient.handleCameraTransforms(model, transformType, isLeftHand);
    }

    private boolean isValidModel(IBakedModel model) {
        return model instanceof IItemRenderer;
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
            int matrixDepth = -1;
            if (ProxyClient.attemptRecoveryOnItemRenderException) {
                matrixDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
            }
            try {
                IBakedModel model = getItemModelWithOverrides(stack, null, livingBase);
                if (isValidModel(model)) {
                    this.zLevel += 50.0F;
                    this.renderItemModelIntoGUI(stack, x, y, model);
                    this.zLevel -= 50.0F;
                    return;
                }
            } catch (Throwable throwable) {
                if (ProxyClient.catchItemRenderExceptions) {
                    handleCaughtException(matrixDepth, throwable, stack);
                    return;
                }
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering IItemRenderer item");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                crashreportcategory.addDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashreportcategory.addDetail("Item Aux", () -> String.valueOf(stack.getMetadata()));
                crashreportcategory.addDetail("Item NBT", () -> String.valueOf(stack.getTagCompound()));
                crashreportcategory.addDetail("Item Foil", () -> String.valueOf(stack.hasEffect()));
                throw new ReportedException(crashreport);
            }
            try {
                parent.zLevel = zLevel;
                parent.renderItemAndEffectIntoGUI(livingBase, stack, x, y);
            } catch (Throwable t) {
                if (ProxyClient.catchItemRenderExceptions) {
                    handleCaughtException(matrixDepth, t, stack);
                    return;
                }
                throw t;
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
