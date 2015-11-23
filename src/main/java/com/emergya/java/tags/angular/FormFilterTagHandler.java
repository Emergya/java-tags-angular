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

    private static final String HTML_TEMPLATE
            = Utils.readClassPathResourceAsString("com/emergya/java/front/tags/angular/filterWidgetTemplate.html");

    private String filterClassName;

    /**
     * Called by the container to invoke this tag. The implementation of this method is provided by the tag library developer, and
     * handles all tag processing, body iteration, etc.
     *
     * @throws javax.servlet.jsp.JspException When there is an output error.
     */
    @Override
    public final void doTag() throws JspException {

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

                if (StringUtils.isEmpty(filterFieldAnnotation.scopeName())
                        || StringUtils.isEmpty(filterFieldAnnotation.fieldName())) {
                    String fieldName, scopeName;

                    fieldName = filterFieldAnnotation.fieldName();
                    if (StringUtils.isEmpty(fieldName)) {
                        fieldName = Utils.getFieldName(method);
                    }

                    scopeName = filterFieldAnnotation.scopeName();
                    if (StringUtils.isEmpty(scopeName)) {
                        scopeName = Utils.getFieldName(method);
                    }

                    FilterFieldDto filterFieldDto = new FilterFieldDto();
                    filterFieldDto.setOrder(filterFieldAnnotation.order());

                    filterFieldDto.setLabel(filterFieldAnnotation.scopeName());
                    filterFieldDto.setOp(filterFieldAnnotation.op());
                    filterFieldDto.setCssClasses(filterFieldAnnotation.cssClasses());
                    filterFieldDto.setAttributes(filterFieldAnnotation.attributes());
                    filterFieldDto.setType(filterFieldAnnotation.type());

                    filterFieldDto.setFieldName(fieldName);
                    filterFieldDto.setScopeName(scopeName);

                    filterFieldAnnotation = filterFieldDto;
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

        String tagHtml = HTML_TEMPLATE.replace("%%FILTER_FIELDS%%", fieldsHtmlBuilder.toString());

        JspWriter out = getJspContext().getOut();
        try {
            out.write(tagHtml);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets the class the filter view will be generated from.
     *
     * @param filterClassName The class name, including its full path.
     */
    public final void setFilterClassName(String filterClassName) {
        this.filterClassName = filterClassName;
    }

    private String addFilterField(FilterField filterFieldAnnotation) {

        return String.format(
                "<div class=\"col-md-4 form-group\" ><label>{{'%s'|translate}}</label>"
                + "<%s class=\"%s %s\" %s %s data-ng-model=\"filter.%s\"></%s></div>",
                filterFieldAnnotation.label(),
                filterFieldAnnotation.type().getDomElement(),
                filterFieldAnnotation.type().getCssClasses(),
                filterFieldAnnotation.cssClasses(),
                filterFieldAnnotation.type().getAttributes(),
                filterFieldAnnotation.attributes(),
                filterFieldAnnotation.scopeName(),
                filterFieldAnnotation.type().getDomElement());
    }

    private static class FilterFieldDto implements FilterField {

        private int order;
        private String fieldName;
        private String scopeName;
        private String label;
        private FilterFieldOp op;
        private String cssClasses;
        private String attributes;
        private FormWidgetType type;

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
            return type;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FilterField.class;
        }

        /**
         * @param order the order to set
         */
        public void setOrder(int order) {
            this.order = order;
        }

        /**
         * @param fieldName the fieldName to set
         */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * @param scopeName the scopeName to set
         */
        public void setScopeName(String scopeName) {
            this.scopeName = scopeName;
        }

        /**
         * @param label the label to set
         */
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * @param op the op to set
         */
        public void setOp(FilterFieldOp op) {
            this.op = op;
        }

        /**
         * @param cssClasses the cssClasses to set
         */
        public void setCssClasses(String cssClasses) {
            this.cssClasses = cssClasses;
        }

        /**
         * @param attributes the attributes to set
         */
        public void setAttributes(String attributes) {
            this.attributes = attributes;
        }

        /**
         * @param formWidgetType the type to set
         */
        public void setType(FormWidgetType formWidgetType) {
            this.type = formWidgetType;
        }

    }

}
