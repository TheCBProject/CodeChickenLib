package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.lib.geometry.Rectangle;
import codechicken.lib.render.CCRenderEventHandler;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 15/11/2023
 */
public class GuiEntityRenderer extends GuiElement<GuiEntityRenderer> implements BackgroundRender {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, Entity> entityCache = new HashMap<>();
    private static final List<ResourceLocation> invalidEntities = new ArrayList<>();

    private Supplier<Float> rotationSpeed = () -> 1F;
    private Supplier<Float> lockedRotation = () -> 0F;
    private Entity entity;
    private ResourceLocation entityName;
    private boolean invalidEntity = false;
    private Supplier<Boolean> rotationLocked = () -> false;
    private Supplier<Boolean> trackMouse = () -> false;
    private Supplier<Boolean> drawName = () -> false;
    public boolean force2dSize = false;

    public GuiEntityRenderer(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    public GuiEntityRenderer setEntity(Entity entity) {
        this.entity = entity;
        if (this.entity == null) {
            invalidEntity = true;
            return this;
        }

        this.entityName = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        invalidEntity = invalidEntities.contains(entityName);
        return this;
    }

    public GuiEntityRenderer setEntity(ResourceLocation entity) {
        this.entityName = entity;
        this.entity = entityCache.computeIfAbsent(entity, resourceLocation -> {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(entity);
            return type == null ? null : type.create(mc().level);
        });

        invalidEntity = this.entity == null;
        if (invalidEntities.contains(entityName)) {
            invalidEntity = true;
        }

        return this;
    }

    public GuiEntityRenderer setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = () -> rotationSpeed;
        return this;
    }

    public GuiEntityRenderer setRotationSpeed(Supplier<Float> rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
        return this;
    }

    public float getRotationSpeed() {
        return rotationSpeed.get();
    }

    public GuiEntityRenderer setLockedRotation(float lockedRotation) {
        this.lockedRotation = () -> lockedRotation;
        return this;
    }

    public GuiEntityRenderer setLockedRotation(Supplier<Float> lockedRotation) {
        this.lockedRotation = lockedRotation;
        return this;
    }

    public float getLockedRotation() {
        return lockedRotation.get();
    }

    public GuiEntityRenderer setRotationLocked(boolean rotationLocked) {
        this.rotationLocked = () -> rotationLocked;
        return this;
    }

    public GuiEntityRenderer setRotationLocked(Supplier<Boolean> rotationLocked) {
        this.rotationLocked = rotationLocked;
        return this;
    }

    public boolean isRotationLocked() {
        return rotationLocked.get();
    }

    public GuiEntityRenderer setTrackMouse(boolean trackMouse) {
        this.trackMouse = () -> trackMouse;
        return this;
    }

    public GuiEntityRenderer setTrackMouse(Supplier<Boolean> trackMouse) {
        this.trackMouse = trackMouse;
        return this;
    }

    public boolean isTrackMouse() {
        return trackMouse.get();
    }

    public GuiEntityRenderer setDrawName(boolean drawName) {
        this.drawName = () -> drawName;
        return this;
    }

    public GuiEntityRenderer setDrawName(Supplier<Boolean> drawName) {
        this.drawName = drawName;
        return this;
    }

    public boolean isDrawName() {
        return drawName.get();
    }

    public GuiEntityRenderer setForce2dSize(boolean force2dSize) {
        this.force2dSize = force2dSize;
        return this;
    }

    @Override
    public double getBackgroundDepth() {
        Rectangle rect = getRectangle();
        float scale = (float) (force2dSize ? (Math.min(rect.height() / entity.getBbHeight(), rect.width() / entity.getBbWidth())) : rect.height() / entity.getBbHeight());
        return scale * 2;
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        if (invalidEntity) return;

        try {
            if (entity != null) {
                Rectangle rect = getRectangle();
                float scale = (float) (force2dSize ? (Math.min(rect.height() / entity.getBbHeight(), rect.width() / entity.getBbWidth())) : rect.height() / entity.getBbHeight());
                float xPos = (float) (rect.x() + (rect.width() / 2D));
                float yPos = (float) ((yMin() + (ySize() / 2)) + (rect.height() / 2));
                float rotation = rotationLocked.get() ? lockedRotation.get() : (CCRenderEventHandler.renderTime + partialTicks) * rotationSpeed.get();
                if (entity instanceof LivingEntity living) {
                    int eyeOffset = (int) ((entity.getEyeHeight()) * scale);
                    if (trackMouse.get()) {
                        renderEntityInInventoryFollowsMouse(render, xPos, yPos, scale, xPos - (float) mouseX, yPos - (float) mouseY - eyeOffset, living);
                    } else {
                        renderEntityInInventoryWithRotation(render, xPos, yPos, scale, rotation, living);
                    }
                }
            }
        } catch (Throwable e) {
            invalidEntity = true;
            invalidEntities.add(entityName);
            LOGGER.error("Failed to render entity in GUI. This is not a bug there are just some entities that can not be rendered like this.");
            LOGGER.error("Entity: " + entity, e);
        }
    }

    public static void renderEntityInInventoryFollowsMouse(GuiRender render, double pX, double pY, double pScale, float offsetX, float offsetY, LivingEntity pEntity) {
        float xAngle = (float)Math.atan(offsetX / 40.0F);
        float yAngle = (float)Math.atan(offsetY / 40.0F);
        renderEntityInInventoryFollowsAngle(render, pX, pY, pScale, xAngle, yAngle, pEntity);
    }

    public static void renderEntityInInventoryFollowsAngle(GuiRender render, double pX, double pY, double pScale, float angleX, float angleY, LivingEntity pEntity) {
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(angleY * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        float f2 = pEntity.yBodyRot;
        float f3 = pEntity.getYRot();
        float f4 = pEntity.getXRot();
        float f5 = pEntity.yHeadRotO;
        float f6 = pEntity.yHeadRot;
        pEntity.yBodyRot = 180.0F + angleX * 20.0F;
        pEntity.setYRot(180.0F + angleX * 40.0F);
        pEntity.setXRot(-angleY * 20.0F);
        pEntity.yHeadRot = pEntity.getYRot();
        pEntity.yHeadRotO = pEntity.getYRot();
        renderEntityInInventory(render, pX, pY, pScale, quaternionf, quaternionf1, pEntity);
        pEntity.yBodyRot = f2;
        pEntity.setYRot(f3);
        pEntity.setXRot(f4);
        pEntity.yHeadRotO = f5;
        pEntity.yHeadRot = f6;
    }

    public static void renderEntityInInventoryWithRotation(GuiRender render, double xPos, double yPos, double scale, double rotation, LivingEntity living) {
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = Axis.YP.rotationDegrees((float) rotation);
        quaternionf.mul(quaternionf1);
        float f2 = living.yBodyRot;
        float f3 = living.getYRot();
        float f4 = living.getXRot();
        float f5 = living.yHeadRotO;
        float f6 = living.yHeadRot;
        living.yBodyRot = 180.0F;
        living.setYRot(180.0F);
        living.setXRot(0);
        living.yHeadRot = living.getYRot();
        living.yHeadRotO = living.getYRot();
        renderEntityInInventory(render, xPos, yPos, scale, quaternionf, quaternionf1, living);
        living.yBodyRot = f2;
        living.setYRot(f3);
        living.setXRot(f4);
        living.yHeadRotO = f5;
        living.yHeadRot = f6;
    }

    public static void renderEntityInInventory(GuiRender render, double pX, double pY, double pScale, Quaternionf quat, @Nullable Quaternionf pCameraOrientation, LivingEntity pEntity) {
        render.pose().pushPose();
        render.pose().translate(pX, pY, 50.0D);
        render.pose().mulPoseMatrix((new Matrix4f()).scaling((float)pScale, (float)pScale, (float)(-pScale)));
        render.pose().mulPose(quat);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (pCameraOrientation != null) {
            pCameraOrientation.conjugate();
            entityrenderdispatcher.overrideCameraOrientation(pCameraOrientation);
        }

        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(pEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, render.pose(), render.buffers(), 15728880));
        render.flush();
        entityrenderdispatcher.setRenderShadow(true);
        render.pose().popPose();
        Lighting.setupFor3DItems();
    }
}
