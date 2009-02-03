
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
			
package com.adito.properties.attributes.wizards.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.DefaultPropertyDefinition;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;
import com.adito.core.CoreException;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributesPropertyClass;
import com.adito.properties.attributes.forms.AttributeDefinitionForm;
import com.adito.properties.attributes.wizards.actions.AttributeDefinitionDetailsAction;
import com.adito.properties.attributes.wizards.actions.AttributeDefinitionOptionsAction;
import com.adito.properties.impl.userattributes.UserAttributes;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 * Additional attribute options. These may be edited after 
 * the attribute has been created.
 * 
 * @author brett
 */
public class AttributeDefinitionOptionsForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(AttributeDefinitionOptionsForm.class);

    // Protected instance variables
    
    protected String label;
    protected String category;
    protected String typeMeta;
    protected int type;
    protected String validationString;
    protected String defaultValue;
    protected int visibility;
    protected int sortOrder;
    protected AttributesPropertyClass attributesClass; 
    
    /**
     * Constructor
     */
    public AttributeDefinitionOptionsForm() {
        super(true, true, "/WEB-INF/jsp/content/properties/attributeDefinitionWizard/attributeDefinitionOptions.jspf", "type", true, false,
                "attributeDefinitionOptions", "properties", "attributeDefinitionWizard.attributeDefinitionOptions", 2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        label = (String)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_LABEL, "");
        category = (String)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_CATEGORY, "");
        typeMeta  = (String)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE_META, "");
        type = ((Integer)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE, PropertyDefinition.TYPE_STRING)).intValue();
        validationString  = (String)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, "com.adito.input.validators.StringValidator(minLength=0,maxLength=30,trim=true,regExp=,pattern=)");
        defaultValue  = (String)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_DEFAULT_VALUE, "");
        visibility = ((Integer)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_VISIBILITY, AttributeDefinition.USER_USEABLE_ATTRIBUTE)).intValue();
        sortOrder = ((Integer)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_SORT_ORDER, 0)).intValue();
        attributesClass = (AttributesPropertyClass)PropertyClassManager.getInstance().getPropertyClass((String)sequence.getAttribute(AttributeDefinitionDetailsAction.ATTR_CLASS, UserAttributes.NAME));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_LABEL, label);
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_CATEGORY, category);
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE_META, typeMeta);
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE, new Integer(type));
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, validationString);
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_DEFAULT_VALUE, defaultValue);
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_VISIBILITY, new Integer(visibility));
        sequence.putAttribute(AttributeDefinitionOptionsAction.ATTR_SORT_ORDER, new Integer(sortOrder));
    }
    
    /**
     * Get the <i>Property Class</i> used for the selected <i>Attribute Class</i>
     * 
     * @return attribute class
     */
    public AttributesPropertyClass getAttributesClass() {
    	return attributesClass;
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


    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(isCommiting()) {
            ActionErrors errs = new ActionErrors(); 
            if(!validationString.trim().equals("")) {
                String className = null;
                int idx = validationString.indexOf('(');
                if(idx == -1) {
                    className = validationString;                        
                }
                else {
                    if(!validationString.endsWith(")")) {
                        errs = new ActionErrors();
                        errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.invalidValidationStringFormat"));
                    }
                    className = validationString.substring(0, idx);
                }
                
                try {
                	Class.forName(className);
                }
                catch(ClassNotFoundException cnfe) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.noSuchValidator", className));                	
                }
                
                try {
                    // If there is a default value then validate it 
                    
                    String defaultValue = getDefaultValue().trim();
                    if(!defaultValue.equals("")) {
                    	AttributeDefinition def = getAttributesClass().createAttributeDefinition(type,
                            (String)getWizardSequence().getAttribute(AttributeDefinitionDetailsAction.ATTR_NAME, null),
                            typeMeta,
                            -1,
                            category,
                            defaultValue,
                            visibility,
                            sortOrder,
                            null,
                            false,
                            label,
                            (String)getWizardSequence().getAttribute(AttributeDefinitionDetailsAction.ATTR_DESCRIPTION, null),
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
                    errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.failedToValidate", className));
                }
            }
            return errs;
        }
        return null;
    }
}
