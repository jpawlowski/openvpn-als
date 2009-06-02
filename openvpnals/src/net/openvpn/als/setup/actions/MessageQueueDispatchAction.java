
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

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.actions.AuthenticatedDispatchAction;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.setup.forms.MessageQueueForm;

/**
 */
public class MessageQueueDispatchAction extends AuthenticatedDispatchAction {
    /**
     */
    public MessageQueueDispatchAction() {
        super(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, 
            new Permission[] { PolicyConstants.PERM_VIEW,
                        PolicyConstants.PERM_SEND, 
                        PolicyConstants.PERM_CONTROL,
                        PolicyConstants.PERM_CLEAR });
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward confirmClearQueue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, PolicyConstants.PERM_CLEAR, request);
        return mapping.findForward("confirmClearQueue");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward clearQueue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE,  PolicyConstants.PERM_CLEAR, request);
        CoreServlet.getServlet().getNotifier().clearAllMessages();
        return mapping.findForward("refresh");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward sendMessage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, PolicyConstants.PERM_SEND, request);
        return mapping.findForward("sendMessage");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward enable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, PolicyConstants.PERM_CONTROL, request);
        String sel = ((MessageQueueForm)form).getSelectedSink(); 
        CoreServlet.getServlet().getNotifier().setEnabled(sel, true);
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY,  new ActionMessage("messageQueue.info.sinkEnabled", sel));
        saveMessages(request, msgs);
        return list(mapping, form, request, response);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward disable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, PolicyConstants.PERM_CONTROL, request);
        String sel = ((MessageQueueForm)form).getSelectedSink(); 
        CoreServlet.getServlet().getNotifier().setEnabled(sel, false);
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("messageQueue.info.sinkDisabled", sel));
        saveMessages(request, msgs);
        return list(mapping, form, request, response);
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return list(mapping, form, request, response);
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
    	CoreUtil.clearFlow(request);
        
        MessageQueueForm messageQueueForm = (MessageQueueForm) form;  
        messageQueueForm.initialize(request.getSession());
        return mapping.findForward("display");
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
}