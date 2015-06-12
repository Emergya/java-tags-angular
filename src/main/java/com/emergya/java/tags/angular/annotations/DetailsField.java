package com.emergya.java.tags.angular.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for filter fields.
 *
 * @author lroman
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DetailsField {

    int order();

    String scopeName() default "";

    String label();

    String[] filters() default {};
    
    DetailsFieldType type() default DetailsFieldType.TEXT;
}
