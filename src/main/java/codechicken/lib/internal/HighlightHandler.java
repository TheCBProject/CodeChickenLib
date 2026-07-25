package codechicken.lib.internal;

import codechicken.lib.colour.EnumColour;
import codechicken.lib.render.CCRenderPipelines;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.Cuboid6;
import com.mojang.blaze3d.vertex.PoseStack;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 9/06/18.
 */
public class HighlightHandler {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    private static final Cuboid6 BOX = Cuboid6.full.copy().expand(0.02);
    private static final float[] RED = EnumColour.RED.getColour(128).packArray();

    @Nullable
    public static BlockPos highlight;
    public static boolean useDepth = true;

    private static final RenderType box = RenderType.create(
            MOD_ID + ":box",
            RenderSetup.builder(CCRenderPipelines.POSITION_COLOR)
                    .createRenderSetup()
    );

    // TODO depth toggle is broken
    private static final RenderType boxNoDepth = RenderType.create(
            MOD_ID + ":box_no_depth",
            RenderSetup.builder(CCRenderPipelines.POSITION_COLOR_NO_DEPTH)
                    .createRenderSetup()
    );

    public static void init() {
        LOCK.lock();
        NeoForge.EVENT_BUS.addListener(HighlightHandler::renderLevelLast);
    }

    private static void renderLevelLast(RenderLevelStageEvent.AfterParticles event) {
        if (highlight != null) {
            MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 cameraPos = camera.position();
            PoseStack pStack = event.getPoseStack();
            pStack.pushPose();

            pStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            pStack.translate(highlight.getX(), highlight.getY(), highlight.getZ());

            RenderUtils.bufferCuboidSolid(
                    new TransformingVertexConsumer(source.getBuffer(useDepth ? box : boxNoDepth), pStack),
                    BOX,
                    RED[0], RED[1], RED[2], RED[3]
            );

            source.endBatch();

            pStack.popPose();
        }
    }

}
