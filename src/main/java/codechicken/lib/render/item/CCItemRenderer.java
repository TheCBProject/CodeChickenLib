package codechicken.lib.render.item;

import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import codechicken.lib.render.item.map.MapRenderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

/**
 * Created by covers1624 on 2/07/2017.
 */
public class CCItemRenderer extends ItemRenderer {

    private ItemRenderer wrapped;
    private static CCItemRenderer INSTANCE;

    public CCItemRenderer(ItemRenderer parent) {
        super(parent.mc);
        this.wrapped = parent;
        ObfMapping mapping = new ObfMapping("net/minecraft/client/renderer/ItemRenderer", "field_178111_g");
        ReflectionManager.setField(mapping, this, parent.renderManager);
        mapping = new ObfMapping("net/minecraft/client/renderer/ItemRenderer", "field_178112_h");
        ReflectionManager.setField(mapping, this, parent.itemRenderer);
    }

    public static void initialize() {
        Minecraft minecraft = Minecraft.getMinecraft();
        INSTANCE = new CCItemRenderer(Minecraft.getMinecraft().getItemRenderer());
        ObfMapping mapping;
        mapping = new ObfMapping("net/minecraft/client/Minecraft", "field_175620_Y");
        ReflectionManager.setField(mapping, minecraft, INSTANCE);
        mapping = new ObfMapping("net/minecraft/client/renderer/EntityRenderer", "field_78516_c");
        ReflectionManager.setField(mapping, minecraft.entityRenderer, INSTANCE);
    }

    private void push() {
        wrapped.itemStackMainHand = this.itemStackMainHand;
        wrapped.itemStackOffHand = this.itemStackOffHand;
        wrapped.equippedProgressMainHand = this.equippedProgressMainHand;
        wrapped.prevEquippedProgressMainHand = this.prevEquippedProgressMainHand;
        wrapped.equippedProgressOffHand = this.equippedProgressOffHand;
        wrapped.prevEquippedProgressOffHand = this.prevEquippedProgressOffHand;
    }

    private void pull() {
        this.itemStackMainHand = wrapped.itemStackMainHand;
        this.itemStackOffHand = wrapped.itemStackOffHand;
        this.equippedProgressMainHand = wrapped.equippedProgressMainHand;
        this.prevEquippedProgressMainHand = wrapped.prevEquippedProgressMainHand;
        this.equippedProgressOffHand = wrapped.equippedProgressOffHand;
        this.prevEquippedProgressOffHand = wrapped.prevEquippedProgressOffHand;
    }

    @Override
    public void renderMapFirstPerson(ItemStack stack) {
        push();
        if (MapRenderRegistry.shouldHandle(stack, false)) {
            MapRenderRegistry.handleRender(stack, false);
        } else {
            wrapped.renderMapFirstPerson(stack);
        }
        pull();
    }

    //All of these are essentially "push, wrapped.call, pull"
    //region Wrapped Overrides.
    //@formatter:off
    @Override public void renderItem(EntityLivingBase a, ItemStack b, TransformType c) {push();wrapped.renderItem(a, b, c);pull();}
    @Override public void renderItemSide(EntityLivingBase a, ItemStack b, TransformType c, boolean d) {push();wrapped.renderItemSide(a, b, c, d);pull();}
    @Override public void rotateArroundXAndY(float a, float b) {push();wrapped.rotateArroundXAndY(a, b);pull();}
    @Override public void setLightmap() {push();wrapped.setLightmap();pull();}
    @Override public void rotateArm(float a) {push();wrapped.rotateArm(a);pull();}
    @Override public float getMapAngleFromPitch(float a) {push();float angle = wrapped.getMapAngleFromPitch(a);pull();return angle;}
    @Override public void renderArms() {push();wrapped.renderArms();pull();}
    @Override public void renderArm(EnumHandSide a) {push();wrapped.renderArm(a);pull();}
    @Override public void renderMapFirstPersonSide(float a, EnumHandSide b, float c, ItemStack d) {push();wrapped.renderMapFirstPersonSide(a, b, c, d);pull();}
    @Override public void renderMapFirstPerson(float a, float b, float c) {push();wrapped.renderMapFirstPerson(a, b, c);pull();}
    @Override public void renderArmFirstPerson(float a, float b, EnumHandSide c) {push();wrapped.renderArmFirstPerson(a, b, c);pull();}
    @Override public void transformEatFirstPerson(float a, EnumHandSide b, ItemStack c) {push();wrapped.transformEatFirstPerson(a, b, c);pull();}
    @Override public void transformFirstPerson(EnumHandSide a, float b) {push();wrapped.transformFirstPerson(a, b);pull();}
    @Override public void transformSideFirstPerson(EnumHandSide a, float b) {push();wrapped.transformSideFirstPerson(a, b);pull();}
    @Override public void renderItemInFirstPerson(float a) {push();wrapped.renderItemInFirstPerson(a);pull();}
    @Override public void renderItemInFirstPerson(AbstractClientPlayer a, float b, float c, EnumHand d, float e, ItemStack f, float g) {push();wrapped.renderItemInFirstPerson(a, b, c, d, e, f, g);pull();}
    @Override public void renderOverlays(float partialTicks) {push();wrapped.renderOverlays(partialTicks);pull();}
    @Override public void renderBlockInHand(TextureAtlasSprite a) {push();wrapped.renderBlockInHand(a);pull();}
    @Override public void renderWaterOverlayTexture(float a) {push();wrapped.renderWaterOverlayTexture(a);pull();}
    @Override public void renderFireInFirstPerson() {push();wrapped.renderFireInFirstPerson();pull();}
    @Override public void updateEquippedItem() {push();wrapped.updateEquippedItem();pull();}
    @Override public void resetEquippedProgress(EnumHand a) {push();wrapped.resetEquippedProgress(a);pull();}
    //@formatter:on
    //endregion
}
