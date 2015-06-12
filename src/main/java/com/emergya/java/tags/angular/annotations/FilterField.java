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
public @interface FilterField {

    int order();

    String fieldName() default "";
    String scopeName() default "";
    String label();

    FilterFieldOp op() default FilterFieldOp.LIKE;

    FormWidgetType type() default FormWidgetType.INPUT;

    String cssClasses() default "";

    String attributes() default "";
}
