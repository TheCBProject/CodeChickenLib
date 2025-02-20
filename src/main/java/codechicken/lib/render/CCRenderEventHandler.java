package codechicken.lib.render;

import codechicken.lib.raytracer.VoxelShapeBlockHitResult;
import codechicken.lib.vec.Matrix4;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;

public class CCRenderEventHandler {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static int renderTime;
    public static float renderFrame;

    public static void init() {
        LOCK.lock();
        NeoForge.EVENT_BUS.addListener(CCRenderEventHandler::clientTick);
        NeoForge.EVENT_BUS.addListener(CCRenderEventHandler::renderTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, CCRenderEventHandler::onBlockHighlight);
    }

    private static void clientTick(ClientTickEvent.Post event) {
        renderTime++;
    }

    private static void renderTick(RenderFrameEvent.Pre event) {
        renderFrame = event.getPartialTick().getGameTimeDeltaPartialTick(true);
    }

    private static void onBlockHighlight(RenderHighlightEvent.Block event) {
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
