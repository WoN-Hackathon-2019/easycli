package at.apf.easycli.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Used for @{@link Command} annotated methods and their parameters to describe their usage.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface Usage {

    /***
     * @return The usage of the annotated command or command parameter.
     */
    String value();
}
