
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
			
package com.ovpnals.applications.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ovpnals.applications.ApplicationShortcut;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyList;
import com.ovpnals.extensions.ApplicationParameterDefinition;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.ShortcutParameterItem;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.input.MultiSelectSelectionModel;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.forms.AbstractFavoriteResourceForm;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;

/**
 * Extension of
 * {@link com.ovpnals.policyframework.forms.AbstractFavoriteResourceForm}
 * that allows editing of an <i>Application Shortcut</i>.
 */
public class ApplicationShortcutForm extends AbstractFavoriteResourceForm {
    static Log log = LogFactory.getLog(ApplicationShortcutForm.class);

    // Private instance variables

    private String selectedTab = "details";
    private List<ShortcutParameterItem> parameterItems;
    private boolean autoStart;
    private List<String> categories;
    private List<String> categoryTitles;
    private Locale locale;

    /**
     * Get the list of {@link com.ovpnals.extensions.ShortcutParameterItem}
     * objects that are appropriate for the selected application.
     * 
     * @return list of application shortcut parameter items
     */
    public List getParameterItems() {
        return parameterItems;
    }

    /**
     * Get a {@link com.ovpnals.extensions.ShortcutParameterItem} from this
     * list of objects that are appropriate for the selected application at the
     * specified index.
     * 
     * @param idx index of parameter
     * @return application shortcut parameter item
     */
    public ShortcutParameterItem getParameterItem(int idx) {
        return (ShortcutParameterItem) parameterItems.get(idx);
    }
    
    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * Get a list of category IDs as {@link String} objects.
     * 
     * @return categories
     */
    public List getCategories() {
        return categories;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        if (isCommiting()) {
            for (Iterator i = parameterItems.iterator(); i.hasNext();) {
                ShortcutParameterItem item = (ShortcutParameterItem) i.next();
                try {
                    ActionMessage err = item.validateItem();
                    if (err != null) {
                        if (errors == null) {
                            errors = new ActionErrors();
                        }
                        errors.add(Globals.ERROR_KEY, err);
                    }
                } catch (Exception e) {
                    log.error("Failed to validate.", e);
                    if (errors == null) {
                        errors = new ActionErrors();
                    }
                    errors.add(Globals.ERROR_KEY, new ActionMessage("editApplicationShortcut.error.failedToValidate", e
                                    .getMessage()));
                }
            }
        }
        return errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        if (parameterItems != null) {
            for (Iterator i = parameterItems.iterator(); i.hasNext();) {
                ShortcutParameterItem item = (ShortcutParameterItem) i.next();
                if (item.getDefinition().getType() == PropertyDefinition.TYPE_BOOLEAN) {
                    item.setValue(Boolean.FALSE.toString());
                } else if (item.getDefinition().getType() == PropertyDefinition.TYPE_LIST) {
                    item.setValue(item.getDefinition().getDefaultValue());
                }
            }
        }
        this.autoStart = false;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.tabs.TabModel#getTabCount()
	 */
	public int getTabCount() {
		return getTabCountWithoutAttribute() + (categories == null ? 0 : categories.size());
	}
    
	private  int getTabCountWithoutAttribute() {
	    return (getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? 2 : 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.tabs.TabModel#getTabName(int)
	 */
	public String getTabName(int idx) {
        if (getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
    		switch (idx) {
    			case 0:
    				return "details";
    			case 1:
    				return "policies";
    			default:
    				return (String) categories.get(idx - getTabCountWithoutAttribute());
    		}
        } else {
            switch (idx) {
                case 0:
                    return "details";
                default:
                    return (String) categories.get(idx - getTabCountWithoutAttribute());
            } 
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.tabs.TabModel#getTabTitle(int)
	 */
	public String getTabTitle(int idx) {
        return idx < getTabCountWithoutAttribute() || ((String) categoryTitles.get(idx - getTabCountWithoutAttribute())).equals("") ? null : (String) categoryTitles.get(idx - getTabCountWithoutAttribute());
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.forms.AbstractFavoriteResourceForm#initialise(javax.servlet.http.HttpServletRequest,
     *      com.ovpnals.policyframework.Resource, boolean,
     *      com.ovpnals.input.MultiSelectSelectionModel,
     *      com.ovpnals.boot.PropertyList, com.ovpnals.security.User,
     *      boolean)
     */
    public void initialise(HttpServletRequest request, Resource resource, boolean editing, MultiSelectSelectionModel policyModel,
                           PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        super.initialise(request, resource, editing, policyModel, selectedPolicies, owner, assignOnly);

        //
        parameterItems = new ArrayList<ShortcutParameterItem>();

        // Get the application selected in the previous step and retrieve all of
        // the shortcut parameter items
        ExtensionDescriptor des = ExtensionStore.getInstance().getExtensionDescriptor(
            ((ApplicationShortcut) resource).getApplication());
        if (des == null) {
            throw new Exception("No descriptor named " + ((ApplicationShortcut) resource).getApplication());
        }

        for (Iterator i = des.getParametersAndDefaults().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            ApplicationParameterDefinition def = (ApplicationParameterDefinition) entry.getValue();
            if (!def.isHidden()) {
                try {
                    String value = (String) ((ApplicationShortcut) resource).getParameters().get(def.getName());
                    if (value == null) {
                        value = def.getDefaultValue();
                    }
                    ShortcutParameterItem item = new ShortcutParameterItem(des, def, value, locale);
                    if (log.isDebugEnabled())
                        log.debug("Adding item " + item.getName());
                    parameterItems.add(item);
                } catch (Exception e) {
                    log.warn("Failed to create shortcut parameter for " + def.getName()
                                    + ". Probably a problem with the extension descriptor.", e);
                }
            }
        }
        Collections.sort(parameterItems);

        // Now we have a sorted list of parameter items, build up the list of
        // categories
        categories = new ArrayList<String>();
        categoryTitles = new ArrayList<String>();
        for (Iterator i = parameterItems.iterator(); i.hasNext();) {
            ShortcutParameterItem spi = (ShortcutParameterItem) i.next();
            String category = String.valueOf(spi.getCategory());
            if (!categories.contains(category)) {
                categories.add(category);
                categoryTitles.add(spi.getLocalisedCategory());
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#getSelectedTab()
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.forms.AbstractResourceForm#applyToResource()
     */
    @SuppressWarnings("unchecked")
    public void applyToResource() throws Exception {
        ApplicationShortcut as = (ApplicationShortcut) getResource();
        as.setAutoStart(isAutoStart());
        Map<String, String> parameterMap = as.getParameters();
        parameterMap.clear();
        for (Iterator i = getParameterItems().iterator(); i.hasNext();) {
            ShortcutParameterItem pi = (ShortcutParameterItem) i.next();
            parameterMap.put(pi.getName(), pi.getPropertyValue().toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }

    /**
     * Set the locale to use for application shortcut parameter item names and
     * descriptions. This must be called before
     * {@link #initialise(User, Resource, boolean, MultiSelectSelectionModel, PropertyList, User, boolean)}
     * 
     * @param locale locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}