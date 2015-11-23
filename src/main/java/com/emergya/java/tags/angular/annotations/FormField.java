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

    /**
     * @return The field's order
     */
    int order();

    /**
     * @return The field's model parent if any.
     */
    String scopeName() default "";

    /**
     * @return the field's label
     */
    String label();

    /**
     * @return The field's type.
     */
    FormWidgetType type() default FormWidgetType.INPUT;

    /**
     * @return CSS classes to apply to the widget's DOM element..
     */
    String cssClasses() default "";

    /**
     * @return Attributes to apply to the widget's DOM element.
     */
    String[] attributes() default "";

    /**
     * @return AngularJS expression for use in the ng-options attribute.
     */
    String optionsExpression() default "";
}
