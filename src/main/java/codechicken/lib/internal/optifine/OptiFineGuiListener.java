package codechicken.lib.internal.optifine;

import codechicken.lib.CodeChickenLib;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber (modid = CodeChickenLib.MOD_ID, value = Dist.CLIENT)
public class OptiFineGuiListener {

    private static boolean notified;
    private static long firstRenderTime;
    private static long previousMouseX;
    private static long previousMouseY;

    @SubscribeEvent
    public static void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (notified || !(event.getGui() instanceof MainMenuScreen)) {
            return;
        }

        GameSettings gameSettings = Minecraft.getInstance().options;
        if (!(gameSettings instanceof OptiFineGameSettingsBridge) || !((OptiFineGameSettingsBridge) gameSettings).bridge$isFastRender()) {
            notified = true;
            return;
        }

        if (firstRenderTime == 0) {
            firstRenderTime = Util.getMillis();
            previousMouseX = event.getMouseX();
            previousMouseY = event.getMouseY();
            return;
        }

        // Wait for fade in effect
        if (Util.getMillis() - firstRenderTime < 1000) {
            return;
        }

        // Wait for user input
        if (previousMouseX == event.getMouseX() && previousMouseY == event.getMouseY()) {
            return;
        }

        notified = true;
        Minecraft.getInstance().getToasts().addToast(new SystemToast(
                SystemToast.Type.TUTORIAL_HINT,
                new TranslationTextComponent("ccl.optifine.toast.title"),
                new TranslationTextComponent("ccl.optifine.toast.description")
        ));
    }
}