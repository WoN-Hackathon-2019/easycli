package at.apf.easycli.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Indicates that the annotated parameter is a boolean flag. Can only get
 * annotated to boolean parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Flag {
    /***
     * @return The character which enables the flag.
     */
    char value();

    /***
     * @return The alternative term which enables the flag. Must start with
     *         "--".
     */
    String alternative() default "";
}
