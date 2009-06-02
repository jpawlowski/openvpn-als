
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
			
package net.openvpn.als.navigation.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.navigation.forms.ProfileSelectionForm;
import net.openvpn.als.properties.ProfilesFactory;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.PropertyProfile;
import net.openvpn.als.properties.impl.profile.ProfilePropertyKey;
import net.openvpn.als.properties.impl.userattributes.UserAttributeKey;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;

/**
 * Action to deal with the form submission from 'selectPropertyProfile'. This
 * places the selected profile into the session so that any user scoped
 * properties come from the newly selected profile. Optionally, it will also set
 * the profile as the users default automatically selected upon next logon. This
 * information is stored as a user attribute.
 */

public class SelectPropertyProfileAction extends AuthenticatedAction {

    final static Log log = LogFactory.getLog(SelectPropertyProfileAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        // Get the property profile selected
        User user = getSessionInfo(request).getUser();
        ProfileSelectionForm profileSelectionForm = (ProfileSelectionForm) form;
        PropertyProfile profile = ProfilesFactory.getInstance().getPropertyProfile(
                        profileSelectionForm.getSelectedPropertyProfile());
        if (profile == null) {
            profile = ProfilesFactory.getInstance().getPropertyProfile(user.getPrincipalName(), "Default",
                            user.getRealm().getResourceId());
            if (profile == null) {
                throw new Exception("No default profile.");
            }
        }

        // Make the selected profile the one in use for this session
        if (log.isInfoEnabled())
        	log.info("Switching user " + user.getPrincipalName() + " to profile " + profile.getResourceName());
        request.getSession().setAttribute(Constants.SELECTED_PROFILE, profile);
        String originalRequest = (String) request.getSession().getAttribute(Constants.ORIGINAL_REQUEST);

        // Optionally set the users default property profile
        if (profileSelectionForm.getMakeDefault()) {
            Property.setProperty(new UserAttributeKey(user, User.USER_STARTUP_PROFILE), String.valueOf(profile.getResourceId()), getSessionInfo(request));
        }

        // Reset the navigation and timeouts, they may be different in this new
        // profile
        CoreUtil.resetMainNavigation(request.getSession());
        LogonControllerFactory.getInstance().resetSessionTimeout(user, profile, request.getSession());

        // The new profile may have 'Automatically launch VPN client' enabled so
        // launch the VPN client
        if (!DefaultAgentManager.getInstance().hasActiveAgent(LogonControllerFactory.getInstance().getSessionInfo(request))
                        && Property.getPropertyBoolean(new ProfilePropertyKey(profile.getResourceId(), user.getPrincipalName(),
                                        "client.autoStart", user.getRealm().getResourceId()))) {
            request.getSession().removeAttribute(Constants.ORIGINAL_REQUEST);
            request.getSession().setAttribute(Constants.REQ_ATTR_LAUNCH_AGENT_REFERER, originalRequest);
            return mapping.findForward("launchAgent");
        }

        /*
         * Its possible the profile selection page is being displayed as the
         * result of the 'Select profile on logon' option. If so we will have
         * the original request URL that we should forward to, otherwise just
         * return to the referer.
         */
        else if (originalRequest != null) {
            request.getSession().removeAttribute(Constants.ORIGINAL_REQUEST);
            return new ActionForward(originalRequest, true);
        } else {
            if (profileSelectionForm.getReferer() != null) {
                ActionForward fwd = new ActionForward(profileSelectionForm.getReferer(), true);
                return fwd;
            } else {
                String referer = CoreUtil.getReferer(request);
                return referer == null ? mapping.findForward("home") : new ActionForward(referer, true);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.AuthenticatedAction#requiresProfile()
     */
    public boolean requiresProfile() {
        // Profile is not required because this is where one might be set!
        return false;
    }
    
    
    @Override
	public ActionForward checkIntercept(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	/*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }

}