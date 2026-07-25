package codechicken.lib.render;

import codechicken.lib.raytracer.VoxelShapeBlockHitResult;
import codechicken.lib.vec.Matrix4;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

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

    private static void onBlockHighlight(ExtractBlockOutlineRenderStateEvent event) {
        if (event.getHitResult() instanceof VoxelShapeBlockHitResult hit) {
            event.setCanceled(true);
            event.getLevelRenderState().blockOutlineRenderState = new BlockOutlineRenderState(
                    event.getBlockPos(),
                    event.isInTranslucentPass(),
                    event.isHighContrast(),
                    hit.shape,
                    List.of()
            );
        }
    }
}
