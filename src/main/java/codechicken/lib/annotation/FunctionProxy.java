package codechicken.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A simple way to define under specific circumstances what proxy class to inject to the annotated field.
 * Very similar to FML's SidedProxy annotation, except YOU control what class to use.
 * Only supports java, if there is a need, create a GitHub issue for other language support.
 * Created by covers1624 on 7/04/2017.
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
public @interface FunctionProxy {

    /**
     * Provides a callback to decide what class to inject to the annotated field.
     * Method must return a String, be static and have no parameters.
     * Defaults to: class.the.annotation.exists.in.proxyCallback
     *
     * @return Callback to decide what Class instance to inject.
     */
    String injectClassCallback() default "";

}
