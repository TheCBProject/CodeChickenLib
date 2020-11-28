package codechicken.lib.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class FontUtils {

    public static FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

    public static void drawCenteredString(MatrixStack mStack, String s, int xCenter, int y, int colour) {
        fontRenderer.drawStringWithShadow(mStack, s, xCenter - fontRenderer.getStringWidth(s) / 2, y, colour);
    }

    public static void drawRightString(MatrixStack mStack, String s, int xRight, int y, int colour) {
        fontRenderer.drawStringWithShadow(mStack, s, xRight - fontRenderer.getStringWidth(s), y, colour);
    }

//    public static final String[] prefixes = new String[] { "K", "M", "G" };
//
//    public static void drawItemQuantity(int x, int y, @Nonnull ItemStack item, String quantity, int mode) {
//        if (item.isEmpty() || (quantity == null && item.getCount() <= 1)) {
//            return;
//        }
//
//        if (quantity == null) {
//            switch (mode) {
//                case 2:
//                    int q = item.getCount();
//                    String postfix = "";
//                    for (int p = 0; p < 3 && q > 1000; p++) {
//                        q /= 1000;
//                        postfix = prefixes[p];
//                    }
//                    quantity = Integer.toString(q) + postfix;
//                    break;
//                case 1:
//                    quantity = "";
//                    if (item.getCount() / 64 > 0) {
//                        quantity += item.getCount() / 64 + "s";
//                    }
//                    if (item.getCount() % 64 > 0) {
//                        quantity += item.getCount() % 64;
//                    }
//                    break;
//                default:
//                    quantity = Integer.toString(item.getCount());
//                    break;
//            }
//        }
//
//        double scale = quantity.length() > 2 ? 0.5 : 1;
//        double sheight = 8 * scale;
//        double swidth = fontRenderer.getStringWidth(quantity) * scale;
//
//        GlStateManager.disableLighting();
//        GlStateManager.disableDepthTest();
//        GlStateManager.pushMatrix();
//        GlStateManager.translated(x + 16 - swidth, y + 16 - sheight, 0);
//        GlStateManager.scaled(scale, scale, 1);
//        fontRenderer.drawStringWithShadow(quantity, 0, 0, 0xFFFFFF);
//        GlStateManager.popMatrix();
//        GlStateManager.enableLighting();
//        GlStateManager.enableDepthTest();
//    }
}
