package com.emergya.java.tags.angular;

import com.emergya.java.Utils;
import com.emergya.java.tags.angular.annotations.DetailsField;
import com.emergya.java.tags.angular.annotations.FormField;
import com.emergya.java.tags.angular.annotations.FormWidgetType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.springframework.util.StringUtils;

/**
 *
 * @author lroman
 */
public class EntityFormTagHandler extends SimpleTagSupport {

    private String className;

    private String formModel = "form";

    /**
     * Called by the container to invoke this tag. The implementation of this method is provided by the tag library developer, and
     * handles all tag processing, body iteration, etc.
     *
     * @throws javax.servlet.jsp.JspException
     */
    @Override
    public final void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();

        Class<?> filterClass;
        try {
            filterClass = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new JspException("Error in FormFilterTag tag", ex);
        }

        Method[] methods = filterClass.getDeclaredMethods();

        List<FormField> sortedAnnotations = new ArrayList<>();

        for (Method method : methods) {
            FormField formFieldAnnotation = method.getAnnotation(FormField.class);
            if (formFieldAnnotation != null) {

                if (StringUtils.isEmpty(formFieldAnnotation.scopeName())) {
                    String scopeName = Utils.getFieldName(method);
                    formFieldAnnotation = new FormFieldDTO(
                            formFieldAnnotation.order(),
                            scopeName, formFieldAnnotation.label(), formFieldAnnotation.type(),
                            formFieldAnnotation.cssClasses(),
                            formFieldAnnotation.attributes(),
                            formFieldAnnotation.optionsExpression());
                }

                sortedAnnotations.add(formFieldAnnotation);

            }
        }

        Collections.sort(sortedAnnotations, new Comparator<FormField>() {

            @Override
            public int compare(FormField o1, FormField o2) {
                return o1.order() - o2.order();
            }
        });

        StringBuilder builder = new StringBuilder();
        builder.append("<div class=\"form-horizontal\">");
        for (FormField fieldViewAnnotation : sortedAnnotations) {
            builder.append(createFormField(formModel, fieldViewAnnotation));
        }

        builder.append("</div>");

        try {
            out.write(builder.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets the class name used to generate the form.
     *
     * @param className The class name including its full class path.
     */
    public final void setClassName(final String className) {
        this.className = className;
    }

    /**
     * Sets the form's model variable.
     *
     * @param model The model variable.
     */
    public final void setModel(final String model) {
        this.formModel = model;
    }

    private Object createFormField(String baseModel, FormField formFieldAnnotation) {

        List<String> attributes = createAttributes(baseModel, formFieldAnnotation);

        String elementHTML = String.format("<%s %s></%s>",
                formFieldAnnotation.type().getDomElement(),
                StringUtils.collectionToDelimitedString(attributes, " "),
                // If we are using a directive with a custom template we cannot add data-validate to the element, as it won't work.
                formFieldAnnotation.type().getDomElement());

        if (!StringUtils.isEmpty(formFieldAnnotation.type().getWrapper())) {
            elementHTML = String.format(formFieldAnnotation.type().getWrapper(), elementHTML);
        }

        String rowHTML = String.format(
                "<div class=\"form-group\" data-ng-class=\"{'has-error':errors.%s.length}\" >"
                + "<label class=\"col-md-3 control-label\">{{'%s'|translate}}</label>"
                + "<div class=\"col-md-9\">%s %s</div></div>",
                formFieldAnnotation.scopeName(),
                formFieldAnnotation.label(),
                elementHTML,
                createErrorsRepeater(formFieldAnnotation.scopeName()));

        return rowHTML;
    }

    private List<String> createAttributes(String baseModel, FormField formFieldAnnotation) {
        ArrayList<String> attributes = new ArrayList<>();

        if (!StringUtils.isEmpty(formFieldAnnotation.type().getAttributes())) {
            attributes.add(formFieldAnnotation.type().getAttributes());
        }

        String[] customAttributes = formFieldAnnotation.attributes();
        if (customAttributes != null) {
            attributes.addAll(Arrays.asList(customAttributes));
        }

        if (formFieldAnnotation.type().isUsingOptionsModel()) {
            String optionsModelAttribute = "options";
            if (!StringUtils.isEmpty(formFieldAnnotation.optionsExpression())) {
                optionsModelAttribute = formFieldAnnotation.optionsExpression();
            }

            attributes.add(String.format("data-ng-options=\"%s\"", optionsModelAttribute));
        }

        attributes.add(String.format(
                "data-ng-model=\"%s.%s\"",
                baseModel,
                formFieldAnnotation.scopeName()));

        attributes.add(String.format(
                "class=\"%s %s\"",
                formFieldAnnotation.type().getCssClasses(),
                formFieldAnnotation.cssClasses()));

        return attributes;
    }

    private String createErrorsRepeater(String scopeName) {
        return String.format(
                "<ul class=\"help-block\"data-ng-show=\"errors.%s.length\">"
                + "<li data-ng-repeat=\"error in errors.%s\">{{error}}</li>"
                + "</ul>", scopeName, scopeName);
    }

    /**
     * Auxiliary class to hold data from the FormField annotation and be able to handle it normally so we can sort fields etc.
     */
    private static class FormFieldDTO implements FormField {

        private final String label;
        private final String scopeName;
        private final int order;
        private final FormWidgetType type;
        private final String cssClasses;
        private final String[] attributes;
        private final String optionsExpression;

        FormFieldDTO(
                int order, String scopeName, String label, FormWidgetType type,
                String cssClasses, String[] attributes, String optionsScopeName) {

            this.order = order;
            this.scopeName = scopeName;
            this.label = label;
            this.type = type;
            this.cssClasses = cssClasses;
            this.attributes = attributes;
            this.optionsExpression = optionsScopeName;
        }

        /**
         * @return the label
         */
        @Override
        public String label() {
            return label;
        }

        /**
         * @return the scopeName
         */
        @Override
        public String scopeName() {
            return scopeName;
        }

        /**
         * @return the order
         */
        @Override
        public int order() {
            return order;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return DetailsField.class;
        }

        @Override
        public FormWidgetType type() {
            return type;
        }

        @Override
        public String cssClasses() {
            return cssClasses;
        }

        @Override
        public String[] attributes() {
            return attributes;
        }

        /**
         * @return the optionsExpression
         */
        @Override
        public String optionsExpression() {
            return optionsExpression;
        }

    }
}
