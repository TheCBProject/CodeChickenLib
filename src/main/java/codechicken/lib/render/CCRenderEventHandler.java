package codechicken.lib.render;

import codechicken.lib.gui.modular.lib.CursorHelper;
import codechicken.lib.raytracer.VoxelShapeBlockHitResult;
import codechicken.lib.vec.Matrix4;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.ScreenEvent;
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
    public void onBlockHighlight(RenderHighlightEvent.Block event) {
        //We have found a CuboidRayTraceResult, Lets render it properly..
        BlockHitResult hit = event.getTarget();
        if (hit instanceof VoxelShapeBlockHitResult voxelHit) {
            event.setCanceled(true);
            Matrix4 mat = new Matrix4(event.getPoseStack());
            mat.translate(voxelHit.getBlockPos());
            RenderUtils.bufferShapeHitBox(mat, event.getMultiBufferSource(), event.getCamera(), voxelHit.shape);
        }
    }
}
