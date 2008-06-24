
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
			
package com.adito.install.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.UserDatabaseManager;
import com.adito.install.forms.SelectUserDatabaseForm;
import com.adito.security.UserDatabase;
import com.adito.security.UserDatabaseDefinition;
import com.adito.wizard.AbstractWizardSequence;

/**
 * Implementation of a {@link AbstractInstallWizardAction} that allows a super
 * user to be created or updated during install.
 * 
 * @see com.adito.install.forms.ConfigureSuperUserForm
 */
public class ConfigureSuperUserAction extends AbstractInstallWizardAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.actions.AbstractWizardAction#previous(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionForward previous = super.previous(mapping, form, request, response);
        AbstractWizardSequence sequence = getWizardSequence(request);
        
        UserDatabase database = (UserDatabase) sequence.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE_INSTANCE, null);
        if (database != null && database.isOpen()) {
            database.close();
        }
        
        String databaseDefinition = (String) sequence.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE, "");
        UserDatabaseDefinition definition = UserDatabaseManager.getInstance().getUserDatabaseDefinition(databaseDefinition);
        if (definition.getInstallationCategory() > 0) {
            return mapping.findForward("configureUserDatabase");
        }
        return previous;
    }
}
