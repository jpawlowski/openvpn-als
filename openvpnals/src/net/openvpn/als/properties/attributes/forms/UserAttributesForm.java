
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
			
package net.openvpn.als.properties.attributes.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.boot.CodedException;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.core.CoreException;
import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.properties.attributes.AttributeValueItem;
import net.openvpn.als.tabs.TabModel;

/**
 * Implementation of {@link net.openvpn.als.core.forms.CoreForm} that allows a
 * user to edit or view their <i>User Attributes</i>.
 */
public class UserAttributesForm extends CoreForm implements TabModel {

    static Log log = LogFactory.getLog(UserAttributesForm.class);

    // Private instance variables

    private List<AttributeValueItem> userAttributeValueItems;
    private String selectedTab;
    private List<String> categoryIds;
    private List<String> categoryTitles;

    /**
     * Initialise the form
     * 
     * @param userAttributeValueItems list of
     *        {@link net.openvpn.als.properties.attributes.AttributeValueItem}
     *        objects
     * @throws Exception on any error
     */
    public void initialize(List<AttributeValueItem> userAttributeValueItems) throws Exception {
        this.userAttributeValueItems = userAttributeValueItems;
        categoryIds = new ArrayList<String>();
        categoryTitles = new ArrayList<String>();
        for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
            AttributeValueItem item = (AttributeValueItem) i.next();
            int idx = categoryIds.indexOf(item.getCategoryId());
            if (idx == -1) {
                categoryIds.add(item.getCategoryId());
                categoryTitles.add(item.getCategoryLabel());
            }
        }
        selectedTab = categoryIds.size() > 0 ? categoryIds.get(0) : "";
    }

    /**
     * Get a list of the category ids
     * 
     * @return category ids
     */
    public List getCategoryIds() {
        return categoryIds;
    }

    /**
     * Get the list of
     * {@link net.openvpn.als.properties.attributes.AttributeValueItem} objects
     * 
     * @return user attributre value items
     */
    public List getUserAttributeValueItems() {
        return userAttributeValueItems;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabCount()
     */
    public int getTabCount() {
        return categoryIds.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabName(int)
     */
    public String getTabName(int idx) {
        return (String) categoryIds.get(idx);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getSelectedTab()
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabTitle(int)
     */
    public String getTabTitle(int idx) {
        return (String) categoryTitles.get(idx);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.forms.CoreForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        if (userAttributeValueItems != null) {
            for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
                AttributeValueItem item = (AttributeValueItem) i.next();
                if (item.getDefinition().getType() == PropertyDefinition.TYPE_BOOLEAN) {
                    item.setSelected(false);
                }
            }
        }
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
            for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
                AttributeValueItem item = (AttributeValueItem) i.next();
                PropertyDefinition def = item.getDefinition();
                try {
                    def.validate(item.getValue().toString(), getClass().getClassLoader());
                } catch (CoreException ce) {
                    ce.getBundleActionMessage().setArg3(item.getLabel());
                    if (errs == null) {
                        errs = new ActionErrors();
                    }
                    errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
                } catch (Exception e) {
                	errs.add(Globals.ERROR_KEY, new ActionMessage("userAttributes.error.failedToValidate", e.getMessage()));
                }
            }
            if (errs != null)
                return errs;
        }
        return super.validate(mapping, request);
    }
}
