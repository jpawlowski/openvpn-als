
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
			
package com.adito.wizard.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyDefinitionCategory;
import com.adito.core.CoreException;
import com.adito.core.CoreUtil;
import com.adito.properties.PropertyItem;
import com.adito.properties.PropertyItemImpl;
import com.adito.properties.forms.PropertiesForm;
import com.adito.tabs.TabModel;
import com.adito.wizard.AbstractWizardSequence;

/**
 */
public abstract class AbstractWizardPropertiesForm extends AbstractWizardForm implements PropertiesForm, TabModel {

    public final static String ATTR_PROPERTY_ITEMS = "propertItems";
    public final static String ATTR_PROPERTY_ITEM_VALUES = "propertItemValues";
    public final static String ATTR_CATEGORY_DEFINITIONS = "categoryDefinitions";
    public final static String ATTR_ALL_PROPERTY_ITEM_VALUES = "allPropertItemValues";

    private PropertyItemImpl[] propertyItemImpls;
    private List<PropertyDefinitionCategory> categoryDefinitions;
    private List<PropertyClass> propertyClasses;
    private int selectedCategory = -1;
    private List<String> tabTitles;
    private HttpServletRequest request;
    private String selectedTab;
    private int categoryIdx;

    public AbstractWizardPropertiesForm(List<PropertyClass> propertyClasses) {
        super();
        setPropertyClasses(propertyClasses);
    }

    public List<PropertyClass> getPropertyClasses() {
        return propertyClasses;
    }

    public void setPropertyClasses(List<PropertyClass> propertyClasses) {
        this.propertyClasses = propertyClasses;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        sequence.putAttribute(ATTR_ALL_PROPERTY_ITEM_VALUES, propertyItemImpls);
        for (int i = 0; i < propertyItemImpls.length; i++) {
            sequence.putAttribute(getKeyForForm(ATTR_PROPERTY_ITEM_VALUES) + "." + propertyItemImpls[i].getName(),
                propertyItemImpls[i].getValue());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {

        this.request = request;

        /*
         * Store the category definitions and property items so they can be used
         * throughout the lifetime of the wizard
         */
        sequence.putAttribute(getKeyForForm(ATTR_PROPERTY_ITEMS), propertyItemImpls);
        if (sequence.getAttribute(getKeyForForm(ATTR_CATEGORY_DEFINITIONS), null) == null) {
            sequence.putAttribute(getKeyForForm(ATTR_CATEGORY_DEFINITIONS), categoryDefinitions);
        } else {
            for (int i = 0; i < propertyItemImpls.length; i++) {
                String n = getKeyForForm(ATTR_PROPERTY_ITEM_VALUES) + "." + propertyItemImpls[i].getName();
                Object val = sequence.getAttribute(n, null);
                if (val != null) {
                    propertyItemImpls[i].setValue(val);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        categoryIdx = 0;
        AbstractWizardSequence seq = getWizardSequence(request);
        propertyItemImpls = seq == null ? null : (PropertyItemImpl[]) seq.getAttribute(getKeyForForm(ATTR_PROPERTY_ITEMS), null);
        categoryDefinitions = seq == null ? null : (List) seq.getAttribute(getKeyForForm(ATTR_CATEGORY_DEFINITIONS), null);
        if (propertyItemImpls != null) {
            for (int i = 0; i < propertyItemImpls.length; i++) {
                if (propertyItemImpls[i].getType() == PropertyDefinition.TYPE_BOOLEAN) {
                    propertyItemImpls[i].setValue("false");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#getCategoryDefinitions()
     */
    public List getCategoryDefinitions() {
        return categoryDefinitions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#getEnabled()
     */
    public boolean getEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#getParentCategory()
     */
    public abstract int getParentCategory();

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#getPropertyItem(int)
     */
    public PropertyItem getPropertyItem(int idx) {
        return propertyItemImpls[idx];
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#getPropertyItems()
     */
    public PropertyItem[] getPropertyItems() {
        return propertyItemImpls;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#setCategoryDefinitions(java.util.List)
     */
    public void setCategoryDefinitions(List categoryDefinitions) {
        this.categoryDefinitions = categoryDefinitions;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#setPropertyItem(int,
     *      com.adito.applications.PropertyItem)
     */
    public void setPropertyItem(int idx, PropertyItemImpl item) {
        propertyItemImpls[idx] = item;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.forms.PropertiesForm#setPropertyItems(com.adito.applications.PropertyItem[])
     */
    public void setPropertyItems(PropertyItemImpl[] propertyItems) {
        this.propertyItemImpls = propertyItems;
    }

    String getKeyForForm(String key) {
        return getPageName() + "." + key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (isCommiting()) {
            ActionErrors errs = null;
            PropertyItem[] items = getPropertyItems();
            for (int i = 0; i < items.length; i++) {
                PropertyDefinition def = items[i].getDefinition();
                try {
                    def.validate(String.valueOf(items[i].getPropertyValue()), getClass().getClassLoader());
                } catch (CoreException ce) {
                    if (errs == null) {
                        errs = new ActionErrors();
                    }
                    ce.getBundleActionMessage().setArg3(
                        CoreUtil.getMessageResources(request.getSession(), def.getMessageResourcesKey()).getMessage(
                            (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY), def.getName() + ".name"));
                    errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
                } catch (Exception e) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("properties.error.failedToValidate", e.getMessage()));
                }
            }
            if (errs != null)
                return errs;

        }
        return super.validate(mapping, request);
    }

    public void setSelectedCategory(int selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public int getSelectedCategory() {
        return selectedCategory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getSelectedTab()
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return categoryDefinitions.get(idx).getBundle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabCount()
     */
    public int getTabCount() {
        return categoryDefinitions.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabName(int)
     */
    public String getTabName(int idx) {
        return "category." + categoryDefinitions.get(idx).getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabTitle(int)
     */
    public String getTabTitle(int idx) {
        PropertyDefinitionCategory cat = categoryDefinitions.get(idx);
        return CoreUtil.getMessageResources(request.getSession(), cat.getBundle()).getMessage(
            (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY), getTabName(idx) + ".name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /**
     * Get a list of the next set of property items for the next category. This
     * iterator is reset upon {@link #reset(ActionMapping, HttpServletRequest)}
     * and returns <code>null</code> when there are no more lists.
     * 
     * @return list of property items for the next category
     */
    public List<PropertyItemImpl> getNextCategory() {
        if (propertyItemImpls.length == 0) {
            return null;
        }
        List<PropertyItemImpl> l = new ArrayList<PropertyItemImpl>();
        int curCat = -1;
        while (categoryIdx < propertyItemImpls.length) {
            if (curCat == -1) {
                curCat = propertyItemImpls[categoryIdx].getCategory();
            } else if (curCat != propertyItemImpls[categoryIdx].getCategory()) {
                break;
            }
            l.add(propertyItemImpls[categoryIdx]);
            categoryIdx++;
        }
        return l;
    }
}
