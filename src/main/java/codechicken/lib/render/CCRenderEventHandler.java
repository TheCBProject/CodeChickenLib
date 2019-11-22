package codechicken.lib.render;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CCRenderEventHandler {

    public static int renderTime;
    public static float renderFrame;

    private static boolean hasInit = false;

    public static void init() {
        if (!hasInit) {
            MinecraftForge.EVENT_BUS.register(new CCRenderEventHandler());
            hasInit = true;
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            renderTime++;
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            renderFrame = event.renderTickTime;
        }
    }


    @Deprecated//TODO, Might not be needed anymore.
    @OnlyIn (Dist.CLIENT)
    @SubscribeEvent (priority = EventPriority.LOW)
    public void onBlockHighlight(DrawBlockHighlightEvent event) {

        //We have found a CuboidRayTraceResult, Lets render it properly..
        RayTraceResult hit = event.getTarget();
        if (hit instanceof CuboidRayTraceResult) {
            CuboidRayTraceResult cuboidHit = (CuboidRayTraceResult) hit;
            event.setCanceled(true);
            RenderUtils.renderHitBox(event.getInfo(), cuboidHit.cuboid6.copy().add(cuboidHit.getPos()));
        }
    }
}
