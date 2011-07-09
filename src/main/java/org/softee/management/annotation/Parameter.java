package org.softee.management.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds description and name to a parameter of an MBean operation.<p>
 * If no annotation is associated with the parameter, a default name "px" is provided, where "x" is the argument index (1 .. n),
 * e.g. "p1", "p2", ...
 *
 * @author morten.hattesen@gmail.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Inherited
public @interface Parameter {
    /**
     * @return Optional description of the parameter
     */
    String value() default "";

    /**
     * @return name of the parameter.
     */
    String name();

}
