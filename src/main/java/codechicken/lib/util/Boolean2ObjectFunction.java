package codechicken.lib.util;

import java.util.function.Function;

/**
 * Created by covers1624 on 3/17/20.
 */
public interface Boolean2ObjectFunction<R> extends Function<Boolean, R> {

    R apply(boolean t);

    @Override
    @Deprecated
    default R apply(Boolean t) {
        return apply(t.booleanValue());
    }
}
