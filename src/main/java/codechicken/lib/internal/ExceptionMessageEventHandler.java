package codechicken.lib.internal;

import codechicken.lib.CodeChickenLib;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by covers1624 on 25/07/18.
 */
@EventBusSubscriber (value = Side.CLIENT, modid = CodeChickenLib.MOD_ID)
public class ExceptionMessageEventHandler {

    public static Set<String> exceptionMessageCache = new HashSet<>();
    private static long lastExceptionClear;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END) {
            //Clear the known exceptions every 5 seconds.
            long time = System.nanoTime();
            if (TimeUnit.NANOSECONDS.toSeconds(time - lastExceptionClear) > 5) {
                lastExceptionClear = time;
                exceptionMessageCache.clear();
            }
        }
    }

}
