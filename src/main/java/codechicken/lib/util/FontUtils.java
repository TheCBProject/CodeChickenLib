package codechicken.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class FontUtils {

    public static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    public static void drawCenteredString(String s, int xCenter, int y, int colour) {
        fontRenderer.drawString(s, xCenter - fontRenderer.getStringWidth(s) / 2, y, colour);
    }

    public static void drawRightString(String s, int xRight, int y, int colour) {
        fontRenderer.drawString(s, xRight - fontRenderer.getStringWidth(s), y, colour);
    }

    public static final String[] prefixes = new String[] { "K", "M", "G" };

    public static void drawItemQuantity(int x, int y, @Nonnull ItemStack item, String quantity, int mode) {
        if (item.isEmpty() || (quantity == null && item.getCount() <= 1)) {
            return;
        }

        if (quantity == null) {
            switch (mode) {
                case 2:
                    int q = item.getCount();
                    String postfix = "";
                    for (int p = 0; p < 3 && q > 1000; p++) {
                        q /= 1000;
                        postfix = prefixes[p];
                    }
                    quantity = Integer.toString(q) + postfix;
                    break;
                case 1:
                    quantity = "";
                    if (item.getCount() / 64 > 0) {
                        quantity += item.getCount() / 64 + "s";
                    }
                    if (item.getCount() % 64 > 0) {
                        quantity += item.getCount() % 64;
                    }
                    break;
                default:
                    quantity = Integer.toString(item.getCount());
                    break;
            }
        }

        double scale = quantity.length() > 2 ? 0.5 : 1;
        double sheight = 8 * scale;
        double swidth = fontRenderer.getStringWidth(quantity) * scale;

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 16 - swidth, y + 16 - sheight, 0);
        GlStateManager.scale(scale, scale, 1);
        fontRenderer.drawStringWithShadow(quantity, 0, 0, 0xFFFFFF);
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
}
