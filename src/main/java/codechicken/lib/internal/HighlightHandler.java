//package codechicken.lib.internal;
//
//import codechicken.lib.CodeChickenLib;
//import codechicken.lib.colour.Colour;
//import codechicken.lib.colour.EnumColour;
//import codechicken.lib.render.RenderUtils;
//import codechicken.lib.vec.Cuboid6;
//import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.platform.GlStateManager;
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.Minecraft;
//import net.minecraft.util.math.BlockPos;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.RenderWorldLastEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
//import org.lwjgl.opengl.GL11;
//
///**
// * Created by covers1624 on 9/06/18.
// */
//@EventBusSubscriber (value = Dist.CLIENT, modid = CodeChickenLib.MOD_ID)
//public class HighlightHandler {
//
//    public static final Cuboid6 BOX = Cuboid6.full.copy().expand(0.02);
//    public static final Colour RED = EnumColour.RED.getColour(128);
//    public static final Colour BLACK = EnumColour.BLACK.getColour(128);
//
//    public static BlockPos highlight;
//    public static boolean useDepth = true;
//
//    @SubscribeEvent
//    public static void renderWorldLast(RenderWorldLastEvent event) {
//        if (highlight != null) {
//            MatrixStack stack = event.getMatrixStack();
//            stack.push();
//            stack.translate(highlight.getX(), highlight.getY(), highlight.getZ());
//
//            RenderSystem.disableTexture();
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            RenderSystem.depthMask(false);
//            if (!useDepth) {
//                RenderSystem.disableDepthTest();
//            }
//            //RED.glColour();
//            //RenderUtils.drawCuboidSolid(BOX, stack);
//            //BLACK.glColour();
//            //RenderUtils.drawCuboidOutline(BOX, stack);
//
//            if (!useDepth) {
//                RenderSystem.enableDepthTest();
//            }
//            RenderSystem.depthMask(true);
//            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//            RenderSystem.disableBlend();
//            RenderSystem.enableTexture();
//
//            stack.pop();
//        }
//    }
//}
