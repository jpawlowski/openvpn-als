
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
			
package com.adito.sample.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyList;
import com.adito.input.MultiSelectSelectionModel;
import com.adito.policyframework.Resource;
import com.adito.policyframework.forms.AbstractFavoriteResourceForm;
import com.adito.security.User;

/**
 * <p>
 * Form for providing the attributes to be edited and validated.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class SampleForm extends AbstractFavoriteResourceForm {
    static Log log = LogFactory.getLog(SampleForm.class);

    private String selectedTab = "details";

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = super.validate(mapping, request);
        if (isCommiting()) {
            // TODO any validation required.
        }
        return errs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabCount()
     */
    public int getTabCount() {
        // TODO the number of tabs on edit.
        return 3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabName(int)
     */
    public String getTabName(int idx) {
        // TODO the name of the tab for column idx
        switch (idx) {
            case 0:
                return "details";
            case 1:
                return "other";
            default:
                return "policies";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.forms.AbstractResourceForm#initialise(com.adito.security.User,
     *      com.adito.boot.policyframework.Resource, boolean,
     *      com.adito.boot.MultiSelectSelectionModel,
     *      com.adito.boot.PropertyList, com.adito.security.User)
     */
    public void initialise(User user, Resource resource, boolean editing, MultiSelectSelectionModel policyModel,
                    PropertyList selectedPolicies, User owner) throws Exception {
        super.initialise(user, resource, editing, policyModel, selectedPolicies, owner);
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
     * @see com.adito.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /* (non-Javadoc)
     * @see com.adito.tabs.TabModel#getTabTitle(int)
     */
    public String getTabTitle(int i) {
        // NOTE return null for to get the tab headings from resources (usually what you want)
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.forms.AbstractResourceForm#applyToResource()
     */
    public void applyToResource() throws Exception {
        /* TODO apply any additional values from the form to the current
         * resource object (obtained usimg getResource()) 
         */
    }

    /* (non-Javadoc)
     * @see com.adito.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }
}