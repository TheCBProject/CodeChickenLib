package codechicken.lib.internal;

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
public class CCLLog {

    public static Logger logger = LogManager.getLogger("CodeChickenLib");
    private static final Set<String> stackTraces = new HashSet<>();

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
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        log(level, "****************************************");
        log(level, "* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            log(level, "*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
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
}
