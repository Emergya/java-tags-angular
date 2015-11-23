package com.emergya.java.tags.angular.annotations;

import org.springframework.util.StringUtils;

/**
 * Enumaration type defining the types of widget supported for generating forms.
 *
 * @author lroman
 */
public enum FormWidgetType {

    INPUT("input", "form-control", null, false, null, false),
    TEXTAREA("textarea", "form-control", null, false, null, false),
    SELECT("select", "form-control", null, false, null, true),
    CHECKBOX("input", "checkbox", new String[]{"type='checkbox'"}, false, "<div class=\"checkbox\"><label>%s</label></div>", false),
    DATE("input", "form-control", new String[]{"data-datepicker-popup"}, false, null, false),
    IMAGE("div", "", new String[]{"data-image-chooser"}, true, null, false);

    private final String domElement;
    private final String classes;
    private final String[] attributes;
    private final boolean usingTemplate;
    private final String wrapper;
    private final boolean usingOptionsModel;

    /**
     * Constructor for the enumaration values.
     *
     * @param domElement The dom element type (e.g., input, textarea, etc.) that will be created
     * @param classes The classes to set into the element's class attribute
     * @param attributes Other attributes to specify for the element, such as styles or AngularJS directives
     * @param usingTemplate If the element will be replaced by an AngularJS directive template. Used to avoid applying some
     * directives that will cause AngularJS errors due both directives applying to the same element.
     * @param wrapper The wrapper HTML that will sorround the created element.
     * @param usesOptionsModel The field uses ng-options.
     */
    FormWidgetType(
            String domElement, String classes, String[] attributes,
            boolean usingTemplate, String wrapper, boolean usesOptionsModel) {

        this.domElement = domElement;
        this.classes = classes;
        this.attributes = attributes;
        this.usingTemplate = usingTemplate;
        this.wrapper = wrapper;
        this.usingOptionsModel = usesOptionsModel;
    }

    /**
     * @return the domElement
     */
    public String getDomElement() {
        return domElement;
    }

    /**
     * @return the classes
     */
    public String getCssClasses() {
        return classes;
    }

    /**
     * @return the attributes
     */
    public String getAttributes() {
        return StringUtils.arrayToDelimitedString(attributes, " ");
    }

    /**
     *
     * @return is using template?
     */
    public boolean isUsingTemplate() {
        return usingTemplate;
    }

    /**
     * @return the wrapper element
     */
    public String getWrapper() {
        return wrapper;
    }

    /**
     * @return the usingOptionsModel
     */
    public boolean isUsingOptionsModel() {
        return usingOptionsModel;
    }
}
