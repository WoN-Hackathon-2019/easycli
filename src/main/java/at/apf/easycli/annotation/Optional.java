package at.apf.easycli.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Indicates that the annotated parameter does not have to be set in the
 * command. Instead null, false, 0 or 0.0 will be passed into the commands
 * implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {
}
