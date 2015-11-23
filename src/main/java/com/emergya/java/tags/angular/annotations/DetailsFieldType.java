package com.emergya.java.tags.angular.annotations;

/**
 * Allows us to define several types of detail view, each one with its own template.
 *
 * In these templates, {scopeField} represents the unfiltered scope values, and {scopeFieldFiltered} the values with the specified
 * filters applied.
 *
 * @author lroman
 */
public enum DetailsFieldType {
    TEXT("{scopeFieldFiltered}"),
    LINK("<a target=\"_blank\" href=\"{scopeField}\" class=\"ellipsize\">{scopeFieldFiltered}</a>"),
    IMAGE("<img data-ng-src=\"{scopeField}\" class=\"img-thumbnail img-responsive\"/>");

    private final String template;

    /**
     * Constructor.
     *
     * @param template the template to use to build the field.
     */
    DetailsFieldType(String template) {
        this.template = template;
    }

    /**
     * Gets the details field template for the type.
     *
     * @return The template.
     */
    public String getTemplate() {
        return template;
    }
}
