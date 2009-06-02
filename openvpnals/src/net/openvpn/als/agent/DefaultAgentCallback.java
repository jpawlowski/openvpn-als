package net.openvpn.als.agent;

import java.net.InetAddress;

import net.openvpn.als.boot.RequestHandlerRequest;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.util.TicketGenerator;

public class DefaultAgentCallback extends BasicAuthenticatorAgentCallback {

	public static final String DEFAULT_AGENT_TYPE = "openvpnalsAgent";

	public AgentTunnel createAgent(String remoteHost, User user, String type,
			RequestHandlerRequest connectionParameters) throws AgentException {

		if (type.equals(DEFAULT_AGENT_TYPE)) {

			String ticket = (String) connectionParameters.getParameters().get("ticket");

			if (ticket == null) {
				
				ticket = (String)connectionParameters.getAttribute("directAgentTicket");
				
				if(ticket==null)
					throw new AgentException("");
			}

			SessionInfo session = DefaultAgentManager.getInstance()
					.getSessionByAgentId(ticket);

			if (session == null)
				throw new AgentException("");

			AgentTunnel t = new AgentTunnel(ticket, session, 
					DEFAULT_AGENT_TYPE, new DefaultAgentChannelFactory());
			DefaultAgentManager.getInstance().registerAgent(ticket, session, t);
			AgentUnregisterHook.register(session);
			return t;

		} else {
			throw new AgentException("");
		}
	}

	public User authenticate(RequestHandlerRequest request) {
		/**
		 * Perform an authentication based on the ticket provided in the request
		 */
		if (request.getField("Authorization") != null) {
			User user = super.authenticate(request);

			if (user != null) {

				try {

					SessionInfo session = SessionInfo.nextSession(null,
							TicketGenerator.getInstance().generateUniqueTicket(
									"DAGENT"), user, InetAddress
									.getByName(request.getRemoteAddr()),
							SessionInfo.AGENT, request.getField("User-Agent")
									.toString());
					LogonControllerFactory.getInstance().getActiveSessions()
							.put(session.getLogonTicket(), session);
					request.setAttribute("directAgentTicket", DefaultAgentManager.getInstance().registerPendingAgent(
							session));
					
					AgentUnregisterHook.register(session);
					return user;
				} catch (Exception e) {
					log.error("Failed to configure session.", e);
				}

			}
			return null;
		} else {
			String ticket = (String) request.getParameters().get("ticket");
			if(ticket != null) {
				SessionInfo session = DefaultAgentManager.getInstance()
						.getSessionByAgentId(
								ticket);
	
				// Make sure only one agent is registered at any one time
				if (session != null
						&& DefaultAgentManager.getInstance().getAgentBySession(
								session) == null)
					return session.getUser();
			}

			return null;
		}

	}

}
