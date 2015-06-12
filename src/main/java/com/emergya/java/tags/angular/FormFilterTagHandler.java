package com.emergya.java.tags.angular;

import com.emergya.java.Utils;
import com.emergya.java.tags.angular.annotations.FilterField;
import com.emergya.java.tags.angular.annotations.FilterFieldOp;
import com.emergya.java.tags.angular.annotations.FormWidgetType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
public class FormFilterTagHandler extends SimpleTagSupport {

    private String filterClassName;
    private static String htmlTemplate;

    /**
     * Called by the container to invoke this tag. The implementation of this method is provided by the tag library developer, and
     * handles all tag processing, body iteration, etc.
     *
     * @throws javax.servlet.jsp.JspException When there is an output error.
     */
    @Override
    public void doTag() throws JspException {

        Class<?> filterClass;
        try {
            filterClass = Class.forName(filterClassName);
        } catch (ClassNotFoundException ex) {
            throw new JspException("Error in FormFilterTag tag", ex);
        }

        Method[] methods = filterClass.getDeclaredMethods();

        List<FilterField> sortedMethods = new ArrayList<>();

        for (Method method : methods) {
            FilterField filterFieldAnnotation = method.getAnnotation(FilterField.class);
            if (filterFieldAnnotation != null) {

                if (StringUtils.isEmpty(filterFieldAnnotation.scopeName()) || StringUtils.isEmpty(filterFieldAnnotation.fieldName())) {
                    String fieldName = Utils.getFieldName(method, filterFieldAnnotation.fieldName());
                    String scopeName = Utils.getFieldName(method, filterFieldAnnotation.scopeName());
                    filterFieldAnnotation = new FilterFieldDto(
                            filterFieldAnnotation.order(), fieldName,
                            scopeName, filterFieldAnnotation.label(),
                            filterFieldAnnotation.op(), filterFieldAnnotation.cssClasses(),
                            filterFieldAnnotation.attributes(), filterFieldAnnotation.type());
                }

                sortedMethods.add(filterFieldAnnotation);

            }
        }

        Collections.sort(sortedMethods, new Comparator<FilterField>() {

            @Override
            public int compare(FilterField o1, FilterField o2) {
                return o1.order() - o2.order();
            }
        });

        StringBuilder fieldsHtmlBuilder = new StringBuilder();
        for (FilterField filterFieldAnnotation : sortedMethods) {
            fieldsHtmlBuilder.append(addFilterField(filterFieldAnnotation));
        }

        if (htmlTemplate == null) {
            try {
                htmlTemplate = Utils.readClassPathResourceAsString("com/emergya/java/front/tags/angular/filterWidgetTemplate.html");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        String tagHtml = htmlTemplate.replace("%%FILTER_FIELDS%%", fieldsHtmlBuilder.toString());

        JspWriter out = getJspContext().getOut();
        try {
            out.write(tagHtml);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setFilterClassName(String filterClassName) {
        this.filterClassName = filterClassName;
    }

    private String addFilterField(FilterField filterFieldAnnotation) {

        return String.format(
                "<div class=\"col-md-4 form-group\" ><label>{{'%s'|translate}}</label><%s class=\"%s %s\" %s %s data-ng-model=\"filter.%s\"></%s></div>",
                filterFieldAnnotation.label(),
                filterFieldAnnotation.type().getDomElement(),
                filterFieldAnnotation.type().getCssClasses(),
                filterFieldAnnotation.cssClasses(),
                filterFieldAnnotation.type().getAttributes(),
                filterFieldAnnotation.attributes(),
                filterFieldAnnotation.scopeName(),
                filterFieldAnnotation.type().getDomElement());
    }

    private class FilterFieldDto implements FilterField {

        private final int order;
        private final String fieldName;
        private final String scopeName;
        private final String label;
        private final FilterFieldOp op;
        private final String cssClasses;
        private final String attributes;
        private final FormWidgetType formWidgetType;

        public FilterFieldDto(
                int order, String fieldName, String scopeName, String label, FilterFieldOp op, String cssClasses,
                String attributes, FormWidgetType formWidgetType) {

            this.order = order;
            this.fieldName = fieldName;
            this.scopeName = scopeName;
            this.label = label;
            this.op = op;
            this.cssClasses = cssClasses;
            this.attributes = attributes;
            this.formWidgetType = formWidgetType;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public String fieldName() {
            return fieldName;
        }

        @Override
        public String scopeName() {
            return scopeName;
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public FilterFieldOp op() {
            return op;
        }

        @Override
        public String cssClasses() {
            return cssClasses;
        }

        @Override
        public String attributes() {
            return attributes;
        }

        @Override
        public FormWidgetType type() {
            return formWidgetType;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FilterField.class;
        }

    }

}
