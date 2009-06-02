
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
			
package net.openvpn.als.policyframework.forms;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.core.BundleActionMessage;
import net.openvpn.als.policyframework.Policy;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.PolicyItem;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.security.AuthenticationScheme;
import net.openvpn.als.security.DefaultAuthenticationScheme;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;


public class PoliciesForm extends AbstractResourcesForm<PolicyItem> {
    
    boolean showPersonalPolicies;
    
    public PoliciesForm() {
        super("policies");
    }
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = new ActionErrors();
        // on removal we need to ensure that we are not ostresizing the super user.
        if (this.getActionTarget() != null && this.getActionTarget().equals("confirmRemove")) {
            SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
            boolean found = false;

            try {
                List authSchemes = ResourceUtil.getGrantedResource(info, PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
                for (Iterator iter = authSchemes.iterator(); iter.hasNext();) {
                    AuthenticationScheme element = (DefaultAuthenticationScheme) iter.next();
                    if (!element.isSystemScheme() && element.getEnabled()) {
                        List attachedPolicies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(
                                        element, info.getUser().getRealm());
                        for (Iterator iterator = attachedPolicies.iterator(); iterator.hasNext();) {
                            Policy policy = (Policy) iterator.next();
                            // we don't check the current policy as it is about to be deleted.
                            if (policy.getResourceId() != this.selectedResource && PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(policy,
                                            info.getUser())) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("authenticationSchemes.error.failedToValidateSuperUserAuthSchemeConnection"));
            }

            try {
                if (!found) {
                    errs.add(Globals.ERROR_KEY, new BundleActionMessage("security", "authenticationSchemes.error.mustHavePolicySuperUserAssociation"));
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            
        }
        return errs;
    }

    /**
     * @return <tt>true</tt> if personal policies should be shown.
     */
    public boolean isShowPersonalPolicies() {
        return showPersonalPolicies;
    }

    /**
     * @param showPersonalPolicies
     */
    public void setShowPersonalPolicies(boolean showPersonalPolicies) {
        this.showPersonalPolicies = showPersonalPolicies;
    }

}