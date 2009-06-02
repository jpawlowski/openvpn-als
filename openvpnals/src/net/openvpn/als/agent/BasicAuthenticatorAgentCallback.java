package net.openvpn.als.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public abstract class BasicAuthenticatorAgentCallback extends UsernameAndPasswordAgentAuthenticator implements AgentCallback {
    static final Log log = LogFactory.getLog(BasicAuthenticatorAgentCallback.class);

    public void removeAgent(AgentTunnel tunnel) throws AgentException {
		DefaultAgentManager.getInstance().unregisterAgent(tunnel);
	}
}