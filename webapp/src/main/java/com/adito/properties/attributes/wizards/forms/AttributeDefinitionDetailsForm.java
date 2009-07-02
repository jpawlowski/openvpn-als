
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributesPropertyClass;
import com.adito.properties.attributes.wizards.actions.AttributeDefinitionDetailsAction;
import com.adito.properties.impl.resource.ResourceAttributes;
import com.adito.properties.impl.userattributes.UserAttributes;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 * Form that allows an administrator to create a new <i>Attribute Definition</i>
 * of some type.
 * 
 * @author brett
 */
public class AttributeDefinitionDetailsForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(AttributeDefinitionDetailsForm.class);

    private String attributeClass;
    private String name;
    private String description;

    /**
     * Constructor
     */
    public AttributeDefinitionDetailsForm() {
        super(true, false, "/WEB-INF/jsp/content/properties/attributeDefinitionWizard/attributeDefinitionDetails.jspf", "name",
                        true, false, "attributeDefinitionDetails", "properties",
                        "attributeDefinitionWizard.attributeDefinitionDetails", 1);
    }

    /**
     * Get the name of the attribute definition
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the attribute definition
     * 
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the description of the attribute definition.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the attribute definition.
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the selected <i>Attribute class</i>. An Attribute class is a
     * specialisation of a <i>Property Class</i> and the class names can
     * normally be found as constants in the <i>Property Class</i>
     * implementation. See {@link UserAttributes#NAME} for example.
     * 
     * @return attribute class
     */
    public String getAttributeClass() {
        return attributeClass;
    }

    /**
     * Get the selected <i>Attribute class</i>.
     * 
     * @param attributeClass
     * @see #getAttributeClass()
     */
    public void setAttributeClass(String attributeClass) {
        this.attributeClass = attributeClass;
    }

    /**
     * Get a list of <i>Attribute Classes</i> that may be chosen in this first
     * step.
     * 
     * @return attribute classes
     */
    public List<String> getAttributeClasses() {
        List<String> l = new ArrayList<String>();
        for (PropertyClass propertyClass : PropertyClassManager.getInstance().getPropertyClasses()) {
            // we don't want to do the resource properties here.
            if (propertyClass instanceof AttributesPropertyClass && !propertyClass.getName().equals(ResourceAttributes.NAME)) {
                l.add(propertyClass.getName());
            }
        }
        return l;
    }

    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        name = (String) sequence.getAttribute(AttributeDefinitionDetailsAction.ATTR_NAME, "");
        description = (String) sequence.getAttribute(AttributeDefinitionDetailsAction.ATTR_DESCRIPTION, "");
        attributeClass = (String) sequence.getAttribute(AttributeDefinitionDetailsAction.ATTR_CLASS, UserAttributes.NAME);
    }

    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(AttributeDefinitionDetailsAction.ATTR_NAME, name);
        sequence.putAttribute(AttributeDefinitionDetailsAction.ATTR_DESCRIPTION, description);
        sequence.putAttribute(AttributeDefinitionDetailsAction.ATTR_CLASS, attributeClass);
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (isCommiting()) {
            ActionErrors errs = new ActionErrors();
            if (getName().trim().equals("")) {
                errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.noName"));
            } else if (getDescription().trim().equals("")) {
                errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.noDescription"));
            } else {
                if (!getEditing()) {
                    try {
                        PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(getAttributeClass());
                        AttributeDefinition def = (AttributeDefinition) propertyClass.getDefinition(getName());
                        if (def != null) {
                            errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.duplicateName", getName()));
                        }
                    } catch (Exception e) {
                        log.error("Failed to test if attribute exists.", e);
                        errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.duplicateName", getName()));
                    }
                }
                if (!getName().matches("^[a-zA-Z0-9_-]*$")) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage(getResourcePrefix() + ".error.invalidName"));
                }
            }
            return errs;
        }
        return null;
    }
}
