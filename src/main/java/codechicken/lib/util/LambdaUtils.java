package codechicken.lib.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Basically a bunch of lambda helpers.
 * <p>
 * Created by covers1624 on 1/06/2017.
 */
public class LambdaUtils {

    /**
     * Tests if a predicate holds for all elements of the iterable.
     *
     * @param iterable  The iterable to test.
     * @param predicate The predicate.
     * @param <T>       What we are dealing with.
     * @return True if the predicate holds for all elements in the iterable.
     */
    //TODO Rename. forAll
    public static <T> boolean forEach(Iterable<T> iterable, Predicate<T> predicate) {
        for (T e : iterable) {
            if (!predicate.test(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * For Each Array lambda support. Because reasons.
     *
     * @param elements What to loop.
     * @param consumer Lambda.
     * @param <E>      The thing!
     */
    public static <E> void forEach(E[] elements, Consumer<E> consumer) {
        for (E element : elements) {
            consumer.accept(element);
        }
    }

    /**
     * Provides a predicate callback for checking a condition.
     *
     * @param argument  The thing we are checking.
     * @param log       What the reason for the predicate failing is.
     * @param predicate The predicate callback.
     * @param <E>       The thing.
     */
    public static <E> void checkArgument(E argument, String log, Predicate<E> predicate) {
        if (predicate.test(argument)) {
            throw new RuntimeException("Argument check failed! Reason: " + log);
        }
    }

    /**
     * Try's the operation quietly, disposing of any exception thrown.
     * Useful for things where you literally don't care what the result is.
     *
     * @param runnable The thing to run.
     */
    public static void tryQuietly(ThrowingRunnable<Throwable> runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
            //pokemon
        }
    }

    /**
     * Attempts to run the supplier, returns null on an exception.
     *
     * @param supplier The supplier to run.
     * @param <T>      The Generic type.
     * @return The thing from the supplier.
     */
    public static <T> T tryOrNull(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return null;
        }
    }
}
