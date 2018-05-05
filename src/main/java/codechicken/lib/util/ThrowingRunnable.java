package codechicken.lib.util;

/**
 * Created by covers1624 on 1/05/18.
 */
@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {

    void run() throws T;
}
