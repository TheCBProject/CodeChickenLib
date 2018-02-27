package codechicken.lib.render.item.map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.IdentityHashMap;

/**
 * Created by covers1624 on 15/02/2017.
 */
public class MapRenderRegistry {

    private static IdentityHashMap<Item, IMapRenderer> mapRenderers = new IdentityHashMap<>();

    public static boolean shouldHandle(ItemStack stack, boolean inFrame) {
        IMapRenderer mapRenderer = mapRenderers.get(stack.getItem());
        if (mapRenderer != null) {
            MapData data = Items.FILLED_MAP.getMapData(stack, Minecraft.getMinecraft().world);
            return mapRenderer.shouldHandle(stack, data, inFrame);
        }
        return false;
    }

    public static void handleRender(ItemStack stack, boolean inFrame) {
        IMapRenderer mapRenderer = mapRenderers.get(stack.getItem());
        if (mapRenderer != null) {
            MapData data = Items.FILLED_MAP.getMapData(stack, Minecraft.getMinecraft().world);
            mapRenderer.renderMap(stack, data, inFrame);
        }
    }

    public static void registerMapRenderer(Item item, IMapRenderer mapRenderer) {
        mapRenderers.put(item, mapRenderer);
    }

    @SubscribeEvent
    public void onItemFrameRender(RenderItemInFrameEvent event) {
        if (shouldHandle(event.getItem(), true)) {
            event.setCanceled(true);
            handleRender(event.getItem(), true);
        }
    }

    @SubscribeEvent
    public void renderFirstPersonHand(RenderSpecificHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (!shouldHandle(stack, false)) {
            return;
        }
        event.setCanceled(true);
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        AbstractClientPlayer player = minecraft.player;
        EnumHand hand = event.getHand();
        float partialTicks = event.getPartialTicks();
        float interpPitch = event.getInterpolatedPitch();
        float swingProgress = event.getSwingProgress();
        float equipProgress = event.getEquipProgress();

        //Begin reimplementation of vanilla -_-
        boolean flag = hand == EnumHand.MAIN_HAND;
        EnumHandSide handSide = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();

        GlStateManager.pushMatrix();
        if(flag && minecraft.getItemRenderer().itemStackOffHand.isEmpty()) {
            float f = MathHelper.sqrt(swingProgress);
            float f1 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
            float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
            GlStateManager.translate(0.0F, -f1 / 2.0F, f2);
            float f3 = itemRenderer.getMapAngleFromPitch(interpPitch);
            GlStateManager.translate(0.0F, 0.04F + equipProgress * -1.2F + f3 * -0.5F, -0.72F);
            GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
            itemRenderer.renderArms();
            float f4 = MathHelper.sin(f * (float)Math.PI);
            GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            handleRender(stack, false);
        } else {
            float f = handSide == EnumHandSide.RIGHT ? 1.0F : -1.0F;
            GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);

            if (!player.isInvisible())
            {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
                itemRenderer.renderArmFirstPerson(equipProgress, swingProgress, handSide);
                GlStateManager.popMatrix();
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);
            float f1 = MathHelper.sqrt(swingProgress);
            float f2 = MathHelper.sin(f1 * (float)Math.PI);
            float f3 = -0.5F * f2;
            float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
            float f5 = -0.3F * MathHelper.sin(swingProgress * (float)Math.PI);
            GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
            GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
            handleRender(stack, false);
            GlStateManager.popMatrix();
            //Render side.
        }
        GlStateManager.popMatrix();
    }

}
