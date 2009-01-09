
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
			
package com.adito.networkplaces.wizards.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreEvent;
import com.adito.core.CoreServlet;
import com.adito.networkplaces.NetworkPlace;
import com.adito.networkplaces.NetworkPlaceDatabaseFactory;
import com.adito.networkplaces.NetworkPlaceResourceType;
import com.adito.networkplaces.NetworkPlacesEventConstants;
import com.adito.networkplaces.wizards.forms.DefaultNetworkPlaceDetailsForm;
import com.adito.networkplaces.wizards.forms.NetworkPlaceDetailsForm;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.security.SessionInfo;
import com.adito.vfs.webdav.DAVProcessor;
import com.adito.vfs.webdav.DAVServlet;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.WizardActionStatus;
import com.adito.wizard.actions.AbstractWizardAction;
import com.adito.wizard.forms.AbstractWizardFinishForm;

/**
 * Implementation of {@link com.adito.wizard.actions.AbstractWizardAction}
 * that actually creates a <i>Network PLace</i> at the end of a wizard.
 */
public class NetworkPlaceFinishAction extends AbstractWizardAction {
    final static Log log = LogFactory.getLog(NetworkPlaceFinishAction.class);
    private NetworkPlace newNetworkPlace;

    /**
     * Constructor
     */
    public NetworkPlaceFinishAction() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        // Do the install
        List actionStatus = new ArrayList();
        AbstractWizardSequence seq = getWizardSequence(request);        
        String scheme = (String) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_SCHEME, null);
        String shortName = (String) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_RESOURCE_NAME, null);
        String description = (String) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);        
        String host = (String) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_HOST, null);
        String path = (String) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_PATH, null);
        int port = ((Integer) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_PORT, new Integer(0))).intValue();
        String username = (String) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_USERNAME, null);
        String password = (String) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_PASSWORD, null);
        boolean favorite = ((Boolean) seq.getAttribute(DefaultNetworkPlaceDetailsForm.ATTR_FAVORITE, Boolean.FALSE)).booleanValue();
        boolean readOnly = ((Boolean) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_READ_ONLY, Boolean.FALSE)).booleanValue();
        boolean allowRecursive = ((Boolean) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_ALLOW_RECURSIVE, Boolean.FALSE))
                        .booleanValue();
        boolean noDelete = ((Boolean) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_NO_DELETE, Boolean.FALSE)).booleanValue();
        boolean showHidden = ((Boolean) seq.getAttribute(NetworkPlaceDetailsForm.ATTR_SHOW_HIDDEN, Boolean.FALSE)).booleanValue();
        boolean autoStart = false; // TODO hook this into the UI
        DAVProcessor processor = DAVServlet.getDAVProcessor(request);
        // TODO get the resource permission that allowed this resource to be
        // created
        try {
            try { 

            	newNetworkPlace = NetworkPlaceDatabaseFactory.getInstance().createNetworkPlace(scheme, shortName, description, host,
                                path, port, username, password, readOnly, allowRecursive, noDelete, showHidden, autoStart, 
                                getSessionInfo(request).getUser().getRealm().getRealmID());

                CoreServlet.getServlet().fireCoreEvent(
                                NetworkPlaceResourceType.addNetworkPlaceAttributes(new ResourceChangeEvent(this,
                                                NetworkPlacesEventConstants.CREATE_NETWORK_PLACE, newNetworkPlace,
                                                getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL), newNetworkPlace));
            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(
                                new ResourceChangeEvent(this, NetworkPlacesEventConstants.CREATE_NETWORK_PLACE,
                                                getSessionInfo(request), e));
                throw e;
            }
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "networkPlaceWizard.networkPlaceFinish.status.profileCreated"));
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "networkPlaceWizard.networkPlaceFinish.status.failedToCreateProfile", e.getMessage()));
        }
        if (newNetworkPlace != null) {
            actionStatus.add(attachToPoliciesAndAddToFavorites("networkPlaceWizard.networkPlaceFinish", seq, newNetworkPlace, favorite, request));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    public NetworkPlace getNewNetworkPlace() {
        return newNetworkPlace;
    }

    /**
     * Exit the wizard
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }

}