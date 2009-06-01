
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
			
package net.openvpn.als.applications.wizards.forms;

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

import net.openvpn.als.applications.ApplicationsPlugin;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.extensions.ApplicationParameterDefinition;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.extensions.ShortcutParameterItem;
import net.openvpn.als.extensions.store.ExtensionStore;
import net.openvpn.als.tabs.TabModel;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.forms.DefaultWizardForm;

/**
 * Extension of a {@link net.openvpn.als.wizard.forms.DefaultWizardForm} that
 * allows an administrator to enter the application shortcut parameters for the
 * selected application.
 * <p>
 * The list of available parameters is also categorised. This allows the UI to
 * organise the items (into tabs in the default UI).
 */
public class ApplicationShortcutWizardAdditionalDetailsForm extends DefaultWizardForm implements TabModel {

	final static Log log = LogFactory.getLog(ApplicationShortcutWizardAdditionalDetailsForm.class);

	/**
	 * Key used to store the map of applicaiton shortcut parameters
	 */
	public final static String ATTR_PARAMETERS = "parameters";
	// Private instance variables

	private List<ShortcutParameterItem> parameterItems;
	private List<String> categories;
	private List<String> categoryTitles;
	private String selectedTab;

	/**
	 * Constructor
	 */
	public ApplicationShortcutWizardAdditionalDetailsForm() {
		super(true,
						true,
						"/WEB-INF/jsp/content/applications/applicationShortcutWizard/additionalDetails.jspf",
						"resourceName",
						true,
						false,
						"applicationShortcutAdditionalDetails",
						ApplicationsPlugin.MESSAGE_RESOURCES_KEY,
						"applicationShortcutWizard.applicationShortcutAdditionalDetails",
						3);
	}

	/**
	 * Get the list of {@link net.openvpn.als.extensions.ShortcutParameterItem}
	 * objects that are appropriate for the selected application.
	 * 
	 * @return list of application shortcut parameter items
	 */
	public List getParameterItems() {
		return parameterItems;
	}

	/**
	 * Get a {@link net.openvpn.als.extensions.ShortcutParameterItem} from this
	 * list of objects that are appropriate for the selected application at the
	 * specified index.
	 * 
	 * @param idx index of parameter
	 * @return application shortcut parameter item
	 */
	public ShortcutParameterItem getParameterItem(int idx) {
		return (ShortcutParameterItem) parameterItems.get(idx);
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
	 * @see net.openvpn.als.wizard.forms.AbstractWizardForm#init(net.openvpn.als.wizard.AbstractWizardSequence,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@SuppressWarnings("unchecked")
	public void init(AbstractWizardSequence wizardSequence, HttpServletRequest request) throws Exception {
		super.init(wizardSequence, request);
		// we need to clear the focused field.
		this.setFocussedField(null);
		//
		parameterItems = (List<ShortcutParameterItem>) wizardSequence.getAttribute(ATTR_PARAMETERS, null);

		// Get the application selected in the previous step and retrieve all of
		// the shortcut parameter items
		ExtensionDescriptor des = ExtensionStore.getInstance()
						.getExtensionDescriptor((String) wizardSequence.getAttribute(ApplicationShortcutWizardApplicationForm.ATTR_SELECTED_APPLICATION,
							null));
		if (parameterItems == null) {
			parameterItems = new ArrayList<ShortcutParameterItem>();
			
			// use the array we we want the index to get the correct element.
			Object[] entryArray = des.getParametersAndDefaults().entrySet().toArray();
			for (int arrayIndex = 0; arrayIndex < entryArray.length; arrayIndex++) {
                ApplicationParameterDefinition def = (ApplicationParameterDefinition) ((Map.Entry)entryArray[arrayIndex]).getValue();
                if (!def.isHidden()) {
                    if (this.getFocussedField() == null){
                        // now set the focused field to the first attribute.
                        this.setFocussedField("f_" + arrayIndex);
                    }
                    ShortcutParameterItem item = new ShortcutParameterItem(des,
                                    def,
                                    def.getDefaultValue().equals(PropertyDefinition.UNDEFINED_PARAMETER) ? ""
                                        : def.getDefaultValue(),
                                    (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY));
                    if (log.isDebugEnabled())
                        log.debug("Adding item " + item.getName());
                    parameterItems.add(item);
                }
            }
			Collections.sort(parameterItems);

			// Now we have a sorted list of parameter items, build up the list
			// of categories
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = null;
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
					errors.add(Globals.ERROR_KEY, new ActionMessage("editApplicationShortcut.error.failedToValidate", e.getMessage()));
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
	public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.tabs.TabModel#getTabCount()
	 */
	public int getTabCount() {
		return categories.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.tabs.TabModel#getTabName(int)
	 */
	public String getTabName(int idx) {
		return (String) categories.get(idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.tabs.TabModel#getTabTitle(int)
	 */
	public String getTabTitle(int idx) {
		String title = (String) categoryTitles.get(idx);
		return title.equals("") ? null : title;
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

	public void apply(AbstractWizardSequence sequence) throws Exception {
		super.apply(sequence);
		sequence.putAttribute(ATTR_PARAMETERS, parameterItems);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.tabs.TabModel#getTabBundle(int)
	 */
	public String getTabBundle(int idx) {
		return null;
	}
}
