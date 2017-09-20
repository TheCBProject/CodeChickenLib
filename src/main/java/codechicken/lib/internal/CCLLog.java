package codechicken.lib.internal;

import codechicken.lib.CodeChickenLib;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal logger for CCL.
 *
 * Created by covers1624 on 20/06/2017.
 */
@EventBusSubscriber (modid = CodeChickenLib.MOD_ID)
public class CCLLog {

    public static Logger logger = LogManager.getLogger("CodeChickenLib");
    private static final Set<String> stackTraces = new HashSet<>();
    private static final Set<String> tickMessages = new HashSet<>();

    public static void log(Level logLevel, Object object) {
        logger.log(logLevel, String.valueOf(object));
    }

    public static void log(Level logLevel, String format, Object... objects) {
        logger.log(logLevel, String.format(format, objects));
    }

    public static void log(Level logLevel, Throwable throwable, Object object) {
        logger.log(logLevel, String.valueOf(object), throwable);
    }

    public static void log(Level logLevel, Throwable throwable, String format, Object... objects) {
        logger.log(logLevel, String.format(format, objects), throwable);
    }

    public static void big(Level level, String format, Object... data) {
        big(level, 8, format, data);
    }

    public static void big(Level level, int lines, String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        log(level, "****************************************");
        log(level, "* " + format, data);
        for (int i = 2; i < lines && i < trace.length; i++) {
            log(level, "*  at %s%s", trace[i].toString(), i == lines - 1 ? "..." : "");
        }
        log(level, "****************************************");
    }

    public static synchronized void errorOnce(Throwable t, String identifier, String format, Object... data) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String stackTrace = identifier + sw.toString();
        synchronized (stackTraces) {
            if (!stackTraces.contains(stackTrace)) {
                log(Level.ERROR, t, format, data);
                stackTraces.add(stackTrace);
            }
        }
    }

    public static synchronized void logOncePerTick(Level level, String format, Object... data) {
        String l = String.format(format, data);
        synchronized (tickMessages) {
            if (!tickMessages.contains(level + l)) {
                log(level, l);
                tickMessages.add(level + l);
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onTickEnd(ClientTickEvent event) {
        onTickEnd((TickEvent) event);
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onTickEnd(ServerTickEvent event) {
        onTickEnd((TickEvent) event);
    }

    public static void onTickEnd(TickEvent event) {
        if (event.phase == Phase.END) {
            if (CodeChickenLib.proxy.isClient()) {
                if (event.type == Type.CLIENT) {
                    synchronized (tickMessages) {
                        tickMessages.clear();
                    }
                }
            } else {
                if (event.type == Type.SERVER) {
                    synchronized (tickMessages) {
                        tickMessages.clear();
                    }
                }
            }
        }
    }

}
