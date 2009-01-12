
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

import com.adito.boot.PropertyClassManager;
import com.adito.properties.attributes.AttributesPropertyClass;
import com.adito.properties.attributes.wizards.actions.AttributeDefinitionDetailsAction;
import com.adito.properties.attributes.wizards.actions.AttributeDefinitionOptionsAction;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 * <p>
 * The form for the resource summary.
 */
public class AttributeDefinitionSummaryForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(AttributeDefinitionSummaryForm.class);

    private String name, label, category;
    private AttributesPropertyClass attributeClass;
    private int type, weight, visibility;

    /**
     * Constructor
     */
    public AttributeDefinitionSummaryForm() {
        super(false, true, "/WEB-INF/jsp/content/properties/attributeDefinitionWizard/attributeDefinitionSummary.jspf", "", true, true,
                "attributeDefinitionSummary", "properties", "attributeDefinitionWizard.attributeDefinitionSummary", 3);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        name = (String) sequence.getAttribute(AttributeDefinitionDetailsAction.ATTR_NAME, null);
        attributeClass = (AttributesPropertyClass)PropertyClassManager.getInstance().getPropertyClass((String) sequence.getAttribute(AttributeDefinitionDetailsAction.ATTR_CLASS, null));
        type = ((Integer)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE, null)).intValue();
        label = (String)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_LABEL, null);
        category = (String)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_CATEGORY, null);
        weight = ((Integer)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_SORT_ORDER, null)).intValue();
        visibility = ((Integer)sequence.getAttribute(AttributeDefinitionOptionsAction.ATTR_VISIBILITY, null)).intValue();
    }

    /**
     * Get the name of the attribute
     * 
     * @return attribute name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the category.
     * 
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Get the label
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get the visibility.
	 * 
	 * @return the visibility
	 */
	public int getVisibility() {
		return visibility;
	}

	/**
	 * Get the weight (or sort order)
	 * 
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
     * Get the property type
     * 
     * @return type
     */
    public int getType() {
    	return type;
    }

    /**
     * Get the attribute class
     * 
     * @return attribute class
     */
    public AttributesPropertyClass getAttributeClass() {    	
        return attributeClass;
    }
}
