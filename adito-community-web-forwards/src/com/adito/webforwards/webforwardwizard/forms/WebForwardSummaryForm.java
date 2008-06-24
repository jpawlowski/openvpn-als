
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
			
package com.adito.webforwards.webforwardwizard.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.adito.boot.PropertyList;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.webforwards.webforwardwizard.actions.WebForwardSpecificDetailsAction;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 * The form for the resource summary.
 */
public class WebForwardSummaryForm extends DefaultWizardForm {
    private User user;
    private String resourceName;
    private List<String> selectedPolicies;
//    private int type = -1;

    /**
     * Construtor
     */
    public WebForwardSummaryForm() {
        super(false, true, "/WEB-INF/jsp/content/webforward/webforwardwizard/webForwardSummary.jspf", "previous", true, true, "webForwardSummary",
                        "webForwards", "webForwardWizard.webForwardSummary", 6);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        resourceName = (String) sequence.getAttribute(WebForwardSpecificDetailsForm.ATTR_RESOURCE_NAME, null);
        PropertyList l = (PropertyList) sequence.getAttribute(WebForwardPolicySelectionForm.ATTR_SELECTED_POLICIES, null);
        selectedPolicies = new ArrayList<String>();
        if (SessionInfo.USER_CONSOLE_CONTEXT == LogonControllerFactory.getInstance().getSessionInfo(request).getNavigationContext()) {
            for(Iterator i = l.iterator(); i.hasNext(); ) {
                selectedPolicies.add(i.next().toString());
            }
        } else {
            for(Iterator i = l.iterator(); i.hasNext(); ) {
                selectedPolicies.add(PolicyDatabaseFactory.getInstance().getPolicy(Integer.parseInt(i.next().toString())).getResourceName());
            }
        }
        user = (User) sequence.getAttribute(WebForwardSpecificDetailsAction.ATTR_USER, null);
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