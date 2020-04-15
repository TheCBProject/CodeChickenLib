package codechicken.lib.util;

/**
 * Created by covers1624 on 18/11/19.
 */
public interface ThrowingFunction<T, R, E extends Throwable> {

    R apply(T thing) throws E;

}
