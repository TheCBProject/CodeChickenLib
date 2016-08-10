package codechicken.lib.render;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class CCRenderEventHandler {
    public static int renderTime;
    public static float renderFrame;

    private static boolean hasInit = false;

    public static void init(){
        if (!hasInit){
            MinecraftForge.EVENT_BUS.register(new CCRenderEventHandler());
            hasInit = true;
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END) {
            renderTime++;
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == Phase.START) {
            renderFrame = event.renderTickTime;
        }
    }
}
