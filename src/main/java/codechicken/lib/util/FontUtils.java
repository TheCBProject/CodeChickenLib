package codechicken.lib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class FontUtils {

    public static Font fontRenderer = Minecraft.getInstance().font;

    public static void drawCenteredString(PoseStack mStack, String s, int xCenter, int y, int colour) {
        fontRenderer.drawShadow(mStack, s, xCenter - fontRenderer.width(s) / 2, y, colour);
    }

    public static void drawRightString(PoseStack mStack, String s, int xRight, int y, int colour) {
        fontRenderer.drawShadow(mStack, s, xRight - fontRenderer.width(s), y, colour);
    }
}
