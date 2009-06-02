
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
			
package net.openvpn.als.core.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.PopupException;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.vfs.webdav.DAVAuthenticationRequiredException;


/**
 * Abstract implementation of a {@link net.openvpn.als.core.actions.AuthenticatedDispatchAction}
 * to be used by actions that exist in a popup window instead of the main interface.
 * <p>
 * Its primary use is to catch all exceptions and turn them into
 * {@link net.openvpn.als.core.PopupException}. This is then detected by
 * the globa error handler which makes sure a different error page is 
 * loaded more suitable for the <i>Popup</i> window it is in.
 */
public abstract class AbstractPopupAuthenticatedDispatchAction extends AuthenticatedDispatchAction {


    /**
     * Use this constructor for actions that do not require any resource
     * permissions
     */
    public AbstractPopupAuthenticatedDispatchAction() {
        super();
    }


    /**
     * Use this constructor for actions that require a resource permission to
     * operator
     * 
     * @param resourceType resource type
     * @param permissions permission required
     */
    public AbstractPopupAuthenticatedDispatchAction(ResourceType resourceType, Permission[] permissions) {
        super(resourceType, permissions);
    }

    /**
     * Use this constructor for actions that require a resource permission to
     * operator
     * 
     * @param resourceType resource type 
     * @param permissions permission required
     * @param requiresResources requires access to resources of type
     */
    public AbstractPopupAuthenticatedDispatchAction(ResourceType resourceType, Permission[] permissions,
                                                    ResourceType requiresResources) {
        super(resourceType, permissions, requiresResources);
    }


    /* (non-Javadoc)
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return super.execute(mapping, form, request, response);
        }
        catch(DAVAuthenticationRequiredException dare) {
            // let this go through
            throw dare;
        }
        catch(Throwable t) {
            throw new PopupException(t);
        }
    }

}
