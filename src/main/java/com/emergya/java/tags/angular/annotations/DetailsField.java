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

    /**
     * @return The field's order
     */
    int order();

    /**
     * @return the scope name.
     */
    String scopeName() default "";

    /**
     * @return the field's label
     */
    String label();

    /**
     * @return the filters to apply to the field.
     */
    String[] filters() default {};

    /**
     * @return The type of widget.
     */
    DetailsFieldType type() default DetailsFieldType.TEXT;
}
