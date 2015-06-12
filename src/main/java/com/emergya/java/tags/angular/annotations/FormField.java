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
public @interface FormField {

    int order();

    String scopeName() default "";

    String label();

    FormWidgetType type() default FormWidgetType.INPUT;

    String cssClasses() default "";

    String[] attributes() default "";

    String optionsExpression() default "";
}
