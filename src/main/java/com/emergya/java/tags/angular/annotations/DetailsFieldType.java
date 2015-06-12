package com.emergya.java.tags.angular.annotations;

/**
 * 
 * Allows us to define several types of detail view, each one with its own template.
 * 
 * In these templates, {scopeField} represents the unfiltered scope values, and {scopeFieldFiltered}
 * the values with the specified filters applied.
 * 
 * @author lroman
 */
public enum DetailsFieldType {
    TEXT("{scopeFieldFiltered}"),
    LINK("<a target=\"_blank\" href=\"{scopeField}\" class=\"ellipsize\">{scopeFieldFiltered}</a>"),
    IMAGE("<img data-ng-src=\"{scopeField}\" class=\"img-thumbnail img-responsive\"/>");   
    
    
    private final String template;

    private DetailsFieldType(String template) {
        this.template = template;
    }
    
    public String getTemplate()  {
        return template;        
    }
}
