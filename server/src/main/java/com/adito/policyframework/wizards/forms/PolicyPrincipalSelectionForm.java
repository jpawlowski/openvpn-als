
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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.PropertyList;
import com.adito.core.BundleActionMessage;
import com.adito.core.UserDatabaseManager;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.UserDatabase;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

public class PolicyPrincipalSelectionForm extends DefaultWizardForm {
    
    private PropertyList selectedAccounts;
    private PropertyList selectedRoles;
    
    // Statics for sequence attributes
    public final static String ATTR_SELECTED_ACCOUNTS = "selectedAccounts";
    public final static String ATTR_SELECTED_ROLES = "selectedRoles";

    public PolicyPrincipalSelectionForm() {
        super(true, true, "/WEB-INF/jsp/content/policyframework/policyWizard/policyPrincipalSelection.jspf", 
            "", true, false, "policyPrincipalSelection", "policyframework", "policyWizard.policyPrincipalSelection", 2);
    }

    /**
     * @return Returns the selectedAccounts.
     */
    public String getSelectedAccounts() {
        return selectedAccounts.getAsTextFieldText();
    }

    /**
     * @param selectedAccounts The selectedAccounts to set.
     */
    public void setSelectedAccounts(String selectedAccounts) {
        this.selectedAccounts.setAsTextFieldText(selectedAccounts);
    }

    /**
     * @return Returns the selectedRoles.
     */
    public String getSelectedRoles() {
        return selectedRoles.getAsTextFieldText();
    }

    /**
     * @param selectedRoles The selectedRoles to set.
     */
    public void setSelectedRoles(String selectedRoles) {
        this.selectedRoles.setAsTextFieldText(selectedRoles);
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.forms.DefaultWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_SELECTED_ACCOUNTS, selectedAccounts);
        sequence.putAttribute(ATTR_SELECTED_ROLES, selectedRoles);
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.forms.DefaultWizardForm#init(com.adito.wizard.AbstractWizardSequence, javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        selectedAccounts = (PropertyList)sequence.getAttribute(ATTR_SELECTED_ACCOUNTS, new PropertyList());
        selectedRoles = (PropertyList)sequence.getAttribute(ATTR_SELECTED_ROLES, new PropertyList());
    }

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errs = super.validate(mapping, request);
		SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
		if(isCommiting()) {
	        try {
	            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(session.getRealm());
		        for(String account : selectedAccounts) {
		        	try {
		        		udb.getAccount(account);
		        	}
		        	catch(Exception e) {   
		        		if(errs == null) {
		        			errs = new ActionErrors();
		        		}
		                errs.add(Globals.ERROR_KEY, new ActionMessage("editPolicy.error.invalidUser", account));     		
		        	}
		        }
		        for(String role : selectedRoles) {
		        	try {
		        		if(udb.getRole(role) == null) {
		        			throw new Exception();
		        		}
		        	}
		        	catch(Exception e) {   
		        		if(errs == null) {
		        			errs = new ActionErrors();
		        		}
		                errs.add(Globals.ERROR_KEY, new ActionMessage("editPolicy.error.invalidRole", role));     		
		        	}
		        }
	        }
	        catch(Exception e) {
	    		if(errs == null) {
	    			errs = new ActionErrors();
	    		}
	            errs.add(Globals.ERROR_KEY, new ActionMessage("editPolicy.failedToValidate", e.getMessage()));
	        	
	        }
		}
		return errs;
	}
}
