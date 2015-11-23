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

    /**
     * The field order.
     *
     * @return the order value
     */
    int order();

    /**
     * If specified, the model field name to use instead of the one computed from the applied method's name.
     *
     * @return the field name
     */
    String fieldName() default "";

    /**
     * The field's model scope.
     *
     * @return the scope name.
     */
    String scopeName() default "";

    /**
     * The field's label.
     *
     * @return the label
     */
    String label();

    /**
     * The filtering operation.
     *
     * @return the filtering operation.
     */
    FilterFieldOp op() default FilterFieldOp.LIKE;

    /**
     * The field widget type.
     *
     * @return The type.
     */
    FormWidgetType type() default FormWidgetType.INPUT;

    /**
     * CSS classes to apply to the field.
     *
     * @return the CSS classes.
     */
    String cssClasses() default "";

    /**
     * Attributes to apply to the field.
     *
     * @return the attributes
     */
    String attributes() default "";
}
