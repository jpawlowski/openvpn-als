
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
			
package net.openvpn.als.properties.actions;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.policyframework.ResourceStack;
import net.openvpn.als.properties.ProfilesFactory;
import net.openvpn.als.properties.PropertyProfile;
import net.openvpn.als.properties.forms.ProfilePropertiesForm;
import net.openvpn.als.security.Constants;

/**
 */
public abstract class AbstractProfilePropertiesAction extends AbstractPropertiesAction {
    static Log log = LogFactory.getLog(AbstractProfilePropertiesAction.class);

    /* (non-Javadoc)
     * @see net.openvpn.als.properties.actions.AbstractPropertiesAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        super.unspecified(mapping, form, request, response);
        ProfilePropertiesForm pf = (ProfilePropertiesForm)form;
        
        if (pf.getSelectedPropertyProfile() == -1) {
            if (request.getSession().getAttribute(Constants.SELECTED_PROFILE) != null) {
                pf.setSelectedPropertyProfile(((PropertyProfile) request.getSession().getAttribute(Constants.SELECTED_PROFILE)).getResourceId());
            } else {
                pf.setSelectedPropertyProfile(0);
            }
        }
        PropertyProfile selectedPropertyProfile = ProfilesFactory.getInstance().getPropertyProfile(pf.getSelectedPropertyProfile());
        if (selectedPropertyProfile == null) {
            selectedPropertyProfile = ProfilesFactory.getInstance().getPropertyProfile(getSessionInfo(request).getUser().getPrincipalName(),
                "Default", getSessionInfo(request).getUser().getRealm().getResourceId());
            if (selectedPropertyProfile == null) {
                selectedPropertyProfile = ProfilesFactory.getInstance().getPropertyProfile(null, "Default",
                                UserDatabaseManager.getInstance().getDefaultUserDatabase().getRealm().getResourceId());
            }
        }
        ResourceStack.pushToEditingStack(request.getSession(), selectedPropertyProfile);
        
        // Get the property profiles
        List propertyProfiles = (List)request.getSession().getAttribute(Constants.PROFILES);
        if (selectedPropertyProfile == null) {
            for (Iterator i = propertyProfiles.iterator(); i.hasNext();) {
                selectedPropertyProfile = (PropertyProfile) i.next();
                if (selectedPropertyProfile.getResourceName().equals("Default")) {
                    break;
                }
            }
        }
        pf.setPropertyProfiles(propertyProfiles);
        pf.setSelectedPropertyProfile(selectedPropertyProfile.getResourceId());
        
        // Build an display
        return rebuildItems(mapping, pf.getParentCategory(), pf, request, getSessionInfo(request).getUser());
    } 

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward configureProfiles(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
    	ResourceStack.popFromEditingStack(request.getSession());
        return mapping.findForward("configureProfiles");
    }
}