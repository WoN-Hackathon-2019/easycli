package at.apf.easycli.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Indicates that the annotated method can get registered by a {@link at.apf.easycli.CliEngine}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /***
     * @return Command name to register in a {@link at.apf.easycli.CliEngine}.
     */
    String value();
}
