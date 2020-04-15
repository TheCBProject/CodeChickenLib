package codechicken.lib.util;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Things that are 'sneaky' :D
 *
 * Created by covers1624 on 3/31/20.
 */
public class SneakyUtils {

    public static Runnable sneak(ThrowingRunnable<Throwable> tr) {
        return () -> sneaky(tr);
    }

    public static <T, R> Function<T, R> sneak(ThrowingFunction<T, R, Throwable> tf) {
        return e -> sneaky(() -> tf.apply(e));
    }

    public static void sneaky(ThrowingRunnable<Throwable> tr) {
        try {
            tr.run();
        } catch (Throwable t) {
            throwUnchecked(t);
        }
    }

    public static <T> T sneaky(ThrowingProducer<T, Throwable> tp) {
        try {
            return tp.get();
        } catch (Throwable t) {
            throwUnchecked(t);
            return null;//Un possible
        }
    }

    public static <T> Consumer<T> sneakyL(ThrowingConsumer<T, Throwable> tc) {
        return t -> {
            try {
                tc.accept(t);
            } catch (Throwable th) {
                throwUnchecked(th);
            }
        };
    }

    @SuppressWarnings ("unchecked")
    public static <T> T unsafeCast(Object obj) {
        return (T) obj;
    }

    /**
     * Throws an exception without compiler warnings.
     */
    @SuppressWarnings ("unchecked")
    public static <T extends Throwable> void throwUnchecked(Throwable t) throws T {
        throw (T) t;
    }

}
