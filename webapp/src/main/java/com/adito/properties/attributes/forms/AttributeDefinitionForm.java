
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.properties.attributes.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.CodedException;
import com.adito.boot.DefaultPropertyDefinition;
import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.core.CoreException;
import com.adito.core.forms.CoreForm;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributesPropertyClass;
import com.adito.properties.impl.userattributes.UserAttributes;


/**
 * Implementation of a {@link com.adito.core.forms.CoreForm}
 * that allows an administrator to edit an <i>Attribute Definition</i>.
 * 
 * @see com.adito.security.AuthenticationScheme
 */
public class AttributeDefinitionForm extends CoreForm {
    
    final static Log log = LogFactory.getLog(AttributeDefinitionForm.class);
    
    // Protected instance variables
    
    protected String name;
    protected String label;
    protected String category;
    protected String typeMeta;
    protected int type;
    protected String validationString;
    protected String description;
    protected String defaultValue;
    protected int visibility;
    protected int sortOrder;
    protected AttributeDefinition definition;
    
    /**
     * Initialise the form
     * 
     * @param definition definition
     */
    public void initialise(AttributeDefinition definition) {
        this.definition = definition;
        this.name = definition.getName();
        this.label = definition.getLabel();
        this.visibility = definition.getVisibility();
        this.defaultValue = definition.getDefaultValue();
        this.sortOrder = definition.getSortOrder();
        this.description = definition.getDescription();
        this.category = definition.getCategoryLabel();
        this.type = definition.getType();
        this.validationString = definition.getValidationString();
        this.typeMeta = definition.getTypeMeta();
    }
    
    /**
     * Get a list of available attribute property clases
     * 
     * @return attribute property classes
     */
    public Collection<AttributesPropertyClass> getAttributePropertyClasses() {
    	List<AttributesPropertyClass> l = new ArrayList<AttributesPropertyClass>();
    	for(PropertyClass propertyClass : PropertyClassManager.getInstance().getPropertyClasses()) {
    		if(propertyClass instanceof AttributesPropertyClass) {
    			l.add((AttributesPropertyClass)propertyClass);
    		}
    	}
    	return l;
    }

    /**
     * Get the default value
     * 
     * @return default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set the default value
     * 
     * @param defaultValue default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Get the label
     * 
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label
     * 
     * @param label label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the description
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get type type meta.
     * 
     * @return type meta.
     */
    public String getTypeMeta() {
        return typeMeta;
    }

    /**
     * Set type type meta.
     * 
     * @param typeMeta type meta.
     */
    public void setTypeMeta(String typeMeta) {
        this.typeMeta = typeMeta;
    }

    /**
     * Get the category
     * 
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Set the category
     * 
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Get the name
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the visibility. See {@link AttributeDefinitionForm} for
     * the constants to use.
     *  
     * @return visibility
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * Set the visibility. See {@link AttributeDefinitionForm} for
     * the contants to use.
     * 
     * @param visibility visibility
     */
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
    
    /**
     * Set the sort order
     * 
     * @param sortOrder sort order
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    /**
     * Get the sort order
     * 
     * @return sort order
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Get the selected type.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Set the selected type
     * 
     * @param type type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Get the validation string
     * 
     * @return validation string
     * @see DefaultPropertyDefinition
     */
    public String getValidationString() {
        return validationString;
    }

    /**
     * Set the validation string
     * 
     * @param validationString validation string
     * @see DefaultPropertyDefinition
     */
    public void setValidationString(String validationString) {
        this.validationString = validationString;
    }

    /**
     * Get the attribute definition object being edited / created
     * 
     * @return definition
     */
    public AttributeDefinition getDefinition() {
        return definition;
    }

    /**
     * Apply ethe entered values to the attribute definition being 
     * edited.
     */
    public void applyToDefinition() {
        definition.setDefaultValue(getDefaultValue().trim());
        definition.setLabel(getLabel().trim());
        definition.setSortOrder(getSortOrder());
        definition.setCategoryLabel(getCategory().trim());
        definition.setDescription(getDescription().trim());
        if(!getEditing()) {
            definition.setVisibility(getVisibility());
            definition.setName(getName().trim());
            definition.setType(getType());
        }
        definition.setValidationString(getValidationString());
        definition.setTypeMeta(getTypeMeta());
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(isCommiting()) {
            ActionErrors errs = new ActionErrors(); 
            if(getName().trim().equals("")) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.noName"));
            } else if(getDescription().trim().equals("")) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.noDescription"));
            } else {
                if(!getEditing()) {
                    try {
                        PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
                        AttributeDefinition def = (AttributeDefinition)propertyClass.getDefinition(getName());
                        if(def != null) {
                            errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.duplicateName", getName()));                        
                        }
                    }
                    catch(Exception e) {
                        log.error("Failed to test if attribute exists.", e);
                        errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.duplicateName", getName()));
                    }
                }
                if (!getName().matches("^[a-zA-Z0-9_-]*$")) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.invalidName"));
                }
            }
            if(!validationString.trim().equals("")) {
                String className = null;
                int idx = validationString.indexOf('(');
                if(idx == -1) {
                    className = validationString;                        
                }
                else {
                    if(!validationString.endsWith(")")) {
                        errs = new ActionErrors();
                        errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.invalidValidationStringFormat"));
                    }
                    className = validationString.substring(0, idx);
                }
                
                try {
                	Class.forName(className);
                }
                catch(ClassNotFoundException cnfe) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.noSuchValidator", className));                	
                }

                try {
                    // If there is a default value then validate it 
                    
                    String defaultValue = getDefaultValue().trim();
                    if(!defaultValue.equals("")) {
                    	AttributeDefinition def = ((AttributesPropertyClass)definition.getPropertyClass()).createAttributeDefinition(type,
                            name,
                            typeMeta,
                            -1,
                            category,
                            defaultValue,
                            visibility,
                            sortOrder,
                            null,
                            false,
                            label,
                            description,
                            false,
                            true,
                            validationString);
                        try { 
                        	def.validate(defaultValue, getClass().getClassLoader());
                        } catch (CoreException ce) {
                            ce.getBundleActionMessage().setArg3(def.getLabel());
                            if (errs == null) {
                                errs = new ActionErrors();
                            }
                            errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
                        } 
                    }
                }
                catch(Exception e) {
                    if(errs == null) {
                        errs = new ActionErrors();
                    }
                    errs.add(Globals.ERROR_KEY, new ActionMessage("editAttributeDefinition.error.failedToValidate", className));
                }
            }
            return errs;
        }
        return null;
    }
}
