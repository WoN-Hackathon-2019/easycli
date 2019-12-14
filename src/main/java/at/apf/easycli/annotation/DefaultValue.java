package at.apf.easycli.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Extends the {@link Optional} annotation and introduces an alternative
 * default value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DefaultValue {

    /***
     * @return The default value for the annotated parameter in string
     *         representation.
     */
    String value();
}
