package codechicken.lib.render;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.raytracer.VoxelShapeRayTraceResult;
import codechicken.lib.vec.Matrix4;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
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

    @OnlyIn (Dist.CLIENT)
    @SubscribeEvent (priority = EventPriority.LOW)
    public void onBlockHighlight(DrawHighlightEvent.HighlightBlock event) {
        //We have found a CuboidRayTraceResult, Lets render it properly..
        BlockRayTraceResult hit = event.getTarget();
        if (hit instanceof CuboidRayTraceResult) {
            CuboidRayTraceResult cuboidHit = (CuboidRayTraceResult) hit;
            event.setCanceled(true);
            Matrix4 mat = new Matrix4(event.getMatrix());
            mat.translate(cuboidHit.getPos());
            RenderUtils.bufferHitbox(mat, event.getBuffers(), event.getInfo(), cuboidHit.cuboid6);
        } else if (hit instanceof VoxelShapeRayTraceResult) {
            VoxelShapeRayTraceResult voxelHit = (VoxelShapeRayTraceResult) hit;
            event.setCanceled(true);
            Matrix4 mat = new Matrix4(event.getMatrix());
            mat.translate(voxelHit.getPos());
            RenderUtils.bufferShapeHitBox(mat, event.getBuffers(), event.getInfo(), voxelHit.shape);
        }
    }
}
