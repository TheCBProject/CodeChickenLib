package codechicken.lib.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Basically a bunch of lambda helpers.
 *
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

}
