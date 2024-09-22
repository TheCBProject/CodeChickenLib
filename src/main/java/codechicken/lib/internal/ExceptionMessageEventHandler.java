package codechicken.lib.internal;

import net.covers1624.quack.util.CrashLock;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by covers1624 on 25/07/18.
 */
public class ExceptionMessageEventHandler {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static Set<String> exceptionMessageCache = new HashSet<>();
    private static long lastExceptionClear;

    public static void init() {
        LOCK.lock();
        NeoForge.EVENT_BUS.addListener(ExceptionMessageEventHandler::clientTick);
    }

    private static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            //Clear the known exceptions every 5 seconds.
            long time = System.nanoTime();
            if (TimeUnit.NANOSECONDS.toSeconds(time - lastExceptionClear) > 5) {
                lastExceptionClear = time;
                exceptionMessageCache.clear();
            }
        }
    }

}
