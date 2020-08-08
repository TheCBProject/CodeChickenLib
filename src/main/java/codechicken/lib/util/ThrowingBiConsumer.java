package codechicken.lib.util;

/**
 * A simple BiConsumer that has a throws declaration.
 * <p>
 * Created by covers1624 on 15/06/18.
 */
@FunctionalInterface
public interface ThrowingBiConsumer<A, B, E extends Throwable> {

    void accept(A a, B b) throws E;
}
