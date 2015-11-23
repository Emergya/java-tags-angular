package com.emergya.java.tags.angular;

import com.emergya.java.Utils;
import com.emergya.java.tags.angular.annotations.DetailsField;
import com.emergya.java.tags.angular.annotations.DetailsFieldType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.springframework.util.StringUtils;

/**
 *
 * @author lroman
 */
public class DetailsViewTagHandler extends SimpleTagSupport {

    private String className;

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

        List<DetailsField> sortedAnnotations = new ArrayList<>();

        for (Method method : methods) {
            DetailsField fieldViewAnnotation = method.getAnnotation(DetailsField.class);
            if (fieldViewAnnotation != null) {

                if (StringUtils.isEmpty(fieldViewAnnotation.scopeName())) {
                    String scopeName = Utils.getFieldName(method);
                    fieldViewAnnotation = new FieldViewDTO(
                            fieldViewAnnotation.order(),
                            scopeName, fieldViewAnnotation.label(), fieldViewAnnotation.filters(), fieldViewAnnotation.type());
                }

                sortedAnnotations.add(fieldViewAnnotation);

            }
        }

        Collections.sort(sortedAnnotations, new Comparator<DetailsField>() {

            @Override
            public int compare(DetailsField o1, DetailsField o2) {
                return o1.order() - o2.order();
            }
        });

        StringBuilder builder = new StringBuilder();
        builder.append("<div class=\"form-horizontal\">");
        for (DetailsField fieldViewAnnotation : sortedAnnotations) {
            builder.append(createFieldView(fieldViewAnnotation));
        }

        builder.append("</div>");

        try {
            out.write(builder.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets the class a details view will be generated from.
     *
     * @param className The class name including its full path.
     */
    public final void setClassName(String className) {
        this.className = className;
    }

    private Object createFieldView(DetailsField fieldViewAnnotation) {

        String scopeBinding = fieldViewAnnotation.scopeName();
        StringBuilder scopeBindingBuffer = new StringBuilder(scopeBinding);
        if (fieldViewAnnotation.filters().length > 0) {
            scopeBindingBuffer.append(" | ");
            for (String filter : fieldViewAnnotation.filters()) {
                if (!StringUtils.isEmpty(filter)) {
                    scopeBindingBuffer.append(filter).append(" ");
                }
            }
        }

        // We prepend the container field:
        scopeBinding = "{{item." + scopeBindingBuffer.toString() + "}}";

        String content = fieldViewAnnotation.type().getTemplate().replaceAll(
                Pattern.quote("{scopeField}"),
                "{{item." + fieldViewAnnotation.scopeName() + "}}");
        content = content.replaceAll(Pattern.quote("{scopeFieldFiltered}"), scopeBinding);

        return String.format("<div class=\"form-group\">"
                + "<label class=\"col-md-3 control-label\">{{'%s' | translate}}</label>"
                + "<div class=\"col-md-9\"><p class=\"form-control-static\">%s</p></div></div>", fieldViewAnnotation.label(),
                content);
    }

    private static class FieldViewDTO implements DetailsField {

        private final String[] filters;
        private final String label;
        private final String scopeName;
        private final int order;
        private final DetailsFieldType type;

        FieldViewDTO(int order, String scopeName, String label, String[] filters, DetailsFieldType type) {
            this.order = order;
            this.scopeName = scopeName;
            this.label = label;
            this.filters = filters;
            this.type = type;
        }

        /**
         * @return the filters
         */
        @Override
        public String[] filters() {
            return filters;
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
        public DetailsFieldType type() {
            return type;
        }
    }

}
