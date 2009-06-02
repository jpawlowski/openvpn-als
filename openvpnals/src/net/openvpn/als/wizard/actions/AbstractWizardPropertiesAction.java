
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.wizard.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.AbstractPropertyKey;
import net.openvpn.als.boot.PropertyClass;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.PropertyDefinitionCategory;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.PropertyItemImpl;
import net.openvpn.als.properties.forms.PropertiesForm;
import net.openvpn.als.wizard.forms.AbstractWizardPropertiesForm;

/**
 * <p>
 * Abstract properties action.
 */
public abstract class AbstractWizardPropertiesAction extends AbstractWizardAction {

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        rebuildItems((PropertiesForm) form, request);
        ActionForward fwd = super.unspecified(mapping, form, request, response);
        return fwd;
    }

    protected void configureProperties() {
    }

    /**
     * Rebuild items.
     * 
     * @param form Properties form
     * @param request request
     * @throws Exception
     */
    protected void rebuildItems(PropertiesForm form, HttpServletRequest request) throws Exception {
        configureProperties();
        AbstractWizardPropertiesForm pf = (AbstractWizardPropertiesForm) form;
        List<PropertyItemImpl> propertyItemImpls = new ArrayList<PropertyItemImpl>();
        List<PropertyDefinitionCategory> categoryDefinitions = new ArrayList<PropertyDefinitionCategory>();
        Collection<PropertyDefinitionCategory> sourceCategories = null;
        int parentCategory = pf.getParentCategory();
        for (PropertyClass propertyClass : pf.getPropertyClasses()) {
            /*
             * If no parent category is supplied, then assume all categories in
             * the class, otherwise get all the child categories of the supplied
             * one
             */
            if (parentCategory == 0) {
                sourceCategories = propertyClass.getCategories();
            } else {
                PropertyDefinitionCategory category = propertyClass.getPropertyDefinitionCategory(parentCategory);
                if (category != null) {
                    sourceCategories = category.getCategories();
                } else {
                    sourceCategories = null;
                }
            }

            if (sourceCategories != null) {
                for (PropertyDefinitionCategory def : sourceCategories) {
                    if (def.isEnabled()) {
                        categoryDefinitions.add(def);
                        if (pf.getSelectedCategory() == -1) {
                            pf.setSelectedCategory(def.getId());
                            pf.setSelectedTab("category." + def.getId());
                        }
                        // TODO needs to more be efficient
                        for (PropertyDefinition propDef : propertyClass.getDefinitions()) {
                            if (!propDef.isHidden() && propDef.getCategory() == def.getId()) {
                                propertyItemImpls.add(new PropertyItemImpl(request, propDef, Property.getProperty(createKey(
                                    propDef, pf))));
                            }
                        }

                    }
                }
            }
        }
        PropertyItemImpl[] items = new PropertyItemImpl[propertyItemImpls.size()];
        propertyItemImpls.toArray(items);
        pf.setPropertyItems(items);
        pf.setCategoryDefinitions(categoryDefinitions);
    }

    /**
     * Create a concrete implementation of an {@link AbstractPropertyKey} that
     * should be used to retrieve the values of properties to be displayed on
     * this form.
     * 
     * @param definition definition
     * @param propertiesForm form
     * @return key
     * @throws Exception if key cannot be created
     */
    public abstract AbstractPropertyKey createKey(PropertyDefinition definition, PropertiesForm propertiesForm) throws Exception;

}
