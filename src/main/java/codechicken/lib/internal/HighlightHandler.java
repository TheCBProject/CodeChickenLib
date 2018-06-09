package codechicken.lib.internal;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.colour.Colour;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

/**
 * Created by covers1624 on 9/06/18.
 */
@EventBusSubscriber (value = Side.CLIENT, modid = CodeChickenLib.MOD_ID)
public class HighlightHandler {

    public static final Cuboid6 BOX = Cuboid6.full.copy().expand(0.02);
    public static final Colour RED = EnumColour.RED.getColour(128);

    public static BlockPos highlight;
    public static boolean useDepth = true;

    @SubscribeEvent
    public static void renderWorldLast(RenderWorldLastEvent event) {
        if (highlight != null) {
            GlStateManager.pushMatrix();
            RenderUtils.translateToWorldCoords(Minecraft.getMinecraft().getRenderViewEntity(), event.getPartialTicks());
            GlStateManager.translate(highlight.getX(), highlight.getY(), highlight.getZ());

            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);
            if (!useDepth) {
                GlStateManager.disableDepth();
            }
            RED.glColour();
            RenderUtils.drawCuboidSolid(BOX);

            GlStateManager.enableTexture2D();
            GL11.glPopAttrib();

            GlStateManager.popMatrix();
        }
    }
}
