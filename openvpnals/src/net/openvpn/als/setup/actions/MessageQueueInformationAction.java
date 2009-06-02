
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
			
package net.openvpn.als.setup.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.actions.AuthenticatedDispatchAction;
import net.openvpn.als.extensions.actions.ExtensionBundleInformationAction;
import net.openvpn.als.notification.Notifier;
import net.openvpn.als.notification.Notifier.MessageWrapper;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.setup.forms.MessageQueueInformationForm;

/**
 */
public final class MessageQueueInformationAction extends AuthenticatedDispatchAction {
   
    final static Log log = LogFactory.getLog(MessageQueueInformationAction.class);

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
            try {
                long messageId = Long.parseLong(request.getParameter("messageId"));
                MessageWrapper message = CoreServlet.getServlet().getNotifier().getMessage(messageId);
                request.setAttribute(Constants.REQ_ATTR_INFO_RESOURCE, message);
                return messageQueueInformation(mapping, form, request, response);
            } catch (Exception e) {
              log.error("Failed to get message information. ", e);
              response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());                
            }
            return null;
        }
        
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward messageQueueInformation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MessageQueueInformationForm informationForm = (MessageQueueInformationForm) form;
        Notifier.MessageWrapper messageWrapper = (Notifier.MessageWrapper) request.getAttribute(Constants.REQ_ATTR_INFO_RESOURCE);
        informationForm.initialise(messageWrapper);
        return mapping.findForward("display");
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}