
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
			
package com.adito.policyframework.wizards.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.PropertyList;
import com.adito.core.UserDatabaseManager;
import com.adito.security.LogonControllerFactory;
import com.adito.security.UserDatabase;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

public class PolicySummaryForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(PolicySummaryForm.class);

    // Private statics for sequence attributes
    
    // Private instance variables
    private String policyName;
    private String policyDescription;
    private List selectedAccounts;
    private List selectedRoles;
    // TODO commented in case nested policies are re-introduced

    public PolicySummaryForm() {
        super(false, true, "/WEB-INF/jsp/content/policyframework/policyWizard/policySummary.jspf",
            "", true, true, "policySummary", "policyframework", "policyWizard.policySummary", 3);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request)  throws Exception {
        policyName = (String)sequence.getAttribute(PolicyDetailsForm.ATTR_RESOURCE_NAME, "");
        policyDescription = (String)sequence.getAttribute(PolicyDetailsForm.ATTR_RESOURCE_DESCRIPTION, "");
        PropertyList la = (PropertyList)sequence.getAttribute(PolicyPrincipalSelectionForm.ATTR_SELECTED_ACCOUNTS, null);
        PropertyList lr = (PropertyList)sequence.getAttribute(PolicyPrincipalSelectionForm.ATTR_SELECTED_ROLES, null);
        selectedAccounts = new ArrayList();
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(LogonControllerFactory.getInstance().getSessionInfo(request).getUser().getRealm());
        for(Iterator i = la.iterator(); i.hasNext(); ) {
            selectedAccounts.add(udb.getAccount(i.next().toString()).getPrincipalName());
        }
        selectedRoles = new ArrayList();
        for(Iterator i = lr.iterator(); i.hasNext(); ) {
            selectedRoles.add(udb.getRole(i.next().toString()).getPrincipalName());
        }
    }

    /**
     * @return Returns the policyName.
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * @return Returns the selectedRoles.
     */
    public List getSelectedRoles() {
        return selectedRoles;
    }

    /**
     * @return Returns the selectedAccounts.
     */
    public List getSelectedAccounts() {
        return selectedAccounts;
    }

    /**
     * @return Returns the policyDescription.
     */
    public String getPolicyDescription() {
        return policyDescription;
    }
}
