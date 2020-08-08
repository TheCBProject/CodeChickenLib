package codechicken.lib.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Things that are 'sneaky' :D
 * <p>
 * Created by covers1624 on 3/31/20.
 */
public class SneakyUtils {

    public static Runnable none() {
        return () -> {};
    }

    public static <T> Callable<T> nullC() {
        return () -> null;
    }

    public static <T> Supplier<T> nullS() {
        return () -> null;
    }

    public static Runnable concat(Runnable a, Runnable b) {
        return () -> {
            a.run();
            b.run();
        };
    }

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

    /**
     * Evaluates a {@link ThrowingProducer} returning the producers result.
     * Re-Throws any exception.
     *
     * @param tp The {@link ThrowingProducer}.
     * @return The result of the producer.
     */
    public static <T> T sneaky(ThrowingProducer<T, Throwable> tp) {
        try {
            return tp.get();
        } catch (Throwable t) {
            throwUnchecked(t);
            return null;//Un possible
        }
    }

    /**
     * Converts a {@link ThrowingConsumer} into a standard {@link Consumer}.
     * Re-Throws any exception.
     *
     * @param tc The {@link ThrowingConsumer}
     * @return The {@link Consumer}
     */
    public static <T> Consumer<T> sneakyL(ThrowingConsumer<T, Throwable> tc) {
        return t -> {
            try {
                tc.accept(t);
            } catch (Throwable th) {
                throwUnchecked(th);
            }
        };
    }

    /**
     * Casts an object to literally whatever this is assigned to without warnings.
     * Useful in cases where unsafe casts are necessary, or complicated generics.
     *
     * @param obj The object.
     * @return The casted thing.
     */
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
