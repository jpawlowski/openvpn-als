/**
 * 
 */
package com.ovpnals.agent;

import com.ovpnals.boot.RequestHandlerRequest;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;

class AttachAgentCallback extends DefaultAgentCallback {
	
	public User authenticate(RequestHandlerRequest request) {
		SessionInfo session = DefaultAgentManager.getInstance()
				.getSessionByAgentId(
						(String) request.getParameters().get("ticket"));

		if (session == null && LogonControllerFactory.getInstance().getActiveSessions().size() > 0) {
			session = (SessionInfo) LogonControllerFactory.getInstance().getActiveSessions().values().iterator().next();
			String ticket = request.getParameters()
				.get("ticket").toString(); 
			LogonControllerFactory.getInstance().registerAuthorizationTicket(ticket, session);
		}
		return super.authenticate(request);
	}
}