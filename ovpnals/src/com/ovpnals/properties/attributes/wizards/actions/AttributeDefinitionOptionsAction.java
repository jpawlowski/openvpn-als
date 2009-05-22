
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.properties.attributes.wizards.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.wizards.forms.AttributeDefinitionOptionsForm;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.wizard.actions.AbstractWizardAction;

/**
 * Additional attribute definition options. These may be edited after 
 * the attribute definition has been created.
 * 
 * @author brett
 */
public class AttributeDefinitionOptionsAction extends AbstractWizardAction {

	/**
	 * Label.  See {@link AttributeDefinition#getLabel()}.
	 */
    public final static String ATTR_LABEL = "label";
    
	/**
	 * Category.  See {@link PropertyDefinition#getCategory()}.
	 */
    public final static String ATTR_CATEGORY = "category";
    
    /**
     * Type meta data.  See {@link PropertyDefinition#getTypeMeta()}.
     */
    public final static String ATTR_TYPE_META = "typeMeta";
    
    /**
     * Type. See {@link AttributeDefinition#getType()}.
     */
    public final static String ATTR_TYPE = "type";
    
    /**
     * Validation string. See {@link AttributeDefinition#getValidationString()}
     */
    public final static String ATTR_VALIDATION_STRING = "validationString";
    
    /**
     * Default value. Category.  See {@link PropertyDefinition#getDefaultValue()}.
     */
    public final static String ATTR_DEFAULT_VALUE = "defaultValue";
    
    /**
     * Visibility. See {@link AttributeDefinition#getVisibility()}.
     */
    public final static String ATTR_VISIBILITY = "visibility";
    
    /**
     * Sort order.  See {@link PropertyDefinition#getSortOrder()}.
     */
    public final static String ATTR_SORT_ORDER = "sortOrder";

    /**
     * Constructor
     */
    public AttributeDefinitionOptionsAction() {
        super();
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

	/**
	 * Type of attribute has changed, so choose a default validator
	 * 
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @return forward forward
	 * @throws Exception on any error
	 */
	public ActionForward typeChanged(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// Set a default validator when type changes
		AttributeDefinitionOptionsForm adof = (AttributeDefinitionOptionsForm)form;
		if(adof.getType() == PropertyDefinition.TYPE_COLOR ||
						adof.getType() == PropertyDefinition.TYPE_LIST ||
						adof.getType() == PropertyDefinition.TYPE_MULTI_ENTRY_LIST ||
						adof.getType() == PropertyDefinition.TYPE_MULTI_SELECT_LIST ||
						adof.getType() == PropertyDefinition.TYPE_TIME_IN_MS) {
			getWizardSequence(request).putAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, "");
		}
		else if(adof.getType() == PropertyDefinition.TYPE_BOOLEAN ) {
			getWizardSequence(request).putAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, "com.ovpnals.input.validators.BooleanValidator");			
		}
		else if(adof.getType() == PropertyDefinition.TYPE_INTEGER) {
			getWizardSequence(request).putAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, "com.ovpnals.input.validators.IntegerValidator(minValue=0,maxValue=9999999)");
		}
		else if(adof.getType() == PropertyDefinition.TYPE_STRING) {
			getWizardSequence(request).putAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, "com.ovpnals.input.validators.StringValidator(minLength=0,maxLength=30,trim=true,regExp=,pattern=)");
		}
		else if(adof.getType() == PropertyDefinition.TYPE_TEXT_AREA) {
			getWizardSequence(request).putAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, "com.ovpnals.input.validators.StringValidator(minLength=0,maxLength=255,trim=true,regExp=,pattern=)");
		}
		
		getWizardSequence(request).putAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE, new Integer(adof.getType()));
		
		return super.unspecified(mapping, form, request, response);
	}
}
