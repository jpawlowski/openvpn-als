
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
			
package com.adito.networkplaces.wizards.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.PropertyList;
import com.adito.networkplaces.NetworkPlacePlugin;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

public class NetworkPlaceSummaryForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(NetworkPlaceSummaryForm.class);

    private User user;
    private String resourceName;
    private List selectedPolicies;

    public NetworkPlaceSummaryForm() {
        super(false, true, "/WEB-INF/jsp/content/vfs/networkingWizard/networkPlaceSummary.jspf", 
            "previous", true, true, "networkPlaceSummary", NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, "networkPlaceWizard.networkPlaceSummary", 4);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request)  throws Exception {
        resourceName = (String)sequence.getAttribute(NetworkPlaceDetailsForm.ATTR_RESOURCE_NAME, null);
        PropertyList l = (PropertyList)sequence.getAttribute(NetworkPlacePolicySelectionForm.ATTR_SELECTED_POLICIES, null);
        selectedPolicies = new ArrayList();
        if (SessionInfo.USER_CONSOLE_CONTEXT == LogonControllerFactory.getInstance().getSessionInfo(request).getNavigationContext()) {
            for(Iterator i = l.iterator(); i.hasNext(); ) {
                selectedPolicies.add(i.next().toString());
            }
        } else {
            for(Iterator i = l.iterator(); i.hasNext(); ) {
                selectedPolicies.add(PolicyDatabaseFactory.getInstance().getPolicy(Integer.parseInt(i.next().toString())).getResourceName());
            }
        }
        user = sequence.getSession().getUser();
    }

    /**
     * @return Returns the name.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return Returns the selectedPolicies.
     */
    public List getSelectedPolicies() {
        return selectedPolicies;
    }

    /**
     * @return Returns the user.
     */
    public User getUser() {
        return user;
    }
}
