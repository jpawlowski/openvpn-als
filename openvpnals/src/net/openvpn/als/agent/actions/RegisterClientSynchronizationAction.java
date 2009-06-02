
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
			
package net.openvpn.als.agent.actions;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.jdom.Document;
import org.jdom.Element;

import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.actions.XMLOutputAction;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.profile.ProfilePropertyKey;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;

/**
 * An {@link net.openvpn.als.core.actions.XMLOutputAction} that blocks until the
 * <i>OpenVPNALS Agent</i> or an application using the <i>Embedded Client API</i>
 * registers.
 * <p>
 * If registration does not occur within the specified amount of time an error
 * XML document will be returned, otherwise and XML document containing the port
 * on which the VPN client has been started is returned.
 * <p>
 * This is used by the agent launcher applet so prevent the next being displayed
 * until the VPN client is up and running.
 */
public class RegisterClientSynchronizationAction extends XMLOutputAction {

    final static Log log = LogFactory.getLog(RegisterClientSynchronizationAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {

        MessageResources resources = CoreUtil.getMessageResources(request.getSession(), "agent");

        // Get the pending VPN session ticket
        String ticket = request.getParameter("ticket");
        
        if (log.isDebugEnabled())
            log.debug("Registering agent synchronization " + ticket);

        if(DefaultAgentManager.getInstance().getSessionByAgentId(ticket)!=null) {
	        SessionInfo session = DefaultAgentManager.getInstance().getSessionByAgentId(ticket);
	        session.setSession(request.getSession());				
	        
	        int timeout = Property.getPropertyInt(new ProfilePropertyKey(
        		CoreUtil.getCurrentPropertyProfileId(request.getSession()), 
        		session.getUser().getPrincipalName(), "client.registration.synchronization.timeout", session.getUser().getRealm().getResourceId()));
	
	        if (log.isDebugEnabled())
	            log.debug("Waiting for agent registration (timeout " + timeout + ")");
	        
	        if (DefaultAgentManager.getInstance().waitForRegistrationAndSynchronization(ticket, timeout)) {
	            if (log.isDebugEnabled())
	                log.debug("Successfull agent registration");		
				LogonControllerFactory.getInstance().removeAuthorizationTicket(ticket);
	            Element root = new Element("success");
	            Document doc = new Document(root);
	            root.setText(resources.getMessage((Locale) request.getSession().getAttribute(Globals.LOCALE_KEY),
	                "registerSync.message.ok"));
	            sendDocument(doc, response);
	            
	            // Were complete
	            return null;
	        }
	        else {
	        	log.error("Registration of agent did not occur when the specified timeout of " + timeout + "ms");
				LogonControllerFactory.getInstance().removeAuthorizationTicket(ticket);
	        }
        }
        
        if (log.isDebugEnabled())
                log.debug("Failed agent registration");
        
        sendError(resources.getMessage((Locale) request.getSession().getAttribute(Globals.LOCALE_KEY),
                "registerSync.message.failed"), response);

        return null;
    }
}