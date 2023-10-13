package codechicken.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class FontUtils {
    public static Font fontRenderer = Minecraft.getInstance().font;

    public static void drawCenteredString(GuiGraphics context, String s, int xCenter, int y, int colour) {
        context.drawString(fontRenderer, s, (int) (xCenter - fontRenderer.width(s) * 0.5F), y, colour);
    }

    public static void drawRightString(GuiGraphics context, String s, int xRight, int y, int colour) {
        context.drawString(fontRenderer, s, xRight - fontRenderer.width(s), y, colour);
    }
}
