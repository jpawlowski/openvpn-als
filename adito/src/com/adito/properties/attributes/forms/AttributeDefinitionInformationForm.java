package com.adito.properties.attributes.forms;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.MessageResources;

import com.adito.core.forms.CoreForm;
import com.adito.properties.attributes.AttributeDefinition;

/**
 * Implementation of {@link com.adito.core.forms.CoreForm} 
 * that allows the creation of an AttributeDefinition.
 */
public class AttributeDefinitionInformationForm extends CoreForm {
    final static Log log = LogFactory.getLog(AttributeDefinitionInformationForm.class);
    
    private AttributeDefinition definition;
    private String categoryLabel, label, description;
    
    /**
     * Initialise the form.
     * @param messageResources
     * @param definition
     */
    public void initialise(MessageResources messageResources, AttributeDefinition definition) {
        this.initialise(messageResources,  definition, null);
    }
    
    /**
     * Initialise the form.
     * @param messageResources
     * @param definition
     * @param locale
     */
    public void initialise(MessageResources messageResources, AttributeDefinition definition, Locale locale) {
    	this.definition = definition;
        String s = messageResources == null ? null : messageResources.getMessage(locale, "attributeCategory." + definition.getCategory() + ".title");
        if (s != null && !s.equals("")) {
            categoryLabel = s;
        } else {
            categoryLabel = definition.getCategoryLabel() != null && !definition.getCategoryLabel().equals("") ? definition.getCategoryLabel() : "Attributes";
        }
        s = messageResources == null ? null : messageResources.getMessage(locale, definition.getPropertyClass().getName() + "." + definition.getName() + ".title");
        if (s != null && !s.equals("")) {
            label = s;
        } else {
            label = definition.getLabel() != null && !definition.getLabel().equals("") ? definition.getLabel() : definition.getName();
        }
        s = messageResources == null ? null : messageResources.getMessage(locale, definition.getPropertyClass().getName() + "." + definition.getName() + ".description");
        if (s != null && !s.equals("")) {
            description = s;
        } else {
            description = definition.getDescription() != null && !definition.getDescription().equals("") ? definition.getDescription() : label;
        }
    }
    
    /**
     * Get the AttributeDefinition
     * @return AttributeDefinition
     */
    public AttributeDefinition getDefinition() {
    	return definition;
    }

	/**
     * Get the category label
	 * @return String
	 */
	public String getCategoryLabel() {
		return categoryLabel;
	}

	/**
     * Get the description 
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
     * Get the label
	 * @return String
	 */
	public String getLabel() {
		return label;
	}
}
