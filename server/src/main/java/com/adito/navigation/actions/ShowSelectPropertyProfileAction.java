
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
			
package com.adito.navigation.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.navigation.forms.ProfileSelectionForm;
import com.adito.properties.ProfilesListDataSource;
import com.adito.properties.Property;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.security.Constants;
import com.adito.security.LogonController;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;

/**
 * Action to 
 * @author brett
 *
 */

public class ShowSelectPropertyProfileAction extends AuthenticatedAction {

    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ProfileSelectionForm profileSelectionForm = (ProfileSelectionForm) form;
        if (LogonControllerFactory.getInstance().hasClientLoggedOn(request, response) != LogonController.LOGGED_ON) {
            throw new Exception("You must be logged on to select a property.");
        }
        List propertyProfiles = null;
        int selectedPropertyProfile = 0;
        User user = LogonControllerFactory.getInstance().getUser(request);
        if (user != null) {
            propertyProfiles = (List)request.getSession().getAttribute(Constants.PROFILES);
            String selectedProfile = Property.getProperty(new UserAttributeKey(user, User.USER_STARTUP_PROFILE));
            if(selectedProfile.equals(ProfilesListDataSource.SELECT_ON_LOGIN)) {
            	selectedPropertyProfile = 0;
            }
            else {
            	try {
            		selectedPropertyProfile = Integer.parseInt(selectedProfile);
            	}
            	catch(NumberFormatException nfe) {
            		selectedPropertyProfile = 0;
            	}
            }
        } else {
            throw new Exception("Not logged on.");
        }
        profileSelectionForm.setReferer(CoreUtil.getReferer(request));
        profileSelectionForm.setPropertyProfiles(propertyProfiles);
        profileSelectionForm.setSelectedPropertyProfile(selectedPropertyProfile);
        return mapping.findForward("success");
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#requiresProfile()
     */
    public boolean requiresProfile() {
        // Profile is not required because this is where one might be set!        
        return false;
    }

   
    @Override
	public ActionForward checkIntercept(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	/* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }
}