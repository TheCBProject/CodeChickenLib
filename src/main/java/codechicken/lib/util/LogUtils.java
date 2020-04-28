package codechicken.lib.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Utils for logging things.
 * <p>
 * Created by covers1624 on 20/06/2017.
 */
public class LogUtils {

    private static final Set<String> stackTraces = new HashSet<>();

    public static synchronized void errorOnce(Logger logger, Throwable t, String identifier, String format, Object... data) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String stackTrace = identifier + sw.toString();
        synchronized (stackTraces) {
            if (!stackTraces.contains(stackTrace)) {
                logger.log(Level.ERROR, format, data, t);
                stackTraces.add(stackTrace);
            }
        }
    }
}
