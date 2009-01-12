/**
 * 
 */
package com.adito.agent;

import com.adito.boot.RequestHandlerRequest;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;

class DummyAgentCallback extends DefaultAgentCallback {

    public User authenticate(RequestHandlerRequest request) {

        /**
         * Perform an authentication based on the ticket provided in the request
         */
        SessionInfo session = DefaultAgentManager.getInstance().getSessionByAgentId((String) request.getParameters().get("ticket"));
        if (session == null) {
            if (!ok(request)) {
                return null;
            }
            try {
                session = (SessionInfo) LogonControllerFactory.getInstance().getActiveSessions().values().iterator().next();
                if (session == null)
                    return null;
                AgentUnregisterHook.register(session);
                return session.getUser();
            } catch (Exception e) {
                log.error("Failed to configure session.", e);
            }

        }
        return super.authenticate(request);
    }

    public AgentTunnel createAgent(String remoteHost, User user, String type, RequestHandlerRequest connectionParameters)
                    throws AgentException {

        if (type.equals(DEFAULT_AGENT_TYPE)) {
            SessionInfo session = (SessionInfo) LogonControllerFactory.getInstance().getActiveSessions().values().iterator().next();
            if (session == null)
                throw new AgentException("No session to attach to");

            String ticket = (String) connectionParameters.getParameters().get("ticket");
            AgentTunnel t = new AgentTunnel(ticket, session, DEFAULT_AGENT_TYPE, new DefaultAgentChannelFactory());
            DefaultAgentManager.getInstance().registerAgent(ticket, session, t);
            AgentUnregisterHook.register(session);
            return t;
        } else {
            throw new AgentException("Not supported by the callback");
        }
    }

    protected boolean ok(RequestHandlerRequest request) {
        return true;
    }
}