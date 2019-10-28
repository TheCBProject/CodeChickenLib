package codechicken.lib.internal;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.colour.Colour;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.opengl.GL11;

/**
 * Created by covers1624 on 9/06/18.
 */
@EventBusSubscriber (value = Dist.CLIENT, modid = CodeChickenLib.MOD_ID)
public class HighlightHandler {

    public static final Cuboid6 BOX = Cuboid6.full.copy().expand(0.02);
    public static final Colour RED = EnumColour.RED.getColour(128);
    public static final Colour BLACK = EnumColour.BLACK.getColour(128);

    public static BlockPos highlight;
    public static boolean useDepth = true;

    @SubscribeEvent
    public static void renderWorldLast(RenderWorldLastEvent event) {
        if (highlight != null) {
            GlStateManager.pushMatrix();
            RenderUtils.translateToWorldCoords(Minecraft.getInstance().getRenderViewEntity(), event.getPartialTicks());
            GlStateManager.translated(highlight.getX(), highlight.getY(), highlight.getZ());

            GlStateManager.disableTexture();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);
            if (!useDepth) {
                GlStateManager.disableDepthTest();
            }
            RED.glColour();
            RenderUtils.drawCuboidSolid(BOX);
            BLACK.glColour();
            RenderUtils.drawCuboidOutline(BOX);

            if (!useDepth) {
                GlStateManager.enableDepthTest();
            }
            GlStateManager.depthMask(true);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture();

            GlStateManager.popMatrix();
        }
    }
}
