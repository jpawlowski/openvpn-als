/**
 * 
 */
package com.adito.agent;

import com.adito.boot.RequestHandlerRequest;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;

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